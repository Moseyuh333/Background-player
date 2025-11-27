# 🚀 QUICK START GUIDE / HƯỚNG DẪN NHANH

## Get Running in 5 Minutes! / Chạy trong 5 phút!

> **Tiếng Việt:** Xem file **HUONG_DAN_BUILD.md** để có hướng dẫn chi tiết bằng tiếng Việt về cách build và cài đặt.

### Step 1: Open Project (1 minute)
1. Launch **Android Studio**
2. Click **File → Open**
3. Navigate to: `D:\New folder\Background-player`
4. Click **OK**
5. Wait for Gradle sync (auto-starts)

### Step 2: Wait for Sync (2-3 minutes)
- Status bar shows "Gradle Sync in progress..."
- Wait for "BUILD SUCCESSFUL" message
- If errors occur, see [Troubleshooting](#troubleshooting)

### Step 3: Connect Device (30 seconds)
**Option A: Physical Device**
- Enable USB Debugging in Developer Options
- Connect via USB cable
- Click "Allow" on device when prompted

**Option B: Emulator**
- Open Device Manager
- Create/Start an Android emulator (API 26+)

### Step 4: Run App (30 seconds)
1. Click **Run** button (green play icon) or press `Shift + F10`
2. Select your device/emulator
3. Click **OK**
4. App installs and launches automatically

### Step 5: Test (1 minute)
1. **Grant Permission** when prompted
2. **Select "Demo Track 1"** button
3. **Verify**:
   - ✅ Music starts playing
   - ✅ Status shows "Playing"
   - ✅ Notification appears
   - ✅ Seek bar moves
4. **Press HOME button**
5. **Verify**:
   - ✅ Music continues playing
   - ✅ Notification still visible
6. **Test notification controls**:
   - Tap Pause → Music pauses
   - Tap Play → Music resumes
   - Tap Stop → Music stops

---

## ✅ Success Checklist

After following the steps above, you should see:

- [x] App opens with 3 track buttons
- [x] Permission dialog appears (first time)
- [x] Music plays when track selected
- [x] Notification shows with track name
- [x] Play/Pause/Stop buttons work
- [x] Seek bar updates during playback
- [x] Music continues when HOME pressed
- [x] Music continues when BACK pressed
- [x] Notification controls work

---

## 🎯 What You're Testing

This app demonstrates:
- **Foreground Service** - keeps music playing in background
- **ExoPlayer** - plays FLAC audio files
- **Notification Controls** - control playback without opening app
- **State Management** - survives screen rotation

---

## 📁 Files Already Included

✅ All source code files (Kotlin)
✅ All XML resource files
✅ All Gradle configuration
✅ **3 FLAC audio files** (ready to use)
✅ Complete documentation

**Nothing else to download or configure!**

---

## 🐛 Troubleshooting

### Problem: Gradle Sync Failed
**Solution**: 
- Check internet connection
- Click "Try Again" in error banner
- If still fails: File → Invalidate Caches → Invalidate and Restart

### Problem: FLAC Files Not Found
**Solution**:
- Files should be at: `app/src/main/res/raw/sample1.flac` (and sample2, sample3)
- If missing, see BUILD_INSTRUCTIONS.md section "Adding FLAC Files"
- After adding: Build → Clean Project → Build → Rebuild Project

### Problem: Permission Not Granted
**Solution**:
- Manually grant in device settings:
  - Settings → Apps → Background Music Player
  - → Permissions → Files and media → Allow

### Problem: No Sound
**Solution**:
- Check device volume (media volume, not ringer)
- Ensure device is not muted
- Try playing FLAC in another app to verify file is valid

### Problem: Build Error "UnstableApi"
**Solution**: Already handled in code with `@UnstableApi` annotations

---

## 📖 More Information

- **Full Setup Guide**: See [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md)
- **Architecture Details**: See [ARCHITECTURE.md](ARCHITECTURE.md)
- **Project Overview**: See [README.md](README.md)
- **Implementation Summary**: See [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)

---

## 🎉 That's It!

You now have a working Android app that plays music in the background using:
- ✅ Foreground Service
- ✅ ExoPlayer
- ✅ FLAC audio files
- ✅ Notification controls

**Enjoy coding!** 🎵

