# Quick Reference: License Compliance Checklist

**Print this. Use this. EVERY TIME.**

---

## Before Adding ANY Dependency

### ✅ VERIFICATION CHECKLIST

- [ ] I have read `docs/standards/licenses.md`
- [ ] I have identified the package and its license
- [ ] License is **MIT**, **Apache 2.0**, **BSD**, **ISC**, or **Unlicense**
- [ ] License is NOT GPL, AGPL, LGPL, SSPL, or proprietary
- [ ] Package is from the approved packages list (if available)
- [ ] I checked transitive dependencies via `./gradlew app:dependencies`
- [ ] Package is actively maintained
- [ ] No known security vulnerabilities

### ❌ IMMEDIATE REJECTION IF

- [ ] License is GPL (v2, v3, AGPL) — STOP
- [ ] License is LGPL or SSPL — STOP
- [ ] License is proprietary or unknown — STOP
- [ ] No LICENSE file is accessible — STOP
- [ ] License is unclear or experimental — STOP

---

## Quick License Check

```bash
# 1. Find the package on Maven Repository or GitHub
# https://mvnrepository.com/

# 2. Look for the LICENSE file in the root of the repository

# 3. Cross-reference with approved licenses in docs/standards/licenses.md

# 4. Check transitive dependencies
./gradlew app:dependencies --configuration releaseRuntimeClasspath
```

---

## Approved Licenses (Use These)

| License         | Risk | Use Case                       |
|-----------------|------|--------------------------------|
| 🟢 MIT          | None | General purpose, most flexible |
| 🟢 Apache 2.0   | None | Enterprise, has patent clause  |
| 🟢 BSD-3-Clause | None | Academic, corporate software   |
| 🟢 BSD-2-Clause | None | Minimal restrictions           |
| 🟢 ISC          | None | Simple, lightweight            |
| 🟡 Unlicense    | Low  | Public domain, uncommon        |

---

## Forbidden Licenses (NEVER Use)

| License        | Risk     | Reason                                   |
|----------------|----------|------------------------------------------|
| 🔴 GPLv2       | CRITICAL | Copyleft — must open-source              |
| 🔴 GPLv3       | CRITICAL | Copyleft — must open-source              |
| 🔴 AGPL        | CRITICAL | Network copyleft — must open-source      |
| 🔴 LGPL        | HIGH     | Weak copyleft — dependencies restricted  |
| 🔴 SSPL        | HIGH     | Restrictive — unclear enforcement        |
| 🔴 Proprietary | CRITICAL | Licensing fees or authorization required |
| 🔴 Unknown     | CRITICAL | Cannot verify compliance                 |

---

## Recommended Packages (Pre-Approved)

### Most Common Categories

**Android UI & Navigation:**
- ✅ `androidx.navigation:navigation-fragment-ktx` — Apache 2.0
- ✅ `androidx.recyclerview:recyclerview` — Apache 2.0
- ✅ `com.google.android.material:material` — Apache 2.0

**Networking:**
- ✅ `com.squareup.retrofit2:retrofit` — Apache 2.0
- ✅ `com.squareup.okhttp3:okhttp` — Apache 2.0

**Image Loading:**
- ✅ `com.github.bumptech.glide:glide` — Apache 2.0 / MIT / BSD
- ✅ `io.coil-kt:coil` — Apache 2.0

**Camera & ML:**
- ✅ `androidx.camera:camera-core` — Apache 2.0
- ✅ `com.google.mlkit:barcode-scanning` — Apache 2.0

**Dependency Injection:**
- ✅ `com.google.dagger:hilt-android` — Apache 2.0

**Database:**
- ✅ `androidx.room:room-runtime` — Apache 2.0

**Coroutines:**
- ✅ `org.jetbrains.kotlinx:kotlinx-coroutines-android` — Apache 2.0

**Logging:**
- ✅ `com.jakewharton.timber:timber` — Apache 2.0

**Testing:**
- ✅ `io.mockk:mockk` — Apache 2.0
- ✅ `androidx.test.espresso:espresso-core` — Apache 2.0

**More options:** See `docs/standards/licenses.md`

---

## If You Find a GPL License

**DO THIS IMMEDIATELY:**

1. **STOP** — Do not add the package
2. **DOCUMENT** — Note what you found
3. **REPORT** — Tell the team lead
4. **FIND ALTERNATIVE** — Use the approved packages list
5. **REPLACE** — Update `build.gradle.kts` to use a compliant package
6. **VERIFY** — Run the license check again

---

## When in Doubt

**STOP. DO NOT PROCEED.**

1. Research the license on https://opensource.org/licenses/
2. Read the actual LICENSE file
3. Ask the team lead for clarification
4. Wait for explicit approval
5. Only then proceed

---

## Red Flags 🚩

❌ "It probably won't matter"  
❌ "Let's use it anyway"  
❌ "We can figure it out later"  
❌ "Just this one GPL package"  
❌ "Unknown license but it looks safe"  

**ANY of these = WRONG. Stop and escalate.**

---

## Tools to Help

```bash
# Full dependency tree
./gradlew app:dependencies --configuration releaseRuntimeClasspath

# Search for GPL in the dependency tree
./gradlew app:dependencies --configuration releaseRuntimeClasspath | grep -i "gpl"

# Clean and rebuild
./gradlew clean assembleDebug

# Search Maven Repository for license info
# https://mvnrepository.com/
```

---

## Remember

✅ **This is NON-NEGOTIABLE**   
✅ **Every single dependency matters**   
✅ **Verify BEFORE adding**   
✅ **Use recommended packages when available**   
✅ **Check transitive dependencies**   
✅ **Escalate when unsure**   
✅ **Never use GPL or proprietary licenses**   

---

**Version:** 2.0
**Last Updated:** March 7, 2026
**Status:** MANDATORY - Print this and keep it handy
