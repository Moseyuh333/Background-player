# 🚀 CÁCH BUILD VÀ CÀI ĐẶT ỨNG DỤNG

## Phương pháp đơn giản nhất: Dùng Android Studio

### Bước 1: Mở Project trong Android Studio
1. Mở **Android Studio**
2. Chọn **File → Open**
3. Tìm đến thư mục: `D:\New folder\Background-player`
4. Click **OK**
5. Đợi Gradle sync tự động (2-5 phút)

### Bước 2: Build và Cài đặt
1. Kết nối điện thoại qua USB (hoặc khởi động emulator)
2. Click nút **Run** (biểu tượng play màu xanh) hoặc nhấn `Shift + F10`
3. Chọn thiết bị của bạn
4. Click **OK**
5. **Xong!** App sẽ tự động build và cài đặt

---

## Phương pháp 2: Dùng Gradle Command Line

### Yêu cầu:
- Android Studio đã cài đặt
- ANDROID_HOME đã được cấu hình
- Device kết nối hoặc emulator đang chạy

### Các lệnh:

#### 1. Mở Command Prompt và di chuyển đến thư mục project:
```cmd
cd /d "D:\New folder\Background-player"
```

#### 2. Build APK debug:
```cmd
gradlew.bat assembleDebug
```

#### 3. Cài đặt lên thiết bị:
```cmd
gradlew.bat installDebug
```

#### Hoặc tất cả trong một lệnh:
```cmd
gradlew.bat clean assembleDebug installDebug
```

---

## Phương pháp 3: Build và Cài đặt thủ công

### Bước 1: Build bằng Android Studio
1. Mở project trong Android Studio
2. Chọn **Build → Build Bundle(s) / APK(s) → Build APK(s)**
3. Đợi build hoàn thành
4. Click **locate** để mở thư mục chứa APK

### Bước 2: Cài đặt APK
**File APK sẽ ở:** `app\build\outputs\apk\debug\app-debug.apk`

**Cách 1 - Dùng ADB:**
```cmd
adb install app\build\outputs\apk\debug\app-debug.apk
```

**Cách 2 - Copy thủ công:**
1. Copy file `app-debug.apk` vào điện thoại
2. Mở File Manager trên điện thoại
3. Tìm file APK và nhấn vào để cài đặt
4. Cho phép cài đặt từ nguồn không xác định nếu được hỏi

---

## Sửa lỗi thường gặp

### Lỗi: "SDK location not found"
**Giải pháp:** Chỉnh sửa file `local.properties`:
```properties
sdk.dir=C:\\Users\\TenBan\\AppData\\Local\\Android\\Sdk
```
(Thay `TenBan` bằng username Windows của bạn)

### Lỗi: "Gradle sync failed"
**Giải pháp:**
1. Kiểm tra kết nối internet
2. Trong Android Studio: **File → Invalidate Caches → Invalidate and Restart**
3. Thử lại

### Lỗi: "No connected devices"
**Giải pháp:**
1. Bật **USB Debugging** trên điện thoại:
   - Vào **Cài đặt → Về điện thoại**
   - Nhấn vào **Số bản dựng** 7 lần để bật Developer Options
   - Vào **Cài đặt → Developer Options**
   - Bật **USB Debugging**
2. Kết nối USB và chấp nhận popup "Allow USB debugging"
3. Kiểm tra: `adb devices`

### Lỗi: Build failed - Dependencies
**Giải pháp:**
```cmd
gradlew.bat clean
gradlew.bat --refresh-dependencies build
```

---

## Script tự động (Windows)

Đã tạo sẵn file **`build_and_install.bat`** trong thư mục project.

**Cách dùng:**
1. Double-click file `build_and_install.bat`
2. Hoặc chạy từ Command Prompt:
   ```cmd
   cd /d "D:\New folder\Background-player"
   build_and_install.bat
   ```

Script này sẽ:
- ✅ Clean build cũ
- ✅ Build APK mới
- ✅ Kiểm tra thiết bị
- ✅ Cài đặt tự động

---

## Kiểm tra sau khi cài đặt

### 1. Xác nhận app đã cài:
```cmd
adb shell pm list packages | findstr backgroundmusicplayer
```

### 2. Khởi chạy app:
```cmd
adb shell am start -n com.example.backgroundmusicplayer/.MainActivity
```

### 3. Xem logs:
```cmd
adb logcat | findstr "MusicPlayerService"
```

---

## Kết quả mong đợi

### ✅ Build thành công:
```
BUILD SUCCESSFUL in 45s
```

### ✅ APK được tạo tại:
```
app\build\outputs\apk\debug\app-debug.apk
```
Kích thước: ~40-50 MB (bao gồm 3 file FLAC)

### ✅ Cài đặt thành công:
```
Installing APK 'app-debug.apk' on 'Device Name'
Installed on 1 device.
```

### ✅ App xuất hiện trên điện thoại:
- Tên: **Background Music Player**
- Icon trong app drawer

---

## Hướng dẫn sử dụng sau khi cài

1. **Mở app** "Background Music Player"
2. **Cấp quyền** truy cập file audio khi được hỏi
3. **Chọn track** (Demo Track 1, 2, hoặc 3)
4. **Verify:**
   - ✅ Nhạc bắt đầu phát
   - ✅ Thanh trạng thái hiện "Playing"
   - ✅ Notification xuất hiện
   - ✅ Seek bar di chuyển
5. **Test background:**
   - Nhấn nút **HOME** → Nhạc tiếp tục phát
   - Nhấn nút **BACK** → Nhạc tiếp tục phát
   - Xem notification → Có nút Play/Pause/Stop
6. **Test notification controls:**
   - Nhấn **Pause** → Nhạc dừng
   - Nhấn **Play** → Nhạc tiếp tục
   - Nhấn **Stop** → Nhạc dừng hẳn, notification mất

---

## Tóm tắt các phương pháp

| Phương pháp | Độ khó | Thời gian | Khuyến nghị |
|-------------|--------|-----------|-------------|
| **Android Studio Run** | ⭐ Dễ nhất | 3-5 phút | ✅ **Khuyến nghị** |
| **Gradle Command** | ⭐⭐ Trung bình | 2-3 phút | Cho người có kinh nghiệm |
| **Build Script** | ⭐ Dễ | 2-3 phút | Tốt cho automation |
| **Manual APK** | ⭐⭐⭐ Khó | 5+ phút | Khi các cách khác thất bại |

---

## 🎯 Phương pháp đề xuất: ANDROID STUDIO

**Lý do:**
- ✅ Tự động sync dependencies
- ✅ Tự động cấu hình SDK
- ✅ Tự động build và install
- ✅ Giao diện trực quan
- ✅ Xem logs dễ dàng
- ✅ Debug nếu có lỗi

**Các bước:**
1. Mở Android Studio
2. File → Open → Chọn thư mục project
3. Đợi sync
4. Click Run
5. Xong!

---

## Hỗ trợ

Nếu gặp vấn đề:
1. Đọc phần **Sửa lỗi thường gặp** ở trên
2. Xem file **BUILD_INSTRUCTIONS.md** (chi tiết hơn)
3. Xem file **GRADLE_BUILD_GUIDE.md** (hướng dẫn Gradle)
4. Kiểm tra Logcat trong Android Studio

---

**Chúc bạn build thành công! 🚀**

