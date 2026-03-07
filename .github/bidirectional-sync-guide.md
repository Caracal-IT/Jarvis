# Bidirectional Sync System — Issues ↔ Specs

Complete guide for the bidirectional synchronization between GitHub Issues and Specification files.

## Overview

The system creates a **two-way relationship** between GitHub issues and spec files:

```
Spec File
    ↓ (create issue)
GitHub Issue
    ↑ (read & update)
Copilot
    ↓ (update checkbox)
Spec File
    ↓ (sync status)
GitHub Issue
```

## How It Works

### 1. Specs Drive Issues

**Spec files are the source of truth.**

When you create or modify a spec file:
1. Workflow triggers automatically
2. Parses spec and creates issue
3. Issue links back to spec file
4. All details come from spec

### 2. Issues Link Back to Specs

**Every GitHub issue includes:**
- Direct link to the spec file
- Checkbox legend (how to update status)
- Full requirement text
- Implementation instructions

### 3. Copilot Can Read Issues

**Two helper files for Copilot:**

**`.github/issue-context.json`**
- Complete JSON mapping of issues to specs
- Machine-readable format
- Grouped by spec file and status
- Updated on every workflow run

**`.github/copilot-issue-summary.md`**
- Human-readable issue summary
- Organized by feature and status
- Action guide for Copilot
- Updated on every workflow run

### 4. Copilot Updates Specs

**Copilot can:**
1. Read issues from `.github/copilot-issue-summary.md`
2. Read JSON context from `.github/issue-context.json`
3. Update checkboxes in spec files
4. Push to main
5. Workflow auto-syncs to GitHub issues

## Workflow Steps

```
Event: Push to docs/specs/**.md
    ↓
1. Parse specs and create issues
    ↓
2. Update issue labels and status
    ↓
3. Sync checkbox status from specs to issues
    ↓
4. Generate Copilot-readable issue context (JSON)
    ↓
5. Prepare Copilot issue summary (Markdown)
    ↓
Result: Copilot has complete context to update specs
```

## Using This System

### As a Developer

**To update implementation status:**

1. **Open the spec file**
   ```bash
   vim docs/specs/shopping/002 shopping-requirements.md
   ```

2. **Update checkboxes as you work**
   ```markdown
   [ ] Not started
   [-] In progress  ← You're here
   [X] Done
   ```

3. **Push to main**
   ```bash
   git add docs/specs/
   git commit -m "Update shopping spec status"
   git push origin main
   ```

4. **Workflow automatically:**
   - Syncs status to GitHub issue
   - Updates issue label
   - Updates Copilot context files

### As Copilot

**To understand current issues:**

1. **Read the summary file**
   - File: `.github/copilot-issue-summary.md`
   - Contains: All issues organized by feature and status
   - Includes: Action guide and implementation hints

2. **Read the context file**
   - File: `.github/issue-context.json`
   - Contains: Machine-readable mapping of issues to specs
   - Includes: Full issue details and spec links

3. **To update a requirement:**
   - Find the spec file path from the issue
   - Open the spec file
   - Update the checkbox: `[ ]` → `[-]` → `[X]`
   - Push to main
   - Workflow auto-syncs to GitHub

4. **To add new requirements:**
   - Open the spec file
   - Add new line with checkbox: `[ ] New requirement`
   - Push to main
   - Workflow auto-creates GitHub issue

## File Relationships

```
docs/specs/shopping/002 shopping-requirements.md
    ↓ (workflow parses)
.github/issue-context.json
    ↓ (contains link)
GitHub Issue #42
    ↓ (issue body links back)
docs/specs/shopping/002 shopping-requirements.md
    ↑ (Copilot reads & updates)
```

## Key Features

### ✅ Complete Traceability
- Every issue links to its spec file
- Every spec has a related issue
- Changes in one reflect in the other

### ✅ Copilot-Friendly
- JSON file for machine-readable data
- Markdown file for human-readable data
- Clear action guide
- Simple checkbox system

### ✅ Automatic Sync
- No manual updates needed
- Workflow handles all syncing
- Immediate feedback on changes

### ✅ Spec-Driven
- Specs are the source of truth
- Issues are derived from specs
- Status flows from specs to issues

## Example Workflow

### Scenario: Implementing Shopping Feature

**Day 1: Start Development**
```markdown
# docs/specs/shopping/002 shopping-requirements.md

[ ] User must be able to add a Shopping item.
[-] User must be able to rename a Shopping item.
[ ] User must be able to remove a Shopping item.
```

Push → Workflow runs → GitHub issue updated to `status/in-progress`

**Day 2: Make Progress**
```markdown
[X] User must be able to add a Shopping item.
[X] User must be able to rename a Shopping item.
[-] User must be able to remove a Shopping item.
```

Push → Workflow runs → GitHub issue still `status/in-progress`

**Day 3: Complete Feature**
```markdown
[X] User must be able to add a Shopping item.
[X] User must be able to rename a Shopping item.
[X] User must be able to remove a Shopping item.
```

Push → Workflow runs → GitHub issue updated to `status/completed`

### Copilot Workflow

**Copilot reads current state:**
```bash
# Reads: .github/copilot-issue-summary.md
# Sees: "Shopping Feature Requirements - In Progress (2/3 items done)"
# Link: https://github.com/.../issues/42
```

