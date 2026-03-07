# GitHub Copilot Instructions — Jarvis Project

> This file is automatically loaded by GitHub Copilot for every chat interaction in this
> repository. It redirects Copilot to the canonical AI assistant instructions.

---

## ⚠️ MANDATORY — Read Before Every Interaction

**Before performing any work on this project, you MUST read the following files in order:**

1. [`docs/standards/readme.md`](../docs/standards/readme.md)
2. [`docs/standards/ai-assistant-instructions.md`](../docs/standards/ai-assistant-instructions.md) ← **Primary instructions**
3. [`docs/standards/kotlin-android-best-practices.md`](../docs/standards/kotlin-android-best-practices.md)
4. [`docs/standards/licenses.md`](../docs/standards/licenses.md)
5. [`docs/standards/license-quick-reference.md`](../docs/standards/license-quick-reference.md)

The file at `docs/standards/ai-assistant-instructions.md` is the **single source of truth**
for all AI assistant behavior on this project.

---

## Quick Reference — Six Core Rules

1. **Always follow best practices** — Kotlin conventions, Clean Architecture, secure coding.
2. **Use resource files for all assets and strings** — no hardcoded values anywhere.
3. **Use design patterns when applicable** — MVVM + Repository is mandatory.
4. **Code must be very maintainable** — readable, KDoc-documented, modular, and testable.
5. **All generated assets must be coherent** — adhere to the Jarvis / Iron Man theme.
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

---

## Baseline Commands

```bash
./gradlew lint          # Lint
./gradlew test          # Unit tests
./gradlew assembleDebug # Build
./gradlew clean         # Clean
```

---

**Full instructions:** [`docs/standards/ai-assistant-instructions.md`](../docs/standards/ai-assistant-instructions.md)

**Version:** 1.1 | **Last Updated:** March 7, 2026 | **Status:** ACTIVE
