#!/usr/bin/env python3
"""
Parse specs from docs/specs folder and create/update GitHub issues.
This script automates issue creation based on specification requirements.
"""

import os
import re
import sys
import json
import requests
from pathlib import Path
from typing import Dict, List, Tuple

# Configuration
SPECS_DIR = Path("docs/specs")
GITHUB_API_URL = "https://api.github.com"
REPO_OWNER = os.getenv("REPO_OWNER")
REPO_NAME = os.getenv("REPO_NAME")
GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")

REPO_URL = f"{GITHUB_API_URL}/repos/{REPO_OWNER}/{REPO_NAME}"
ISSUES_URL = f"{REPO_URL}/issues"

# Headers for API requests
HEADERS = {
    "Authorization": f"token {GITHUB_TOKEN}",
    "Accept": "application/vnd.github.v3+json",
    "Content-Type": "application/json",
}


def get_spec_files() -> List[Tuple[str, Path]]:
    """
    Get all spec files from docs/specs folder, sorted by index.
    Returns list of (folder_path, file_path) tuples.
    """
    spec_files = []

    # Get root spec files
    for file_path in sorted(SPECS_DIR.glob("*.md")):
        if file_path.name.startswith(("001", "002")):
            spec_files.append(("root", file_path))

    # Get feature spec files
    for feature_dir in sorted(SPECS_DIR.iterdir()):
        if feature_dir.is_dir():
            for file_path in sorted(feature_dir.glob("*.md")):
                if not file_path.name.startswith("001"):  # Skip readme files
                    spec_files.append((feature_dir.name, file_path))

    return spec_files


def parse_spec_file(file_path: Path) -> Dict:
    """
    Parse a spec file and extract requirements.
    Returns a dict with file metadata and requirements.
    """
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()

    # Extract title from H1
    title_match = re.search(r"^# (.+)$", content, re.MULTILINE)
    title = title_match.group(1) if title_match else file_path.stem

    # Extract objective/purpose
    objective_match = re.search(
        r"## (?:Objective|Purpose)\n\n(.+?)(?=\n##|\Z)", content, re.DOTALL
    )
    objective = (
        objective_match.group(1).strip() if objective_match else ""
    )

    # Extract functional requirements
    functional_match = re.search(
        r"## Functional Requirements\n\n(.*?)(?=\n## Non-Functional|\Z)",
        content,
        re.DOTALL,
    )
    functional_reqs = functional_match.group(1) if functional_match else ""

    # Extract acceptance criteria
    acceptance_match = re.search(
        r"## Acceptance Criteria\n\n(.*?)(?=\Z)", content, re.DOTALL
    )
    acceptance = acceptance_match.group(1) if acceptance_match else ""

    return {
        "title": title,
        "file_path": str(file_path),
        "objective": objective,
        "functional_requirements": functional_reqs,
        "acceptance_criteria": acceptance,
        "full_content": content,
    }


def extract_requirements(text: str) -> List[str]:
    """Extract individual requirements from requirement text."""
    requirements = []
    # Match lines starting with checkbox or number
    pattern = r"^\s*\[\s*\]\s+(.+?)$|^\d+\.\s+(.+?)$"
    for match in re.finditer(pattern, text, re.MULTILINE):
        req = match.group(1) or match.group(2)
        if req:
            requirements.append(req.strip())
    return requirements


def create_github_issue(title: str, body: str, labels: List[str]) -> Dict:
    """
    Create a GitHub issue.
    Returns the response from the GitHub API.
    """
    payload = {
        "title": title,
        "body": body,
        "labels": labels,
    }

    response = requests.post(ISSUES_URL, headers=HEADERS, json=payload)

    if response.status_code == 201:
        print(f"✅ Created issue: {title}")
        return response.json()
    else:
        print(f"❌ Failed to create issue: {title}")
        print(f"   Status: {response.status_code}")
        print(f"   Response: {response.text}")
        return {}


def find_existing_issue(title: str) -> Dict | None:
    """Find an existing issue by title."""
    response = requests.get(
        ISSUES_URL,
        headers=HEADERS,
        params={"state": "all", "per_page": 100},
    )

    if response.status_code == 200:
        for issue in response.json():
            if issue["title"] == title:
                return issue
    return None


def get_spec_label(file_path: str) -> str:
    """Determine label based on spec file path."""
    if "shopping" in file_path:
        return "feature/shopping"
    elif "home" in file_path:
        return "feature/home"
    else:
        return "feature/core"


def generate_issue_body(spec: Dict) -> str:
    """Generate GitHub issue body from spec."""
    body = f"""## Overview
{spec['objective']}

## Specification File
📄 [`{spec['file_path']}`]({spec['file_path']})

### Checkbox Status Legend
Track implementation progress in the spec file:
- `[ ]` — Not started
- `[-]` — In progress
- `[X]` — Done

## Functional Requirements
{spec['functional_requirements'] or '_None specified_'}

## Acceptance Criteria
{spec['acceptance_criteria'] or '_None specified_'}

---
**Issue Status:** This issue's GitHub status label updates automatically based on checkbox marks in the spec file.
- All `[ ]` → `status/backlog`
- Mix of `[ ]` and others → `status/in-progress`
- All `[X]` or filled → `status/completed`

*This issue was automatically created from specification requirements.*
"""
    return body


def main():
    """Main execution function."""
    if not all([REPO_OWNER, REPO_NAME, GITHUB_TOKEN]):
        print("❌ Missing required environment variables:")
        print(f"   REPO_OWNER: {REPO_OWNER}")
        print(f"   REPO_NAME: {REPO_NAME}")
        print(f"   GITHUB_TOKEN: {'***' if GITHUB_TOKEN else 'NOT SET'}")
        sys.exit(1)

    print(f"🔍 Scanning specs in {SPECS_DIR}...")
    spec_files = get_spec_files()

    if not spec_files:
        print("⚠️  No spec files found.")
        return

    print(f"📋 Found {len(spec_files)} spec files\n")

    created_count = 0
    skipped_count = 0

    for folder, file_path in spec_files:
        print(f"Processing: {file_path}")

        # Skip readme files
        if file_path.name.startswith("001"):
            print("   ⏭️  Skipping readme file\n")
            continue

        # Parse spec
        spec = parse_spec_file(file_path)
        print(f"   Title: {spec['title']}")

        # Check if issue already exists
        existing = find_existing_issue(spec["title"])
        if existing:
            print(f"   ℹ️  Issue already exists: #{existing['number']}\n")
            skipped_count += 1
            continue

        # Generate issue body and determine labels
        body = generate_issue_body(spec)
        labels = [
            get_spec_label(spec["file_path"]),
            "from/specs",
            "status/backlog",
        ]

        # Create issue
        result = create_github_issue(spec["title"], body, labels)
        if result:
            created_count += 1
        print()

    # Summary
    print("\n" + "=" * 60)
    print("📊 Summary")
    print("=" * 60)
    print(f"✅ Created: {created_count}")
    print(f"⏭️  Skipped (already exist): {skipped_count}")
    print(f"📝 Total processed: {len(spec_files) - 1}")  # -1 for readme


if __name__ == "__main__":
    main()

