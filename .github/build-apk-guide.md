# Build APK Workflow Guide

Complete documentation for the Build APK GitHub Actions workflow.

---

## Overview

This workflow automatically builds debug and release APKs whenever code is pushed to `main` or a pull request is created. It also lints the code and runs unit tests.

---

## What It Does

### Build Steps

1. **Checkout Code** — Clones the repository
2. **Set up Java** — Installs Java 21 (Temurin distribution)
3. **Gradle Setup** — Caches dependencies and sets up Gradle
4. **Run Lint** — Checks for code quality issues
5. **Run Unit Tests** — Executes all unit tests
6. **Build Debug APK** — Compiles debug build
7. **Build Release APK** — Compiles release build
8. **Upload Artifacts** — Saves APKs as downloadable artifacts
9. **Report Status** — Generates build summary

---

## Triggers

The workflow runs when:

- **Push to main** — Automatically builds on every merge
- **Pull Request to main** — Validates PR can build successfully
- **Manual trigger** — Via "Run workflow" button in Actions tab

---

## Output Artifacts

### Debug APK
- **File:** `app-debug.apk`
- **Purpose:** Development and testing
- **Retention:** 30 days
- **Download:** From Actions tab after build completes

### Release APK
- **File:** `app-release.apk`
- **Purpose:** Production distribution
- **Retention:** 30 days
- **Download:** From Actions tab after build completes

---

## How to Download APK

### From GitHub Actions

1. Go to GitHub repository → **Actions** tab
2. Click the latest **Build APK** workflow run
3. Scroll to **Artifacts** section
4. Click **debug-apk** or **release-apk** to download
5. Extract the APK from the ZIP file

### Installing the APK

Once you have the APK file, see: **[How to Install Jarvis APK](./how-to-install-apk.md)**

The installation guide covers:
- ✅ Android Studio (emulator)
- ✅ Android Studio (physical device)
- ✅ ADB command line
- ✅ File manager direct install
- ✅ Troubleshooting
- ✅ Verification steps

---

## Build Status

### Success Indicators

✅ **Build succeeded when:**
- All steps complete successfully
- Debug APK is generated
- Release APK is generated
- Both artifacts are uploaded

⚠️ **Warnings (non-blocking):**
- Lint issues may be reported but don't stop build
- Test failures may be reported but don't stop build
- You can still download APK if lint/tests fail

❌ **Build failed when:**
- Compilation errors occur
- Java setup fails
- Gradle initialization fails
- APK generation fails

---

## Troubleshooting

### Build Fails with "Gradle not found"

**Solution:**
- Run: `./gradlew clean` locally
- Commit `gradlew` wrapper files
- Push to main and retry

### Java Version Mismatch

**Solution:**
- Ensure `build.gradle.kts` targets Java 21
- Check `.java-version` file exists
- Update workflow if needed

### APK Not Generated

**Check:**
1. Compilation errors in logs
2. Insufficient disk space
3. Invalid Gradle configuration

### Lint or Test Failures Don't Block Build

**By design:** Lint and test failures are non-blocking (continue-on-error: true)

To make them blocking:
- Edit `.github/workflows/build-apk.yml`
- Remove `continue-on-error: true`
- Commit and push

---

## Configuration

### Change Java Version

Edit `.github/workflows/build-apk.yml`:

```yaml
- name: Set up Java
  uses: actions/setup-java@v4
  with:
    java-version: '21'  # Change this version
    distribution: 'temurin'
```

### Change Artifact Retention

Edit `.github/workflows/build-apk.yml`:

```yaml
- name: Upload debug APK
  with:
    retention-days: 30  # Change this
```

### Build Only Debug APK

Remove or comment out release build step:

```yaml
# - name: Build release APK
#   run: ./gradlew assembleRelease --stacktrace
```

---

## Performance

**Typical build times:**
- First run: 5-8 minutes (downloading dependencies)
- Subsequent runs: 2-3 minutes (cached dependencies)

**Optimization tips:**
- Gradle dependency cache is enabled
- Java distribution caching reduces setup time
- Parallel builds are configured in Gradle

---

## Security

**Best Practices:**
- No credentials stored in workflow
- Debug APK is unsigned (development only)
- Release APK needs signing for distribution
- Artifacts auto-deleted after 30 days

**For Production Release:**
- Set up keystore signing
- Use GitHub Secrets for credentials
- Configure release signing in build.gradle.kts

---

## Manual Trigger

**To manually build APK:**

1. Go to GitHub → **Actions** tab
2. Select **Build APK** workflow
3. Click **Run workflow**
4. Click **Run workflow** button
5. Wait for build to complete

No additional parameters needed.

---

## Integration with Other Workflows

This workflow:
- ✅ Runs independently of specs-to-issues automation
- ✅ Builds on every push to main
- ✅ Works alongside PR validation
- ✅ Can be triggered manually anytime

---

## Next Steps

### First Build
1. Ensure `build.gradle.kts` is valid
2. Check `gradlew` and `gradlew.bat` are committed
3. Push to main or PR to trigger build
4. Wait 2-3 minutes for completion

### After First Build
1. Download debug APK from artifacts
2. Install and test on Android device/emulator
3. See: [How to Install Jarvis APK](./how-to-install-apk.md)
4. Verify app works correctly
5. Download release APK when ready for production

### Production Release
1. Set up keystore signing
2. Configure release signing credentials
3. Use release APK for Play Store distribution

---

## Artifacts Retention Policy

| Artifact    | Retention | Purpose                  |
|-------------|-----------|--------------------------|
| Debug APK   | 30 days   | Testing and development  |
| Release APK | 30 days   | Distribution preparation |

To change retention, edit `retention-days` in workflow.

---

**Version:** 1.0  
**Status:** ✅ Active  
**Last Updated:** March 7, 2026  
**Created:** March 7, 2026

