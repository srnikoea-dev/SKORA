package com.rekapnilai.merdeka.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rekapnilai.merdeka.data.model.KategoriFormatif
import com.rekapnilai.merdeka.ui.components.AppTopBar
import com.rekapnilai.merdeka.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RekapSiswaScreen(
    siswaId: Long,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val siswaList by viewModel.siswaList.collectAsState()
    val tpList by viewModel.tujuanPembelajaran.collectAsState()
    val allFormatif by viewModel.allFormatif.collectAsState()
    val allSumatif by viewModel.allSumatif.collectAsState()
    val allSas by viewModel.allSas.collectAsState()
    val allDeskripsi by viewModel.allDeskripsi.collectAsState()
    val settings by viewModel.settings.collectAsState()

    val siswa = siswaList.find { it.id == siswaId }
    var nilaiAkhir by remember { mutableStateOf<Float?>(null) }
    var deskripsiText by remember { mutableStateOf("") }
    var isEditingDeskripsi by remember { mutableStateOf(false) }

    LaunchedEffect(siswaId) {
        nilaiAkhir = viewModel.getNilaiAkhir(siswaId)
        val existing = allDeskripsi.find { it.siswaId == siswaId }
        deskripsiText = existing?.deskripsi ?: ""
        if (deskripsiText.isBlank()) {
            viewModel.regenerateDeskripsi(siswaId)
        }
    }

    LaunchedEffect(allDeskripsi) {
        val existing = allDeskripsi.find { it.siswaId == siswaId }
        if (existing != null && !isEditingDeskripsi) {
            deskripsiText = existing.deskripsi
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "Rekap Siswa", onBack = onBack) }
    ) { padding ->
        if (siswa == null) {
            Box(Modifier.fillMaxSize().padding(padding)) { Text("Siswa tidak ditemukan") }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Card {
                Column(Modifier.padding(16.dp)) {
                    Text(settings?.namaSekolah ?: "", style = MaterialTheme.typography.bodySmall)
                    Text("${settings?.kelasFase} • ${settings?.mataPelajaran}", style = MaterialTheme.typography.bodySmall)
                    Text("${settings?.tahunAjaran} • ${settings?.semester}", style = MaterialTheme.typography.bodySmall)
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Text("No. ${siswa.nomor} • ${siswa.nisNisn}", style = MaterialTheme.typography.bodySmall)
                    Text(siswa.nama, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    if (nilaiAkhir != null) {
                        Text(
                            "Nilai Akhir: ${"%.1f".format(nilaiAkhir)}",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            // Rincian TP
            Text("Rincian Capaian per TP", fontWeight = FontWeight.Bold)
            tpList.forEach { tp ->
                val formatif = allFormatif.find { it.siswaId == siswaId && it.tpId == tp.id }
                val sumatif = allSumatif.find { it.siswaId == siswaId && it.tpId == tp.id }
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(tp.kodeTp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(tp.deskripsi, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Formatif: ${
                                when (formatif?.kategori) {
                                    KategoriFormatif.PERLU_BIMBINGAN -> "Perlu Bimbingan"
                                    KategoriFormatif.CUKUP -> "Cukup"
                                    KategoriFormatif.BAIK -> "Baik"
                                    KategoriFormatif.SANGAT_BAIK -> "Sangat Baik"
                                    null -> "-"
                                }
                            }"
                        )
                        if (!formatif?.catatan.isNullOrBlank()) {
                            Text("Catatan: ${formatif?.catatan}", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("Sumatif: ${sumatif?.nilai?.let { "%.0f".format(it) } ?: "-"} (KKTP: ${tp.targetKktp})")
                    }
                }
            }

            val sas = allSas.find { it.siswaId == siswaId }
            if (sas?.nilai != null) {
                Text("SAS: ${"%.0f".format(sas.nilai)}", fontWeight = FontWeight.Medium)
            }

            // Deskripsi Capaian
            Text("Deskripsi Capaian Kompetensi", fontWeight = FontWeight.Bold)
            if (isEditingDeskripsi) {
                OutlinedTextField(
                    value = deskripsiText,
                    onValueChange = { deskripsiText = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        viewModel.saveDeskripsi(siswaId, deskripsiText, isManual = true)
                        isEditingDeskripsi = false
                    }) { Text("Simpan") }
                    OutlinedButton(onClick = { isEditingDeskripsi = false }) { Text("Batal") }
                }
            } else {
                Card {
                    Text(
                        text = deskripsiText.ifBlank { "Belum ada deskripsi." },
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { isEditingDeskripsi = true }) { Text("Edit Manual") }
                    OutlinedButton(onClick = { viewModel.regenerateDeskripsi(siswaId) }) {
                        Text("Generate Ulang")
                    }
                }
            }
        }
    }
}
