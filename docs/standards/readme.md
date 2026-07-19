# Standards and Guidelines

⚠️ **CRITICAL: AI assistants MUST read and reference this file before EVERY interaction with the Jarvis project.**

## Overview

This folder contains the standards and best practices that **MUST** be followed throughout the Jarvis project. All developers, including AI assistants, **MUST** reference these documents when making code changes, adding features, or reviewing code.

**This readme.md file is the PRIMARY CONTEXT for all AI chat interactions on this project.**

## Purpose

These standards ensure:
- **Consistency** across the codebase
- **Quality** and maintainability of code
- **Best practices** aligned with Kotlin and Android community standards
- **Efficient collaboration** between team members and AI assistants

## Documents in This Folder

This `readme.md` is the **single source of truth** for all AI assistants — the
starting point before any other document. It contains the core rules, the
pre-work checklist, and pointers to every other standards document below.

> **GitHub Copilot** is automatically pointed here via `.github/copilot-instructions.md`.
>
> **JetBrains AI Assistant** is automatically pointed here via `.idea/ai-context.md`.
> A reusable prompt is also available at `prompts/jarvis-standards.md`.
>
> **All other AI assistants** must be manually directed to this file at the start of every session.

### 1. kotlin-android-best-practices.md
Comprehensive guide covering:
- Code style and formatting conventions (Kotlin)
- Naming conventions for packages, classes, functions, constants, and resources
- MVVM architecture and the Repository pattern
- Resource management (strings, colors, dimensions, drawables)
- Null safety and idiomatic Kotlin
- Coroutines and asynchronous code patterns
- Error handling with sealed classes and `Result<T>`
- Documentation standards (KDoc)
- Project structure and feature-based package organization
- Testing conventions (JUnit, MockK, Espresso)
- Performance optimization tips
- Dependency management with the version catalog
- Security best practices for Android

### 2. licenses.md
Full license policy for all project dependencies.

### 3. license-quick-reference.md
Quick-reference checklist for license compliance before adding any dependency.

### 4. design/style-guide.md
The Jarvis visual design system: the "Iron Man / Arc Reactor" dark-tech
aesthetic, color palette and semantic usage rules, typography, spacing
tokens, shape/elevation/component patterns, interaction and motion
patterns, and a pre-submission UI checklist. **MUST** be followed for any
layout, drawable, theme, or style resource changes.

## Markdown File Naming Convention (Non-Negotiable)

All `.md` files in this project **MUST** use:
- **Lowercase only** — no uppercase letters.
- **Hyphens to separate words** — no spaces, underscores, or camelCase.

Example: `kotlin-android-best-practices.md`, `readme.md`, `license-quick-reference.md`

**Exception — numbered spec files:** files under `docs/specs/` intentionally
use a `NNN slug.md` pattern (a three-digit index, a space, then the
kebab-case name, e.g. `002 home-requirements.md`). The space after the
index is load-bearing — `.github/scripts/generate_docs_summary.py` matches
it with `^\d{3}\s` to build `docs/readme.md` and order specs. Do not
"fix" these to hyphens; that would break the generator.

## AI Assistant Instructions

### Context Requirement
**ALL** documents in this standards folder must be read and understood before:
- Writing new code
- Modifying existing code
- Adding features or functionality
- Reviewing code changes
- Making architectural decisions

### Mandatory Compliance
The following must be adhered to at all times:

1. **Standard American English**
   - All text — chat responses, code comments, KDoc, XML comments, string resources, `.md` files,
     and commit messages — MUST be written in Standard American English. No exceptions.
   - Use American English spelling: color (not colour), behavior (not behaviour),
     initialize (not initialise), organize (not organise), license (not licence), etc.

2. **Markdown File Naming**
   - All `.md` files must use lowercase, hyphen-separated names (e.g., `my-file.md`).
   - Never use uppercase, underscores, spaces, or camelCase in markdown filenames.

3. **Code Format**
   - All Kotlin code must follow the [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html).
   - Variable and function names must follow the naming conventions specified in `kotlin-android-best-practices.md`.
   - Exported symbols (public classes, functions) use `PascalCase` / `camelCase` per Kotlin conventions.

4. **Resource Management**
   - No hardcoded strings, colors, or dimensions in layout files or Kotlin source.
   - All values must reside in the appropriate resource files (`strings.xml`, `colors.xml`, `dimens.xml`).

5. **Error Handling**
   - Never swallow exceptions silently.
   - Use sealed classes (`UiState`) or `Result<T>` to represent outcomes.
   - Log errors with context.

6. **Documentation**
   - All public classes, functions, and complex logic must have KDoc comments.
   - Comments must be clear, concise, and written in Standard American English.

7. **Architecture**
   - Follow MVVM with the Repository pattern.
   - Fragments and Activities contain no business logic.
   - ViewModels communicate with repositories, never with data sources directly.

8. **Code Organization**
   - Organize code by **feature** (e.g., `shopping/`, `scanner/`, `baseitems/`).
   - Keep Fragment, ViewModel, Repository, and Model classes together in their feature package.

9. **Testing**
   - Write unit tests for all ViewModels and Repositories.
   - Place unit tests in `src/test/` and instrumentation tests in `src/androidTest/`.

10. **Asset Coherence**
    - All generated assets (icons, backgrounds, UI elements) must follow the "Jarvis / Iron Man" theme.
    - Adhere strictly to the defined color palette: Iron Man Red `#7A0019`, Gold `#F1D56D`, Cyan `#00E5FF`, Dark Tech `#020810`.

### Before Making Changes

AI assistants must:
1. ✅ **Read this file (`docs/standards/readme.md`) before EVERY interaction**
2. ✅ Read and understand all standards documents
3. ✅ Check that proposed changes comply with all guidelines
4. ✅ Ensure code follows Kotlin and Android best practices
5. ✅ Validate that error handling is proper
6. ✅ Verify that documentation is complete
7. ✅ Run tests and lint before finalizing changes

### AI Usage Protocol

**This is NON-NEGOTIABLE:**
- Every AI chat session on this project MUST start by reading `docs/standards/readme.md`.
- Every code change MUST be validated against these standards.
- If an AI assistant does not reference these standards, remind them to do so.
- All code must comply with `kotlin-android-best-practices.md` guidelines.
- No exceptions without explicit approval and documentation.

### Common Commands Reference

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest

# Run lint
./gradlew lint

# Clean build
./gradlew clean
```

## Escalation and Exceptions

If you believe an exception to these standards is necessary:
1. Document the reason clearly
2. Explain why the standard cannot be followed
3. Propose an alternative that maintains code quality
4. Request approval before implementation

## Contact and Questions

For questions about these standards or to propose updates, please contact the team lead or create a discussion in the project repository.

---

**Last Updated:** July 19, 2026
**Status:** Active - Must be followed for all new code and changes
