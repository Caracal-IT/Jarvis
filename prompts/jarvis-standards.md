---
name: Jarvis Project — AI Assistant Instructions
description: >
  Load this prompt at the start of every AI session in this project to ensure compliance
  with the Jarvis coding standards. References the canonical instruction file.
---

Before performing any work on this project, read and apply the following standards documents
in order:

1. `docs/standards/readme.md` — Project standards overview.
2. `docs/standards/ai-assistant-instructions.md` — **Canonical source of truth. Non-negotiable.**
3. `docs/standards/kotlin-android-best-practices.md` — Kotlin and Android conventions.
4. `docs/standards/licenses.md` — Dependency license policy.
5. `docs/standards/license-quick-reference.md` — License quick-reference checklist.

Apply every rule in `docs/standards/ai-assistant-instructions.md` to all code, responses,
comments, and documentation produced in this session.

Key non-negotiable rules (full details in the file above):

- Write all text in Standard American English — chat, comments, KDoc, strings, and commit messages.
- All markdown files must use lowercase, hyphen-separated filenames (e.g., `my-file.md`).
- MVVM + Repository architecture on every screen.
- No business logic in Fragments or Activities.
- No hardcoded strings, colors, or dimensions — use resource files exclusively.
- Use `ViewBinding`; never use `findViewById`.
- Use `Dispatchers.IO` for all I/O operations.
- All public classes and functions must have KDoc comments.
- Handle errors with sealed classes or `Result<T>` — never swallow exceptions.
- All visual assets must follow the Jarvis / Iron Man theme and color palette:
  - Iron Man Red `#7A0019` · Gold `#F1D56D` · Cyan `#00E5FF` · Dark Tech `#020810`
- Only use permissive licenses (MIT, Apache 2.0, BSD, ISC, Unlicense).
- Never add GPL, AGPL, LGPL, SSPL, or proprietary dependencies.

