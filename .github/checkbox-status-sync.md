# Checkbox Status Sync — GitHub Workflow Update

## Overview

The GitHub Actions workflow has been updated to automatically sync checkbox status from spec files to GitHub issues.

## How It Works

### Checkbox Status System

In your spec files, use checkboxes to track implementation progress:

```markdown
[ ] Not started
[-] In progress  
[X] Done (or any non-empty character except dash)
```

### Automatic Status Updates

When you push changes to spec files, the workflow:

1. **Parses checkboxes** in all spec files
2. **Calculates overall status**:
   - All `[ ]` → Issue labeled `status/backlog`
   - Mix of statuses → Issue labeled `status/in-progress`
   - All `[X]` → Issue labeled `status/completed`
3. **Updates GitHub issue** with the appropriate status label
4. **Tracks completion** percentage

## Workflow Steps

```
1. Push spec file changes
        ↓
2. Workflow triggers
        ↓
3. Parse specs and create issues (if new)
        ↓
4. Update status from PR activity
        ↓
5. Sync checkbox status from specs ← NEW STEP
        ↓
6. Issue status labels updated automatically
```

## Usage Example

### Initial State
```markdown
# Shopping Feature Requirements

## Functional Requirements

[ ] User must be able to add items
[ ] User must be able to remove items
[ ] List must persist after restart
```

**Workflow Result:**
- Creates GitHub issue
- Labels: `feature/shopping`, `from/specs`, `status/backlog`

### Start Development
```markdown
[ ] User must be able to add items
[-] User must be able to remove items
[ ] List must persist after restart
```

**Workflow Result:**
- Issue status updated to `status/in-progress`

### Complete Feature
```markdown
[X] User must be able to add items
[X] User must be able to remove items
[X] List must persist after restart
```

**Workflow Result:**
- Issue status updated to `status/completed`

## Key Features

✅ **Automatic Status Syncing**
- Push spec changes → Issue status updates instantly
- No manual issue updates needed

✅ **Completion Tracking**
- Workflow calculates completion percentage
- Visible in logs for monitoring

✅ **Multiple Status Indicators**
- GitHub issue labels (status/*)
- Completion percentage
- Detailed logging

✅ **Clear Issue Communication**
- Issue body includes checkbox legend
- Developers know how to update status
- Status rules clearly documented

## New Workflow Files

### Updated Files
- `.github/workflows/specs-to-issues.yml` — Added sync step
- `.github/scripts/specs_to_issues.py` — Enhanced issue body template

### New Files
- `.github/scripts/sync_spec_status.py` — New sync script

## Workflow Behavior

### On Every Push to Specs
1. Issues are created (if new specs)
2. Issues are synced with current checkbox status
3. Issue labels are updated to match
4. Completion metrics are calculated

### Checkbox Status Rules

| Condition | Result Status |
|-----------|---|
| All `[ ]` (not started) | `status/backlog` |
| At least one `[-]` (in progress) | `status/in-progress` |
| At least one `[X]` AND some `[ ]` | `status/in-progress` |
| All `[X]` or non-empty (completed) | `status/completed` |

### Completion Percentage
- Calculated as: `(completed_items / total_items) * 100`
- Shown in workflow logs
- Updated on every spec change

## Integration with GitHub

### Issue Labels
- **Before:** Only `from/specs`, `feature/*`, `status/backlog`
- **After:** `status/backlog` → `status/in-progress` → `status/completed`

### Issue Body
- Includes checkbox legend
- Explains how to update spec
- Shows status update rules

### Automatic Updates
- Workflow runs on push → No manual updates needed
- Developer changes spec checkboxes → Issue auto-updates
- Workflow logs show all status changes

## Best Practices

### Keep Specs Updated
As you develop, update the checkboxes:
```bash
# Before pushing:
vim docs/specs/shopping/002 shopping-requirements.md
# Update checkboxes
[-] Requirement in progress

git add docs/specs/
git commit -m "Update shopping spec status"
git push
# Workflow runs automatically
```

### Use the Legend
Every spec file includes:
```markdown
## Checkbox Status Legend
- `[ ]` — Not started
- `[-]` — In progress
- `[X]` — Done
```

### Monitor in GitHub
1. Go to **Issues** tab
2. Look at issue labels:
   - `status/backlog` = waiting to start
   - `status/in-progress` = actively developing
   - `status/completed` = done
3. Completion % shown in workflow logs

## Troubleshooting

### Status Not Updating?

**Check:**
1. Spec file has proper checkbox format: `[ ]`, `[-]`, `[X]`
2. Push includes `docs/specs/**/*.md` changes
3. GitHub issue exists with label `from/specs`

**View logs:**
1. Go to Actions tab
2. Find "Sync checkbox status from specs" step
3. Check logs for which issues were updated

### Checkbox Not Recognized?

**Ensure:**
- Checkboxes are exactly: `[space]`, `[-]`, or `[X]`
- They're at the start of a line (after spaces)
- They're followed by text and two trailing spaces

❌ Wrong:
```
[  ] Extra space
[ ]- Hyphen outside
[ x] Lowercase x
```

✅ Correct:
```
[ ] Not started
[-] In progress
[X] Done
```

### Issue Has Wrong Status?

**Check file:**
```bash
grep -E '\[[^]]*\]' docs/specs/shopping/002\ shopping-requirements.md | head
```

Count the statuses and verify the workflow calculated correctly.

## Workflow Logs Example

```
🔄 Syncing checkbox status from specs to GitHub issues...

Processing: 002 shopping-requirements.md
   Title: Shopping Feature Requirements
   Issue #: 42
   Status: in-progress
   Completion: 66%
   ✅ Updated status label to 'in-progress'

==============================================================
📊 Sync Summary
==============================================================
✅ Updated: 1
⏭️  Skipped: 0
📝 Total processed: 4
==============================================================
```

## Advanced Usage

### Monitor Completion Across Features

In workflow logs, you'll see:
```
Shopping: 66% complete (4/6 items done)
Restock: 50% complete (2/4 items done)
Home: 100% complete (5/5 items done)
```

### Track Team Progress

1. Weekly, check workflow logs
2. See completion % for each feature
3. Monitor status distribution
4. Plan sprint based on progress

## Files Summary

| File | Purpose |
|------|---------|
| `.github/workflows/specs-to-issues.yml` | Main workflow (updated) |
| `.github/scripts/specs_to_issues.py` | Issue creation (updated) |
| `.github/scripts/sync_spec_status.py` | Status sync (NEW) |
| `docs/specs/**/*.md` | Your spec files (use checkboxes) |

---

## Quick Reference

### To Update Issue Status
1. Edit spec file
2. Change checkboxes: `[ ]` → `[-]` → `[X]`
3. Push to main
4. Workflow runs automatically
5. GitHub issue status updates

### Example Workflow
```bash
# Day 1: Start feature
vim docs/specs/shopping/002 shopping-requirements.md
# Change some to: [-] In progress

git commit -m "Start shopping feature"
git push
# Workflow runs → Issue marked in-progress

# Day 3: More progress
# Change more to: [X] Done

git commit -m "Complete item management"
git push
# Workflow runs → Issue still in-progress (more items to do)

# Day 5: Feature complete
# Change all to: [X] Done

git commit -m "Complete shopping feature"
git push
# Workflow runs → Issue marked completed
```

---

**Status:** ✅ Workflow Updated  
**Created:** March 7, 2026  
**Version:** 2.0

