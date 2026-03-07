# GitHub Workflow Update — Bidirectional Issue Sync

## What Changed

The GitHub workflow has been enhanced to support **bidirectional synchronization** between specs and issues, enabling Copilot to read issues and update spec progress.

## New Workflow Features

### ✅ Specs Drive Issues (No Change)
Issues are still created from specs automatically.

### ✅ Issues Link Back to Specs (Enhanced)
Every GitHub issue now includes:
- Direct link to the spec file
- Checkbox status legend
- Instructions for Copilot

### ✅ Copilot-Readable Context Files (NEW)
Two new files generated automatically:

**1. `.github/copilot-issue-summary.md`**
- Human-readable summary of all issues
- Organized by feature and status
- Action guide for Copilot
- Quick status overview

**2. `.github/issue-context.json`**
- Machine-readable JSON mapping
- Complete issue details
- Spec file associations
- Status breakdown

## New Workflow Steps

```
1. Parse specs and create/update issues
2. Update issue labels from PR activity
3. Sync checkbox status from specs
4. Generate Copilot-readable issue context (JSON)  ← NEW
5. Prepare Copilot issue summary (Markdown)        ← NEW
```

## How Copilot Uses This

### Step 1: Read the Summary
```bash
File: .github/copilot-issue-summary.md
Content:
  - All issues listed by feature
  - Status icons (🔴 backlog, 🟡 in-progress, 🟢 completed)
  - Direct links to GitHub issues
  - Links back to spec files
```

### Step 2: Read the Context
```bash
File: .github/issue-context.json
Content:
  - Mapping of issues to spec files
  - Detailed issue information
  - Status labels
  - Grouped by spec file
```

### Step 3: Update Spec Files
```bash
Open: docs/specs/shopping/002 shopping-requirements.md
Update: [ ] → [-] → [X] checkboxes
Push: git push origin main
```

### Step 4: Workflow Auto-Syncs
- Parses updated checkboxes
- Updates GitHub issue status
- Regenerates copilot-issue-summary.md
- Updates issue-context.json
- Copilot sees new context on next interaction

## Files Modified

### Workflow File
- `.github/workflows/specs-to-issues.yml` — Added two new sync steps

### Python Scripts (NEW)
- `.github/scripts/generate_issue_context.py` — Creates JSON context
- `.github/scripts/prepare_copilot_context.py` — Creates markdown summary

### Documentation
- `.github/bidirectional-sync-guide.md` — Complete guide (NEW)
- `.github/copilot-instructions.md` — Updated with new workflow info

## Key Benefits

✅ **For Developers:**
- Easy to track what needs to be done
- Simple checkbox-based progress tracking
- Automatic GitHub issue updates

✅ **For Copilot:**
- Clear summary of pending work
- Machine-readable issue context
- Direct links to specs
- Action guide on what to do

✅ **For Project Management:**
- Real-time progress tracking
- Automatic status synchronization
- Complete traceability
- Specs-driven work management

## Example Usage

### Developer Perspective
```bash
# Update spec as you work
vim docs/specs/shopping/002 shopping-requirements.md

# Change checkboxes
[ ] User can add items
[-] User can remove items
[X] List persists

# Push
git commit -m "Update shopping progress"
git push

# Result: GitHub issue #42 auto-updated to "in-progress"
```

### Copilot Perspective
```bash
# Read current state
cat .github/copilot-issue-summary.md
# See: "Shopping Feature - In Progress (2/3 items)"

# Get detailed info
cat .github/issue-context.json
# Find: spec_file: "docs/specs/shopping/002 shopping-requirements.md"

# Open and update
vim docs/specs/shopping/002 shopping-requirements.md
# Mark next item: [ ] → [-]

# Push changes
git commit -m "Progress on shopping feature"
git push

# Workflow auto-updates GitHub issue
```

## Workflow Triggers

The enhanced workflow runs when:
1. **Push to `docs/specs/**/*.md`** — Main trigger
2. **Pull request to main with spec changes** — Validation
3. **Manual trigger** — On-demand sync
4. **Issue events** (new, edited, labeled) — Future sync from issues back to specs

## Generated Files

These files are created/updated every workflow run:

| File | Location | Content | Audience |
|------|----------|---------|----------|
| `issue-context.json` | `.github/` | JSON mapping of issues to specs | Copilot, Tools |
| `copilot-issue-summary.md` | `.github/` | Markdown summary of issues | Copilot, Humans |

## Integration with Copilot

**Copilot should:**
1. Check `.github/copilot-issue-summary.md` for current work
2. Reference `.github/issue-context.json` for detailed info
3. Update spec checkboxes when making progress
4. Push changes to main
5. Workflow handles syncing to GitHub

**Copilot knows to:**
- Spec files are the source of truth
- Checkbox marks drive GitHub issue status
- Pushing to main triggers automatic sync
- JSON and markdown files are always current

## Backward Compatibility

This update is **fully backward compatible:**
- Existing issues continue to work
- Old workflow steps still run
- Specs continue to drive issues
- No breaking changes

## Next Steps

1. **Test the workflow:**
   - Make a test change to a spec file
   - Push to main
   - Check that both context files are generated

2. **Read the full guide:**
   - `.github/bidirectional-sync-guide.md`

3. **Start using for progress tracking:**
   - Update spec checkboxes as you work
   - Watch GitHub issues auto-update
   - Let Copilot read context files

## Troubleshooting

**Context files not generated?**
1. Check workflow logs in Actions tab
2. Verify spec files have checkboxes
3. Run workflow manually if needed

**Issue status not updating?**
1. Verify spec file path in issue body
2. Check checkboxes follow format: `[ ]`, `[-]`, `[X]`
3. Push to main (not a branch)

**Copilot can't find issues?**
1. Verify `.github/copilot-issue-summary.md` exists
2. Check `.github/issue-context.json` has content
3. Look for spec file links in issue body

## Architecture Diagram

```
Spec Files
    ↓
Workflow Triggers
    ↓
1. Create Issues ─→ GitHub Issues
2. Sync Status
3. Update Labels
4. Generate JSON ─→ issue-context.json
5. Generate Markdown ─→ copilot-issue-summary.md
    ↓
Copilot Reads Context Files
    ↓
Updates Specs (checkboxes)
    ↓
Push to main
    ↓
Workflow Runs Again
    ↓
Loop
```

## Summary

The updated workflow creates a **fully bidirectional system**:

✅ **Specs → Issues** (automatic)
✅ **Issues → Context Files** (automatic)  
✅ **Context Files → Copilot** (readable)
✅ **Copilot → Specs** (updates checkboxes)
✅ **Specs → GitHub** (auto-sync)

---

**Status:** ✅ Implemented and Ready
**Version:** 2.0 (Bidirectional)
**Date:** March 7, 2026
**Impact:** Copilot can now read issues and update progress automatically

