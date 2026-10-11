# 🎧 AudioBook

> Ứng dụng Android chuyển đổi tài liệu PDF / Word thành audiobook, cho phép nghe lại bất cứ lúc nào — hoạt động **offline hoàn toàn** sau khi xử lý.

<p align="center">
  <img src="docs/screenshots/home.png" width="240" alt="Home"/>
  <img src="docs/screenshots/reader.png" width="240" alt="Reader"/>
  <img src="docs/screenshots/library.png" width="240" alt="Library"/>
</p>

<p align="center">
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin" alt="Kotlin"/></a>
  <a href="https://developer.android.com/jetpack/compose"><img src="https://img.shields.io/badge/Compose-BOM%202024.12-4285F4?logo=jetpackcompose" alt="Compose"/></a>
  <a href="https://developer.android.com/studio"><img src="https://img.shields.io/badge/minSdk-24%20|%20targetSdk-35-3DDC84?logo=android" alt="Android"/></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License"/></a>
</p>

---

## 📖 Giới thiệu

**AudioBook** là ứng dụng Android native giúp bạn:

- 📄 **Nhập** tài liệu PDF hoặc Word (.docx) từ bộ nhớ máy.
- 🔊 **Chuyển đổi** văn bản thành file âm thanh (WAV) bằng Text-to-Speech của hệ thống.
- 🎧 **Nghe lại** mọi lúc — kể cả khi không có mạng, không cần chờ xử lý lại.
- 📚 **Quản lý thư viện** sách cá nhân với đầy đủ metadata (tiêu đề, thời lượng, trạng thái).

Dự án được xây dựng như **reference implementation** cho kiến trúc **MVVM + Clean Architecture** hiện đại trên Android: tách tầng rõ ràng, testable, dễ mở rộng.

### 🎯 Đối tượng

- Người học ngoại ngữ muốn nghe tài liệu thay vì đọc.
- Người khiếm thị / thị lực kém.
- Bất kỳ ai muốn "đọc" tài liệu trong lúc di chuyển, tập thể dục, làm việc nhà.

---

## ✨ Tính năng

### Đã hoàn thành

- ✅ **Import tài liệu** qua Storage Access Framework (SAF) — không cần xin quyền storage.
- ✅ **Hỗ trợ định dạng**: `PDF`, `DOCX`.
- ✅ **Text-to-Speech** bằng engine hệ thống, chia đoạn theo câu để tối ưu chất lượng âm thanh.
- ✅ **Lưu file WAV** vào internal storage, merge các chunk thành 1 file duy nhất.
- ✅ **Phát lại** với MediaPlayer + progress bar realtime.
- ✅ **Thư viện** hiển thị các sách đã sẵn sàng nghe.
- ✅ **Xoá sách** kèm dọn file audio.
- ✅ **Dark mode** theo cài đặt hệ thống.

### Đang phát triển

- 🚧 Đồng bộ Cloud (Firebase Storage) để nghe trên nhiều thiết bị.
- 🚧 Điều khiển playback từ notification (Media3 / ExoPlayer).
- 🚧 Tốc độ đọc (0.5x – 2.0x), chọn giọng đọc.
- 🚧 Bookmark / ghi chú theo timestamp.
- 🚧 Sleep timer.

### Ngoài phạm vi

- ❌ Định dạng `.doc` cũ (chỉ hỗ trợ `.docx`).
- ❌ OCR cho PDF dạng ảnh scan.
- ❌ Streaming audio realtime (audio được xử lý offline trước).

---

## 🛠 Công nghệ & Kiến trúc

### Tech stack

