# Specs-to-Issues Reference Guide

Quick reference for working with the automation system.

## Command Reference

### Trigger Automation by Pushing Specs
```bash
git add docs/specs/
git commit -m "Update specifications"
git push origin main
```

### Manually Trigger Workflow
1. Go to GitHub → **Actions**
2. Select **Specs to Issues Automation**
3. Click **Run workflow**

### Link PR to Issue
```markdown
# In PR description:
Closes #123
Closes #124
Closes #125
```

## Workflow File Reference

**Location:** `.github/workflows/specs-to-issues.yml`

| Setting | Value |
|---------|-------|
| Trigger | Push/PR to main with spec changes |
| Python | 3.11 |
| Scripts | specs_to_issues.py, update_issue_status.py |
| Permissions | contents:read, issues:write |

## Spec File Format Reference

### Required Elements

```markdown
# Title (becomes issue title)

## Objective
Description of the goal.

## Functional Requirements

[ ] Requirement 1
[ ] Requirement 2

## Acceptance Criteria

[ ] Criterion 1
[ ] Criterion 2
```

### Parsing Rules

| Section | Used For | Format |
|---------|----------|--------|
| `# Heading` | Issue title | Single H1 required |
| `## Objective` | Issue overview | Extracted as-is |
| `## Functional Requirements` | Issue body | Bullet/checkbox list |
| `## Acceptance Criteria` | Issue body | Bullet/checkbox list |

## Label Reference

### Feature Labels
```
feature/home         — Home Page
feature/shopping     — Shopping feature
feature/scanner      — Barcode scanner
feature/base-items   — Base items management
feature/core         — Core system
```

### Status Labels
```
status/backlog       — Ready to start
status/in-progress   — Being developed
status/completed     — Done, PR merged
```

### System Labels
```
from/specs           — Created from specs
```

## Issue Lifecycle

```
1. Spec created/updated
        ↓
2. Push to main
        ↓
3. Workflow runs
        ↓
4. Issue created with status/backlog
        ↓
5. Developer starts work
        ↓
6. Create PR with "Closes #123"
        ↓
7. Issue marked status/in-progress (auto)
        ↓
8. PR merged
        ↓
9. Issue marked status/completed (auto)
        ↓
10. Manually close issue (optional)
```

## File Paths

| File | Purpose | Edit? |
|------|---------|-------|
| `.github/workflows/specs-to-issues.yml` | Main workflow | Only if changing triggers |
| `.github/scripts/specs_to_issues.py` | Create issues | For advanced customization |
| `.github/scripts/update_issue_status.py` | Update status | For advanced customization |
| `.github/specs-to-issues.config.yml` | Settings | Yes, customize here |
| `docs/specs/**/*.md` | Your specs | Yes, write requirements here |

## Common Tasks

### Add a New Spec File
```bash
# Create file
cat > docs/specs/feature-name.md << 'EOF'
# Feature Name

## Objective
What this feature does.

## Functional Requirements

[ ] Requirement 1
[ ] Requirement 2

## Acceptance Criteria

[ ] It works
[ ] It's tested
EOF

# Push
git add docs/specs/feature-name.md
git commit -m "Add feature spec"
git push

# ✅ Issue automatically created
```

### Update a Spec
```bash
# Edit
vim docs/specs/shopping/002 shopping-requirements.md

# Push
git add docs/specs/
git commit -m "Update shopping spec"
git push

# ✅ Issue updated (if exists) or created (if new)
```

### Create PR for Spec Issue
```bash
# Start branch
git checkout -b feat/shopping

# Make changes
# ...

# Commit
git commit -m "Implement shopping feature"

# Push
git push origin feat/shopping

# Create PR on GitHub
# Title: Implement Shopping Feature
# Description:
#   Closes #42
#
#   This implements the shopping feature as specified.

# ✅ Issue #42 marked as in-progress
# ✅ When merged, issue auto-closes
```

## Debugging

### Check Workflow Status
1. Go to GitHub repo
2. Click **Actions** tab
3. Find **Specs to Issues Automation**
4. Click latest run to see logs

### View Workflow Logs
```
GitHub → Actions → Specs to Issues Automation → [Run] → [Job]
↓
"Parse specs and create issues" section shows details
```

### Check If Issue Was Created
1. Go to **Issues** tab
2. Filter by label: `from/specs`
3. Look for your spec title

### Verify Spec Format
Spec must have:
- ✅ H1 title (`# Feature Name`)
- ✅ At least one section (`## Objective`, etc.)
- ✅ Be in `/docs/specs/` folder
- ✅ End with `.md`

## Configuration Reference

**File:** `.github/specs-to-issues.config.yml`

### Common Customizations

**Change feature label:**
```yaml
feature_labels:
  my_feature: "feature/my-feature"
```

**Add default label:**
```yaml
default_labels:
  - "from/specs"
  - "custom-label"
```

**Change status names:**
```yaml
status_labels:
  backlog: "ready-to-start"
  in_progress: "actively-developing"
  completed: "shipped"
```

## API Reference

Not typically needed, but for reference:

```
GitHub API: https://api.github.com/repos/{OWNER}/{REPO}/issues

Python scripts use:
- requests library
- GitHub REST API v3
- OAuth token (automatic)
```

## Performance Notes

- **First run:** May take 30-60 seconds
- **Subsequent runs:** 10-20 seconds
- **Max issues per run:** 50 (configurable)
- **API rate limit:** 5,000 requests/hour

## Limitations

- Cannot create issues without proper GitHub permissions
- Spec file parsing is regex-based (must match format)
- Requires `.md` extension
- Requires GitHub Actions enabled in repository

## Examples

### Simple Spec
```markdown
# My Feature

## Objective
This feature does something cool.

## Functional Requirements

[ ] It works

## Acceptance Criteria

[ ] Tests pass
```

### Complete Spec
```markdown
# Shopping Feature Requirements

## Objective
Enable users to maintain a shopping list with persistence.

## Functional Requirements

### List Management
[ ] Add items to list
[ ] Remove items from list
[ ] View all items

### Persistence
[ ] Save list to database
[ ] Restore list on app restart

## Acceptance Criteria

[ ] User can add 50+ items
[ ] List persists after restart
[ ] UI is responsive
[ ] No crashes during normal use
```

## Tips & Tricks

1. **Use checkboxes** — `[ ]` for tracking implementation
2. **Detailed specs** — More detail = better issues
3. **Link specs** — Reference other specs in text
4. **Keep updated** — Update spec when feature changes
5. **Use "Closes #"** — Always link PRs to issues
6. **Monitor status** — Check Issues dashboard weekly

## Quick Links

- **Main workflow:** `.github/workflows/specs-to-issues.yml`
- **Full docs:** `.github/SPECS_TO_ISSUES.md`
- **Quick start:** `.github/SPECS_TO_ISSUES_QUICKSTART.md`
- **Configuration:** `.github/specs-to-issues.config.yml`
- **Your specs:** `docs/specs/`

---

**Quick Test:**
```bash
# Make small change
echo "" >> docs/specs/002\ home-requirements.md

# Push
git add docs/specs/
git commit -m "Test automation"
git push

# Check Actions tab → Should run automatically
# Check Issues tab → Should find related issues
```

---

**Version:** 1.0  
**Updated:** March 7, 2026  
**Status:** Ready to use

