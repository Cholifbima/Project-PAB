# QR Code Generator & Scanner Android App

Aplikasi Android untuk generate dan scan QR code yang dibuat menggunakan Jetpack Compose untuk project PAB (Pemrograman Aplikasi Bergerak).

## 📱 Fitur

### QR Code Generator
- **Text to QR Code**: Konversi teks biasa menjadi QR code
- **URL to QR Code**: Konversi URL/link menjadi QR code  
- **Email to QR Code**: Konversi email (dengan subject & body optional) menjadi QR code

### QR Code Scanner
- **Real-time scanning**: Scan QR code menggunakan kamera
- **Flash control**: Toggle flash on/off untuk scan di tempat gelap
- **Multi-format support**: Support berbagai format QR code (text, URL, email)

### Fitur Tambahan
- **Modern UI**: Desain sesuai dengan Figma mockup
- **Dark Mode**: Toggle dark/light mode di settings
- **Navigation**: Bottom navigation dan navigation drawer
- **Responsive**: UI yang responsive untuk berbagai ukuran layar

## 🛠️ Teknologi yang Digunakan

### Android Framework
- **Jetpack Compose**: Modern UI toolkit untuk Android
- **Material Design 3**: Latest Material Design components
- **Navigation Component**: Untuk navigasi antar screen
- **CameraX**: Untuk akses kamera dan preview
- **MLKit Barcode Scanning**: Untuk scan QR code

### Libraries
- **ZXing**: QR code generation (`com.google.zxing:core:3.5.2`)
- **ZXing Android Embedded**: Android integration (`com.journeyapps:zxing-android-embedded:4.3.0`)
- **Accompanist Permissions**: Permission handling (`com.google.accompanist:accompanist-permissions:0.32.0`)

## 📁 Struktur Project

```
app/src/main/java/com/example/pabproject/
├── MainActivity.kt
├── navigation/
│   └── QRNavigation.kt
├── ui/
│   ├── components/
│   │   ├── QRCodeDisplay.kt
│   │   └── BottomNavigationBar.kt
│   ├── screens/
│   │   ├── DashboardScreen.kt
│   │   ├── MenuScreen.kt
│   │   ├── TextToQRScreen.kt
│   │   ├── URLToQRScreen.kt
│   │   ├── EmailToQRScreen.kt
│   │   ├── QRScannerScreen.kt
│   │   └── SettingsScreen.kt
│   └── theme/
└── utils/
    └── QRCodeGenerator.kt
```

## 🚀 Cara Instalasi & Menjalankan

### Prerequisites
- Android Studio Iguana atau lebih baru
- Android SDK API level 29 atau lebih tinggi
- Gradle 8.0+
- Kotlin 1.9+

### Steps
1. **Clone project**
   ```bash
   git clone <repository-url>
   cd PABProject
   ```

2. **Open di Android Studio**
   - File → Open → pilih folder project
   - Wait for Gradle sync

3. **Build project**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run aplikasi**
   - Connect Android device atau start emulator
   - Click Run button atau `Shift + F10`

## 📋 Dependencies

Tambahkan dependencies berikut di `app/build.gradle.kts`:

```kotlin
dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")
    
    // QR Code Generation
    implementation("com.google.zxing:core:3.5.2")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    
    // CameraX for QR Code Scanning
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")
    implementation("androidx.camera:camera-extensions:1.3.1")
    
    // MLKit for QR Code scanning
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    
    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // Material Icons Extended
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
}
```

## 🔐 Permissions

Aplikasi membutuhkan permissions berikut di `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## 💡 Cara Penggunaan

### Generate QR Code
1. Buka aplikasi
2. Tap "Start Generating" atau icon Menu
3. Pilih jenis QR code (Text/URL/Email)
4. Input data sesuai jenis yang dipilih
5. QR code akan ter-generate otomatis
6. Tap "Generate" untuk aksi tambahan (save/share)

### Scan QR Code
1. Tap icon Scanner di bottom navigation
2. Allow camera permission jika diminta
3. Arahkan kamera ke QR code
4. Hasil scan akan muncul di bawah preview
5. Gunakan flash jika perlu dengan tap icon flash

## 🎨 UI/UX Design

Aplikasi ini mengikuti design system yang telah dibuat di Figma dengan:
- **Color Scheme**: Purple primary (#8B5CF6)
- **Typography**: Material Design 3 typography scale
- **Spacing**: Consistent 16dp grid system
- **Corner Radius**: 12dp untuk cards dan buttons
- **Bottom Navigation**: 4 tabs (Home, Scanner, Settings, History)

## 🔧 Implementasi Detail

### QR Code Generation
```kotlin
// Generate QR code bitmap
val bitmap = QRCodeGenerator.generateQRCode(text, width, height)

// Generate email QR code
val emailQR = QRCodeGenerator.generateEmailQRCode(
    email = "user@example.com",
    subject = "Subject",
    body = "Message body"
)
```

### QR Code Scanning
- Menggunakan CameraX untuk preview kamera
- MLKit BarcodeScanning untuk deteksi QR code
- Support real-time scanning dengan callback

### Navigation
- Navigation Compose untuk routing
- Sealed class untuk type-safe navigation
- Bottom navigation dengan state management

## 🐛 Troubleshooting

### Build Errors
1. **Gradle sync failed**: 
   - Clean project: `Build → Clean Project`
   - Invalidate caches: `File → Invalidate Caches and Restart`

2. **Camera permission denied**:
   - Check AndroidManifest.xml permissions
   - Manual grant permission di device settings

3. **QR generation failed**:
   - Check input text tidak kosong
   - Verify ZXing dependencies

## 📄 License

This project is created for educational purposes (PAB Project).

## 👥 Contributors

- [Your Name] - Developer

## 📞 Support

Jika ada pertanyaan atau issues, silakan contact melalui:
- Email: [your-email]
- GitHub Issues: [repository-issues-url] 