 th# License Policy for Jarvis Project

## 🔐 LICENSING STANDARDS (NON-NEGOTIABLE)

**The Jarvis project uses ONLY permissive, open-source licenses. All dependencies MUST comply.**

---

## ✅ APPROVED LICENSES

### Tier 1 - Highly Recommended (Use These First)

| License          | Restrictiveness        | Best For                      | Example Projects               |
|------------------|------------------------|-------------------------------|--------------------------------|
| **MIT**          | Minimal                | General use, most packages    | Retrofit, Glide, Gson          |
| **Apache 2.0**   | Low with patent clause | Enterprise, patent protection | Android SDK, Kotlin, Hilt      |
| **BSD-3-Clause** | Low                    | Academic, enterprise          | Various Android libraries      |
| **BSD-2-Clause** | Very Low               | Simple permissive use         | Various utility libraries      |
| **ISC**          | Minimal                | Lightweight, simple           | Various open-source tools      |

### Tier 2 - Acceptable (If Tier 1 Not Available)

| License       | Notes                      |
|---------------|----------------------------|
| **Unlicense** | Public domain equivalent   |
| **CC0 1.0**   | Public domain dedication   |
| **WTFPL**     | Very permissive (informal) |
| **Zlib**      | Permissive for data        |

---

## ❌ FORBIDDEN LICENSES

### Copyleft Licenses (NEVER Use)

| License   | Why Forbidden                   | Risk                             |
|-----------|---------------------------------|----------------------------------|
| **GPLv2** | Requires source sharing         | Must open-source our code        |
| **GPLv3** | Copyleft with more restrictions | Must open-source our code        |
| **AGPL**  | Network copyleft                | Must open-source for network use |
| **LGPL**  | Weak copyleft                   | Dependency restrictions          |
| **SSPL**  | Restrictive, controversial      | License violations               |

### Proprietary Licenses (NEVER Use)

- Commercial licenses with fees
- Proprietary software licenses
- Custom restrictive licenses
- Licenses requiring payment or authorization

### Experimental/Unstable (NEVER Use)

- Pre-release licenses
- Deprecated licenses
- Custom-made licenses
- Licenses with "experimental" status

---

## 📋 APPROVED PACKAGES BY CATEGORY

### Android UI & Navigation

**✅ Approved**
- `androidx.fragment:fragment-ktx` - Apache 2.0
- `androidx.navigation:navigation-fragment-ktx` - Apache 2.0
- `androidx.navigation:navigation-ui-ktx` - Apache 2.0
- `androidx.recyclerview:recyclerview` - Apache 2.0
- `com.google.android.material:material` - Apache 2.0
- `androidx.constraintlayout:constraintlayout` - Apache 2.0

**❌ Blocked**
- Any UI library with GPL dependencies

### Networking & API

**✅ Approved**
- `com.squareup.retrofit2:retrofit` - Apache 2.0
- `com.squareup.okhttp3:okhttp` - Apache 2.0
- `com.squareup.okhttp3:logging-interceptor` - Apache 2.0
- `com.squareup.moshi:moshi` - Apache 2.0
- `com.google.code.gson:gson` - Apache 2.0

**❌ Blocked**
- Packages with GPL dependencies

### Image Loading

**✅ Approved**
- `com.github.bumptech.glide:glide` - BSD, MIT, Apache 2.0
- `io.coil-kt:coil` - Apache 2.0

**❌ Blocked**
- GPL-licensed image libraries

### Camera & Barcode Scanning

**✅ Approved**
- `androidx.camera:camera-core` - Apache 2.0
- `androidx.camera:camera-camera2` - Apache 2.0
- `androidx.camera:camera-lifecycle` - Apache 2.0
- `androidx.camera:camera-view` - Apache 2.0
- `com.google.mlkit:barcode-scanning` - Apache 2.0
- `com.google.mlkit:text-recognition` - Apache 2.0

**❌ Blocked**
- GPL-licensed camera or ML libraries

### Dependency Injection

**✅ Approved**
- `com.google.dagger:hilt-android` - Apache 2.0
- `androidx.hilt:hilt-navigation-fragment` - Apache 2.0

**❌ Blocked**
- DI frameworks with GPL dependencies

### Database & Storage

**✅ Approved**
- `androidx.room:room-runtime` - Apache 2.0
- `androidx.room:room-ktx` - Apache 2.0
- `androidx.datastore:datastore-preferences` - Apache 2.0
- `androidx.security:security-crypto` - Apache 2.0

**❌ Blocked**
- GPL-licensed database libraries

### Asynchronous Programming

**✅ Approved**
- `org.jetbrains.kotlinx:kotlinx-coroutines-android` - Apache 2.0
- `org.jetbrains.kotlinx:kotlinx-coroutines-core` - Apache 2.0
- `androidx.lifecycle:lifecycle-viewmodel-ktx` - Apache 2.0
- `androidx.lifecycle:lifecycle-runtime-ktx` - Apache 2.0

**❌ Blocked**
- Concurrency libraries with GPL dependencies

### Testing & Mocking

**✅ Approved**
- `junit:junit` - Eclipse Public License 1.0 (acceptable for test scope)
- `androidx.test.ext:junit` - Apache 2.0
- `androidx.test.espresso:espresso-core` - Apache 2.0
- `io.mockk:mockk` - Apache 2.0
- `org.jetbrains.kotlinx:kotlinx-coroutines-test` - Apache 2.0

