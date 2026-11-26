package com.rekapnilai.merdeka

import android.app.Application
import com.rekapnilai.merdeka.data.repository.RekapRepository

class RekapNilaiApp : Application() {
    lateinit var repository: RekapRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = RekapRepository(this)
    }
}
