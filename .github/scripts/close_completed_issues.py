#!/usr/bin/env python3
"""
Sync GitHub issue open/closed state from spec completion.

Rules:
- Close issue when all real requirement checkboxes are completed.
- Reopen issue when not all real requirement checkboxes are completed.

Checkboxes in the "Checkbox Status Legend" section are ignored.
"""

import os
import re
import sys
import requests
from pathlib import Path
from typing import Dict, List

# Configuration
SPECS_DIR = Path("docs/specs")
GITHUB_API_URL = "https://api.github.com"
REPO_OWNER = os.getenv("REPO_OWNER")
REPO_NAME = os.getenv("REPO_NAME")
GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")

REPO_URL = f"{GITHUB_API_URL}/repos/{REPO_OWNER}/{REPO_NAME}"
ISSUES_URL = f"{REPO_URL}/issues"

HEADERS = {
    "Authorization": f"token {GITHUB_TOKEN}",
    "Accept": "application/vnd.github.v3+json",
    "Content-Type": "application/json",
}


def get_spec_files() -> List[Path]:
    """
    Get all spec files (excluding readmes).
    Picks up all files matching: NNN *-requirements.md
    Skips: 001 readme.md files
    """
    spec_files = []

    # Get root spec files (002+, skip 001 readme)
    for file_path in sorted(SPECS_DIR.glob("*.md")):
        # Skip readme files (001 prefix)
        if file_path.name.startswith("001"):
            continue
        # Include all other numbered files (002, 003, 004, etc.)
        if re.match(r"^\d{3}\s", file_path.name):
            spec_files.append(file_path)

    # Get feature spec files
    for feature_dir in sorted(SPECS_DIR.iterdir()):
        if feature_dir.is_dir():
            for file_path in sorted(feature_dir.glob("*.md")):
                # Skip readme files (001 prefix)
                if file_path.name.startswith("001"):
                    continue
                # Include all other numbered files
                if re.match(r"^\d{3}\s", file_path.name):
                    spec_files.append(file_path)

    return spec_files


def parse_checkbox_status(content: str) -> str:
    """
    Parse spec file and determine overall checkbox status.
    Returns: "backlog", "in-progress", or "completed"
    Ignores checkboxes in the "Checkbox Status Legend" section.
    """
    # Remove the Checkbox Status Legend section
    legend_start = content.find("## Checkbox Status Legend")
    if legend_start != -1:
        next_section = content.find("\n## ", legend_start + 1)
        if next_section == -1:
            legend_end = legend_start
            for line in content[legend_start:].split('\n')[1:]:
                if line.strip() and not line.startswith('-') and not line.startswith('`'):
                    break
                legend_end += len(line) + 1
        else:
            legend_end = next_section

        content_without_legend = content[:legend_start] + content[legend_end:]
    else:
        content_without_legend = content

    # Extract checkboxes
    checkbox_pattern = r"\[(.)\]"
    matches = re.findall(checkbox_pattern, content_without_legend)

    if not matches:
        return "backlog"

    # Count status types
    not_started = sum(1 for m in matches if m == " ")
    in_progress = sum(1 for m in matches if m == "-")
    completed = sum(1 for m in matches if m != " " and m != "-")

    total = len(matches)

    # Determine overall status
    if not_started == total:
        return "backlog"
    elif completed == total:
        return "completed"
    elif in_progress > 0 or (completed > 0 and not_started > 0):
        return "in-progress"
    else:
        return "backlog"


def extract_spec_file_from_issue(issue_body: str) -> str | None:
    """Extract spec file path from issue body."""
    match = re.search(r"\[`(docs/specs/[^`]+)`\]", issue_body)
    return match.group(1) if match else None


def get_spec_title(file_path: Path) -> str:
    """Get title from spec file."""
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()
    title_match = re.search(r"^# (.+)$", content, re.MULTILINE)
    return title_match.group(1) if title_match else file_path.stem


def find_issue_by_title(title: str) -> Dict | None:
    """Find GitHub issue by title."""
    response = requests.get(
        ISSUES_URL,
        headers=HEADERS,
        params={"state": "all", "per_page": 100},
    )

    if response.status_code == 200:
        for issue in response.json():
            if issue["title"] == title and "from/specs" in [label["name"] for label in issue.get("labels", [])]:
                return issue
    return None


def close_github_issue(issue_number: int) -> bool:
    """Close a GitHub issue."""
    url = f"{ISSUES_URL}/{issue_number}"
    payload = {"state": "closed"}

    response = requests.patch(url, headers=HEADERS, json=payload)

    if response.status_code == 200:
        print(f"✅ Closed issue: #{issue_number}")
        return True
    else:
        print(f"❌ Failed to close issue: #{issue_number}")
        print(f"   Status: {response.status_code}")
        print(f"   Response: {response.text}")
        return False


def reopen_github_issue(issue_number: int) -> bool:
    """Reopen a GitHub issue."""
    url = f"{ISSUES_URL}/{issue_number}"
    payload = {"state": "open"}

    response = requests.patch(url, headers=HEADERS, json=payload)

    if response.status_code == 200:
        print(f"✅ Reopened issue: #{issue_number}")
        return True
    else:
        print(f"❌ Failed to reopen issue: #{issue_number}")
        print(f"   Status: {response.status_code}")
        print(f"   Response: {response.text}")
        return False


def main():
    """Close completed issues and reopen incomplete issues."""
    if not all([REPO_OWNER, REPO_NAME, GITHUB_TOKEN]):
        print("❌ Missing required environment variables")
        sys.exit(1)

    print("🔍 Syncing issue state from spec completion...\n")

    spec_files = get_spec_files()
    if not spec_files:
        print("⚠️  No spec files found")
        return

    closed_count = 0
    reopened_count = 0
    skipped_count = 0

    for file_path in spec_files:
        if file_path.name.startswith("001"):  # Skip readme
            continue

        # Read spec file
        with open(file_path, "r", encoding="utf-8") as f:
            content = f.read()

        # Get title and status
        title = get_spec_title(file_path)
        status = parse_checkbox_status(content)

        print(f"Checking: {title}")
        print(f"  File: {file_path}")
        print(f"  Status: {status}")

        issue = find_issue_by_title(title)
        if not issue:
            print("  Issue: Not found\n")
            skipped_count += 1
            continue

        issue_number = issue["number"]
        issue_state = issue["state"]
        print(f"  Issue #: {issue_number} ({issue_state})")

        if status == "completed":
            if issue_state == "open":
                if close_github_issue(issue_number):
                    closed_count += 1
            else:
                print("  Action: Already closed")
                skipped_count += 1
        else:
            if issue_state == "closed":
                if reopen_github_issue(issue_number):
                    reopened_count += 1
            else:
                print("  Action: Keep open (not completed)")
                skipped_count += 1

        print()

    # Summary
    print("\n" + "=" * 60)
    print("📊 Summary")
    print("=" * 60)
    print(f"✅ Closed: {closed_count}")
    print(f"🔓 Reopened: {reopened_count}")
    print(f"⏭️  Skipped: {skipped_count}")
    print(f"📝 Total processed: {len(spec_files)}")
    print("=" * 60)


if __name__ == "__main__":
    main()

