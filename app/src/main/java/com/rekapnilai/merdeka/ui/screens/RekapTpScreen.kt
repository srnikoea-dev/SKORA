package com.rekapnilai.merdeka.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rekapnilai.merdeka.data.model.KategoriFormatif
import com.rekapnilai.merdeka.data.model.RekapTp
import com.rekapnilai.merdeka.ui.components.AppTopBar
import com.rekapnilai.merdeka.ui.components.EmptyState
import com.rekapnilai.merdeka.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RekapTpScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val tpList by viewModel.tujuanPembelajaran.collectAsState()
    val siswaList by viewModel.siswaList.collectAsState()
    val allFormatif by viewModel.allFormatif.collectAsState()

    val rekapList = remember(tpList, siswaList, allFormatif) {
        tpList.map { tp ->
            val formatifForTp = allFormatif.filter { it.tpId == tp.id }
            val siswaById = siswaList.associateBy { it.id }

            fun byKategori(kat: KategoriFormatif) =
                formatifForTp.filter { it.kategori == kat }.mapNotNull { siswaById[it.siswaId] }

            RekapTp(
                tp = tp,
                perluBimbingan = byKategori(KategoriFormatif.PERLU_BIMBINGAN),
                cukup = byKategori(KategoriFormatif.CUKUP),
                baik = byKategori(KategoriFormatif.BAIK),
                sangatBaik = byKategori(KategoriFormatif.SANGAT_BAIK)
            )
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "Rekap per TP", onBack = onBack) }
    ) { padding ->
        if (tpList.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                EmptyState("Belum ada Tujuan Pembelajaran.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(rekapList) { rekap ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                rekap.tp.kodeTp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(rekap.tp.deskripsi, style = MaterialTheme.typography.bodySmall)

                            KategoriSection("Perlu Bimbingan / Remedial", rekap.perluBimbingan, MaterialTheme.colorScheme.error)
                            KategoriSection("Cukup", rekap.cukup, MaterialTheme.colorScheme.onSurfaceVariant)
                            KategoriSection("Baik", rekap.baik, MaterialTheme.colorScheme.tertiary)
                            KategoriSection("Sangat Baik / Pengayaan", rekap.sangatBaik, MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KategoriSection(
    title: String,
    siswa: List<com.rekapnilai.merdeka.data.model.Siswa>,
    color: androidx.compose.ui.graphics.Color
) {
    Text(title, fontWeight = FontWeight.SemiBold, color = color)
    if (siswa.isEmpty()) {
        Text("—", style = MaterialTheme.typography.bodySmall)
    } else {
        Text(
            siswa.joinToString(", ") { it.nama },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