**❌ Blocked**
- GPL-licensed testing frameworks

### Logging

**✅ Approved**
- `com.jakewharton.timber:timber` - Apache 2.0
- Android's built-in `android.util.Log` - Apache 2.0

**❌ Blocked**
- Logging libraries with GPL dependencies

---

## 🔍 HOW TO VERIFY LICENSES

### Step 1: Check Repository

```
# Navigate to the library's GitHub/Maven repository
# Look for the LICENSE file in the root directory
# Read the license text
```

### Step 2: Use Gradle Tools

```bash
# Check declared dependencies
./gradlew dependencies

# Inspect a specific configuration
./gradlew app:dependencies --configuration releaseRuntimeClasspath

# Verify the build
./gradlew assembleDebug
```

### Step 3: Check Online Databases

- https://opensource.org/licenses/
- https://mvnrepository.com/ (shows license per artifact)
- https://choosealicense.com/
- https://spdx.org/licenses/

### Step 4: Inspect Transitive Dependencies

```bash
# Full dependency tree
./gradlew app:dependencies --configuration releaseRuntimeClasspath | grep -i "gpl"
```

### Step 5: When in Doubt

**DO NOT add the package.** Instead:
1. Document the uncertainty
2. Request team lead review
3. Find an alternative package
4. Wait for explicit approval

---

## 📝 DEPENDENCY TRACKING

### Required Documentation

Every significant dependency must be documented in `docs/DEPENDENCIES.md`:

```markdown
| Package                          | Version | License    | Purpose          | Added    | Notes                     |
|----------------------------------|---------|------------|------------------|----------|---------------------------|
| com.squareup.retrofit2:retrofit  | 2.9.0   | Apache 2.0 | HTTP networking  | 2026-03  | Actively maintained       |
| com.github.bumptech.glide:glide  | 4.16.0  | Apache 2.0 | Image loading    | 2026-03  | Recommended by community  |
```

---

## 🚨 AUDIT PROCEDURES

### Periodic License Audit

```bash
# Export all dependencies
./gradlew app:dependencies --configuration releaseRuntimeClasspath > /tmp/deps.txt

# Check for GPL
grep -i "gpl" /tmp/deps.txt && echo "GPL FOUND - ALERT!" || echo "✅ No GPL"
```

### CI/CD License Checks

Include in your pipeline:

```yaml
# Pseudo-code for CI/CD
- name: Check Licenses
  run: |
    ./gradlew app:dependencies --configuration releaseRuntimeClasspath | grep -Ei "gpl|proprietary" && exit 1 || echo "✅ Licenses OK"
    ./gradlew assembleDebug
```

---

## 📧 ESCALATION PROCEDURE

### If You Find a License Violation

**Immediate Action Required:**

1. **STOP all work** on that dependency
2. **Document the issue:**
   - Package name and version
   - License found
   - Why it is problematic
3. **Report to team lead** immediately
4. **Find alternative** package from the approved list
5. **Remove the problematic package** from `build.gradle.kts`
6. **Replace with an approved alternative**

### If You're Unsure

**DO NOT GUESS. Instead:**

1. Research the license thoroughly
2. Check multiple sources
3. Read the actual LICENSE file
4. Ask for clarification from the team lead
5. Wait for explicit approval before proceeding

---

## 🎯 BEST PRACTICES

### Do's ✅

- ✅ Always check licenses BEFORE adding a dependency
- ✅ Prefer AndroidX and Google-provided libraries when available
- ✅ Use libraries from the approved list
- ✅ Document all major dependencies
- ✅ Run periodic license audits
- ✅ Keep `libs.versions.toml` clean and up to date
- ✅ Review transitive dependencies

### Don'ts ❌

- ❌ Don't add packages with unclear licenses
- ❌ Don't assume a license is permissive
- ❌ Don't ignore GPL licenses "just this once"
- ❌ Don't skip license verification
- ❌ Don't add GPL-licensed packages
- ❌ Don't use proprietary packages
- ❌ Don't commit `build.gradle.kts` changes without license review

---

## 📚 RESOURCES

### Official License Information

- https://opensource.org/licenses/ - Official open-source licenses
- https://choosealicense.com/ - License picker and guide
- https://tldrlegal.com/ - License summaries in plain English
- https://spdx.org/licenses/ - SPDX license list

### Android/Kotlin-Specific Resources

- https://developer.android.com/studio/projects/android-library
- https://mvnrepository.com/ - Maven/Gradle package information
- https://kotlinlang.org/docs/coding-conventions.html

### License Compliance Tools

- `gradle-license-plugin` - Gradle plugin to check Android project licenses
- GitHub's license detection - Built into GitHub
- FOSSA / WhiteSource - Commercial compliance tools

---

## ✋ FINAL REMINDER

**This is not a suggestion. This is policy.**

Every dependency added to Jarvis:
- ✅ MUST have MIT, Apache 2.0, BSD, ISC, or Unlicense
- ✅ MUST be from the approved packages list
- ✅ MUST be verified before adding
- ✅ MUST be documented
- ❌ MUST NOT be GPL, AGPL, LGPL, SSPL, or proprietary
- ❌ MUST NOT bypass this verification process

**Non-compliance is a blocking issue.**

---

**Version:** 2.0
**Last Updated:** March 7, 2026
**Status:** ACTIVE - MANDATORY POLICY
**Enforced By:** AI Assistants and Development Team