| Layer | Công nghệ |
|---|---|
| **Ngôn ngữ** | Kotlin 2.0.21 |
| **UI** | Jetpack Compose + Material 3 |
| **Kiến trúc** | MVVM + Clean Architecture (Domain / Data / Presentation) |
| **DI** | Hilt 2.53 |
| **Local DB** | Room 2.6.1 (single source of truth) |
| **Remote** | Retrofit 2.11 + OkHttp 4.12 + Gson |
| **Async** | Coroutines 1.9 + Flow |
| **PDF parsing** | PdfBox-Android 2.0.27 |
| **Word parsing** | XmlPullParser (custom, không dùng Apache POI) |
| **TTS** | Android `TextToSpeech` API |
| **Playback** | Android `MediaPlayer` |
| **Testing** | JUnit 5 + MockK + Turbine |
| **Build** | AGP 8.7 + KSP + R8 Full Mode |

### Cấu trúc thư mục

```
app/src/main/java/com/example/app/
├── AudioBookApplication.kt
├── MainActivity.kt
│
├── core/                          # Cross-cutting concerns
│   ├── common/Resource.kt         # Wrapper Loading / Success / Error
│   ├── di/                        # Hilt modules
│   │   ├── AppModule.kt
│   │   ├── DatabaseModule.kt
│   │   └── NetworkModule.kt
│   └── navigation/
│       ├── Routes.kt
│       └── AudioBookNavHost.kt
│
├── domain/                        # Pure Kotlin — no Android dependency
│   ├── model/
│   │   └── AudioBook.kt
│   ├── repository/
│   │   └── AudioBookRepository.kt # Interface
│   ├── service/
│   │   ├── DocumentParser.kt      # Interface
│   │   └── TextToSpeechService.kt # Interface
│   └── usecase/                   # 1 use case = 1 file
│       ├── ObserveBooksUseCase.kt
│       ├── GetBookUseCase.kt
│       ├── ImportDocumentUseCase.kt
│       ├── SynthesizeBookUseCase.kt
│       └── DeleteBookUseCase.kt
│
├── data/                          # Implements domain interfaces
│   ├── local/
│   │   ├── AudioBookDatabase.kt
│   │   ├── dao/AudioBookDao.kt
│   │   └── entity/AudioBookEntity.kt
│   ├── remote/
│   │   ├── ApiService.kt
│   │   └── dto/AudioBookDto.kt
│   ├── parser/
│   │   ├── PdfDocumentParser.kt
│   │   ├── WordDocumentParser.kt
│   │   └── DocumentParserFactory.kt
│   ├── tts/
│   │   └── AndroidTextToSpeechService.kt
│   ├── mapper/AudioBookMapper.kt
│   └── repository/AudioBookRepositoryImpl.kt
│
└── presentation/
    ├── theme/                     # Color, Type, Theme
    └── screens/
        ├── home/                  # HomeScreen + HomeViewModel + HomeUiState
        ├── reader/                # ReaderScreen + ReaderViewModel
        └── library/               # LibraryScreen + LibraryViewModel
```

### Luồng dữ liệu

```
┌─────────────┐    collect    ┌──────────────┐    invoke    ┌──────────────┐
│  Compose UI │ ────────────► │  ViewModel   │ ───────────► │   UseCase    │
└─────────────┘               └──────────────┘              └──────────────┘
       ▲                              │                             │
       │    UiState (StateFlow)       │                             ▼
       └──────────────────────────────┘                   ┌────────────────────┐
                                                          │ Repository (Domain)│
                                                          └────────────────────┘
                                                                    ▲
                                                                    │ implements
                                                          ┌────────────────────┐
                                                          │ RepositoryImpl     │
                                                          │  ├─ Room (SSOT)    │
                                                          │  ├─ Retrofit       │
                                                          │  ├─ ParserFactory  │
                                                          │  └─ TTS Service    │
                                                          └────────────────────┘
```

**Nguyên tắc:**

- `domain/` **không** import bất kỳ class nào từ `android.*` → thuần Kotlin, test nhanh.
- `data/` implement interface từ `domain/`, chịu trách nhiệm I/O.
- `presentation/` chỉ giao tiếp qua UseCase, không biết Retrofit hay DAO tồn tại.
- **Room là single source of truth** — UI luôn observe Room, remote chỉ sync vào Room.

