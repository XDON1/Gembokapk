# GEMBOK v1.0.1

Rilis Android native pertama GEMBOK, password vault offline untuk menyimpan dan mengelola kredensial di perangkat.

## Fitur

- PIN utama dengan konfirmasi dan recovery key.
- Password vault lokal menggunakan Room Database.
- Field vault dienkripsi dengan AES-GCM dan Android Keystore.
- Tambah, edit, hapus, dan cari entri password.
- Salin password dengan pembersihan clipboard otomatis setelah 60 detik.
- Generator password dengan pilihan panjang dan karakter.
- Password health berdasarkan entri yang benar-benar tersimpan.
- Deteksi password lemah dan password yang digunakan ulang.
- Auto-lock saat aplikasi masuk background.
- Tema terang dan gelap mengikuti pengaturan sistem Android.
- Avatar layanan lokal tanpa koneksi favicon atau pengiriman URL ke layanan eksternal.
- Empty, loading, dan error state.

## Cara Install

1. Download `app-debug.apk` dari bagian **Assets**.
2. Transfer APK ke HP Android.
3. Buka file APK di HP.
4. Izinkan instalasi dari sumber ini jika Android memintanya.
5. Tekan **Install**.

## Persyaratan

- Android 8.0 (API 26) atau lebih baru.
- Ruang penyimpanan kosong sekitar 50 MB.

## Catatan Rilis

- APK ini adalah build debug untuk preview dan pengujian.
- Simpan recovery key di tempat aman. Recovery key diperlukan jika PIN terlupa.
- Jangan memasukkan data produksi sebelum proses backup dan pemulihan diuji.
- Laporkan bug atau saran melalui tab Issues.

## Detail Teknis

- **Versi**: 1.0.1
- **Build**: Android debug APK
- **Application ID**: `com.gembok.passwordmanager`
- **Bahasa**: Kotlin
- **UI**: Jetpack Compose dan Material 3
- **Database**: Room
- **Keamanan**: Android Keystore dan AES-GCM
- **Build Date**: 18 September 2026

## Verifikasi

- Build debug berhasil dijalankan dengan Gradle Wrapper.
- APK berhasil dipasang ke perangkat Android yang terhubung.
- Alur tambah password telah diuji setelah perbaikan crash favicon online.

Full changelog:

https://github.com/XDON1/Gembokapk/commits/main
