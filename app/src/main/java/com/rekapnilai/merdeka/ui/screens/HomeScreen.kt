package com.rekapnilai.merdeka.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rekapnilai.merdeka.ui.components.AppTopBar
import com.rekapnilai.merdeka.ui.components.BigMenuButton
import com.rekapnilai.merdeka.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToTp: () -> Unit,
    onNavigateToSiswa: () -> Unit,
    onNavigateToRekapTp: () -> Unit,
    onNavigateToBackup: () -> Unit,
    onSiswaClick: (Long) -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val siswaList by viewModel.siswaList.collectAsState()
    val tpList by viewModel.tujuanPembelajaran.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(title = "Rekap Nilai Merdeka")
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header identitas
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = settings?.namaSekolah?.ifBlank { "Belum diatur" } ?: "Belum diatur",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = listOfNotNull(
                                settings?.kelasFase?.takeIf { it.isNotBlank() },
                                settings?.mataPelajaran?.takeIf { it.isNotBlank() }
                            ).joinToString(" • ").ifBlank { "Kelas & Mapel belum diatur" }
                        )
                        Text(
                            text = listOfNotNull(
                                settings?.tahunAjaran?.takeIf { it.isNotBlank() },
                                settings?.semester?.takeIf { it.isNotBlank() }
                            ).joinToString(" • ").ifBlank { "Tahun ajaran belum diatur" },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Menu utama
            item {
                Text(
                    text = "Menu Utama",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                BigMenuButton(
                    text = "Pengaturan Kelas & Mapel",
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    onClick = onNavigateToSettings
                )
            }
            item {
                BigMenuButton(
                    text = "Tujuan Pembelajaran (TP)",
                    icon = { Icon(Icons.Default.Flag, contentDescription = null) },
                    onClick = onNavigateToTp,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            }
            item {
                BigMenuButton(
                    text = "Daftar Siswa",
                    icon = { Icon(Icons.Default.People, contentDescription = null) },
                    onClick = onNavigateToSiswa
                )
            }
            item {
                BigMenuButton(
                    text = "Rekap per TP (Remedial / Pengayaan)",
                    icon = { Icon(Icons.Default.Analytics, contentDescription = null) },
                    onClick = onNavigateToRekapTp,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            }
            item {
                BigMenuButton(
                    text = "Backup / Export / Import",
                    icon = { Icon(Icons.Default.CloudUpload, contentDescription = null) },
                    onClick = onNavigateToBackup
                )
            }

            // Ringkasan cepat
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ringkasan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Siswa",
                        value = "${siswaList.size}",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "TP",
                        value = "${tpList.size}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Akses cepat siswa (5 teratas)
            if (siswaList.isNotEmpty()) {
                item {
                    Text(
                        text = "Akses Cepat Input Nilai",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(siswaList.take(8)) { siswa ->
                    Card(
                        onClick = { onSiswaClick(siswa.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${siswa.nomor}.",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(36.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = siswa.nama, fontWeight = FontWeight.Medium)
                                if (siswa.nisNisn.isNotBlank()) {
                                    Text(
                                        text = siswa.nisNisn,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
