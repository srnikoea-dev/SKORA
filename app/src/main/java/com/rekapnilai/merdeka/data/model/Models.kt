package com.rekapnilai.merdeka.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ==================== PENGATURAN KELAS / MATA PELAJARAN ====================
@Entity(tableName = "settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val namaSekolah: String = "",
    val kelasFase: String = "",          // contoh: "VII-A / Fase D"
    val mataPelajaran: String = "",
    val tahunAjaran: String = "",        // contoh: "2025/2026"
    val semester: String = "Ganjil",     // Ganjil / Genap
    val bobotSumatifTp: Float = 0.7f,    // bobot rata-rata nilai sumatif TP
    val bobotSas: Float = 0.3f,          // bobot nilai SAS
    val kktpDefault: Int = 75            // Kriteria Ketercapaian Tujuan Pembelajaran
)

// ==================== TUJUAN PEMBELAJARAN (TP) ====================
@Entity(tableName = "tujuan_pembelajaran")
data class TujuanPembelajaran(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val kodeTp: String,                  // contoh: "TP.1", "TP.2"
    val deskripsi: String,
    val targetKktp: Int = 75,            // target capaian TP ini
    val urutan: Int = 0
)

// ==================== SISWA ====================
@Entity(tableName = "siswa")
data class Siswa(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nomor: Int,                      // No urut
    val nisNisn: String = "",
    val nama: String
)

// ==================== PENILAIAN FORMATIF ====================
enum class KategoriFormatif {
    PERLU_BIMBINGAN,
    CUKUP,
    BAIK,
    SANGAT_BAIK
}

@Entity(
    tableName = "nilai_formatif",
    foreignKeys = [
        ForeignKey(
            entity = Siswa::class,
            parentColumns = ["id"],
            childColumns = ["siswaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TujuanPembelajaran::class,
            parentColumns = ["id"],
            childColumns = ["tpId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["siswaId", "tpId"], unique = true)]
)
data class NilaiFormatif(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val siswaId: Long,
    val tpId: Long,
    val kategori: KategoriFormatif = KategoriFormatif.CUKUP,
    val catatan: String = "",            // observasi / catatan proses
    val skorInternal: Int? = null        // opsional, hanya untuk guru
)

// ==================== PENILAIAN SUMATIF ====================
@Entity(
    tableName = "nilai_sumatif",
    foreignKeys = [
        ForeignKey(
            entity = Siswa::class,
            parentColumns = ["id"],
            childColumns = ["siswaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TujuanPembelajaran::class,
            parentColumns = ["id"],
            childColumns = ["tpId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["siswaId", "tpId"], unique = true)]
)
data class NilaiSumatif(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val siswaId: Long,
    val tpId: Long,
    val nilai: Float? = null             // 0 - 100
)

// ==================== NILAI SAS (Sumatif Akhir Semester) ====================
@Entity(
    tableName = "nilai_sas",
    foreignKeys = [
        ForeignKey(
            entity = Siswa::class,
            parentColumns = ["id"],
            childColumns = ["siswaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["siswaId"], unique = true)]
)
data class NilaiSas(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val siswaId: Long,
    val nilai: Float? = null             // 0 - 100
)

// ==================== DESKRIPSI CAPAIAN (bisa diedit manual) ====================
@Entity(
    tableName = "deskripsi_capaian",
    foreignKeys = [
        ForeignKey(
            entity = Siswa::class,
            parentColumns = ["id"],
            childColumns = ["siswaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["siswaId"], unique = true)]
)
data class DeskripsiCapaian(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val siswaId: Long,
    val deskripsi: String = "",          // narasi otomatis + bisa diedit
    val isManual: Boolean = false        // true jika guru sudah mengedit
)

// ==================== MODEL HELPER UNTUK UI ====================
data class SiswaWithNilai(
    val siswa: Siswa,
    val nilaiFormatif: Map<Long, NilaiFormatif> = emptyMap(), // key = tpId
    val nilaiSumatif: Map<Long, NilaiSumatif> = emptyMap(),
    val nilaiSas: NilaiSas? = null,
    val deskripsi: DeskripsiCapaian? = null,
    val nilaiAkhir: Float? = null
)

data class RekapTp(
    val tp: TujuanPembelajaran,
    val perluBimbingan: List<Siswa> = emptyList(),
    val cukup: List<Siswa> = emptyList(),
    val baik: List<Siswa> = emptyList(),
    val sangatBaik: List<Siswa> = emptyList()
)
