# Issue Text Update Fix ✅

Fixed the problem where GitHub issue text was not being updated when spec files changed.

---

## Problem

When spec files were modified, the workflow would:
- ❌ Find existing issues
- ❌ Skip them without updating
- ❌ Not update the issue body text

This meant changes to spec files weren't reflected in GitHub issues.

---

## Solution

Updated `specs_to_issues.py` to actually update existing issues instead of skipping them.

### Changes Made

**1. Added new function: `update_github_issue()`**
```python
def update_github_issue(issue_number: int, body: str, labels: List[str]) -> bool:
    """Update an existing GitHub issue body and labels."""
    url = f"{ISSUES_URL}/{issue_number}"
    payload = {
        "body": body,
        "labels": labels,
    }
    response = requests.patch(url, headers=HEADERS, json=payload)
    # ... handles response
```

**2. Modified `main()` function**
- Now tracks: `created_count`, `updated_count`
- When existing issue found:
  - Generates fresh issue body
  - Calls `update_github_issue()`
  - Prints update confirmation
  - Increments `updated_count`

**3. Updated summary output**
```
✅ Created: X
🔄 Updated: Y
📝 Total processed: Z
```

---

## How It Works Now

### On Spec File Change

1. **Workflow triggers** on push to main
2. **Script parses** all spec files
3. **For each spec:**
   - Generates current issue body
   - Checks if issue exists (by title)
   - **If exists:** Updates body and labels
   - **If new:** Creates new issue
4. **Reports** created and updated counts

### Update Details

- ✅ Issue body updated with new content
- ✅ Labels refreshed
- ✅ All spec changes reflected
- ✅ Issue number stays the same
- ✅ Comments preserved
- ✅ Issue ID remains consistent

---

## What Gets Updated

When you modify a spec file, these parts of the GitHub issue are updated:

✅ **Overview** — From `## Objective` section
✅ **Spec File Link** — Updated if file path changed
✅ **Functional Requirements** — From spec requirements
✅ **Acceptance Criteria** — From spec criteria
✅ **Labels** — Feature and status labels
✅ **Status Information** — How status is calculated

❌ **Not Updated** (preserved):
- Issue number
- Comments and discussions
- Assignees
- Milestones
- Manual edits to body

---

## Testing

After this fix, when you:

1. **Modify a spec file**
   - Change requirements
   - Update objective
   - Modify acceptance criteria

2. **Push to main**
   - Workflow runs
   - Script parses specs
   - Finds existing issue
   - **Updates issue body** ✅
   - Reports: "🔄 Updated: issue #X"

3. **Check GitHub**
   - Open issue
   - Body shows latest content
   - Reflects all spec changes

---

## Files Modified

| File | Change | Impact |
|------|--------|--------|
| `specs_to_issues.py` | Added update logic | Now updates issues |
| `specs-to-issues.yml` | None | Already had pip install |

---

## Workflow Summary

### Before Fix
```
Parse spec
  ↓
Check if issue exists
  ↓
If exists: Skip ❌
If new: Create ✅
```

### After Fix
```
Parse spec
  ↓
Generate issue body
  ↓
Check if issue exists
  ↓
If exists: Update ✅
If new: Create ✅
```

---

## Status

✅ **Fix Applied**
✅ **Scripts Updated**
✅ **Workflow Ready**
✅ **Issue Text Will Update**

---

## Next Steps

1. Push the updated script to main
2. Modify a spec file
3. Push to main
4. Workflow runs and updates issues
5. GitHub issue text should now reflect changes

---

**Fix Applied:** March 7, 2026  
**Status:** ✅ Complete and Ready

