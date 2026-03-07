# GitHub Copilot: Jarvis Project Standards Checklist

## 🤖 AI Assistant Compliance Protocol

**This checklist MUST be completed before EVERY interaction on the Jarvis project.**

---

## ✅ PRE-INTERACTION CHECKLIST

### Step 1: Read Standards (Required)
- [ ] Read `docs/standards/readme.md`
- [ ] Read `docs/standards/ai-assistant-instructions.md`
- [ ] Read `docs/standards/kotlin-android-best-practices.md`
- [ ] Read `docs/standards/licenses.md`
- [ ] Keep `docs/standards/license-quick-reference.md` available

### Step 2: Understand License Requirements
- [ ] Approved licenses: MIT, Apache 2.0, BSD-3-Clause, BSD-2-Clause, ISC, Unlicense
- [ ] Forbidden licenses: GPLv2, GPLv3, AGPL, LGPL, SSPL, Proprietary
- [ ] Pre-approved packages memorized from the recommended list
- [ ] License verification process understood

### Step 3: Verify Project Structure
- [ ] Understand: feature-based package organization (e.g., `shopping/`, `scanner/`, `baseitems/`)
- [ ] Understand: `res/values/` contains all strings, colors, and dimensions
- [ ] Understand: `docs/standards/` contains all policies
- [ ] Understand: MVVM + Repository architecture is mandatory

### Step 4: Ready to Work
- [ ] All standards understood
- [ ] License requirements clear
- [ ] Verification procedures known
- [ ] Red flags identified
- [ ] Ready to ensure compliance

---

## 📋 PRE-CODE CHECKLIST

Before writing ANY code:
- [ ] Understand the requirement completely
- [ ] Check if similar code already exists
- [ ] Plan to follow Kotlin and Android best practices
- [ ] Identify any dependencies needed
- [ ] Prepare to verify dependency licenses
- [ ] Confirm no values will be hardcoded in layout or source files
- [ ] Confirm all text will be written in Standard American English
- [ ] Confirm any new `.md` files will use lowercase, hyphen-separated filenames

---

## 🔐 DEPENDENCY CHECKLIST

Before adding ANY dependency:

**License Verification:**
- [ ] Package has an accessible LICENSE file
- [ ] License is MIT, Apache 2.0, BSD, ISC, or Unlicense
- [ ] NOT GPL, AGPL, LGPL, SSPL, or proprietary
- [ ] Check transitive dependencies with `./gradlew app:dependencies`

**Package Verification:**
- [ ] Package is from the pre-approved list (preferred)
- [ ] Package is actively maintained
- [ ] No known security vulnerabilities
- [ ] Package solves the exact problem

**Approved to Add:**
- [ ] Ready to add to `libs.versions.toml`
- [ ] Ready to reference in `build.gradle.kts`
- [ ] Ready to document in `docs/dependencies.md`

---

## 💻 CODE QUALITY CHECKLIST

Before finalizing ANY code:

