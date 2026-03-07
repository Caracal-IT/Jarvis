# Quick Start: Issue-Driven Development with Copilot

Simple guide to use the new bidirectional sync system.

## 3-Minute Overview

**The System:**
- Specs create GitHub issues automatically
- GitHub issues link back to specs
- Copilot reads issue summaries
- Copilot updates spec checkboxes
- Workflow auto-syncs everything

## Quick Workflow

### As a Developer

```
Step 1: Open spec file
├─ vim docs/specs/shopping/002 shopping-requirements.md

Step 2: Update checkbox as you work
├─ [ ] Not started
├─ [-] In progress ← You're here
└─ [X] Done

Step 3: Push to main
├─ git add docs/specs/
├─ git commit -m "Update progress"
└─ git push origin main

Step 4: Workflow runs automatically
├─ Parses your checkboxes
├─ Updates GitHub issue #42
├─ Updates issue-context.json
└─ Updates copilot-issue-summary.md

Result: Everyone sees the latest progress
```

### As Copilot

```
Step 1: Read issue summary
├─ File: .github/copilot-issue-summary.md
├─ See: "Shopping Feature - In Progress (2/3)"
└─ Find: Link to spec file

Step 2: Check issue details
├─ File: .github/issue-context.json
├─ Get: Complete issue mapping
└─ Find: spec_file path

Step 3: Open and update spec
├─ File: docs/specs/shopping/002 shopping-requirements.md
├─ Change: [ ] → [-] for current work
└─ Add: New requirements with [ ] as needed

Step 4: Push the changes
├─ git commit -m "Progress on shopping"
├─ git push origin main
└─ Workflow auto-syncs to GitHub

Result: GitHub issue automatically updated
```

## The Two Files Copilot Should Read

### 1. Issue Summary (Markdown)
```
File: .github/copilot-issue-summary.md
Format: Easy-to-read markdown
Use: Understand current work and what needs to be done
Includes: Status icons, links, action guide
```

**Example:**
```markdown
### SHOPPING

#### 🟡 #42: Shopping Feature Requirements
- Status: in-progress
- Spec: docs/specs/shopping/002 shopping-requirements.md
- URL: https://github.com/...

#### 🔴 #43: Restock Requirements
- Status: backlog
- Spec: docs/specs/shopping/004 restock-requirements.md
```

### 2. Issue Context (JSON)
```
File: .github/issue-context.json
Format: Machine-readable JSON
Use: Get detailed info about issues and their specs
Includes: All issue details, status, links
```

**Example:**
```json
{
  "by_spec": {
    "docs/specs/shopping/002 shopping-requirements.md": [
      {
        "issue_number": 42,
        "title": "Shopping Feature Requirements",
        "status_label": "status/in-progress",
        "spec_file": "docs/specs/shopping/002 shopping-requirements.md"
      }
    ]
  }
}
```

## Checkbox Rules

Use these in your spec files:

| Mark | Meaning | Example |
|------|---------|---------|
| `[ ]` | Not started | `[ ] Add shopping items` |
| `[-]` | In progress | `[-] Remove shopping items` |
| `[X]` | Done | `[X] List persists` |

## Real Example

**Initial State (Day 1):**
```markdown
[ ] User can add items
[ ] User can remove items
[ ] List persists
```
→ GitHub issue: `status/backlog`

**Making Progress (Day 2):**
```markdown
[X] User can add items
[-] User can remove items
[ ] List persists
```
→ GitHub issue: `status/in-progress`

**Complete (Day 3):**
```markdown
[X] User can add items
[X] User can remove items
[X] List persists
```
→ GitHub issue: `status/completed`

## What Happens Automatically

**When you push to `docs/specs/**/*.md`:**

1. ✅ Workflow triggers
2. ✅ Parses spec files
3. ✅ Updates GitHub issues
4. ✅ Syncs checkbox status
5. ✅ Generates `issue-context.json`
6. ✅ Generates `copilot-issue-summary.md`
7. ✅ Done (all in 30 seconds)

**No manual updates needed!**

## How to Track Progress

**Option 1: GitHub UI**
1. Go to Issues tab
2. Look for `from/specs` label
3. Check status label: `status/backlog`, `status/in-progress`, `status/completed`

**Option 2: Copilot**
1. Read `.github/copilot-issue-summary.md`
2. See all issues organized by feature
3. See quick status overview

**Option 3: JSON Data**
1. Read `.github/issue-context.json`
2. Get complete mapping
3. See status breakdown

## Common Tasks

### Add a New Requirement

```markdown
# docs/specs/shopping/002 shopping-requirements.md

## Functional Requirements

[ ] Existing requirement
[ ] Another requirement
[ ] New requirement ← Add this line with [ ]

```

Push → Workflow creates new GitHub issue automatically

### Update Status

```markdown
[ ] Not started
[-] Now in progress ← Change to [-]
[X] Already done ← Change to [X]
```

Push → Workflow updates GitHub issue label automatically

### Complete a Feature

When all items are `[X]`:
```markdown
[X] Requirement 1
[X] Requirement 2
[X] Requirement 3
```

Push → GitHub issue marked `status/completed` automatically

## File Locations

```
Specs (source of truth):
├─ docs/specs/002 home-requirements.md
└─ docs/specs/shopping/
   ├─ 002 shopping-requirements.md
   ├─ 003 shopping-list-requirements.md
   └─ 004 restock-requirements.md

Generated Context Files (for Copilot):
├─ .github/copilot-issue-summary.md ← Read this first
└─ .github/issue-context.json ← Reference this for details

GitHub Issues:
└─ Labels: from/specs, feature/*, status/*
```

## Tips for Success

✅ **Do:**
- Update checkboxes as you work
- Push regularly (daily or per task)
- Use spec files as your source of truth
- Let Copilot read the context files

❌ **Don't:**
- Manually create GitHub issues (auto-created)
- Manually update issue status (auto-synced)
- Skip updating spec checkboxes
- Push directly without spec updates

## Copilot Workflow

```
Copilot's Job:

1. Read .github/copilot-issue-summary.md
   └─ "What's the current state?"

2. Check .github/issue-context.json
   └─ "What are the details?"

3. Open spec file
   └─ "Where's the source of truth?"

4. Update checkboxes
   └─ "Mark my progress"

5. Push to main
   └─ "Trigger the workflow"

6. Workflow auto-updates GitHub issues
   └─ "Done!"
```

## Troubleshooting

**Q: Context files not appearing?**
A: Check workflow logs. Make sure spec files have checkboxes.

**Q: Issue status not updating?**
A: Verify you pushed to `main` (not a branch).

**Q: Copilot can't find issues?**
A: Open `.github/copilot-issue-summary.md` in the repo.

## Full Documentation

For more details, see:
- `.github/bidirectional-sync-guide.md` — Complete guide
- `.github/workflow-update-summary.md` — Technical details
- `.github/copilot-instructions.md` — Copilot setup

## Quick Checklist

**To get started:**
- [ ] Read this file
- [ ] Read `.github/copilot-issue-summary.md`
- [ ] Read `.github/issue-context.json`
- [ ] Update a spec checkbox
- [ ] Push to main
- [ ] Watch issue auto-update

**Done!** You're now using the bidirectional sync system.

---

**Ready?** Go update a spec file and watch the magic happen! 🚀

