#!/usr/bin/env python3
"""
Sync checkbox status from spec files to GitHub issues.
Updates issue status LABELS based on spec file checkbox marks.

IMPORTANT: Checkbox marks do NOT affect whether an issue is open/closed.
They only affect the status LABEL on the issue.

Status Label Mapping:
  All [ ] → status/backlog (not started yet)
  Mix of [ ] and others → status/in-progress (partially done)
  All [X] or filled → status/completed (implementation done)

GitHub Issue Open/Closed:
  Checkboxes are ignored when determining if issue is open.
  Issues stay open until manually closed.
  Status labels are just tracking implementation progress.
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
    """Get all spec files (excluding readmes)."""
    spec_files = []

    # Get root spec files
    for file_path in sorted(SPECS_DIR.glob("*.md")):
        if file_path.name.startswith("002"):  # Only requirement files, skip readme
            spec_files.append(file_path)

    # Get feature spec files
    for feature_dir in sorted(SPECS_DIR.iterdir()):
        if feature_dir.is_dir():
            for file_path in sorted(feature_dir.glob("*.md")):
                if not file_path.name.startswith("001"):  # Skip readme files
                    spec_files.append(file_path)

    return spec_files


def parse_checkbox_status(content: str) -> Dict[str, str]:
    """
    Parse spec file and determine overall checkbox status.
    Returns: "backlog", "in-progress", or "completed"

    Ignores checkboxes in the "Checkbox Status Legend" section.
    Only counts checkboxes in actual requirements.
    """
    # Remove the Checkbox Status Legend section to avoid counting its examples
    # Find the legend section and remove it
    legend_start = content.find("## Checkbox Status Legend")
    if legend_start != -1:
        # Find the next ## section or end of file
        next_section = content.find("\n## ", legend_start + 1)
        if next_section == -1:
            # Legend is the last section, but check if there's content after it
            # Find the next line that's not part of the legend (doesn't start with -)
            legend_end = legend_start
            for line in content[legend_start:].split('\n')[1:]:
                if line.strip() and not line.startswith('-') and not line.startswith('`'):
                    break
                legend_end += len(line) + 1
        else:
            legend_end = next_section

        # Remove legend section from content for checkbox parsing
        content_without_legend = content[:legend_start] + content[legend_end:]
    else:
        content_without_legend = content

    # Extract all checkbox lines (only from actual requirements, not legend)
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


def extract_spec_title(content: str) -> str:
    """Extract title from H1 heading."""
    match = re.search(r"^# (.+)$", content, re.MULTILINE)
    return match.group(1) if match else ""


def find_issue_by_spec_file(spec_file_path: str) -> Dict | None:
    """Find GitHub issue related to a spec file."""
    response = requests.get(
        ISSUES_URL,
        headers=HEADERS,
        params={"state": "all", "labels": "from/specs", "per_page": 100},
    )

    if response.status_code == 200:
        for issue in response.json():
            if spec_file_path in issue.get("body", ""):
                return issue
    return None


def update_issue_status(issue_number: int, status: str) -> bool:
    """Update issue labels based on checkpoint status."""
    url = f"{ISSUES_URL}/{issue_number}"

    # Get current labels
    response = requests.get(url, headers=HEADERS)
    if response.status_code != 200:
        return False

    issue = response.json()
    current_labels = [label["name"] for label in issue.get("labels", [])]

    # Remove old status labels
    new_labels = [
        label for label in current_labels
        if not label.startswith("status/")
    ]

    # Add new status label
    status_label_map = {
        "backlog": "status/backlog",
        "in-progress": "status/in-progress",
        "completed": "status/completed",
    }

    new_labels.append(status_label_map.get(status, "status/backlog"))

    # Update issue
    payload = {"labels": new_labels}
    response = requests.patch(url, headers=HEADERS, json=payload)

    return response.status_code == 200


def get_completion_percentage(content: str) -> int:
    """Calculate completion percentage from checkboxes."""
    checkbox_pattern = r"\[(.)\]"
    matches = re.findall(checkbox_pattern, content)

    if not matches:
        return 0

    completed = sum(1 for m in matches if m != " " and m != "-")
    return int((completed / len(matches)) * 100)


def main():
    """Sync checkbox status from spec files to GitHub issues."""
    if not all([REPO_OWNER, REPO_NAME, GITHUB_TOKEN]):
        print("❌ Missing required environment variables")
        sys.exit(1)

    print("🔄 Syncing checkbox status from specs to GitHub issues...\n")

    spec_files = get_spec_files()
    if not spec_files:
        print("⚠️  No spec files found")
        return

    updated_count = 0
    skipped_count = 0

    for spec_file in spec_files:
        print(f"Processing: {spec_file.name}")

        with open(spec_file, "r", encoding="utf-8") as f:
            content = f.read()

        title = extract_spec_title(content)
        if not title:
            print("   ⏭️  No title found, skipping\n")
            continue

        # Find related GitHub issue
        issue = find_issue_by_spec_file(str(spec_file))
        if not issue:
            print("   ℹ️  No related issue found\n")
            skipped_count += 1
            continue

        # Parse checkbox status
        status = parse_checkbox_status(content)
        completion = get_completion_percentage(content)

        print(f"   Title: {title}")
        print(f"   Issue #: {issue['number']}")
        print(f"   Status: {status}")
        print(f"   Completion: {completion}%")

        # Update issue
        if update_issue_status(issue["number"], status):
            print(f"   ✅ Updated status label to '{status}'")
            updated_count += 1
        else:
            print(f"   ❌ Failed to update issue")

        print()

    # Summary
    print("\n" + "=" * 60)
    print("📊 Sync Summary")
    print("=" * 60)
    print(f"✅ Updated: {updated_count}")
    print(f"⏭️  Skipped: {skipped_count}")
    print(f"📝 Total processed: {len(spec_files)}")
    print("=" * 60)


if __name__ == "__main__":
    main()

