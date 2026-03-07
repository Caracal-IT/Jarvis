#!/usr/bin/env python3
"""
Generate Copilot-readable issue context file.
Creates a JSON file with all issues linked to specs for Copilot to read and update.
"""

import os
import json
import requests
from pathlib import Path
from typing import Dict, List

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

OUTPUT_FILE = Path(".github/issue-context.json")


def get_all_spec_issues() -> List[Dict]:
    """Fetch all issues created from specs."""
    issues = []
    page = 1

    while True:
        response = requests.get(
            ISSUES_URL,
            headers=HEADERS,
            params={
                "state": "all",
                "labels": "from/specs",
                "per_page": 100,
                "page": page,
            },
        )

        if response.status_code != 200:
            break

        page_issues = response.json()
        if not page_issues:
            break

        issues.extend(page_issues)
        page += 1

    return issues


def extract_spec_file_from_issue(issue_body: str) -> str | None:
    """Extract spec file path from issue body."""
    import re

    match = re.search(r"\[`(docs/specs/[^`]+)`\]", issue_body)
    return match.group(1) if match else None


def format_issue_for_copilot(issue: Dict) -> Dict:
    """Format issue data for Copilot readability."""
    spec_file = extract_spec_file_from_issue(issue.get("body", ""))

    return {
        "issue_number": issue["number"],
        "title": issue["title"],
        "state": issue["state"],
        "spec_file": spec_file,
        "url": issue["html_url"],
        "body": issue["body"],
        "labels": [label["name"] for label in issue.get("labels", [])],
        "status_label": next(
            (label["name"] for label in issue.get("labels", [])
             if label["name"].startswith("status/")),
            "status/backlog",
        ),
        "created_at": issue["created_at"],
        "updated_at": issue["updated_at"],
    }


def create_copilot_context(issues: List[Dict]) -> Dict:
    """Create context object for Copilot."""
    formatted_issues = [format_issue_for_copilot(issue) for issue in issues]

    # Group by spec file
    by_spec = {}
    for issue_data in formatted_issues:
        if issue_data["spec_file"]:
            spec = issue_data["spec_file"]
            if spec not in by_spec:
                by_spec[spec] = []
            by_spec[spec].append(issue_data)

    return {
        "metadata": {
            "total_issues": len(formatted_issues),
            "total_specs": len(by_spec),
            "generated_at": Path(".github/issue-context.json").stat().st_mtime
            if OUTPUT_FILE.exists()
            else None,
            "description": "Complete mapping of GitHub issues to specification files",
            "usage": "Use this file to understand current issues and update spec file progress",
        },
        "by_spec": by_spec,
        "by_status": {
            "backlog": [
                i for i in formatted_issues
                if i["status_label"] == "status/backlog"
            ],
            "in-progress": [
                i for i in formatted_issues
                if i["status_label"] == "status/in-progress"
            ],
            "completed": [
                i for i in formatted_issues
                if i["status_label"] == "status/completed"
            ],
        },
        "all_issues": formatted_issues,
    }


def main():
    """Generate and save issue context for Copilot."""
    if not all([REPO_OWNER, REPO_NAME, GITHUB_TOKEN]):
        print("❌ Missing environment variables")
        return

    print("📖 Generating Copilot-readable issue context...\n")

    # Fetch all spec-based issues
    issues = get_all_spec_issues()
    print(f"Found {len(issues)} issues from specs")

    if not issues:
        print("⚠️  No spec-based issues found")
        return

    # Create context
    context = create_copilot_context(issues)

    # Save to file
    with open(OUTPUT_FILE, "w") as f:
        json.dump(context, f, indent=2)

    print(f"✅ Saved context to {OUTPUT_FILE}")
    print(f"   Total issues: {context['metadata']['total_issues']}")
    print(f"   Total specs: {context['metadata']['total_specs']}")
    print(f"\n   Status breakdown:")
    print(f"   - Backlog: {len(context['by_status']['backlog'])}")
    print(f"   - In Progress: {len(context['by_status']['in-progress'])}")
    print(f"   - Completed: {len(context['by_status']['completed'])}")


if __name__ == "__main__":
    main()

