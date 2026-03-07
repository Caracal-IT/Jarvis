# Specs-to-Issues Setup Checklist

## ✅ Implementation Complete

All automation files have been created and configured.

## Files Created

### Workflow Files
- [x] `.github/workflows/specs-to-issues.yml` — Main GitHub Actions workflow
- [x] `.github/scripts/specs_to_issues.py` — Issue creation script
- [x] `.github/scripts/update_issue_status.py` — Status update script

### Configuration
- [x] `.github/specs-to-issues.config.yml` — Customization settings

### Documentation
- [x] `.github/SPECS_TO_ISSUES.md` — Full documentation
- [x] `.github/SPECS_TO_ISSUES_QUICKSTART.md` — Quick start guide
- [x] `.github/IMPLEMENTATION_SUMMARY.md` — Implementation summary
- [x] `.github/REFERENCE.md` — Quick reference guide
- [x] `.github/SETUP_CHECKLIST.md` — This file

## Pre-Deployment Checks

### Repository Setup
- [ ] GitHub repository created
- [ ] Repository is public or has Actions enabled
- [ ] You have push access to main branch
- [ ] Specs are in `docs/specs/` folder

### Spec Files
- [ ] Spec files follow format (H1 title required)
- [ ] All spec files are in `/docs/specs/`
- [ ] Files are named with index (e.g., `002 home-requirements.md`)
- [ ] Checkboxes use format `[ ]` (space inside)

### GitHub Actions
- [ ] Actions tab is enabled in repository
- [ ] Workflow file is in `.github/workflows/`
- [ ] Workflow syntax is valid YAML

### Python Scripts
- [ ] Python 3.11+ is available (GitHub Actions provides it)
- [ ] Scripts have execute permissions (not always needed)
- [ ] Scripts location is correct (`.github/scripts/`)

## First-Time Setup

### Step 1: Commit Files
```bash
git add .github/workflows/
git add .github/scripts/
git add .github/*.md
git add .github/*.yml
git commit -m "Add specs-to-issues automation"
```

### Step 2: Push to Main
```bash
git push origin main
```

### Step 3: Verify Workflow Activated
1. Go to GitHub repository
2. Click **Actions** tab
3. Look for **Specs to Issues Automation**
4. If shown, workflow is enabled ✅

### Step 4: Test the Automation
```bash
# Make a test change to a spec
touch docs/specs/002\ home-requirements.md
git add docs/specs/
git commit -m "Test automation"
git push
```

### Step 5: Verify Workflow Ran
1. Go to **Actions** tab
2. Find the most recent run
3. Check status (should be green ✅)
4. Click to view logs

### Step 6: Check Issues Created
1. Go to **Issues** tab
2. Look for issues with label `from/specs`
3. Verify title matches spec file

## Post-Deployment

### Week 1: Validation
- [ ] Workflow runs without errors
- [ ] Issues are created from spec changes
- [ ] Issue titles match spec file titles
- [ ] Labels are applied correctly
- [ ] Issue body contains objective and requirements

### Week 2: Integration
- [ ] Create PR with "Closes #123" for a spec issue
- [ ] Verify issue status changes to in-progress
- [ ] Merge PR and verify issue marked completed
- [ ] Test updating a spec file triggers workflow

### Week 3: Refinement
- [ ] Review and adjust labels in config if needed
- [ ] Customize feature categories if desired
- [ ] Train team on workflow
- [ ] Set up project board (optional)

### Ongoing: Maintenance
- [ ] Monitor workflow runs in Actions tab
- [ ] Keep specs updated in `/docs/specs/`
- [ ] Link PRs to issues with "Closes #"
- [ ] Review issue status dashboard monthly

## Common Issues & Solutions

### Issue: "Workflow not showing in Actions tab"

**Solution:**
1. Verify file is in `.github/workflows/specs-to-issues.yml`
2. Verify workflow YAML syntax is valid
3. Try manual trigger:
   - Actions → Specs to Issues Automation → Run workflow

### Issue: "Issues not being created"

