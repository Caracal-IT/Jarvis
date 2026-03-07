# Specs Requirements Index

This folder contains high-level, end-state requirements documents for the Jarvis app and its features.

## Purpose

These documents define:

- What the product must do at completion
- What each feature must deliver
- How completion is validated

The focus is user outcomes and expected behavior, not implementation detail.

## Root Structure

The active root of `docs/specs/` must contain only:

- `001 readme.md`
- `002 home-requirements.md`

Feature requirements must be placed in separate feature folders (for example, `shopping/`).

Deprecated root files are temporary and should be removed as part of cleanup.

## Document Map

- `002 home-requirements.md`
  - Requirements for the Home Page
- `003 documentation-summary-requirements.md`
  - Requirements for automated documentation summary generation
- `shopping/`
  - `002 shopping-requirements.md` — Shopping feature requirements
  - `003 shopping-list-requirements.md` — Shopping List requirements
  - `004 restock-requirements.md` — Restock requirements for Shopping

## Writing Standards

1. Use Standard American English.
2. Use clear requirement language (`must`, `should`, `out of scope`).
3. Keep requirements testable and concise.
4. Keep file names lowercase kebab-case.
5. Use Title Case for all Markdown headers.

## Change Control

When behavior changes, update relevant requirements in `docs/specs/` in the same change set.

1. Update Home Page requirements if app-level behavior changes.
2. Update Shopping requirements in `docs/specs/shopping/`, including:
   - `002 shopping-requirements.md`
   - `003 shopping-list-requirements.md`
   - `004 restock-requirements.md`
3. Update acceptance criteria where needed.
4. Keep scope and completion criteria current.
