# Rekap Nilai Merdeka

Aplikasi Android **offline** untuk guru dalam mengelola penilaian siswa berbasis **Kurikulum Merdeka**.

## Fitur Utama

- ✅ **Fully Offline** — Semua data tersimpan lokal di perangkat (Room Database)
- ✅ Pengaturan identitas: Sekolah, Kelas/Fase, Mata Pelajaran, Tahun Ajaran, Semester
- ✅ Manajemen **Tujuan Pembelajaran (TP)** (kode, deskripsi, KKTP)
- ✅ Manajemen **Siswa** (CRUD + pencarian cepat)
- ✅ **Penilaian Formatif** per TP (kategori: Perlu Bimbingan / Cukup / Baik / Sangat Baik + catatan observasi)
- ✅ **Penilaian Sumatif** per TP + nilai **SAS**
- ✅ Hitung **Nilai Akhir** otomatis (bobot dapat disesuaikan)
- ✅ **Deskripsi Capaian Kompetensi** otomatis (bisa diedit manual)
- ✅ Rekap per siswa & rekap per TP (remedial / pengayaan)
- ✅ Export backup JSON & daftar siswa CSV

## Cara Build & Jalankan

### Persyaratan
- Android Studio Hedgehog (2023.1.1) atau lebih baru
- JDK 17
- Android SDK 34

### Langkah
1. Clone repository ini
2. Buka folder proyek di Android Studio
3. Tunggu Gradle sync selesai
4. Jalankan di emulator atau perangkat (minSdk 26)

```bash
./gradlew assembleDebug
```

## Struktur Proyek

```
app/src/main/java/com/rekapnilai/merdeka/
├── data/
│   ├── local/          # Room Database + DAO
│   ├── model/          # Entity & data class
│   └── repository/     # RekapRepository
├── ui/
│   ├── components/     # Komponen UI bersama
│   ├── navigation/     # NavGraph
│   ├── screens/        # Semua layar
│   └── theme/          # Material 3 Theme
├── viewmodel/          # MainViewModel
├── MainActivity.kt
└── RekapNilaiApp.kt
```

## Catatan Pengembangan

- Import JSON penuh (dengan mapping ID) masih sederhana di v1.0 — export sudah lengkap.
- Icon launcher default masih menggunakan placeholder Android. Ganti di `res/mipmap`.
- Untuk produksi, pertimbangkan menambahkan:
  - Import CSV/JSON via Storage Access Framework
  - Print/Share PDF rekap
  - Multi-kelas / multi-mapel (saat ini 1 kelas + 1 mapel per instalasi)

## Lisensi

Bebas digunakan dan dimodifikasi untuk keperluan pendidikan.

---
Dibuat untuk mendukung implementasi Kurikulum Merdeka di sekolah.