**Solution:**
1. Check spec file format (must have H1 title)
2. Verify spec is in `docs/specs/` folder
3. Check workflow logs for errors:
   - Actions → Latest run → Parse specs step
4. Verify GitHub token has permission:
   - Settings → Actions → Permissions

### Issue: "Script error: ModuleNotFoundError"

**Solution:**
1. Python 3.11 should be available (GitHub Actions has it)
2. Check script syntax in `.github/scripts/`
3. Verify `requests` library is available
4. View full logs in Actions tab

### Issue: "API rate limit exceeded"

**Solution:**
1. Reduce frequency of spec changes
2. Increase delay between requests in config
3. Reduce max_issues_per_run in config
4. GitHub rate limit resets hourly

## Manual Trigger (If Needed)

If automatic trigger isn't working:

1. Go to GitHub repository
2. Click **Actions** tab
3. Select **Specs to Issues Automation**
4. Click **Run workflow** button
5. Select branch: `main`
6. Click **Run workflow**

## Logs & Debugging

### Access Workflow Logs
1. Actions tab → Specs to Issues Automation
2. Click on the workflow run
3. Click on job name
4. Expand sections to view logs

### View Specific Step Logs
- "Set up Python"
- "Parse specs and create issues"
- "Update issue labels and status"

### Common Log Messages
```
✅ Created issue: Shopping Feature Requirements
   → Issue successfully created

ℹ️  Issue already exists: #42
   → Issue exists, skipped

📋 Found {N} spec files
   → Workflow found specs to process

❌ Failed to create issue: {title}
   → Check spec format and permissions
```

## Customization Checklist

After basic setup, consider customizing:

- [ ] Edit `specs-to-issues.config.yml` for custom labels
- [ ] Add feature categories matching your project
- [ ] Adjust issue template in config
- [ ] Set notification preferences
- [ ] Configure rate limits if needed

## Documentation Checklist

For your team:

- [ ] Share `.github/SPECS_TO_ISSUES_QUICKSTART.md`
- [ ] Share `.github/REFERENCE.md`
- [ ] Train on spec file format
- [ ] Show how to link PRs to issues
- [ ] Explain status labels and workflow

## Final Verification

```bash
# Verify all files exist
ls -la .github/workflows/specs-to-issues.yml
ls -la .github/scripts/specs_to_issues.py
ls -la .github/scripts/update_issue_status.py
ls -la .github/specs-to-issues.config.yml

# Should show:
# .github/workflows/specs-to-issues.yml
# .github/scripts/specs_to_issues.py
# .github/scripts/update_issue_status.py
# .github/specs-to-issues.config.yml
```

## Deployment Summary

✅ **System Status: Ready**

- ✅ Workflow files created
- ✅ Python scripts created
- ✅ Configuration file created
- ✅ Documentation complete
- ✅ Ready for deployment

**Next Action:** Commit and push to main branch

```bash
git add .github/
git commit -m "Add specs-to-issues automation"
git push origin main
```

**Then:** 
1. Go to GitHub Actions tab
2. Verify workflow appears
3. Make a test spec change
4. Watch workflow run automatically

## Support & Help

**Documentation:**
- Full guide: `.github/SPECS_TO_ISSUES.md`
- Quick start: `.github/SPECS_TO_ISSUES_QUICKSTART.md`
- Reference: `.github/REFERENCE.md`
- This checklist: `.github/SETUP_CHECKLIST.md`

**Troubleshooting:**
1. Check Actions tab logs
2. Review spec file format
3. Verify GitHub permissions
4. Check configuration settings

## Success Criteria

System is working correctly when:
1. ✅ Workflow runs on spec file changes
2. ✅ Issues are created automatically
3. ✅ Issues have correct labels
4. ✅ Issue status updates on PR merge
5. ✅ No errors in workflow logs

---

## Status

**Created:** March 7, 2026
**Status:** ✅ Ready for deployment
**Version:** 1.0

**Next Step:** Push to main and verify in Actions tab

