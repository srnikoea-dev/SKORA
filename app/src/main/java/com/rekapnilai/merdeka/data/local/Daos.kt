package com.rekapnilai.merdeka.data.local

import androidx.room.*
import com.rekapnilai.merdeka.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSettings(): Flow<AppSettings?>

    @Query("SELECT * FROM settings WHERE id = 1")
    suspend fun getSettingsOnce(): AppSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: AppSettings)
}

@Dao
interface TujuanPembelajaranDao {
    @Query("SELECT * FROM tujuan_pembelajaran ORDER BY urutan ASC, id ASC")
    fun getAll(): Flow<List<TujuanPembelajaran>>

    @Query("SELECT * FROM tujuan_pembelajaran ORDER BY urutan ASC, id ASC")
    suspend fun getAllOnce(): List<TujuanPembelajaran>

    @Query("SELECT * FROM tujuan_pembelajaran WHERE id = :id")
    suspend fun getById(id: Long): TujuanPembelajaran?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tp: TujuanPembelajaran): Long

    @Update
    suspend fun update(tp: TujuanPembelajaran)

    @Delete
    suspend fun delete(tp: TujuanPembelajaran)

    @Query("DELETE FROM tujuan_pembelajaran")
    suspend fun deleteAll()
}

@Dao
interface SiswaDao {
    @Query("SELECT * FROM siswa ORDER BY nomor ASC, nama ASC")
    fun getAll(): Flow<List<Siswa>>

    @Query("SELECT * FROM siswa ORDER BY nomor ASC, nama ASC")
    suspend fun getAllOnce(): List<Siswa>

    @Query("SELECT * FROM siswa WHERE id = :id")
    suspend fun getById(id: Long): Siswa?

    @Query("SELECT * FROM siswa WHERE nama LIKE '%' || :query || '%' OR nisNisn LIKE '%' || :query || '%' ORDER BY nomor ASC")
    fun search(query: String): Flow<List<Siswa>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(siswa: Siswa): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(siswaList: List<Siswa>)

    @Update
    suspend fun update(siswa: Siswa)

    @Delete
    suspend fun delete(siswa: Siswa)

    @Query("DELETE FROM siswa")
    suspend fun deleteAll()
}

@Dao
interface NilaiFormatifDao {
    @Query("SELECT * FROM nilai_formatif")
    fun getAll(): Flow<List<NilaiFormatif>>

    @Query("SELECT * FROM nilai_formatif WHERE siswaId = :siswaId")
    suspend fun getBySiswa(siswaId: Long): List<NilaiFormatif>

    @Query("SELECT * FROM nilai_formatif WHERE tpId = :tpId")
    suspend fun getByTp(tpId: Long): List<NilaiFormatif>

    @Query("SELECT * FROM nilai_formatif WHERE siswaId = :siswaId AND tpId = :tpId")
    suspend fun get(siswaId: Long, tpId: Long): NilaiFormatif?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(nilai: NilaiFormatif)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<NilaiFormatif>)

    @Delete
    suspend fun delete(nilai: NilaiFormatif)

    @Query("DELETE FROM nilai_formatif")
    suspend fun deleteAll()
}

@Dao
interface NilaiSumatifDao {
    @Query("SELECT * FROM nilai_sumatif")
    fun getAll(): Flow<List<NilaiSumatif>>

    @Query("SELECT * FROM nilai_sumatif WHERE siswaId = :siswaId")
    suspend fun getBySiswa(siswaId: Long): List<NilaiSumatif>

    @Query("SELECT * FROM nilai_sumatif WHERE tpId = :tpId")
    suspend fun getByTp(tpId: Long): List<NilaiSumatif>

    @Query("SELECT * FROM nilai_sumatif WHERE siswaId = :siswaId AND tpId = :tpId")
    suspend fun get(siswaId: Long, tpId: Long): NilaiSumatif?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(nilai: NilaiSumatif)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<NilaiSumatif>)

    @Delete
    suspend fun delete(nilai: NilaiSumatif)

    @Query("DELETE FROM nilai_sumatif")
    suspend fun deleteAll()
}

@Dao
interface NilaiSasDao {
    @Query("SELECT * FROM nilai_sas")
    fun getAll(): Flow<List<NilaiSas>>

    @Query("SELECT * FROM nilai_sas WHERE siswaId = :siswaId")
    suspend fun getBySiswa(siswaId: Long): NilaiSas?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(nilai: NilaiSas)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<NilaiSas>)

    @Query("DELETE FROM nilai_sas")
    suspend fun deleteAll()
}

@Dao
interface DeskripsiCapaianDao {
    @Query("SELECT * FROM deskripsi_capaian")
    fun getAll(): Flow<List<DeskripsiCapaian>>

    @Query("SELECT * FROM deskripsi_capaian WHERE siswaId = :siswaId")
    suspend fun getBySiswa(siswaId: Long): DeskripsiCapaian?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(deskripsi: DeskripsiCapaian)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<DeskripsiCapaian>)

    @Query("DELETE FROM deskripsi_capaian")
    suspend fun deleteAll()
}
