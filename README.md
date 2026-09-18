# 🔐 GEMBOK

Aplikasi password vault offline-first dengan prinsip desain iOS yang bersih, tenang, dan berbobot di Android:
- **Warna & Palet**: Charcoal, Warm White, dan aksen Zaitun (Warm Olive).
- **Glassmorphism**: Diterapkan selektif hanya pada panel ringkasan keamanan utama.
- **Surface Solid**: Seluruh daftar, tombol angka, dan form input menggunakan surface solid yang kontras dan tajam.
- **Password Health Nyata**: Perhitungan kesehatan sandi dihitung murni dari data kredensial nyata di perangkat (tanpa klaim keamanan palsu).
- **Dukungan Adaptif**: Responsif untuk perangkat mobile maupun desktop/tablet dengan batas lebar konten optimal.

## ✨ Fitur & Layar
1. **Layar Pembuatan & Input PIN**: Langkah pembuatan Master PIN (4-6 digit) dan konfirmasi untuk brankas baru, serta keypad autentikasi.
2. **Beranda Vault & Pencarian**: Filter pencarian real-time berdasarkan judul layanan, nama pengguna, atau URL situs.
3. **Panel Password Health**: Metrik kesehatan sandi (Kuat, Cukup, Lemah, serta deteksi sandi yang dipakai ulang) dari database lokal Room.
4. **Daftar Sandi**: Kartu solid dengan nama layanan, username, salin dengan clipboard timeout 60 detik, edit, dan hapus.
5. **Form Tambah & Edit Sandi**: Input akun, situs, validasi form, dan indikator kekuatan kata sandi.
6. **Generator Kata Sandi**: Slider panjang sandi (8-32 karakter), sakelar variasi karakter (huruf besar/kecil, angka, simbol), dan tombol salin/terapkan.
7. **Empty, Loading & Error State**: Status visual yang tenang, informatif, dan menyediakan aksi pemulihan langsung.

## 🛠️ Dibuat Dengan
- Kotlin
- Jetpack Compose & Material 3
- Android Room Database
- Android Architecture Components (ViewModel, StateFlow)

## 📝 Lisensi
MIT License — lihat file LICENSE untuk detail.

