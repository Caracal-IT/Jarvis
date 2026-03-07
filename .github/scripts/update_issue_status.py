#!/usr/bin/env python3
"""
Update GitHub issue status based on implementation progress.
Monitors issue labels and updates based on PR activity.
"""

import os
import requests
from typing import List, Dict

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


def get_spec_issues() -> List[Dict]:
    """Get all issues created from specs."""
    response = requests.get(
        ISSUES_URL,
        headers=HEADERS,
        params={
            "state": "open",
            "labels": "from/specs",
            "per_page": 100,
        },
    )

    if response.status_code == 200:
        return response.json()
    return []


def update_issue_label(issue_number: int, labels: List[str]) -> bool:
    """Update labels on an issue."""
    url = f"{ISSUES_URL}/{issue_number}"
    payload = {"labels": labels}

    response = requests.patch(url, headers=HEADERS, json=payload)
    return response.status_code == 200


def get_issue_related_prs(issue_number: int) -> List[Dict]:
    """Get pull requests related to an issue."""
    url = f"{REPO_URL}/search"
    query = f"is:pr is:merged mentions:#{issue_number}"

    response = requests.get(
        f"{GITHUB_API_URL}/search/issues",
        headers=HEADERS,
        params={"q": f"{query} repo:{REPO_OWNER}/{REPO_NAME}"},
    )

    if response.status_code == 200:
        return response.json().get("items", [])
    return []


def main():
    """Update issue statuses based on implementation progress."""
    print("🔄 Updating issue statuses from specs...\n")

    issues = get_spec_issues()
    updated_count = 0

    for issue in issues:
        issue_number = issue["number"]
        title = issue["title"]
        labels = [label["name"] for label in issue["labels"]]

        print(f"Checking issue #{issue_number}: {title}")

        # Check for related PRs
        prs = get_issue_related_prs(issue_number)

        if prs:
            # If there are merged PRs, mark as in progress or done
            merged_prs = [pr for pr in prs if pr["state"] == "closed"]
            if merged_prs:
                new_labels = [l for l in labels if not l.startswith("status/")]
                new_labels.append("status/completed")

                print(f"   ✅ Found merged PR(s), marking as completed")
            else:
                new_labels = [l for l in labels if not l.startswith("status/")]
                new_labels.append("status/in-progress")

                print(f"   🚀 Found open PR(s), marking as in progress")

            if update_issue_label(issue_number, new_labels):
                updated_count += 1
                print(f"   ✅ Updated labels")
            else:
                print(f"   ❌ Failed to update labels")
        else:
            print(f"   ℹ️  No related PRs found")

        print()

    print("=" * 60)
    print(f"📊 Updated {updated_count} issue(s)")
    print("=" * 60)


if __name__ == "__main__":
    main()

