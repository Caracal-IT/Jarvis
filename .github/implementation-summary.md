# Specs-to-Issues Automation — Implementation Summary

## ✅ Automation System Created

Your Jarvis project now has a fully automated system to create and maintain GitHub issues from specification files.

## What Was Created

### 1. GitHub Actions Workflow
📁 `.github/workflows/specs-to-issues.yml`
- Automatically triggered on spec file changes
- Parses spec files and creates issues
- Updates issue status based on PR activity
- Runs on push, pull request, or manual trigger

### 2. Python Scripts

**📄 `.github/scripts/specs_to_issues.py`**
- Scans `/docs/specs/` folder
- Parses specification files
- Extracts: title, objective, requirements, acceptance criteria
- Creates GitHub issues with proper formatting
- Prevents duplicate issues
- Applies appropriate labels and categories

**📄 `.github/scripts/update_issue_status.py`**
- Monitors related pull requests
- Updates issue status: backlog → in-progress → completed
- Links PRs to issues automatically

### 3. Configuration Files

**📄 `.github/specs-to-issues.config.yml`**
- Centralized configuration
- Feature labels
- Default labels
- Status workflows
- Notification settings
- Customizable templates

### 4. Documentation

**📄 `.github/SPECS_TO_ISSUES.md`**
- Comprehensive documentation
- How the system works
- Issue structure and lifecycle
- Best practices
- Troubleshooting guide
- Future enhancements

**📄 `.github/SPECS_TO_ISSUES_QUICKSTART.md`**
- Quick-start guide
- Step-by-step usage
- Minimal examples
- Common issues

## How It Works

```
1. You update/add spec file
   ↓
2. Push to main or create PR
   ↓
3. GitHub Actions workflow triggers
   ↓
4. specs_to_issues.py parses specs
   ↓
5. Issues created automatically
   ↓
6. Labels applied (feature, status)
   ↓
7. You develop the feature
   ↓
8. Create PR with "Closes #123"
   ↓
9. PR merged
   ↓
10. Issue auto-marked as completed
```

## Spec File → GitHub Issue Mapping

| Your Spec File | Creates Issue With |
|---|---|
| `docs/specs/002 home-requirements.md` | Title: "Home Page Requirements" |
| `docs/specs/shopping/002 shopping-requirements.md` | Title: "Shopping Feature Requirements" |
| `docs/specs/shopping/003 shopping-list-requirements.md` | Title: "Shopping List Requirements" |
| `docs/specs/shopping/004 restock-requirements.md` | Title: "Restock Requirements" |

## Automatic Labels Applied

Each issue gets:
- `from/specs` — Created from specifications
- `feature/<category>` — Feature category (shopping, home, etc.)
- `status/backlog` → `status/in-progress` → `status/completed`

## Key Features

✅ **Automatic Creation**
- New specs automatically become issues
- No manual issue creation needed

✅ **Prevents Duplicates**
- Checks for existing issues before creating
- Skips if issue already exists

✅ **Smart Status Tracking**
- Backlog → In Progress → Completed
- Automatic status updates on PR activity

✅ **Spec-to-Code Linking**
- Issues link directly to spec files
- Easy to find requirements in source

✅ **Safe Automation**
- Uses GitHub's built-in token (scoped to repo)
- Minimal permissions required
- No external dependencies

✅ **Easy to Customize**
- Edit `.github/specs-to-issues.config.yml`
- Modify Python scripts as needed
- Control which specs trigger issues

## Usage Examples

### Push Changes to Specs
```bash
# Modify a spec file
vim docs/specs/shopping/002 shopping-requirements.md

# Commit and push
git add docs/specs/
git commit -m "Update shopping requirements"
git push origin main

# ✅ Workflow runs automatically
# ✅ Issues created in GitHub
```

### Link PR to Issue
```bash
# When creating a PR:
Implements shopping feature requirements.

Closes #42
```

This automatically links the PR and marks the issue complete when merged.

### Manual Trigger
1. Go to GitHub repository
2. Click **Actions** tab
3. Select **Specs to Issues Automation**
4. Click **Run workflow**

## Files Created

```
.github/
├── workflows/
│   └── specs-to-issues.yml                 # Main workflow
├── scripts/
│   ├── specs_to_issues.py                  # Issue creation
│   └── update_issue_status.py               # Status updates
├── specs-to-issues.config.yml               # Configuration
├── SPECS_TO_ISSUES.md                       # Full documentation
└── SPECS_TO_ISSUES_QUICKSTART.md            # Quick start
```

## Next Steps

### Immediate (Today)
1. ✅ Review `.github/SPECS_TO_ISSUES_QUICKSTART.md`
2. ✅ Test by pushing a spec file change
3. ✅ Verify workflow runs in Actions tab
4. ✅ Check GitHub Issues for new issues

### Short-term (This Week)
1. Start creating issues from existing specs
2. Begin implementing features with linked PRs
3. Monitor issue status updates

### Long-term
1. Refine automation based on usage
2. Add custom labels/categories as needed
3. Consider future enhancements (Slack, burndown, etc.)

## Testing the Automation

**Quick Test:**
```bash
# Make a small change to a spec
touch docs/specs/shopping/002 shopping-requirements.md
git add docs/specs/
git commit -m "Test automation"
git push origin main
```

Then:
1. Go to GitHub repository
2. Click **Actions** tab
3. Watch workflow run
4. Check **Issues** tab for new issues

## Important Notes

- **First Run:** May need to manually authorize workflow in GitHub repository settings
- **GitHub Token:** Uses automatic GitHub token (no setup needed)
- **Permissions:** Minimal required (contents:read, issues:write)
- **Rate Limiting:** Respects GitHub API rate limits

## Configuration

To customize behavior, edit:
```
.github/specs-to-issues.config.yml
```

Changes:
- Automatically picked up on next workflow run
- No code changes needed
- Restart workflow for changes to take effect

## Monitoring

Check progress:
1. **Actions tab** — See workflow runs and logs
2. **Issues tab** — See created issues
3. **Project board** — Track implementation (optional, manual setup)

## Troubleshooting

See: `.github/SPECS_TO_ISSUES.md` → Troubleshooting section

Common issues:
- Issues not created → Check spec file format
- Status not updating → Ensure PR links with "Closes #123"
- Workflow not running → Check paths in workflow file

## Support

**Documentation:**
- Full guide: `.github/SPECS_TO_ISSUES.md`
- Quick start: `.github/SPECS_TO_ISSUES_QUICKSTART.md`

**Debug:**
1. Check Actions tab for workflow logs
2. Verify spec file in `/docs/specs/`
3. Confirm GitHub token permissions

---

## Summary

✅ **What You Have:**
- Fully automated spec-to-issue conversion
- Automatic status tracking
- Smart linking of PRs to issues
- Zero manual issue creation

✅ **What Happens:**
1. You update specs
2. Workflow automatically creates/updates issues
3. You develop features with linked PRs
4. Issues auto-close when PR merges

✅ **What's Next:**
1. Push some spec changes
2. Watch issues appear automatically
3. Develop features with GitHub integration
4. Track progress in Issues dashboard

**Status:** ✅ Ready to use  
**Created:** March 7, 2026  
**Last Updated:** March 7, 2026