---

## 🚀 Hướng dẫn cài đặt

### Yêu cầu hệ thống

| Công cụ | Phiên bản tối thiểu |
|---|---|
| Android Studio | Ladybug (2024.2.1) hoặc mới hơn |
| JDK | 17 |
| Android SDK | API 35 (compileSdk) |
| Gradle | 8.9+ (dùng wrapper đi kèm) |
| RAM | ≥ 8 GB (khuyến nghị 16 GB) |
| Dung lượng | ≥ 5 GB cho SDK + build cache |

### Các bước

**1. Clone repository**

```bash
git clone https://github.com/your-username/AudioBook.git
cd AudioBook
```

**2. Cấu hình Android SDK**

Tạo file `local.properties` ở thư mục gốc (nếu chưa có):

```properties
# macOS
sdk.dir=/Users/yourname/Library/Android/sdk

# Windows
# sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk

# Linux
# sdk.dir=/home/yourname/Android/Sdk
```

**3. (Tuỳ chọn) Cấu hình Firebase**

Nếu bạn muốn build bản có Crashlytics:

1. Tạo project trên [Firebase Console](https://console.firebase.google.com/).
2. Tải `google-services.json` → đặt vào `app/google-services.json`.
3. Nếu **không** cần, comment các dòng sau trong `app/build.gradle.kts`:

   ```kotlin
   // alias(libs.plugins.google.services)
   // alias(libs.plugins.crashlytics)
   // implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
   // implementation("com.google.firebase:firebase-crashlytics-ktx")
   ```

**4. Sync Gradle**

Mở project bằng Android Studio → chờ Gradle sync hoàn tất, hoặc chạy:

```bash
./gradlew --refresh-dependencies
```

**5. (Khuyến nghị) Bật KSP incremental để build nhanh hơn**

Đã có sẵn trong `gradle.properties`. Không cần làm gì thêm.

---

## 🔨 Build

### Debug build

```bash
# Assemble APK debug
./gradlew :app:assembleDebug

# Cài trực tiếp lên thiết bị đang kết nối
./gradlew :app:installDebug

# Build + cài + mở app
./gradlew :app:installDebug && adb shell am start -n com.example.app/.MainActivity
```

APK đầu ra: `app/build/outputs/apk/debug/app-debug.apk`

### Release build

**1. Tạo keystore** (chỉ làm 1 lần)

```bash
keytool -genkey -v -keystore audiobook-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias audiobook
```

**2. Cấu hình signing**

Tạo `keystore.properties` ở thư mục gốc (**KHÔNG commit**):

```properties
storeFile=../audiobook-release.jks
storePassword=your_store_password
keyAlias=audiobook
keyPassword=your_key_password
```

Sửa `app/build.gradle.kts`:

```kotlin
import java.util.Properties

val keystorePropsFile = rootProject.file("keystore.properties")
val keystoreProps = Properties().apply {
    if (keystorePropsFile.exists()) load(keystorePropsFile.inputStream())
}

android {
    signingConfigs {
        create("release") {
            storeFile = file(keystoreProps.getProperty("storeFile") ?: "")
            storePassword = keystoreProps.getProperty("storePassword")
            keyAlias = keystoreProps.getProperty("keyAlias")
            keyPassword = keystoreProps.getProperty("keyPassword")
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

**3. Build**

```bash
# APK
./gradlew :app:assembleRelease

# App Bundle (khuyến nghị cho Google Play)
./gradlew :app:bundleRelease
```

APK đầu ra: `app/build/outputs/apk/release/app-release.apk`  
AAB đầu ra: `app/build/outputs/bundle/release/app-release.aab`

**4. Upload mapping file** (nếu dùng Crashlytics)

```bash
./gradlew :app:uploadCrashlyticsMappingFileRelease
```

### Build variants

| Variant | Minify | Debug | Dùng cho |
|---|---|---|---|
| `debug` | ❌ | ✅ | Dev hàng ngày |
| `release` | ✅ (R8 Full Mode) | ❌ | Production / Play Store |

---

## 🧪 Chạy test

### Unit test (JUnit 5 + MockK + Turbine)

```bash
# Chạy toàn bộ unit test
./gradlew :app:testDebugUnitTest

# Chạy 1 class cụ thể
./gradlew :app:testDebugUnitTest --tests "com.example.app.domain.usecase.GetAudioBooksUseCaseTest"

# Chạy 1 method
./gradlew :app:testDebugUnitTest --tests "*GetAudioBooksUseCaseTest.returns books from repository"

# Chạy test với report HTML
./gradlew :app:testDebugUnitTest
open app/build/reports/tests/testDebugUnitTest/index.html
```

### Instrumented test

```bash
# Cần thiết bị / emulator đang kết nối
./gradlew :app:connectedDebugAndroidTest
```

### Chạy toàn bộ kiểm tra chất lượng

```bash
# Unit + lint + build
./gradlew check

# Chỉ lint
./gradlew :app:lintDebug

# Report lint
open app/build/reports/lint-results-debug.html
```

### Đo độ phủ test (nếu đã setup JaCoCo)

```bash
./gradlew :app:jacocoTestReport
open app/build/reports/jacoco/testDebugUnitTest/html/index.html
```

### Test coverage mục tiêu

| Layer | Mục tiêu | Ghi chú |
|---|---|---|
| `domain/usecase` | ≥ 90% | Logic thuần, dễ test, **phải** cover |
| `domain/model` | 100% | Data class, không cần test riêng |
| `data/repository` | ≥ 80% | Test bằng MockK cho DAO / API |
| `data/parser` | ≥ 70% | Cần test với file mẫu |
| `presentation/ViewModel` | ≥ 80% | Test state emission bằng Turbine |
| `presentation/Compose` | — | Ưu tiên screenshot test (chưa setup) |

### Viết test mới

Xem ví dụ trong `app/src/test/java/com/example/app/`:

- `domain/usecase/GetAudioBooksUseCaseTest.kt`
- `data/repository/AudioBookRepositoryImplTest.kt`
- `core/common/ResourceTest.kt`

**Nguyên tắc:**

- Tên test method dùng **backtick** để mô tả bằng tiếng Việt/Anh tự nhiên.
- Dùng **Given-When-Then** trong comment nếu logic phức tạp.
- **MockK** cho dependency, **Turbine** cho Flow, **`runTest`** cho coroutine.

---

## 🤝 Đóng góp

### Quy trình

1. **Fork** repo.
2. Tạo branch mới theo convention:
   ```bash
   git checkout -b feature/ten-tinh-nang          # Tính năng mới
   git checkout -b fix/ten-bug                    # Sửa bug
   git checkout -b refactor/ten-module            # Refactor
   git checkout -b docs/cap-nhat-readme           # Tài liệu
   ```
3. Commit theo [Conventional Commits](https://www.conventionalcommits.org/):
   ```
   feat: thêm tính năng đổi tốc độ đọc
   fix: sửa crash khi import file .docx rỗng
   refactor: tách SynthesizeBookUseCase thành 2 use case nhỏ
   test: bổ sung test cho HomeViewModel
   docs: cập nhật README phần build release
   chore: nâng Kotlin lên 2.1.0
   ```
4. Push branch:
   ```bash
   git push origin feature/ten-tinh-nang
   ```
5. Mở **Pull Request** với mô tả rõ ràng:
   - **Vấn đề** đang giải quyết.
   - **Giải pháp** đã triển khai.
   - **Cách test** (steps + screenshots nếu là UI).
   - **Checklist** đã tick.

### Checklist trước khi submit PR

- [ ] Code build được: `./gradlew :app:assembleDebug`
- [ ] Tất cả unit test pass: `./gradlew :app:testDebugUnitTest`
- [ ] Lint không có error: `./gradlew :app:lintDebug`
- [ ] Có test cho code mới (không giảm coverage)
- [ ] Tuân thủ **Clean Architecture** — không để `domain/` import `android.*`
- [ ] Không có `TODO` / `FIXME` / `println` debug còn sót
- [ ] Đã format code (Android Studio → `Ctrl+Alt+L` / `Cmd+Option+L`)
- [ ] Commit message theo Conventional Commits
- [ ] Nếu thay đổi UI → có screenshot / GIF

### Coding conventions

**Kotlin style** — theo [official Kotlin style guide](https://kotlinlang.org/docs/coding-conventions.html):

- **Indent**: 4 spaces.
- **Max line**: 120 ký tự.
- **Naming**:
  - `PascalCase` — class, interface, composable.
  - `camelCase` — function, variable, property.
  - `UPPER_SNAKE_CASE` — const val, top-level val.
  - `snake_case` — package (không dùng camelCase).
- **Composable** function: **PascalCase**, có `@Composable`.
- **UseCase**: có hậu tố `UseCase`, dùng `operator fun invoke()`.
- **Repository**: interface ở `domain/repository/`, impl ở `data/repository/` với hậu tố `Impl`.
- **DTO**: chỉ ở `data/remote/dto/`, không rò rỉ ra `domain/`.

**Commit conventions:**

```
<type>(<scope>): <mô tả ngắn gọn, không viết hoa đầu, không dấu chấm cuối>

[optional body: giải thích WHY, không phải WHAT]

[optional footer: BREAKING CHANGE, closes #123]
```

**Types** hợp lệ: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `style`, `perf`, `ci`.

### Báo cáo bug

Mở [Issue](https://github.com/your-username/AudioBook/issues/new) với template:

```markdown
**Mô tả bug**
Mô tả rõ ràng, ngắn gọn bug là gì.

**Các bước tái hiện**
1. Vào màn hình...
2. Nhấn nút...
3. Thấy lỗi...

**Kết quả mong đợi**
Mô tả cái đáng lẽ ra phải xảy ra.

**Screenshots / Logs**
Đính kèm ảnh chụp hoặc logcat.

**Môi trường**
- Thiết bị: Pixel 7
- Android: 14 (API 34)
- App version: 1.0.0 (build 42)
```

### Đề xuất tính năng

Mở [Issue](https://github.com/your-username/AudioBook/issues/new) với label `enhancement`:

```markdown
**Vấn đề cần giải quyết**
Người dùng không thể... / Rất khó để...

**Giải pháp đề xuất**
Mô tả tính năng bạn muốn.

**Giải pháp thay thế**
Có cách nào khác không?

**Ngữ cảnh thêm**
Screenshots, mockups, link tham khảo.
```

---

## 📋 Changelog

Xem [CHANGELOG.md](CHANGELOG.md) để biết lịch sử thay đổi.

Format theo [Keep a Changelog](https://keepachangelog.com/) + [Semantic Versioning](https://semver.org/).

---

## 📄 License

```
MIT License

Copyright (c) 2024 Your Name

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 🙏 Credits & Tham khảo

- [**PdfBox-Android**](https://github.com/TomRoush/PdfBox-Android) — PDF parsing trên Android.
- [**Architecture Samples**](https://github.com/android/architecture-samples) — Google's official Clean Architecture reference.
- [**Now in Android**](https://github.com/android/nowinandroid) — Modern Android development best practices.
- [**Compose Samples**](https://github.com/android/compose-samples) — Jetpack Compose patterns.

---

## 📬 Liên hệ

- **Tác giả**: Your Name — [@your-handle](https://github.com/your-username)
- **Email**: your.email@example.com
- **Issues**: [GitHub Issues](https://github.com/your-username/AudioBook/issues)

---

<p align="center">
  Nếu bạn thấy dự án hữu ích, hãy ⭐ star repo để ủng hộ mình nhé!
</p>