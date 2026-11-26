package com.rekapnilai.merdeka.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rekapnilai.merdeka.data.model.*
import com.rekapnilai.merdeka.data.repository.RekapRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(private val repository: RekapRepository) : ViewModel() {

    val settings: StateFlow<AppSettings?> = repository.getSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val tujuanPembelajaran: StateFlow<List<TujuanPembelajaran>> = repository.getAllTp()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val siswaList: StateFlow<List<Siswa>> = repository.getAllSiswa()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredSiswa: StateFlow<List<Siswa>> = combine(siswaList, _searchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.nama.contains(query, ignoreCase = true) ||
                    it.nisNisn.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFormatif: StateFlow<List<NilaiFormatif>> = repository.getAllFormatif()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSumatif: StateFlow<List<NilaiSumatif>> = repository.getAllSumatif()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSas: StateFlow<List<NilaiSas>> = repository.getAllSas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDeskripsi: StateFlow<List<DeskripsiCapaian>> = repository.getAllDeskripsi()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Settings
    fun saveSettings(settings: AppSettings) = viewModelScope.launch {
        repository.saveSettings(settings)
    }

    // TP
    fun addTp(kode: String, deskripsi: String, kktp: Int, urutan: Int) = viewModelScope.launch {
        repository.insertTp(
            TujuanPembelajaran(kodeTp = kode, deskripsi = deskripsi, targetKktp = kktp, urutan = urutan)
        )
    }

    fun updateTp(tp: TujuanPembelajaran) = viewModelScope.launch {
        repository.updateTp(tp)
    }

    fun deleteTp(tp: TujuanPembelajaran) = viewModelScope.launch {
        repository.deleteTp(tp)
    }

    // Siswa
    fun addSiswa(nomor: Int, nis: String, nama: String) = viewModelScope.launch {
        repository.insertSiswa(Siswa(nomor = nomor, nisNisn = nis, nama = nama))
    }

    fun updateSiswa(siswa: Siswa) = viewModelScope.launch {
        repository.updateSiswa(siswa)
    }

    fun deleteSiswa(siswa: Siswa) = viewModelScope.launch {
        repository.deleteSiswa(siswa)
    }

    fun importSiswa(list: List<Siswa>) = viewModelScope.launch {
        repository.insertSiswaBatch(list)
    }

    // Nilai
    fun saveFormatif(siswaId: Long, tpId: Long, kategori: KategoriFormatif, catatan: String, skor: Int?) =
        viewModelScope.launch {
            val existing = allFormatif.value.find { it.siswaId == siswaId && it.tpId == tpId }
            repository.upsertFormatif(
                NilaiFormatif(
                    id = existing?.id ?: 0,
                    siswaId = siswaId,
                    tpId = tpId,
                    kategori = kategori,
                    catatan = catatan,
                    skorInternal = skor
                )
            )
        }

    fun saveSumatif(siswaId: Long, tpId: Long, nilai: Float?) = viewModelScope.launch {
        val existing = allSumatif.value.find { it.siswaId == siswaId && it.tpId == tpId }
        repository.upsertSumatif(
            NilaiSumatif(
                id = existing?.id ?: 0,
                siswaId = siswaId,
                tpId = tpId,
                nilai = nilai
            )
        )
        // Regenerate deskripsi jika belum manual
        repository.regenerateDeskripsiJikaBelumManual(siswaId)
    }

    fun saveSas(siswaId: Long, nilai: Float?) = viewModelScope.launch {
        val existing = allSas.value.find { it.siswaId == siswaId }
        repository.upsertSas(
            NilaiSas(
                id = existing?.id ?: 0,
                siswaId = siswaId,
                nilai = nilai
            )
        )
    }

    fun saveDeskripsi(siswaId: Long, text: String, isManual: Boolean = true) = viewModelScope.launch {
        val existing = allDeskripsi.value.find { it.siswaId == siswaId }
        repository.upsertDeskripsi(
            DeskripsiCapaian(
                id = existing?.id ?: 0,
                siswaId = siswaId,
                deskripsi = text,
                isManual = isManual
            )
        )
    }

    fun regenerateDeskripsi(siswaId: Long) = viewModelScope.launch {
        val text = repository.generateDeskripsiOtomatis(siswaId)
        saveDeskripsi(siswaId, text, isManual = false)
    }

    suspend fun getNilaiAkhir(siswaId: Long): Float? = repository.hitungNilaiAkhir(siswaId)

    suspend fun getRekapPerTp(): List<RekapTp> = repository.getRekapPerTp()

    suspend fun exportJson() = repository.exportToJson()
    suspend fun exportSiswaCsv() = repository.exportSiswaCsv()

    companion object {
        fun factory(repository: RekapRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel(repository) as T
                }
            }
    }
}
