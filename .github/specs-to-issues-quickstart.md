# Specs-to-Issues Quick Start

## What This Does

Automatically creates GitHub issues from your specification files whenever specs change.

## Setup (Already Done ✅)

The following files have been created:

- `.github/workflows/specs-to-issues.yml` — Main workflow
- `.github/scripts/specs_to_issues.py` — Issue creation script
- `.github/scripts/update_issue_status.py` — Status update script
- `.github/SPECS_TO_ISSUES.md` — Full documentation

## How to Use

### 1. Push Your Specs

```bash
# Edit a spec file
vim docs/specs/shopping/002 shopping-requirements.md

# Commit and push
git add docs/specs/
git commit -m "Update shopping requirements"
git push origin main
```

### 2. Workflow Runs Automatically

GitHub Actions automatically:
- Scans spec files
- Creates issues for new specs
- Updates status of existing issues

### 3. Monitor Progress

1. Go to your GitHub repository
2. Click **Issues** tab
3. Filter by label `from/specs`
4. See all auto-generated issues

## Issue Naming Convention

Specs are converted to issues with this naming:

| Spec File | Issue Title |
|-----------|-------------|
| `002 shopping-requirements.md` | Shopping Feature Requirements |
| `003 shopping-list-requirements.md` | Shopping List Requirements |
| `004 restock-requirements.md` | Restock Requirements |
| `002 home-requirements.md` | Home Page Requirements |

## Linking PRs to Issues

When you create a PR, link it to the issue:

```markdown
# Pull Request Description

This implements the shopping feature.

Closes #123
```

The `Closes #123` automatically:
- Links the PR to issue #123
- Marks issue as completed when PR merges

## Checking Workflow Status

1. Go to GitHub repository
2. Click **Actions** tab
3. Find **Specs to Issues Automation** workflow
4. Check the latest run:
   - ✅ Green = Success
   - ❌ Red = Failed (check logs)

## What Gets Created

Each issue includes:

```
Title: Shopping Feature Requirements

Body:
- Overview (from Objective section)
- Link to spec file
- Functional requirements
- Acceptance criteria

Labels:
- from/specs (created from specs)
- feature/shopping (feature category)
- status/backlog (initial status)
```

## Issue Status Labels

Issues automatically update with status:

| Status | Meaning |
|--------|---------|
| `status/backlog` | Ready to start |
| `status/in-progress` | Open PR exists |
| `status/completed` | PR merged |

## Minimal Example

```bash
# 1. Create/update a spec
echo "# New Feature" > docs/specs/new-feature.md

# 2. Push to main
git add docs/specs/
git commit -m "Add new feature spec"
git push

# 3. GitHub Actions runs automatically
# 4. Issue created in GitHub
# 5. You develop the feature
# 6. Create PR with "Closes #123"
# 7. Merge PR → Issue auto-closes
```

## Troubleshooting

**Q: Issues not being created?**
- Check that spec files are in `docs/specs/` folder
- Ensure files are named with index (e.g., `002 shopping-requirements.md`)
- Push must include changes to `.md` files

**Q: Can't see new issues?**
- Go to **Issues** tab
- Filter by `from/specs` label
- Check all state (open + closed)

**Q: Status not updating?**
- Link PR with `Closes #123` in PR description
- Merge the PR to main
- Workflow runs on merge

## Next Steps

1. **Verify setup** — Go to Actions tab, run workflow manually
2. **Create test issue** — Modify a spec file and push
3. **Monitor issues** — Check GitHub Issues dashboard
4. **Develop features** — Create PRs linked to issues
5. **Track progress** — Issues auto-update as PRs merge

## Documentation

For detailed information, see: `.github/SPECS_TO_ISSUES.md`

---

**Created:** March 7, 2026  
**Status:** Ready to use  
**Next:** Push specs to trigger automation