**Formatting & Linting:**
- [ ] Code follows [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [ ] Code passes: `./gradlew lint`
- [ ] Code passes: `./gradlew test`
- [ ] No hardcoded strings, colors, or dimensions

**Language & File Naming:**
- [ ] All comments, KDoc, and strings are in Standard American English
- [ ] All spelling uses American English variants (color, behavior, initialize, organize, etc.)
- [ ] Any new `.md` files use lowercase, hyphen-separated filenames

**Architecture:**
- [ ] MVVM pattern followed — no business logic in Fragments or Activities
- [ ] All data access is via a Repository
- [ ] ViewModels use `viewModelScope` for coroutines
- [ ] UI state represented using sealed classes or `StateFlow`

**Resource Compliance:**
- [ ] All strings are in `strings.xml`
- [ ] All colors reference `colors.xml`
- [ ] All dimensions reference `dimens.xml`
- [ ] All drawables follow the naming convention (`ic_`, `bg_`, `img_`)

**Documentation:**
- [ ] All public classes have KDoc comments
- [ ] All public functions have KDoc comments
- [ ] Complex logic is explained with inline comments
- [ ] Comments are clear, complete, and in Standard American English

**Error Handling:**
- [ ] No exceptions are swallowed silently
- [ ] Sealed classes or `Result<T>` used for outcomes
- [ ] Errors are logged with context

**Testing:**
- [ ] ViewModels have unit tests
- [ ] Repositories have unit tests
- [ ] Tests are in `src/test/` (unit) or `src/androidTest/` (instrumentation)

---

## 🚫 RED FLAGS — STOP IF ANY APPLY

❌ **STOP IF ANY OF THESE:**
- GPL-licensed package being added
- License is unknown or unclear
- License is proprietary or experimental
- No LICENSE file is accessible
- Not in pre-approved packages list and license not verified
- Hardcoded string, color, or dimension in layout XML or Kotlin source
- Business logic found in a Fragment or Activity
- `!!` (not-null assertion) used without justification
- No KDoc comment on public classes or functions
- No tests for ViewModel or Repository logic
- Non-American English spelling in any comment, KDoc, string, or `.md` file
- A new `.md` file named with uppercase letters, underscores, or spaces

**IF RED FLAG FOUND:**
1. Stop all work
2. Document the issue
3. Report to the team lead
4. Find an alternative solution
5. Restart with compliance

---

## ✨ COMPLIANCE CONFIRMATION

### Before Submitting Code

Confirm:
- [ ] All standards have been followed
- [ ] All licenses have been verified
- [ ] All errors are handled properly
- [ ] All code is documented with KDoc in Standard American English
- [ ] All tests pass
- [ ] Lint passes: `./gradlew lint`
- [ ] No hardcoded values remain
- [ ] MVVM architecture maintained
- [ ] Jarvis / Iron Man visual theme adhered to
- [ ] All text is in Standard American English
- [ ] All `.md` files use lowercase, hyphen-separated filenames
- [ ] Ready for production

### Response Format

Include in every response about code:
```
📚 STANDARDS REFERENCE
- [Relevant section from kotlin-android-best-practices.md]

✅ COMPLIANCE CHECKLIST
- [x] All standards followed
- [x] Kotlin coding conventions applied
- [x] Standard American English used throughout
- [x] No hardcoded values
- [x] MVVM architecture maintained
- [x] Documentation complete

🔐 LICENSE VERIFICATION (if applicable)
- [x] Licenses verified
- [x] No GPL found
- [x] Using approved packages

📝 IMPLEMENTATION
[Description]
```

---

## 🎯 KEY REMINDERS

**ALWAYS:**
1. ✅ Write all text in Standard American English — chat, comments, KDoc, strings, and commit messages
2. ✅ Name all markdown files in lowercase with hyphens (e.g., `my-file.md`)
3. ✅ Read standards first
4. ✅ Verify licenses before adding dependencies
5. ✅ Use pre-approved packages
6. ✅ Follow Kotlin and Android best practices
7. ✅ Handle errors with sealed classes or `Result<T>`
8. ✅ Document all public symbols with KDoc
9. ✅ Use resource files for all strings, colors, and dimensions
10. ✅ Follow MVVM + Repository architecture
11. ✅ Adhere to the Jarvis / Iron Man visual theme
12. ✅ Run lint and tests before finalizing

**NEVER:**
1. ❌ Use non-American English spelling (colour, behaviour, initialise, organise, etc.)
2. ❌ Name a markdown file with uppercase letters, underscores, or spaces
3. ❌ Skip license verification
4. ❌ Add GPL-licensed packages
5. ❌ Swallow exceptions silently
6. ❌ Skip documentation
7. ❌ Skip testing
8. ❌ Assume licenses are safe
9. ❌ Use proprietary packages
10. ❌ Bypass standards
11. ❌ Hardcode strings, colors, or dimensions
12. ❌ Put business logic in Fragments or Activities

---

## 📞 ESCALATION CONTACTS

If encountering issues:

**GPL or Proprietary License Found:**
- Document the package and license
- Report to the team lead immediately
- Find an alternative from the approved list
- Do NOT proceed with a non-compliant package

**Unclear License:**
- Research on https://opensource.org/licenses/
- Check the package README and LICENSE file
- Ask the team lead for clarification
- Do NOT proceed without clarity

**Not in Pre-Approved List:**
- Verify the license is on the approved list
- If yes, document and proceed
- If no, find an alternative or ask the team lead
- Update the pre-approved list if appropriate

---

## 📊 QUICK STATS

**Approved Licenses:** 5 primary + 4 secondary
- ✅ MIT (Most Common)
- ✅ Apache 2.0 (Enterprise — used by Android SDK, Kotlin, and most AndroidX libraries)
- ✅ BSD-3-Clause (Academic)
- ✅ BSD-2-Clause (Minimal)
- ✅ ISC (Simple)

**Forbidden Licenses:** 6
- ❌ GPLv2, GPLv3, AGPL, LGPL, SSPL, Proprietary

**Pre-Approved Packages:** 25+
- AndroidX UI, Navigation, Room, DataStore, CameraX, ML Kit, Retrofit, OkHttp, Glide, Coil, Hilt, Coroutines, Timber, MockK, Espresso

**Standard Documents:** 6 files
- `readme.md`, `ai-assistant-instructions.md`, `kotlin-android-best-practices.md`, `licenses.md`, `license-quick-reference.md`, `copilot-compliance-checklist.md`

---

## ✅ SIGN-OFF

**I, GitHub Copilot, acknowledge:**

1. ✅ I have read and understand all standards documents
2. ✅ I will write all text in Standard American English — no exceptions
3. ✅ I will name all new `.md` files in lowercase with hyphens
4. ✅ I understand the non-negotiable license requirements
5. ✅ I will verify licenses before adding ANY dependency
6. ✅ I will NEVER add GPL, AGPL, LGPL, SSPL, or proprietary packages
7. ✅ I will ALWAYS use pre-approved packages when available
8. ✅ I will ALWAYS escalate license violations immediately
9. ✅ I will ALWAYS follow Kotlin and Android best practices
10. ✅ I will ALWAYS use resource files — no hardcoded values
11. ✅ I will ALWAYS follow the MVVM + Repository architecture
12. ✅ I will ALWAYS adhere to the Jarvis / Iron Man visual theme
13. ✅ I will ALWAYS document my work with KDoc
14. ✅ I will ALWAYS comply with these standards

**Compliance Status:** ✅ ACTIVE — READY FOR WORK

---

**Version:** 3.0
**Last Updated:** March 7, 2026
**Status:** MANDATORY FOR ALL AI INTERACTIONS
**Enforced By:** GitHub Copilot & Development Team

*This checklist must be completed before every interaction on the Jarvis project.*
