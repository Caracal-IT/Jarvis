# Specs-to-Issues Automation

Automatically create and maintain GitHub issues from specification files in your Jarvis project.

## Overview

This automation system monitors your `/docs/specs/` folder and:

1. **Creates GitHub issues** from specification files
2. **Tracks implementation progress** via pull request activity
3. **Updates issue status** automatically (backlog → in-progress → completed)
4. **Maintains consistency** between specs and GitHub issues

## How It Works

### Trigger Points

The workflow (`specs-to-issues.yml`) runs automatically when:

- **Push to main branch** with changes to `docs/specs/**/*.md`
- **Pull request to main** with changes to `docs/specs/**/*.md`
- **Manual trigger** via GitHub Actions "Run workflow" button

### Workflow Steps

```
1. Checkout repository
2. Parse all spec files
3. Extract requirements
4. Create issues for new requirements
5. Update labels and status for existing issues
6. Report summary
```

## Issue Structure

Each created issue includes:

- **Title:** Spec file title (e.g., "Shopping Feature Requirements")
- **Body:**
  - Objective from spec
  - Link to spec file
  - Functional requirements
  - Acceptance criteria
- **Labels:**
  - `from/specs` — Created from specifications
  - `feature/<name>` — Feature category (shopping, home, etc.)
  - `status/backlog` → `status/in-progress` → `status/completed`

## Spec File Format

Your spec files are automatically parsed. Ensure they follow this structure:

```markdown
# Feature Title

## Objective
Clear description of the feature goal.

## Functional Requirements

[ ] Requirement 1
[ ] Requirement 2

## Acceptance Criteria

[ ] Criterion 1
[ ] Criterion 2
```

The automation extracts:
- `#` heading as issue title
- `## Objective` section
- `## Functional Requirements` section
- `## Acceptance Criteria` section

## Issue Lifecycle

### 1. Creation (Automatic)

When a new spec file is added or modified:
- Issue is created with `status/backlog` label
- Assigned labels: `from/specs`, `feature/<category>`

### 2. Development (Manual)

When you create a PR to implement the feature:
- Link PR to issue using `Closes #123` in PR description
- PR can reference issue with `#123`

### 3. Progress Update (Automatic)

The workflow automatically updates status:
- **status/in-progress** — When open PRs are found
- **status/completed** — When related PR is merged

### 4. Closure (Automatic)

When PR is merged:
- Issue is marked completed
- You can manually close it

## Usage

### Manual Trigger

1. Go to GitHub repository
2. Click **Actions** tab
3. Select **Specs to Issues Automation**
4. Click **Run workflow**
5. Select branch: `main`
6. Click **Run workflow**

### Automatic Updates

Just push or merge changes to spec files in `docs/specs/`:

```bash
# Add/modify a spec file
vim docs/specs/shopping/002 shopping-requirements.md

# Push to main (or create PR)
git add docs/specs/
git commit -m "Update shopping requirements"
git push origin main
```

Workflow runs automatically → Issues created/updated.

## Best Practices

### 1. Maintain Spec Files
- Update specs when requirements change
- Keep acceptance criteria clear and testable
- Use checkboxes `[ ]` for tracking

### 2. Link Issues to PRs
```markdown
# PR Description
Implements requirements from #123

Closes #123
```

### 3. Monitor Issue Status
- Check GitHub Issues dashboard
- Filter by `from/specs` label
- Track implementation progress

### 4. Keep Specs Updated
- When closing an issue, update the spec file
- Mark completed items with `[x]`
- Add implementation notes

## Troubleshooting

### Issues Not Being Created

**Check:**
1. Workflow is enabled (`.github/workflows/specs-to-issues.yml` exists)
2. Push includes changes to `docs/specs/**/*.md`
3. Spec file has proper format (H1 title required)
4. GitHub token has `issues:write` permission

**Debug:**
1. Go to Actions tab
2. Find the workflow run
3. Check logs for errors

### Issues Not Updating Status

**Check:**
1. PR is linked to issue (use `Closes #123` in description)
2. PR is merged to main
3. Workflow has `repo_owner` and `repo_name` set correctly

## Configuration

### Customize Triggers

Edit `.github/workflows/specs-to-issues.yml`:

```yaml
on:
  push:
    branches:
      - main
      - develop  # Add other branches
    paths:
      - 'docs/specs/**/*.md'
```

### Customize Labels

Edit `.github/scripts/specs_to_issues.py`:

```python
def get_spec_label(file_path: str) -> str:
    if "shopping" in file_path:
        return "feature/shopping"  # Customize here
    # ... add more categories
```

### Customize Issue Body

Edit the `generate_issue_body()` function in `specs_to_issues.py`.

## Files

```
.github/
├── workflows/
│   └── specs-to-issues.yml           # Main workflow
└── scripts/
    ├── specs_to_issues.py             # Create issues
    └── update_issue_status.py          # Update status
```

## Security

- **Token:** Uses GitHub-provided `GITHUB_TOKEN` (scoped to current repo)
- **Permissions:** Requests minimal required (`contents:read`, `issues:write`)
- **Sensitive data:** No credentials stored in workflow

## Future Enhancements

Potential additions:

- [ ] Auto-close completed issues
- [ ] Generate kanban board from specs
- [ ] Send Slack notifications
- [ ] Generate release notes from closed issues
- [ ] Track time estimates from spec complexity
- [ ] Generate burndown charts

## Support

For issues or questions:

1. Check **Actions** tab for workflow logs
2. Review **Issues** dashboard
3. Check spec file format in `/docs/specs/`

---

**Last Updated:** March 7, 2026  
**Status:** Active  
**Maintained By:** Jarvis Project Team

