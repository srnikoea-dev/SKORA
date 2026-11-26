package com.rekapnilai.merdeka.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rekapnilai.merdeka.data.model.KategoriFormatif
import com.rekapnilai.merdeka.ui.components.AppTopBar
import com.rekapnilai.merdeka.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputNilaiScreen(
    siswaId: Long,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onLihatRekap: () -> Unit
) {
    val siswaList by viewModel.siswaList.collectAsState()
    val tpList by viewModel.tujuanPembelajaran.collectAsState()
    val allFormatif by viewModel.allFormatif.collectAsState()
    val allSumatif by viewModel.allSumatif.collectAsState()
    val allSas by viewModel.allSas.collectAsState()

    val siswa = siswaList.find { it.id == siswaId }
    val scope = rememberCoroutineScope()
    var nilaiAkhir by remember { mutableStateOf<Float?>(null) }

    LaunchedEffect(siswaId, allSumatif, allSas) {
        nilaiAkhir = viewModel.getNilaiAkhir(siswaId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = siswa?.nama ?: "Input Nilai",
                onBack = onBack,
                actions = {
                    IconButton(onClick = onLihatRekap) {
                        Icon(Icons.Default.Description, contentDescription = "Lihat Rekap")
                    }
                }
            )
        }
    ) { padding ->
        if (siswa == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Siswa tidak ditemukan")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text("No. ${siswa.nomor} • ${siswa.nisNisn.ifBlank { "-" }}", style = MaterialTheme.typography.bodySmall)
                        Text(siswa.nama, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        if (nilaiAkhir != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Nilai Akhir: ${"%.1f".format(nilaiAkhir)}",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Nilai SAS
            item {
                Text("Sumatif Akhir Semester (SAS)", fontWeight = FontWeight.Bold)
                val currentSas = allSas.find { it.siswaId == siswaId }?.nilai
                var sasText by remember(currentSas) { mutableStateOf(currentSas?.toString() ?: "") }
                OutlinedTextField(
                    value = sasText,
                    onValueChange = { sasText = it },
                    label = { Text("Nilai SAS (0-100)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        TextButton(onClick = {
                            val v = sasText.toFloatOrNull()?.coerceIn(0f, 100f)
                            viewModel.saveSas(siswaId, v)
                        }) { Text("Simpan") }
                    }
                )
            }

            items(tpList, key = { it.id }) { tp ->
                val formatif = allFormatif.find { it.siswaId == siswaId && it.tpId == tp.id }
                val sumatif = allSumatif.find { it.siswaId == siswaId && it.tpId == tp.id }

                TpNilaiCard(
                    kodeTp = tp.kodeTp,
                    deskripsi = tp.deskripsi,
                    kktp = tp.targetKktp,
                    currentKategori = formatif?.kategori ?: KategoriFormatif.CUKUP,
                    currentCatatan = formatif?.catatan ?: "",
                    currentSkorInternal = formatif?.skorInternal,
                    currentSumatif = sumatif?.nilai,
                    onSaveFormatif = { kat, catatan, skor ->
                        viewModel.saveFormatif(siswaId, tp.id, kat, catatan, skor)
                    },
                    onSaveSumatif = { nilai ->
                        viewModel.saveSumatif(siswaId, tp.id, nilai)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TpNilaiCard(
    kodeTp: String,
    deskripsi: String,
    kktp: Int,
    currentKategori: KategoriFormatif,
    currentCatatan: String,
    currentSkorInternal: Int?,
    currentSumatif: Float?,
    onSaveFormatif: (KategoriFormatif, String, Int?) -> Unit,
    onSaveSumatif: (Float?) -> Unit
) {
    var kategori by remember(currentKategori) { mutableStateOf(currentKategori) }
    var catatan by remember(currentCatatan) { mutableStateOf(currentCatatan) }
    var skorInternal by remember(currentSkorInternal) { mutableStateOf(currentSkorInternal?.toString() ?: "") }
    var sumatifText by remember(currentSumatif) { mutableStateOf(currentSumatif?.toString() ?: "") }
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(kodeTp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(deskripsi, style = MaterialTheme.typography.bodyMedium)
            Text("KKTP: $kktp", style = MaterialTheme.typography.bodySmall)

            HorizontalDivider()

            // Formatif
            Text("Penilaian Formatif", fontWeight = FontWeight.SemiBold)
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = when (kategori) {
                        KategoriFormatif.PERLU_BIMBINGAN -> "Perlu Bimbingan"
                        KategoriFormatif.CUKUP -> "Cukup"
                        KategoriFormatif.BAIK -> "Baik"
                        KategoriFormatif.SANGAT_BAIK -> "Sangat Baik"
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kategori Ketercapaian") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    KategoriFormatif.entries.forEach { kat ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    when (kat) {
                                        KategoriFormatif.PERLU_BIMBINGAN -> "Perlu Bimbingan"
                                        KategoriFormatif.CUKUP -> "Cukup"
                                        KategoriFormatif.BAIK -> "Baik"
                                        KategoriFormatif.SANGAT_BAIK -> "Sangat Baik"
                                    }
                                )
                            },
                            onClick = {
                                kategori = kat
                                expanded = false
                            }
                        )
                    }
                }
            }
            OutlinedTextField(
                value = catatan,
                onValueChange = { catatan = it },
                label = { Text("Catatan Proses / Observasi") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            OutlinedTextField(
                value = skorInternal,
                onValueChange = { skorInternal = it },
                label = { Text("Skor Internal (opsional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    onSaveFormatif(kategori, catatan, skorInternal.toIntOrNull())
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Simpan Formatif") }

            HorizontalDivider()

            // Sumatif
            Text("Penilaian Sumatif", fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = sumatifText,
                onValueChange = { sumatifText = it },
                label = { Text("Nilai Sumatif (0-100)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    TextButton(onClick = {
                        val v = sumatifText.toFloatOrNull()?.coerceIn(0f, 100f)
                        onSaveSumatif(v)
                    }) { Text("Simpan") }
                }
            )
        }
    }
}
