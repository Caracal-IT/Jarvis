# Documentation Summary Feature

## Checkbox Status Legend

- `[ ]` — Not started
- `[-]` — In progress
- `[X]` — Done

Use these checkboxes to track implementation status of each requirement.

## Purpose

This document defines the requirements for an automated documentation summary feature that generates a `readme.md` in the `docs/` folder to summarize all specification files.

## Product Outcome

The documentation summary must provide users, contributors, and AI assistants with a clear, up-to-date overview of all specification documents in the project without manually maintaining a summary file.

## User Goals

[X] Understand what specifications exist in the project at a glance.  
[X] Navigate quickly to the specific spec they need.  
[X] See the current implementation status of each feature.  
[X] Access this information without leaving the repository.

## Functional Requirements

### Documentation Summary Generation

[X] The system must automatically generate a `docs/readme.md` file.  
[X] The summary must list all specification files from `docs/specs/`.  
[X] The summary must include the title of each spec file.  
[X] The summary must include the file path as a clickable link.  
[X] The summary must show the current status (backlog, in-progress, completed) for each spec.  
[X] The summary must be organized by feature category.  
[X] The summary must include a table of contents at the top.  
[X] The summary must display the last updated timestamp.

### Automation

[X] The `docs/readme.md` must regenerate automatically when spec files change.  
[X] The generation must occur during the specs-to-issues workflow.  
[X] The generation must complete within 30 seconds.  
[X] The generation must not fail if a spec file is malformed.

### Content Quality

[X] The summary must use proper Standard American English.  
[X] The summary must follow the project's markdown naming conventions.  
[X] The summary must be formatted for readability.  
[X] The summary must include helpful navigation links.

## Non-Functional Requirements

### Maintainability

[X] The summary generation logic must be in a dedicated Python script.  
[X] The script must be located in `.github/scripts/`.  
[X] The script must follow the existing project coding standards.  
[X] The script must include clear documentation and comments.

### Reliability

[X] The script must handle missing or empty spec directories gracefully.  
[X] The script must validate file paths before accessing files.  
[X] The script must report errors clearly in workflow logs.  
[X] The script must not break the workflow if it fails.

### Performance

[X] The summary generation must not significantly increase workflow time.  
[X] The script must process all specs in under 10 seconds.

## Acceptance Criteria

The Documentation Summary feature is complete when all are true:

[X] A `docs/readme.md` file exists in the repository.  
[X] The file lists all specification documents from `docs/specs/`.  
[X] The file shows the current status of each spec.  
[X] The file is automatically regenerated when specs change.  
[X] The file includes the installation guide reference.  
[X] The file follows project markdown standards (lowercase, kebab-case links).  
[X] The workflow includes the summary generation step.  
[X] The summary generation completes successfully in CI.

## Example Output

The generated `docs/readme.md` should look similar to:

```markdown
# Jarvis Documentation

Last updated: March 7, 2026

## Specifications

### Core Features

- [Home Page Requirements](./specs/002 home-requirements.md) — Status: Completed ✅
- [Documentation Summary](./specs/003 documentation-summary-requirements.md) — Status: In Progress 🟡

### Shopping Feature

- [Shopping Requirements](./specs/shopping/002 shopping-requirements.md) — Status: In Progress 🟡
- [Shopping List Requirements](./specs/shopping/003 shopping-list-requirements.md) — Status: Backlog 🔴
- [Restock Requirements](./specs/shopping/004 restock-requirements.md) — Status: Backlog 🔴

## Installation

See: [How to Install Jarvis APK](./.github/how-to-install-apk.md)

## Standards

See: [docs/standards/](./standards/)
```

