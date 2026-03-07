# GitHub Automation — Complete Guide

Comprehensive documentation for the Jarvis project's GitHub automation systems, including specs-to-issues and bidirectional synchronization.

---

## Table of Contents

1. [Quick Start (5 minutes)](#quick-start)
2. [System Overview](#system-overview)
3. [How It Works](#how-it-works)
4. [Using the System](#using-the-system)
5. [Technical Reference](#technical-reference)
6. [Troubleshooting](#troubleshooting)
7. [Architecture](#architecture)

---

## Quick Start

### 3-Minute Overview

**The System:**
- Specs create GitHub issues automatically
- GitHub issues link back to specs
- Copilot reads issue summaries
- Copilot updates spec checkboxes
- Workflow auto-syncs everything

### Your First Workflow

**As a Developer:**
```bash
# 1. Open spec file
vim docs/specs/shopping/002 shopping-requirements.md

# 2. Update checkbox as you work
[ ] Not started
[-] In progress ← You're here
[X] Done

# 3. Push to main
git add docs/specs/
git commit -m "Update progress"
git push origin main

# 4. Workflow runs automatically
# → GitHub issue updated
# → Context files regenerated
```

**As Copilot:**
```bash
# 1. Read the summary
cat .github/copilot-issue-summary.md

# 2. Check the context
cat .github/issue-context.json

# 3. Find the spec file and open it
vim docs/specs/shopping/002 shopping-requirements.md

# 4. Update checkboxes and push
git commit -m "Progress update"
git push origin main
```

### Checkbox Rules

| Mark | Meaning |
|------|---------|
| `[ ]` | Not started |
| `[-]` | In progress |
| `[X]` | Done |

### Manual Trigger

You can manually trigger the workflow anytime:

**Steps:**
1. Go to GitHub repository → **Actions** tab
2. Select **Specs to Issues Automation (Bidirectional Sync)**
3. Click **Run workflow** button
4. Optionally select a **sync mode** (default: `full`)
5. Click **Run workflow**

**Sync Mode Options:**
- `full` — Run all steps (recommended for most cases)
- `create-issues-only` — Only create/update issues from specs
- `sync-status-only` — Only sync checkbox status to GitHub
- `regenerate-context` — Only regenerate context files for Copilot

**Use Cases:**
- Sync specs after editing them directly on GitHub
- Regenerate context files if they seem out of date
- Test the workflow without pushing to main
- Force an immediate sync without waiting for push

---

## System Overview

### What Is This?

A complete **bidirectional automation system** that:
- ✅ Converts specifications into GitHub issues
- ✅ Links issues back to specs
- ✅ Syncs checkbox status automatically
- ✅ Generates Copilot-readable context files
- ✅ Enables Copilot to read and update progress

### Core Components

**Workflow File:**
- `.github/workflows/specs-to-issues.yml` — Main GitHub Actions workflow

**Python Scripts:**
- `scripts/specs_to_issues.py` — Creates issues from specs
- `scripts/update_issue_status.py` — Updates status from PR activity
- `scripts/sync_spec_status.py` — Syncs checkboxes to issues
- `scripts/generate_issue_context.py` — Creates JSON context
- `scripts/prepare_copilot_context.py` — Creates markdown summary

**Configuration:**
- `specs-to-issues.config.yml` — Customization settings

**Auto-Generated Files:**
- `issue-context.json` — Machine-readable issue mapping (generated on each run)
- `copilot-issue-summary.md` — Human-readable issue summary (generated on each run)

### File Structure

```
.github/
├── workflows/
│   └── specs-to-issues.yml
├── scripts/
│   ├── specs_to_issues.py
│   ├── update_issue_status.py
│   ├── sync_spec_status.py
│   ├── generate_issue_context.py
│   └── prepare_copilot_context.py
├── specs-to-issues.config.yml
└── github-automation-guide.md (this file)

docs/specs/
├── 001 readme.md
├── 002 home-requirements.md
└── shopping/
    ├── 001 readme.md
    ├── 002 shopping-requirements.md
    ├── 003 shopping-list-requirements.md
    └── 004 restock-requirements.md
```

---

## How It Works

### The Complete Cycle

```
1. Spec File Modified (in PR or locally)
   ↓
2. Merge/Push to main
   ↓
3. GitHub Workflow Triggers (only on main)
   ├─ Parse specs
   ├─ Create/update issues
   ├─ Sync checkbox status
   ├─ Generate JSON context
   └─ Generate markdown summary
   ↓
4. GitHub Issues Updated
   ↓
5. Context Files Ready for Copilot
   ├─ .github/issue-context.json
   └─ .github/copilot-issue-summary.md
   ↓
6. Copilot Can Read and Update
   ├─ Read context files
   ├─ Update spec files
   └─ Push to main (triggers workflow)
   ↓
7. Workflow Runs Again
   └─ Loop continues
```

### Workflow Triggers

The workflow (`specs-to-issues.yml`) runs automatically when:

- **Push to main branch** with changes to `docs/specs/**/*.md`

You can also trigger manually:

- **Manual trigger** via GitHub Actions "Run workflow" button with optional sync mode selection

The workflow does **NOT** run on:
- Pull requests (changes are staged but not processed)
- Other branches
- Issues or comments

### Manual Trigger Options

When manually triggering the workflow, you can choose a sync mode:

| Mode | What It Does |
|------|---|
| `full` (default) | Runs all 5 steps (create issues, sync status, generate context) |
| `create-issues-only` | Only creates/updates GitHub issues from specs |
| `sync-status-only` | Only syncs checkbox status to issue labels |
| `regenerate-context` | Only regenerates context files for Copilot |

**How to manually trigger:**

1. Go to GitHub repository
2. Click **Actions** tab
3. Select **Specs to Issues Automation (Bidirectional Sync)**
4. Click **Run workflow** button
5. Select **sync_mode** from dropdown (or leave as `full`)
6. Click **Run workflow**

The workflow will start within seconds.
- Scans `/docs/specs/**/*.md` files
- Extracts: title, objective, requirements, acceptance criteria
- Creates GitHub issues (prevents duplicates)
- Applies labels: `from/specs`, `feature/<name>`, `status/backlog`

**Step 2: Update issue labels and status**
- Checks for related pull requests
- Updates issue status based on PR activity
- Syncs PR closures to issue completion

**Step 3: Sync checkbox status from specs**
- Reads checkbox marks: `[ ]`, `[-]`, `[X]`
- Calculates overall status:
  - All `[ ]` → `status/backlog`
  - Mix → `status/in-progress`
  - All `[X]` → `status/completed`
- Updates issue labels

**Step 4: Generate Copilot issue context (JSON)**
- Creates `.github/issue-context.json`
- Machine-readable mapping of issues to specs
- Grouped by spec file and status

**Step 5: Prepare Copilot issue summary (Markdown)**
- Creates `.github/copilot-issue-summary.md`
- Human-readable summary for Copilot
- Organized by feature and status
- Includes action guide

### Issue Structure

Each GitHub issue created from specs includes:

**Title:**
- Direct from spec H1 heading
- Example: "Shopping Feature Requirements"

**Body:**
```markdown
## Overview
{objective from spec}

## Specification File
📄 [`{spec file path}`]({spec file path})

### Checkbox Status Legend
Track implementation progress in the spec file:
- `[ ]` — Not started
- `[-]` — In progress
- `[X]` — Done

## Functional Requirements
{requirements from spec}

## Acceptance Criteria
{acceptance criteria from spec}

---
**Issue Status:** This issue's GitHub status label updates automatically...
```

**Labels:**
- `from/specs` — Created from specification
- `feature/shopping` — Feature category
- `status/backlog`, `status/in-progress`, or `status/completed` — Current status

---

## Using the System

### For Developers

**Track implementation in spec files:**

1. Open the spec file
2. Update checkboxes as you work:
   ```markdown
   [ ] User must be able to add items
   [-] User must be able to remove items  ← Currently working on this
   [X] Items persist after restart
   ```
3. Push to main:
   ```bash
   git add docs/specs/
   git commit -m "Update shopping spec status"
   git push origin main
   ```
4. Watch GitHub issues auto-update in real-time

**Add new requirements:**

1. Open the spec file
2. Add new line with checkbox:
   ```markdown
   [ ] New requirement goes here
   ```
3. Push to main
4. Workflow automatically creates GitHub issue for it

**Monitor progress:**

1. Go to GitHub Issues tab
2. Filter by label: `from/specs`
3. See all issues with status labels:
   - `status/backlog` = not started
   - `status/in-progress` = being worked on
   - `status/completed` = done

### For Copilot

**Understand current work:**

1. Read `.github/copilot-issue-summary.md` for overview
2. Read `.github/issue-context.json` for detailed mapping
3. Find which specs need updates

**Update implementation status:**

1. Identify the spec file from issue context
2. Open the spec file
3. Update checkbox: `[ ]` → `[-]` → `[X]`
4. Commit and push to main
5. Workflow auto-updates GitHub issue

**Add new items:**

1. Open the spec file
2. Add line with checkbox: `[ ] New requirement`
3. Commit and push to main
4. Workflow auto-creates GitHub issue

---

## Technical Reference

### Spec File Format

All spec files must follow this structure:

```markdown
# Feature Title

## Checkbox Status Legend

- `[ ]` — Not started
- `[-]` — In progress
- `[X]` — Done (or any non-empty character except dash)

Use these checkboxes to track implementation status.

## Objective

Description of what this feature does.

## Functional Requirements

### Section Name

[ ] Requirement 1 with description
[ ] Requirement 2 with description

## Acceptance Criteria

[ ] Acceptance criterion 1
[ ] Acceptance criterion 2
```

### Checkpoint Status Mapping

| Spec Checkboxes | GitHub Issue Status Label | Meaning |
|---|---|---|
| All `[ ]` | `status/backlog` | Not started |
| At least one `[-]` | `status/in-progress` | Being developed |
| At least one `[X]`, some `[ ]` | `status/in-progress` | Partially done |
| All `[X]` | `status/completed` | Finished |

**IMPORTANT:** Checkbox marks only affect the **status label**. They do NOT determine if the issue is open or closed.
- Issues remain **open** until manually closed, regardless of checkbox marks
- Checkboxes are for tracking implementation progress only
- Use manual issue closure when work is truly complete and done

### Feature Labels

The workflow automatically assigns feature labels based on spec location:

| Spec Location | Label |
|---|---|
| `docs/specs/shopping/**` | `feature/shopping` |
| `docs/specs/home-requirements.md` | `feature/home` |
| Other | `feature/core` |

Customize in `.github/specs-to-issues.config.yml`

### Generated Context Files

**`.github/issue-context.json`**

Machine-readable JSON with:
- Complete issue data
- Grouped by spec file
- Grouped by status
- Timestamp information

```json
{
  "metadata": {
    "total_issues": 12,
    "total_specs": 4,
    "generated_at": timestamp
  },
  "by_spec": {
    "docs/specs/shopping/002 shopping-requirements.md": [
      {
        "issue_number": 42,
        "title": "Shopping Feature Requirements",
        "status_label": "status/in-progress",
        "spec_file": "..."
      }
    ]
  },
  "by_status": {
    "backlog": [...],
    "in-progress": [...],
    "completed": [...]
  }
}
```

**`.github/copilot-issue-summary.md`**

Markdown with:
- Quick status overview table
- Issues grouped by feature
- Issues grouped by status
- Copilot action guide
- Implementation metrics

---

## Troubleshooting

### Workflow Not Running

**Check:**
1. Push includes changes to `docs/specs/**/*.md`
2. Pushed to `main` branch (not a feature branch)
3. Workflow file exists: `.github/workflows/specs-to-issues.yml`

**Debug:**
1. Go to GitHub → Actions tab
2. Find "Specs to Issues Automation" workflow
3. Check logs for errors

### Issues Not Being Created

**Check:**
1. Spec file has H1 title: `# Title`
2. Spec file in `/docs/specs/` folder
3. File ends with `.md`

**Verify:**
1. Issue has `from/specs` label
2. Title matches spec file title
3. Issue body contains spec file link

### Checkbox Status Not Syncing

**Check:**
1. Checkboxes use correct format: `[ ]`, `[-]`, `[X]`
2. Spec file saved and pushed
3. Workflow ran successfully (check Actions tab)

**Verify:**
1. GitHub issue has correct `status/` label
2. Spec file path in issue body is correct

### Context Files Not Generated

**Check:**
1. Workflow completed successfully
2. No Python errors in workflow logs
3. At least one spec-based issue exists

**Debug:**
1. Go to Actions → Workflow run
2. Find "Generate copilot-readable issue context" step
3. View logs for errors

### Copilot Can't Find Issues

**Verify:**
1. `.github/copilot-issue-summary.md` exists
2. `.github/issue-context.json` contains data
3. Files were generated on last workflow run

---

## Architecture

### System Diagram

```
┌──────────────────────────────────────────┐
│ Specification Files (Specs)              │
│ docs/specs/**/*.md                       │
│ (Source of Truth)                        │
└────────────┬─────────────────────────────┘
             │
             ↓ (workflow triggers)
┌──────────────────────────────────────────┐
│ GitHub Actions Workflow                  │
│ .github/workflows/specs-to-issues.yml    │
│                                          │
│ 1. Parse specs                           │
│ 2. Create/update issues                  │
│ 3. Sync checkbox status                  │
│ 4. Generate JSON context                 │
│ 5. Generate markdown summary             │
└──┬──────────────────┬────────────┬───────┘
   │                  │            │
   ↓                  ↓            ↓
┌─────────────┐  ┌──────────┐  ┌──────────────────┐
│ GitHub      │  │ JSON     │  │ Markdown Summary │
│ Issues      │  │ Context  │  │ for Copilot      │
│ (Visible)   │  │ (Machine)│  │ (Human-readable) │
└─────────────┘  └──────────┘  └──────────────────┘
                       ↓              ↓
                  Copilot Reads Both Files
                       ↓
              Updates Specs (checkboxes)
                       ↓
              Push to main (git push)
                       ↓
         Workflow Runs Again (Loop)
```

### Data Flow

```
User Updates Spec File
    ↓
Commits and Pushes to main
    ↓
GitHub Detects Change to docs/specs/**/*.md
    ↓
Workflow Triggers
    ↓
specs_to_issues.py
├─ Parses spec files
├─ Extracts data
└─ Creates/updates GitHub issues
    ↓
update_issue_status.py
├─ Checks PR activity
└─ Updates issue status
    ↓
sync_spec_status.py
├─ Reads checkboxes
├─ Calculates status
└─ Updates issue labels
    ↓
generate_issue_context.py
├─ Fetches all issues
├─ Maps to specs
└─ Generates JSON
    ↓
prepare_copilot_context.py
├─ Formats for Copilot
├─ Generates markdown
└─ Updates summary file
    ↓
Workflow Complete
    ↓
Copilot Reads Context Files
    ↓
(Cycle repeats)
```

---

## Best Practices

### ✅ Do

- Update checkboxes as you work
- Push regularly (daily or per task)
- Keep spec files updated
- Use clear checkbox transitions: `[ ]` → `[-]` → `[X]`
- Reference the spec file from issues
- Let workflow handle GitHub syncing

### ❌ Don't

- Manually create GitHub issues (they're auto-created)
- Manually update issue status (it auto-syncs)
- Mix old and new systems
- Skip updating spec checkboxes
- Push directly without updating specs
- Edit generated files (they'll be overwritten)

---

## Configuration

### Customize Feature Labels

Edit `.github/specs-to-issues.config.yml`:

```yaml
feature_labels:
  shopping: "feature/shopping"
  home: "feature/home"
  scanner: "feature/scanner"
  default: "feature/core"
```

### Customize Status Labels

```yaml
status_labels:
  backlog: "status/backlog"
  in_progress: "status/in-progress"
  completed: "status/completed"
```

### Customize Issue Template

Edit `specs_to_issues.py` function `generate_issue_body()` to customize issue body format.

---

## Key Concepts

### Specs Are the Source of Truth
- Specs drive issues (not vice versa)
- Update specs, issues follow automatically
- No manual GitHub issue management needed

### Bidirectional Flow
- Specs → GitHub Issues (automatic)
- GitHub Issues → Copilot Context (automatic)
- Copilot → Specs (manual update)
- Specs → GitHub Issues (auto-sync)

### Checkbox System
- Simple and effective progress tracking
- No external tools needed
- Integrated with GitHub issues
- Copilot-friendly format

### Automatic Synchronization
- No manual labor required
- Workflow runs on every spec change
- Context files always current
- Real-time progress visibility

---

## Support & Resources

**For Quick Questions:**
- Read the appropriate section above
- Check workflow logs in GitHub Actions

**For Specific Issues:**
- Check the "Troubleshooting" section above
- Verify spec file format matches requirements
- Ensure workflow has proper permissions

**For Copilot Integration:**
- Read `.github/copilot-issue-summary.md` (auto-generated)
- Read `.github/issue-context.json` (auto-generated)
- Check `.github/copilot-instructions.md` in repository root

---

## Summary

This system provides:

✅ **Automatic issue creation** from specifications  
✅ **Automatic status syncing** via checkboxes  
✅ **Copilot-readable context** files  
✅ **Bidirectional workflow** between specs and issues  
✅ **Complete traceability** from spec to implementation  
✅ **Zero manual work** for issue management  

**Start using it:**
1. Update a spec file checkbox
2. Push to main
3. Watch GitHub issue auto-update
4. Read context files to understand work

---

## Build APK Workflow

In addition to the specs-to-issues automation, there is also an automated build workflow.

### What It Does

The **Build APK** workflow automatically:
- Compiles the Android app
- Generates debug APK for testing
- Generates release APK for distribution
- Runs lint checks
- Runs unit tests
- Uploads APKs as downloadable artifacts

### When It Runs

The workflow runs when:
- **Push to main** — Every commit or merge triggers build
- **Pull Request to main** — Every PR to main validates build works
- **Manual trigger** — Anytime via Actions "Run workflow" button

### Download APK

1. Go to **Actions** tab
2. Find latest **Build APK** run
3. Click the run
4. Scroll to **Artifacts**
5. Download **debug-apk** or **release-apk**

### Install APK

See complete installation guide: **[How to Install Jarvis APK](./how-to-install-apk.md)**

**Quick methods:**
- Android Studio (easiest)
- ADB command line
- File manager direct install

### Typical Build Time

- First build: 5-8 minutes (downloading dependencies)
- Subsequent builds: 2-3 minutes (cached dependencies)

For more details, see:
- `.github/build-apk-guide.md` — Complete workflow guide
- `.github/how-to-install-apk.md` — Installation instructions

---

**Version:** 2.0 (Consolidated)  
**Status:** ✅ Production Ready  
**Last Updated:** March 7, 2026