**Copilot checks the context file:**
```bash
# Reads: .github/issue-context.json
# Finds: spec_file: "docs/specs/shopping/002 shopping-requirements.md"
# Issue: #42, Status: in-progress
```

**Copilot updates the spec file:**
```bash
# Opens: docs/specs/shopping/002 shopping-requirements.md
# Changes: [ ] → [-] for item being worked on
# Commits: "Update shopping spec - item removal in progress"
# Pushes: git push origin main
```

**Workflow auto-syncs:**
- Parses new checkbox status
- Updates GitHub issue #42
- Regenerates copilot-issue-summary.md
- Updates issue-context.json
- Copilot has new context on next read

## Files in This System

| File | Purpose | Audience | Format |
|------|---------|----------|--------|
| `docs/specs/**/*.md` | Source of truth | Humans | Markdown |
| `.github/issue-context.json` | Machine-readable mapping | Copilot | JSON |
| `.github/copilot-issue-summary.md` | Human-readable summary | Copilot/Humans | Markdown |
| GitHub Issues | Tracking & visibility | All | GitHub UI |

## Workflow Scripts

| Script | Purpose |
|--------|---------|
| `specs_to_issues.py` | Parse specs, create/update issues |
| `sync_spec_status.py` | Sync checkbox status to issues |
| `update_issue_status.py` | Update based on PR activity |
| `generate_issue_context.py` | Create JSON context for Copilot |
| `prepare_copilot_context.py` | Create markdown summary for Copilot |

## Triggering the Workflow

### Automatic (Recommended)
Just push changes to spec files:
```bash
git add docs/specs/
git commit -m "Update requirements"
git push origin main
```

Workflow triggers automatically.

### Manual
Go to GitHub → Actions → Specs to Issues Automation → Run workflow

## Best Practices

### ✅ Do
- Update checkboxes as you work
- Push regularly (daily or per task)
- Keep spec files updated
- Use clear checkbox transitions: `[ ]` → `[-]` → `[X]`

### ❌ Don't
- Manually create GitHub issues (they're auto-created)
- Manually update issue status (it auto-syncs)
- Mix old and new systems
- Skip updating spec checkboxes

## Copilot Integration

### What Copilot Can Do
1. **Read** `.github/copilot-issue-summary.md`
2. **Understand** which features are pending
3. **Find** the spec file from the issue context
4. **Update** checkboxes in the spec file
5. **Push** changes back to GitHub
6. **Trigger** workflow to sync status

### What Copilot Should Know
- Spec files are the source of truth
- Always check the issue context JSON
- Checkbox marks drive issue status
- Updates should be pushed to main
- Status flows: `[ ]` → `[-]` → `[X]`

## Monitoring

**To see current progress:**
1. Go to GitHub Issues tab
2. Filter by `from/specs`
3. Look at status labels:
   - `status/backlog` = not started
   - `status/in-progress` = being worked on
   - `status/completed` = done

**To see Copilot context:**
1. Open `.github/copilot-issue-summary.md`
2. See all issues organized by feature
3. Find action items

**To see machine details:**
1. Open `.github/issue-context.json`
2. See mapping of issues to specs
3. See status breakdown

## Troubleshooting

### Issue Not Updating?
1. Check spec file has checkbox in the format: `[ ]`, `[-]`, `[X]`
2. Push to main (not a branch)
3. Check workflow ran in Actions tab
4. Check workflow logs for errors

### Copilot Can't Find Issue?
1. Check `.github/copilot-issue-summary.md` exists
2. Verify issue is labeled `from/specs`
3. Check issue body contains spec file link
4. Verify JSON in `.github/issue-context.json`

### Status Not Syncing?
1. Verify spec file path in issue body
2. Check all checkboxes follow format
3. Make sure file is in `docs/specs/` folder
4. Run workflow manually if needed

## Example Files

### Issue Context JSON Structure
```json
{
  "metadata": {
    "total_issues": 12,
    "total_specs": 4
  },
  "by_spec": {
    "docs/specs/shopping/002 shopping-requirements.md": [
      {
        "issue_number": 42,
        "title": "Shopping Feature Requirements",
        "status_label": "status/in-progress",
        "spec_file": "docs/specs/shopping/002 shopping-requirements.md"
      }
    ]
  },
  "all_issues": [...]
}
```

### Copilot Summary Structure
```markdown
# Copilot Issue Summary

## Quick Status Overview
| Status | Count |
|--------|-------|
| Backlog | 8 |
| In Progress | 3 |
| Completed | 1 |

## Issues by Feature
### SHOPPING
#### 🟡 #42: Shopping Feature Requirements
- Status: in-progress
- Spec: docs/specs/shopping/002 shopping-requirements.md
```

---

## Summary

This bidirectional system enables:
1. ✅ Specs drive issues (not vice versa)
2. ✅ Issues link back to specs
3. ✅ Copilot can read issue context
4. ✅ Copilot can update spec files
5. ✅ Automatic workflow syncs everything
6. ✅ Complete traceability

**For Copilot:** Read `.github/copilot-issue-summary.md` and `.github/issue-context.json` to understand work and update progress.

**For Developers:** Update spec checkboxes, push to main, and watch issues auto-update.

---

**Status:** ✅ Operational
**Version:** 2.0 (Bidirectional)
**Created:** March 7, 2026

