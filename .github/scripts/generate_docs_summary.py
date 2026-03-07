#!/usr/bin/env python3
"""
Generate a docs/readme.md summary of all specification files.
This script runs during the specs-to-issues workflow to keep
the documentation index up to date automatically.
"""

import re
import sys
from datetime import datetime, timezone
from pathlib import Path
from typing import Dict, List, Tuple

SPECS_DIR = Path("docs/specs")
OUTPUT_FILE = Path("docs/readme.md")

# Human-readable category names keyed on the sub-folder name.
# "root" is the virtual category for specs that live directly in docs/specs/.
CATEGORY_NAMES: Dict[str, str] = {
    "root": "Core Features",
    "shopping": "Shopping Feature",
}


def get_spec_files() -> List[Tuple[str, Path]]:
    """
    Collect all spec files from docs/specs/, sorted by index.
    Returns a list of (category, file_path) tuples.
    Skips 001 readme.md files.
    """
    spec_files: List[Tuple[str, Path]] = []

    # Root-level specs (002 and above)
    for file_path in sorted(SPECS_DIR.glob("*.md")):
        if file_path.name.startswith("001"):
            continue
        if re.match(r"^\d{3}\s", file_path.name):
            spec_files.append(("root", file_path))

    # Feature sub-folder specs
    for feature_dir in sorted(SPECS_DIR.iterdir()):
        if not feature_dir.is_dir():
            continue
        for file_path in sorted(feature_dir.glob("*.md")):
            if file_path.name.startswith("001"):
                continue
            if re.match(r"^\d{3}\s", file_path.name):
                spec_files.append((feature_dir.name, file_path))

    return spec_files


def get_spec_status(file_path: Path) -> Tuple[str, str]:
    """
    Determine the status of a spec file by inspecting its checkboxes.

    All checkboxes are counted except those in the "Checkbox Status Legend"
    section, which contains example checkboxes that must not affect the result.

    Rules (matching sync_spec_status.py):
      All [ ]          → Backlog  🔴
      All [X]          → Completed ✅
      Any [-] or mix   → In Progress 🟡

    Returns (status_label, status_emoji).
    """
    try:
        content = file_path.read_text(encoding="utf-8")
    except (OSError, UnicodeDecodeError) as exc:
        print(f"⚠️  Could not read {file_path}: {exc}", file=sys.stderr)
        return ("Backlog", "🔴")

    # Strip only the "Checkbox Status Legend" section — it contains example
    # checkboxes that must not influence the status calculation.
    excluded_sections = re.compile(
        r"## Checkbox Status Legend.*?(?=\n## |\Z)",
        re.DOTALL,
    )
    content = excluded_sections.sub("", content)

    matches = re.findall(r"\[(.)\]", content)
    if not matches:
        return ("Backlog", "🔴")

    not_started = sum(1 for m in matches if m == " ")
    in_progress = sum(1 for m in matches if m == "-")
    completed = sum(1 for m in matches if m != " " and m != "-")
    total = len(matches)

    if not_started == total:
        return ("Backlog", "🔴")
    if completed == total:
        return ("Completed", "✅")
    return ("In Progress", "🟡")


def get_spec_title(file_path: Path) -> str:
    """Extract the H1 title from a spec file, falling back to the file stem."""
    try:
        content = file_path.read_text(encoding="utf-8")
    except (OSError, UnicodeDecodeError):
        return file_path.stem

    match = re.search(r"^# (.+)$", content, re.MULTILINE)
    return match.group(1).strip() if match else file_path.stem


def category_display_name(category: str) -> str:
    """Return the human-readable category heading for a given folder name."""
    return CATEGORY_NAMES.get(category, category.replace("-", " ").title())


def generate_summary(spec_files: List[Tuple[str, Path]]) -> str:
    """Build the full markdown content for docs/readme.md."""
    if spec_files:
        # Use the latest spec file modification time as the "Last updated" date
        latest_mtime = max(file_path.stat().st_mtime for _, file_path in spec_files)
        last_updated = datetime.fromtimestamp(latest_mtime, tz=timezone.utc)
    else:
        last_updated = datetime.now(timezone.utc)
    timestamp = f"{last_updated.strftime('%B')} {last_updated.day}, {last_updated.year}"

    # Group specs by category, preserving insertion order
    categories: Dict[str, List[Path]] = {}
    for category, file_path in spec_files:
        categories.setdefault(category, []).append(file_path)

    # --- Table of contents entries ---
    toc_lines: List[str] = []
    for category in categories:
        display = category_display_name(category)
        anchor = display.lower().replace(" ", "-")
        toc_lines.append(f"- [{display}](#{anchor})")

    toc_lines += [
        "- [Installation](#installation)",
        "- [Standards](#standards)",
    ]

    # --- Spec sections ---
    spec_sections: List[str] = []
    for category, files in categories.items():
        display = category_display_name(category)
        lines: List[str] = [f"### {display}", ""]
        for file_path in files:
            title = get_spec_title(file_path)
            status_label, status_emoji = get_spec_status(file_path)
            # Build a relative link from the docs/ directory
            rel_link = "./" + str(file_path.relative_to("docs"))
            lines.append(
                f"- [{title}]({rel_link}) — Status: {status_label} {status_emoji}"
            )
        spec_sections.append("\n".join(lines))

    toc_block = "\n".join(toc_lines)
    specs_block = "\n\n".join(spec_sections)

    return f"""# Jarvis Documentation

Last updated: {timestamp}

## Table of Contents

{toc_block}

## Specifications

{specs_block}

## Installation

See: [How to Install Jarvis APK](../.github/how-to-install-apk.md)

## Standards

See: [docs/standards/](./standards/)
"""


def main() -> None:
    """Entry point."""
    print(f"🔍 Scanning specs in {SPECS_DIR}...")

    if not SPECS_DIR.exists():
        print(f"⚠️  Specs directory not found: {SPECS_DIR}", file=sys.stderr)
        OUTPUT_FILE.parent.mkdir(parents=True, exist_ok=True)
        OUTPUT_FILE.write_text("# Jarvis Documentation\n\nNo specs found.\n", encoding="utf-8")
        print(f"📄 Wrote empty summary to {OUTPUT_FILE}")
        return

    spec_files = get_spec_files()
    print(f"📋 Found {len(spec_files)} spec file(s)")

    summary = generate_summary(spec_files)

    OUTPUT_FILE.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT_FILE.write_text(summary, encoding="utf-8")
    print(f"✅ Documentation summary written to {OUTPUT_FILE}")


if __name__ == "__main__":
    main()
