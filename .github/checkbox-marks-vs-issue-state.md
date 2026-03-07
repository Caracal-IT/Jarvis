# Checkbox Marks vs Issue Open/Closed State

Critical clarification on how checkbox marks work in the automation system.

---

## Important Distinction

### Checkbox Marks (`[ ]`, `[-]`, `[X]`)
- **Purpose:** Track implementation progress
- **Affects:** Status LABEL on GitHub issue
- **Does NOT affect:** Whether issue is open or closed

### GitHub Issue Open/Closed State
- **Determined by:** Manual closure
- **Not affected by:** Checkbox marks
- **Remains:** Open until manually closed

---

## How Checkboxes Work

### Status Label Assignment

Checkboxes in spec files determine the **status label** on GitHub issues:

```
Spec Checkboxes          →  GitHub Issue Status Label
├─ All [ ]              →  status/backlog
├─ Mix [ ] and [-]      →  status/in-progress
├─ Mix [ ] and [X]      →  status/in-progress
└─ All [X]              →  status/completed
```

### Issue Open/Closed State

Issues stay **open** regardless of checkbox state:

```
Issue State              Status Label           Checkbox Mark
├─ Open                 + status/backlog       [ ] All not started
├─ Open                 + status/in-progress   [-] Some in progress
├─ Open                 + status/completed     [X] All done
└─ Closed               + any label            (manually closed)
```

---

## Practical Examples

### Example 1: Backlog Item

**Spec File:**
```markdown
[ ] User must be able to add items
[ ] User must be able to remove items
[ ] List must persist
```

**GitHub Issue:**
- ✅ **State:** Open
- 🏷️ **Status Label:** `status/backlog`
- 📝 **Meaning:** Not started yet

### Example 2: In Progress

**Spec File:**
```markdown
[X] User must be able to add items
[-] User must be able to remove items
[ ] List must persist
```

**GitHub Issue:**
- ✅ **State:** Open
- 🏷️ **Status Label:** `status/in-progress`
- 📝 **Meaning:** Being worked on (1/3 done)

### Example 3: Completed

**Spec File:**
```markdown
[X] User must be able to add items
[X] User must be able to remove items
[X] List must persist
```

**GitHub Issue:**
- ✅ **State:** Open (until manually closed!)
- 🏷️ **Status Label:** `status/completed`
- 📝 **Meaning:** Implementation done, but issue still open

### Example 4: Actually Closed

**Spec File:**
```markdown
[X] User must be able to add items
[X] User must be able to remove items
[X] List must persist
```

**GitHub Issue (after manual closure):**
- ❌ **State:** Closed (manually)
- 🏷️ **Status Label:** `status/completed`
- 📝 **Meaning:** Work is done and verified

---

## Workflow Logic

### What Checkboxes Control

✅ **Do Control:**
- Status label assignment
- Progress tracking
- Visual status indicators
- Copilot context generation

### What Checkboxes Do NOT Control

❌ **Do NOT Control:**
- Whether issue is open/closed
- Automatic issue closure
- Issue visibility
- Issue filtering (by state)

---

## When to Close an Issue

Close a GitHub issue manually when:

1. ✅ All checkboxes are `[X]` (implementation done)
2. ✅ Code is tested and working
3. ✅ All acceptance criteria met
4. ✅ PR merged to main
5. ✅ Feature is verified in production

**How to Close:**
1. Go to GitHub issue
2. Click "Close issue" button
3. Add closing comment if needed
4. Issue state changes to "Closed"

---

## Status Label Reference

### status/backlog
- **What it means:** Feature not started yet
- **When assigned:** All checkboxes are `[ ]`
- **Issue state:** Open
- **Action:** Ready to start development

### status/in-progress
- **What it means:** Feature is being developed
- **When assigned:** Mix of `[ ]`, `[-]`, and/or `[X]`
- **Issue state:** Open
- **Action:** Keep working on it

