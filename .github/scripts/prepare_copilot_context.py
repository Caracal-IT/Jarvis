#!/usr/bin/env python3
"""
Prepare Copilot context with actionable issue summary.
Creates a readable markdown file that Copilot can use to understand the current state.
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
}

OUTPUT_FILE = Path(".github/copilot-issue-summary.md")


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


def get_status_label(issue: Dict) -> str:
    """Extract status label from issue."""
    for label in issue.get("labels", []):
        if label["name"].startswith("status/"):
            return label["name"].replace("status/", "")
    return "backlog"


def get_feature_label(issue: Dict) -> str:
    """Extract feature label from issue."""
    for label in issue.get("labels", []):
        if label["name"].startswith("feature/"):
            return label["name"].replace("feature/", "").upper()
    return "CORE"


def extract_spec_file(issue_body: str) -> str | None:
    """Extract spec file path from issue body."""
    import re

    match = re.search(r"\[`(docs/specs/[^`]+)`\]", issue_body)
    return match.group(1) if match else None


def generate_markdown_summary(issues: List[Dict]) -> str:
    """Generate markdown summary of all issues for Copilot."""
    if not issues:
        return "# Copilot Issue Summary\n\nNo issues found.\n"

    # Group by feature
    by_feature = {}
    by_status = {"backlog": [], "in-progress": [], "completed": []}

    for issue in issues:
        feature = get_feature_label(issue)
        status = get_status_label(issue)

        if feature not in by_feature:
            by_feature[feature] = []
        by_feature[feature].append(issue)
        by_status[status].append(issue)

    # Build markdown
    md = """# Copilot Issue Summary

**Generated for:** GitHub Actions Automation
**Purpose:** Help Copilot understand current issues and update spec files

---

## Quick Status Overview

| Status | Count | Action |
|--------|-------|--------|
| 🔴 Backlog | {backlog_count} | Review and start implementation |
| 🟡 In Progress | {in_progress_count} | Continue development |
| 🟢 Completed | {completed_count} | Close and validate |

---

## Issues by Feature

""".format(
        backlog_count=len(by_status["backlog"]),
        in_progress_count=len(by_status["in-progress"]),
        completed_count=len(by_status["completed"]),
    )

    for feature in sorted(by_feature.keys()):
        feature_issues = by_feature[feature]
        md += f"\n### {feature}\n\n"

        for issue in sorted(feature_issues, key=lambda x: x["number"]):
            status = get_status_label(issue)
            spec_file = extract_spec_file(issue.get("body", ""))

            status_icon = {
                "backlog": "🔴",
                "in-progress": "🟡",
                "completed": "🟢",
            }.get(status, "⚪")

            md += f"#### {status_icon} #{issue['number']}: {issue['title']}\n"
            md += f"- **Status:** `{status}`\n"
            if spec_file:
                md += f"- **Spec:** [`{spec_file}`]({spec_file})\n"
            md += f"- **URL:** {issue['html_url']}\n"
            md += "\n"

    # Add by-status section
    md += "\n---\n\n## Issues by Status\n"

    for status in ["backlog", "in-progress", "completed"]:
        issues_in_status = by_status[status]
        if not issues_in_status:
            continue

        status_title = status.replace("-", " ").title()
        md += f"\n### {status_title}\n\n"

        for issue in sorted(issues_in_status, key=lambda x: x["number"]):
            spec_file = extract_spec_file(issue.get("body", ""))
            md += f"- #{issue['number']}: [{issue['title']}]({issue['html_url']})"
            if spec_file:
                md += f" → `{spec_file}`"
            md += "\n"

    # Add action items for Copilot
    md += """

---

## Copilot Action Guide

### How to Update Issue Status

**To mark items as In Progress:**
1. Find the issue's spec file
2. Update checkboxes: `[ ]` → `[-]`
3. Push to main
4. Workflow auto-updates GitHub issue

**To mark items as Completed:**
1. Find the issue's spec file
2. Update checkboxes: `[ ]` → `[X]`
3. Push to main
4. Workflow auto-updates GitHub issue

**To add new items:**
1. Edit the spec file
2. Add new requirement with `[ ]` checkbox
3. Push to main
4. Workflow auto-creates new issue

### Linking Back to Specs

All issues include:
- **Link to spec file** in the issue body
- **Checkbox legend** explaining the status system
- **Implementation rules** for consistency

### How Copilot Uses This

This file helps Copilot:
1. Understand what work is pending
2. Know which specs need updates
3. Track implementation status
4. Update progress when completing work
5. Add new requirements as discovered

---

## Implementation Status Summary

"""

    # Calculate metrics
    total = len(issues)
    completed = len(by_status["completed"])
    in_progress = len(by_status["in-progress"])
    backlog = len(by_status["backlog"])

    completion_pct = int((completed / total) * 100) if total > 0 else 0

    md += f"""
**Total Issues:** {total}
- Completed: {completed} ({completion_pct}%)
- In Progress: {in_progress}
- Backlog: {backlog}

**Next Steps:**
1. Review backlog items
2. Start implementation
3. Update spec checkboxes as work progresses
4. Workflow automatically syncs status

---

**Note:** This file is auto-generated by the GitHub Actions workflow.
It's designed to be read by Copilot and humans to understand progress.
"""

    return md


def main():
    """Generate Copilot-readable issue summary."""
    if not all([REPO_OWNER, REPO_NAME, GITHUB_TOKEN]):
        print("❌ Missing environment variables")
        return

    print("📝 Preparing Copilot issue summary...\n")

    # Fetch issues
    issues = get_all_spec_issues()
    print(f"Found {len(issues)} issues")

    # Generate markdown
    markdown = generate_markdown_summary(issues)

    # Save file
    with open(OUTPUT_FILE, "w") as f:
        f.write(markdown)

    print(f"✅ Saved summary to {OUTPUT_FILE}")
    print(f"   File size: {len(markdown)} bytes")
    print(f"   Ready for Copilot to read")


if __name__ == "__main__":
    main()

