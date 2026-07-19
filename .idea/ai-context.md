# JetBrains AI Assistant — Jarvis Project Context

> This file is automatically loaded by the JetBrains AI Assistant (Android Studio / IntelliJ IDEA)
> as project-level context for every AI chat interaction in this repository.

---

## ⚠️ MANDATORY — Read Before Every Interaction

**Before performing any work on this project, you MUST read the following files in order:**

1. `docs/standards/readme.md` ← **Primary instructions — single source of truth**
2. `docs/standards/kotlin-android-best-practices.md`
3. `docs/standards/licenses.md`
4. `docs/standards/license-quick-reference.md`
5. `docs/standards/design/style-guide.md` (for any UI, layout, drawable, theme, or style change)

The file at `docs/standards/readme.md` is the **canonical source of truth**
for all AI assistant behavior on this project. Every rule, checklist, and workflow defined there
is **non-negotiable**.

---

## Quick Reference — Six Core Rules

1. **Always follow best practices** — Kotlin conventions, Clean Architecture, secure coding.
2. **Use resource files for all assets and strings** — no hardcoded values anywhere.
3. **Use design patterns when applicable** — MVVM + Repository is mandatory on every screen.
4. **Code must be very maintainable** — readable, KDoc-documented, modular, and testable.
5. **All generated assets must be coherent** — adhere strictly to the Jarvis / Iron Man theme.
6. **All markdown files must be lowercase kebab-case** — e.g., `my-file.md`.

---

## Color Palette (Non-Negotiable)

| Token                | Hex       | Usage                                  |
|----------------------|-----------|----------------------------------------|
| Iron Man Red         | `#7A0019` | Primary actions, active states         |
| Iron Man Gold (Dark) | `#F1D56D` | Accent, active text / icon highlights  |
| Cyan                 | `#00E5FF` | Secondary text, inactive states        |
| Dark Tech            | `#020810` | Backgrounds                            |

---

## Architecture (Mandatory)

- **MVVM + Repository** on every screen.
- No business logic in Fragments or Activities.
- All values in resource files (`strings.xml`, `colors.xml`, `dimens.xml`).
- Use `ViewBinding` — never `findViewById`.
- Use `Dispatchers.IO` for all I/O operations.
- Use `viewModelScope.launch` for all ViewModel coroutines.

---

## Project Structure

- Organize by **feature**, not by type: `groceries/`, `scanner/`, `baseitems/`, etc.
- Each feature package contains its Fragment, ViewModel, Repository, and model classes.

---

## Baseline Commands

```bash
./gradlew lint          # Lint
./gradlew test          # Unit tests
./gradlew assembleDebug # Build
./gradlew clean         # Clean
```

---

**Full instructions:** `docs/standards/ai-assistant-instructions.md`

**Version:** 1.1 | **Last Updated:** March 7, 2026 | **Status:** ACTIVE
