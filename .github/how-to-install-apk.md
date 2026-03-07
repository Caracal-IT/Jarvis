# How to Install Jarvis APK

Step-by-step guide to install the Jarvis Android application from an APK file.

---

## Prerequisites

Before installing, ensure you have:

- ✅ Android device or emulator running Android 12 or higher
- ✅ At least 100 MB of free storage space
- ✅ APK file downloaded from GitHub Actions
- ✅ USB cable (for physical device installation)

---

## Installation Methods

Choose the method that works best for you:

1. [Android Studio (Emulator)](#android-studio-emulator)
2. [Android Studio (Physical Device)](#android-studio-physical-device)
3. [ADB Command Line](#adb-command-line)
4. [File Manager (Direct Install)](#file-manager-direct-install)
5. [Android Device Portal](#android-device-portal)

---

## Android Studio (Emulator)

**Easiest method for testing on emulator.**

### Steps

1. **Open Android Studio**
   - Launch Android Studio on your computer

2. **Open Device Manager**
   - Click **Device Manager** in the right sidebar
   - Or: **Tools → Device Manager**

3. **Start an Emulator**
   - Select an emulator (e.g., Pixel 6)
   - Click the play button to launch
   - Wait for emulator to fully start

4. **Open the APK**
   - Go to **File → Profile or Debug APK**
   - Select the APK file you downloaded
   - Click **OK**

5. **Wait for Installation**
   - Android Studio installs and launches the app
   - App appears on emulator home screen

6. **Launch the App**
   - Tap the **Jarvis** icon on emulator
   - App starts and is ready to use

---

## Android Studio (Physical Device)

**For testing on your actual Android phone or tablet.**

### Prerequisites

- USB cable connected to computer
- USB debugging enabled on device

### Enable USB Debugging

**On your Android device:**

1. Open **Settings**
2. Scroll to **About Phone**
3. Find **Build Number**
4. Tap **Build Number** 7 times quickly
5. Go back to **Settings**
6. Open **Developer Options** (now visible)
7. Enable **USB Debugging**
8. Tap **OK** to confirm

### Installation Steps

1. **Connect Device**
   - Plug in USB cable to computer
   - Device screen: Tap **Allow** to allow debugging

2. **Open Android Studio**
   - Launch Android Studio

3. **Verify Device is Connected**
   - Go to **Tools → Device Manager**
   - Your device should appear in the list

4. **Open the APK**
   - Go to **File → Profile or Debug APK**
   - Select the debug APK file
   - Click **OK**

5. **Select Target Device**
   - Choose your connected device
   - Click **OK**

6. **Wait for Installation**
   - Android Studio installs APK to device
   - May take 1-2 minutes

7. **Launch the App**
   - App appears on device home screen
   - Tap **Jarvis** icon to open

---

## ADB Command Line

**For advanced users comfortable with command line.**

### Prerequisites

- ADB (Android Debug Bridge) installed
- Android device connected via USB or WiFi
- USB debugging enabled

### Installation Steps

1. **Open Terminal/Command Prompt**
   - Windows: `cmd` or `PowerShell`
   - macOS/Linux: Terminal

2. **Verify ADB is Installed**
   ```bash
   adb version
   ```

3. **Verify Device is Connected**
   ```bash
   adb devices
   ```
   - Should show your device with `device` status

4. **Install the APK**
   ```bash
   adb install /path/to/app-debug.apk
   ```
   - Replace path with actual APK location
   - Example: `adb install ~/Downloads/app-debug.apk`

5. **Verify Installation**
   - Command shows: `Success` message
   - App appears on device home screen

6. **Launch the App**
   ```bash
   adb shell am start -n com.github.caracal.jarvis/.MainActivity
   ```

### Common Issues

**Device not found:**
```bash
adb kill-server
adb start-server
adb devices
```

**Permission denied:**
- Check USB debugging is enabled
- Unplug and replug USB cable

**APK already installed:**
```bash
adb install -r /path/to/app-debug.apk
# -r flag reinstalls over existing app
```

---

## File Manager (Direct Install)

**Simplest method if no computer tools available.**

### Prerequisites

- APK file transferred to Android device
- Unknown sources allowed (or Google Play Protect configured)

### Steps

1. **Transfer APK to Device**
   - Connect device to computer via USB
   - Drag APK into device file manager
   - Or: Email APK and download on device
   - Or: Use cloud storage (Google Drive, etc.)

2. **Enable Installation from Unknown Sources**
   - Device: **Settings → Apps → Install unknown apps**
   - Select file manager app
   - Enable **Allow from this source**

3. **Locate the APK File**
   - Open **Files** app (or file manager)
   - Navigate to Downloads folder
   - Find `app-debug.apk` or `app-release.apk`

4. **Tap to Install**
   - Tap the APK file
   - System shows installation dialog
   - Tap **Install** button

5. **Verify Installation**
   - Dialog shows: **App installed**
   - Tap **Open** to launch app
   - Or find icon on home screen

---

## Android Device Portal

**For advanced setup and debugging (Android Studio).**

### Steps

1. **Open Android Studio**
   - Launch Android Studio

2. **Open Device Manager**
   - **Tools → Device Manager**

3. **Create Virtual Device** (if needed)
   - Click **Create device**
   - Select device type (e.g., Pixel 6)
   - Click **Next** through configuration
   - Select system image (Android 12+)
   - Click **Finish**

4. **Start Device**
   - Find device in list
   - Click play button to launch emulator
   - Wait for emulator to fully boot

5. **Install APK**
   - **File → Profile or Debug APK**
   - Select APK file
   - Choose your virtual device
   - Click **OK**

6. **Verify Installation**
   - APK installs automatically
   - App appears on emulator

---

## Troubleshooting

### Installation Fails

**Problem:** "App not installed"

**Solutions:**
1. Check device has 100+ MB free space
2. Try older Android version (if using emulator)
3. Uninstall old version first:
   ```bash
   adb uninstall com.github.caracal.jarvis
   ```
4. Download APK again (may be corrupted)

### "Unknown app from Android"

**Problem:** Security warning during install

**Solution:**
- Tap **Install anyway** (debug builds)
- Or enable **Install unknown apps** in Settings

### Device Not Recognized

**Problem:** `adb devices` shows nothing

**Solutions:**
1. Check USB cable is properly connected
2. Enable USB debugging on device
3. Restart ADB:
   ```bash
   adb kill-server
   adb start-server
   ```
4. Restart device
5. Try different USB port

### App Crashes on Startup

**Problem:** App opens then closes immediately

**Solutions:**
1. Check device meets minimum requirements (Android 12+)
2. Try reinstalling:
   ```bash
   adb uninstall com.github.caracal.jarvis
   adb install app-debug.apk
   ```
3. Check device logs:
   ```bash
   adb logcat | grep "caracal"
   ```
4. Try release APK instead of debug

### APK Download Issues

**Problem:** Can't download APK from GitHub

**Solutions:**
1. Verify workflow completed successfully
2. Check artifact hasn't expired (30-day limit)
3. Try different browser
4. Disable VPN/proxy
5. Check GitHub account has repo access

---

## Verification

### How to Verify Installation

**On Android Device:**

1. Open **Settings**
2. Go to **Apps** or **Installed Apps**
3. Search for **Jarvis** or **caracal**
4. Should show: `com.github.caracal.jarvis`
5. Verify version matches your APK

### Launch the App

1. Find **Jarvis** icon on home screen
2. Tap to launch
3. App should start and display main screen
4. Verify Iron Man theme colors appear

---

## Uninstalling the App

If you need to remove Jarvis:

### From Device Settings
1. **Settings → Apps → Installed Apps**
2. Find **Jarvis**
3. Tap **Uninstall**
4. Confirm deletion

### Via ADB
```bash
adb uninstall com.github.caracal.jarvis
```

---

## Getting APK File

### Download from GitHub Actions

1. Go to GitHub repository
2. Click **Actions** tab
3. Find latest **Build APK** workflow
4. Click the run
5. Scroll to **Artifacts**
6. Download:
   - **debug-apk** — For testing (faster builds)
   - **release-apk** — For distribution (optimized)

### Which APK to Use?

| APK | Use Case |
|-----|----------|
| `app-debug.apk` | Testing, development, QA |
| `app-release.apk` | Production, distribution |

---

## Next Steps

### After Installation

1. ✅ Launch the app
2. ✅ Verify all screens display correctly
3. ✅ Test main features
4. ✅ Check for crashes or errors
5. ✅ Report issues on GitHub

### For Development

1. Keep APK for testing
2. Update APK from new builds
3. Clear app data to reset state:
   ```bash
   adb shell pm clear com.github.caracal.jarvis
   ```
4. Check device logs for errors:
   ```bash
   adb logcat
   ```

---

## Requirements Reference

| Requirement | Minimum | Recommended |
|---|---|---|
| Android Version | 12 (API 32) | 14+ |
| RAM | 2 GB | 4+ GB |
| Storage | 100 MB free | 500+ MB free |
| Screen | 4.5 inches | 5+ inches |
| Processor | ARMv8 | Recent processor |

---

## Security & Privacy

### About Debug APK
- ✅ Safe to install for testing
- ✅ Unsigned (development only)
- ⚠️ Don't distribute to others
- ⚠️ Has debug logging enabled

### About Release APK
- ✅ Optimized for distribution
- ✅ Can be signed for Play Store
- ⚠️ Not signed by default
- ⚠️ Only use official releases

---

## Support

### If You Have Issues

1. Check troubleshooting section above
2. Verify device meets requirements
3. Try reinstalling APK
4. Check GitHub Issues for known problems
5. Report new issues with:
   - Device model and Android version
   - APK version used
   - Error message
   - Steps to reproduce

### Useful Commands

```bash
# View device info
adb shell getprop ro.build.version.release

# Check app is installed
adb shell pm list packages | grep caracal

# Clear app data
adb shell pm clear com.github.caracal.jarvis

# View app logs
adb logcat | grep "caracal"

# Uninstall app
adb uninstall com.github.caracal.jarvis
```

---

## Quick Checklist

Before installing, verify you have:

- [ ] Android device or emulator (Android 12+)
- [ ] APK file downloaded from GitHub
- [ ] 100+ MB free storage
- [ ] USB cable (for physical device)
- [ ] USB debugging enabled (if using device)
- [ ] Read these instructions

---

**Version:** 1.0  
**Created:** March 7, 2026  
**Last Updated:** March 7, 2026  
**Status:** ✅ Complete