### status/completed
- **What it means:** Feature implementation is done
- **When assigned:** All checkboxes are `[X]`
- **Issue state:** Open (until manually closed)
- **Action:** Review, test, close when ready

---

## Common Mistakes to Avoid

### ❌ Mistake 1: Thinking Checkboxes Close Issues
```
Spec file: [X] all items done
GitHub: Issue is still OPEN
Expected: Issue auto-closes ← WRONG!
```

**Reality:** You must manually close the issue. Checkboxes only update the label.

### ❌ Mistake 2: Thinking Closed Issues Mean Done
```
GitHub: Issue state = "Closed"
Reality: Issue was closed manually
It may or may not be implemented yet
```

**Check:** Look at the status LABEL and spec checkboxes to see if actually done.

### ❌ Mistake 3: Ignoring the Status Label
```
Spec: [X] all items done (status/completed)
GitHub: Issue is still Open
Assumption: Feature is not done
Reality: Feature IS done, just not closed yet
```

**Solution:** Check the `status/completed` label to see actual implementation status.

---

## Workflow Summary

```
Developer Updates Spec Checkboxes
    ↓
Push to main
    ↓
Workflow Triggers
    ↓
Parse Checkbox Marks
    ↓
Assign Status Label
    ↓
Issue Gets Label (status/backlog, in-progress, or completed)
    ↓
Issue Remains OPEN
    ↓
Developer Manually Closes When Ready
    ↓
Issue Becomes CLOSED
```

---

## Key Takeaways

### ✅ Checkboxes Do This
- Update status LABEL (`status/backlog` → `in-progress` → `completed`)
- Track implementation progress
- Show what's done vs pending
- Generate Copilot context

### ❌ Checkboxes Do NOT Do This
- Close GitHub issues
- Mark issues as "done"
- Remove issues from backlog
- Affect issue visibility

### Manual Actions Required
- **Closing issues:** Manually click close when truly done
- **Opening issues:** Not auto-opened; create explicitly
- **Reopening:** Manual action if work resumes

---

## Best Practice

### Recommended Workflow

1. **Create issue** (from spec or manually)
2. **Update checkboxes** as you work
   - `[ ]` → `[-]` when starting
   - `[-]` → `[X]` when completing
3. **Push changes** (updates status label)
4. **When fully done:**
   - All checkboxes `[X]`
   - PR merged
   - Testing complete
5. **Manually close** the GitHub issue
6. **Status label** shows: `status/completed` ✅

---

## Configuration

Checkbox status mapping is defined in:
- `.github/workflows/specs-to-issues.yml` (workflow documentation)
- `.github/scripts/sync_spec_status.py` (implementation)

To change status labels or mapping:
1. Edit sync_spec_status.py
2. Modify status determination logic
3. Update documentation
4. Test thoroughly

---

## FAQ

**Q: If all checkboxes are [X], is the issue closed?**
A: No. Checkboxes only update the status label. Issue remains open until manually closed.

**Q: Should I close an issue when all checkboxes are done?**
A: Yes, after verifying the work is complete and tested.

**Q: What if I need to reopen a closed issue?**
A: Click "Reopen" in GitHub, then update checkboxes as needed.

**Q: Do checkbox marks appear in commits?**
A: No, they only exist in spec files. Use commit messages to describe changes.

**Q: Can I filter issues by status label?**
A: Yes! In GitHub Issues, use "Label" filter to show `status/backlog`, `status/in-progress`, or `status/completed`.

---

## Summary

| Aspect | Checkboxes | Manual Close |
|--------|---|---|
| Track progress | ✅ Yes | ❌ N/A |
| Update status label | ✅ Yes | ❌ No |
| Close issue | ❌ No | ✅ Yes |
| Affect visibility | ❌ No | ✅ Yes |
| Require manual action | ❌ Auto | ✅ Manual |

---

**Version:** 1.0  
**Created:** March 7, 2026  
**Status:** ✅ Final

