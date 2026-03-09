# AI Assistant Instructions — Jarvis Project

> **Canonical source of truth for all AI assistants on this project.**
> Every AI assistant (GitHub Copilot, Gemini, ChatGPT, Claude, or any other) **MUST** read and
> follow this document before performing any work on the Jarvis project.

---

## ⚠️ CRITICAL PROTOCOL

**EVERY AI assistant interaction with the Jarvis project MUST:**
1. Read `docs/standards/readme.md` FIRST.
2. Read this file (`docs/standards/ai-assistant-instructions.md`) SECOND.
3. Read `docs/standards/kotlin-android-best-practices.md` THIRD.
4. Always review `docs/specs/` for additional feature and product context before implementation.
5. If working on UI/UX, read `docs/style-guide/ux-style-guide.md` for visual implementation details.
6. Only then proceed with any work.

**Failure to follow this protocol is unacceptable.**

---

## Core Rules (Non-Negotiable)

These five rules are the foundation of all work on the Jarvis project. Every other section in
this document expands on them.

### Rule 1 — Always Follow Best Practices
- Adhere to official [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
  and [Android best practices](https://developer.android.com/guide/practices).
- Apply Clean Architecture: maintain a clear separation of concerns across the **UI**, **Domain**,
  and **Data** layers.
- Avoid security anti-patterns: never hardcode secrets, always validate inputs, and use only
  secure, license-compliant dependencies.
- See `docs/standards/kotlin-android-best-practices.md` for the full set of conventions.

### Rule 2 — Use Resource Files for All Assets and Strings
- **No hardcoded values** anywhere in Kotlin source or layout XML files.
- All strings → `res/values/strings.xml`
- All colors → `res/values/colors.xml`
- All dimensions → `res/values/dimens.xml`
- All drawables and mipmaps → appropriate `res/drawable/` or `res/mipmap/` directories.
- All styles and themes → `res/values/themes.xml` / `res/values/styles.xml`.

### Rule 3 — Use Design Patterns When Applicable
- Apply standard design patterns (MVVM, Repository, Singleton, Factory, Observer) where they
  solve a specific, real problem.
- **Avoid over-engineering**: use patterns to simplify and decouple, not to add unnecessary
  complexity.
- MVVM with the Repository pattern is **mandatory** for all screens.

### Rule 4 — Code Must Be Very Maintainable
- Write self-documenting code: use meaningful, descriptive names for all variables, functions,
  and classes.
- Add **KDoc** comments to all public classes, functions, and complex logic.
- Break large functions and classes into smaller, focused, reusable components.
- Write code that is easy to unit test — favor dependency injection over hard-coded dependencies.
- Organize code by **feature** (e.g., `shopping/`, `scanner/`, `baseitems/`), not by type.

### Rule 5 — All Generated Assets Must Be Coherent
- All generated assets (icons, backgrounds, UI elements) **must** adhere to the
  **Jarvis / Iron Man** theme.
- Follow the color palette, typography, spacing, and component specifications defined in
  `docs/style-guide/ux-style-guide.md`.
- Maintain a futuristic, high-tech, geometric visual style for all elements.

### Rule 6 — Markdown File Naming (Non-Negotiable)
All markdown files in this project **MUST** follow these naming rules:
- **Lowercase only** — no uppercase letters anywhere in the filename.
- **Words separated by hyphens** — no spaces, underscores, or camelCase.
- **Extension:** always `.md` (lowercase).

| ✅ Correct                          | ❌ Incorrect                         |
|------------------------------------|-------------------------------------|
| `readme.md`                        | `README.md`                         |
| `ai-assistant-instructions.md`     | `AI_ASSISTANT_INSTRUCTIONS.md`      |
| `kotlin-android-best-practices.md` | `kotlin_android_best_practices.md`  |
| `license-quick-reference.md`       | `LICENSE_QUICK_REFERENCE.md`        |
| `copilot-compliance-checklist.md`  | `COPILOT_COMPLIANCE_CHECKLIST.md`   |

This rule applies to every `.md` file in the project, including files in `.github/`, `.idea/`,
`prompts/`, `docs/`, and the project root.

### Rule 7 — Layout File Naming Convention (Non-Negotiable)
All layout filenames in `res/layout/` **must** use lowercase snake_case and begin with a scope prefix.
This convention is generic and applies to all current and future features.

#### Required Prefix Strategy
- `shared_` → components shared across the entire app
- `<feature>_` → components shared within a feature
- `<feature>_<subfeature>_` → components used only by a subfeature/screen

#### Examples (Shopping)
- `shared_loading_state.xml` (app-wide shared)
- `shopping_tab_bar.xml` (shared by Shopping List and Replenish)
- `shopping_list_item_row.xml` (Shopping List only)
- `shopping_replenish_item_row.xml` (Shopping Replenish only)

#### Generic Examples (Future Features)
- `inventory_card.xml` (feature-shared within Inventory)
- `inventory_scan_camera_overlay.xml` (Inventory Scan only)
- `network_status_panel.xml` (feature-shared within Network)

#### Additional Constraints
- Use lowercase only.
- Use underscores only (no spaces, no hyphens, no camelCase).
- Keep names explicit and descriptive.
- Do not create new layouts that violate this scheme.
- When modifying or replacing existing layouts, migrate names to this scheme when practical.

---

## Pre-Work Checklist

Before writing **any** code, adding **any** feature, or making **any** change, verify:

### Standard Documentation (Required Reading)
- [ ] I have read `docs/standards/readme.md`.
- [ ] I have read `docs/standards/ai-assistant-instructions.md` (this file).
- [ ] I have read `docs/standards/kotlin-android-best-practices.md`.
- [ ] I have read `docs/standards/licenses.md`.
- [ ] I have read `docs/standards/license-quick-reference.md`.
- [ ] I have reviewed `docs/specs/` for additional context relevant to the task.
- [ ] I have reviewed `docs/style-guide/ux-style-guide.md` for UI implementation details.

### Relevant Feature Documentation
- [ ] If working on **UI / Fragments** — read all docs above plus `docs/style-guide/ux-style-guide.md` plus any feature-specific docs in `docs/specs/`.
- [ ] If working on **data / repository** — read all docs above plus any feature-specific docs in `docs/specs/`.
- [ ] If working on **scanning / camera** — read all docs above plus any feature-specific docs in `docs/specs/`.
- [ ] If working on **navigation** — read all docs above plus any feature-specific docs in `docs/specs/`.

### Compliance Verification
- [ ] I understand all mandatory compliance requirements.
- [ ] I will write all text — comments, KDoc, docs, strings, and chat — in Standard American English.
- [ ] Any new `.md` file I create uses a lowercase, hyphen-separated filename.
- [ ] I am prepared to follow Kotlin and Android best practices.
- [ ] I have verified the license of every dependency I intend to add.
- [ ] I will validate all changes before submission.

---

## Mandatory Requirements

### Standard American English — Rule 0 (Non-Negotiable)

**All text produced on this project MUST be written in standard American English. No exceptions.**

This applies universally to every piece of text the AI assistant produces, including but not
limited to:

| Context | Examples |
|---|---|
| **Chat responses** | All replies to the developer |
| **Inline code comments** | `//` and `/* */` comments in Kotlin source |
| **KDoc** | All `/** */` documentation blocks, `@param`, `@return`, `@throws` tags |
| **XML comments** | Comments inside layout, manifest, navigation, and resource files |
| **String resources** | All text inside `strings.xml` |
| **Documentation files** | All `.md` files, including this one |
| **Commit messages** | All Git commit titles and bodies |
| **Variable / function names** | Names must use correct English words (no abbreviations or slang) |

#### American English Spelling (Non-Negotiable)

Use American English spelling at all times. British, Australian, or other regional variants are
**not acceptable**.

| ✅ American English (Required) | ❌ Other Variants (Forbidden) |
|---|---|
| color | colour |
| behavior | behaviour |
| center | centre |
| initialize | initialise |
| organize | organise |
| recognize | recognise |
| license | licence |
| canceled | cancelled |
| program | programme |
| favorite | favourite |
| gray | grey |
| analyze | analyse |
| serialize | serialise |
| customize | customise |
| synchronize | synchronise |

> **Note:** Android resource files and Gradle use American English by convention
> (e.g., `colors.xml`, `dimens.xml`). All project files must follow the same standard.

#### Language Quality Requirements

- Use correct grammar, spelling, and punctuation in every output.
- Write clear, professional, and complete sentences — no fragments, no run-ons.
- Avoid slang, shorthand, informal abbreviations, and filler words.
- Use consistent technical terminology throughout code, comments, and documentation.
- If user input contains spelling or grammar mistakes, respond in correct Standard American
  English while fully preserving the user's intent.

#### American English QA Checklist (Required Before Every Response)

- [ ] All spelling uses American English variants (color, behavior, initialize, etc.).
- [ ] Grammar is correct — no fragments or run-on sentences.
- [ ] Punctuation and capitalization are correct.
- [ ] Wording is clear, concise, and professional.
- [ ] Technical terms are used consistently throughout.
- [ ] All code comments and KDoc are in Standard American English.
- [ ] All string resources are written in Standard American English.

### Code Quality
Every code change must meet these criteria:
- Passes Android Studio lint: `./gradlew lint`.
- Follows Kotlin naming conventions (see `kotlin-android-best-practices.md`).
- Errors are handled using sealed classes or `Result<T>` — never swallowed silently.
- Unit tests are added or updated for all behavior changes.
- All public classes and functions have KDoc comments.

### Architecture
- **MVVM + Repository** is mandatory for all screens.
- Fragments and Activities **contain no business logic**.
- ViewModels communicate with repositories only — never with data sources directly.
- Use `viewModelScope.launch` for ViewModel coroutines; use `Dispatchers.IO` for I/O work.
- Represent UI state with sealed classes:

```kotlin
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

### Resource File Compliance (Non-Negotiable)
No hardcoded strings, colors, or dimensions are permitted anywhere in source code or layout XML.

| Value type  | File                          |
|-------------|-------------------------------|
| Strings     | `res/values/strings.xml`      |
| Colors      | `res/values/colors.xml`       |
| Dimensions  | `res/values/dimens.xml`       |
| Drawables   | `res/drawable/`               |

### Dependency and License Compliance (Non-Negotiable)

**Allowed licenses:** MIT · Apache 2.0 · BSD (2 / 3 clause) · ISC · Unlicense / CC0

**Forbidden licenses:** GPL · AGPL · LGPL · SSPL · Proprietary / unknown

See `docs/standards/licenses.md` for the full approved package list and verification procedure.

### Performance and Reliability (Non-Negotiable)
- Never perform network or database operations on the main thread.
- Use `Dispatchers.IO` for all I/O-bound work.
- Use `DiffUtil` in `RecyclerView` adapters.
- Use `ViewBinding` — **never** `findViewById`.
- Ensure all coroutines are canceled on the appropriate lifecycle end to prevent memory leaks.
- Flag any potential deadlock or memory-leak risk explicitly, even if unconfirmed.

### Documentation
Every public class and function must have a KDoc comment:

```kotlin
/**
 * Loads the shopping items and updates the UI state.
 *
 * @param forceRefresh If true, bypasses the cache and fetches fresh data.
 */
fun loadShoppingItems(forceRefresh: Boolean = false)
```

---

## Workflow

### When Starting Work
1. Read all relevant standards documents.
2. Fully understand the request.
3. Plan the implementation with standards in mind.
4. Implement.
5. Validate (lint, tests).
6. Confirm compliance before submitting.

### When Making Changes
- Confirm no hardcoded values are introduced.
- Run `./gradlew lint`.
- **Update spec checkboxes** — when implementing a feature, mark the corresponding requirement checkbox as complete (`[X]`) in the relevant `docs/specs/` file.
- Run `./gradlew test`.
- Ensure all public symbols have KDoc.
- Run benchmarks or coverage reports **only if explicitly requested**.

---

## Baseline Validation Commands

```bash
# Lint
./gradlew lint

# Unit tests
./gradlew test

# Build
./gradlew assembleDebug

# Clean
./gradlew clean
```

---

## Summary — Always / Never

### Always
1. Write all text in Standard American English — chat, comments, KDoc, docs, strings, and commit messages.
2. Name all markdown files in lowercase with hyphens (e.g., `my-file.md`).
3. Always review `docs/specs/` for additional context before implementation.
4. Read standards first.
5. Validate against guidelines before every code change.
6. Follow Kotlin and Android best practices.
7. Use resource files — no hardcoded values.
8. Follow MVVM + Repository architecture.
9. Document all public behavior with KDoc.
10. Handle errors with sealed classes or `Result<T>`.
11. Test all changed behavior.
12. Verify approved licenses before adding any dependency.
13. Adhere to the Jarvis / Iron Man visual theme.
14. Review all long-lived operations for memory leaks.

### Never
1. Use non-American English spelling (colour, behaviour, initialise, organise, etc.).
2. Name a markdown file with uppercase letters, spaces, or underscores.
3. Put business logic in a Fragment or Activity.
4. Use `!!` (not-null assertion) without explicit justification.
5. Swallow exceptions silently.
6. Hardcode any string, color, or dimension.
7. Add a GPL, AGPL, LGPL, SSPL, or proprietary dependency.
8. Skip KDoc on public classes or functions.
9. Skip tests for ViewModel or Repository logic.
10. Perform I/O on the main thread.
11. Use `findViewById` — use `ViewBinding` instead.
12. Run benchmarks or coverage unless explicitly asked.

---

**Version:** 3.4
**Last Updated:** March 7, 2026
**Status:** ACTIVE — Mandatory for all AI assistants
**Compliance:** Required — no exceptions without explicit approval
