package com.rekapnilai.merdeka.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.rekapnilai.merdeka.data.model.*

class Converters {
    @TypeConverter
    fun fromKategori(value: KategoriFormatif): String = value.name

    @TypeConverter
    fun toKategori(value: String): KategoriFormatif = KategoriFormatif.valueOf(value)
}

@Database(
    entities = [
        AppSettings::class,
        TujuanPembelajaran::class,
        Siswa::class,
        NilaiFormatif::class,
        NilaiSumatif::class,
        NilaiSas::class,
        DeskripsiCapaian::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun tpDao(): TujuanPembelajaranDao
    abstract fun siswaDao(): SiswaDao
    abstract fun formatifDao(): NilaiFormatifDao
    abstract fun sumatifDao(): NilaiSumatifDao
    abstract fun sasDao(): NilaiSasDao
    abstract fun deskripsiDao(): DeskripsiCapaianDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rekap_nilai_merdeka.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
