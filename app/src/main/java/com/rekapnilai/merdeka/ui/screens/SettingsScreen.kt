package com.rekapnilai.merdeka.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rekapnilai.merdeka.data.model.AppSettings
import com.rekapnilai.merdeka.ui.components.AppTopBar
import com.rekapnilai.merdeka.ui.components.SectionTitle
import com.rekapnilai.merdeka.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val current by viewModel.settings.collectAsState()
    var namaSekolah by remember(current) { mutableStateOf(current?.namaSekolah ?: "") }
    var kelasFase by remember(current) { mutableStateOf(current?.kelasFase ?: "") }
    var mataPelajaran by remember(current) { mutableStateOf(current?.mataPelajaran ?: "") }
    var tahunAjaran by remember(current) { mutableStateOf(current?.tahunAjaran ?: "") }
    var semester by remember(current) { mutableStateOf(current?.semester ?: "Ganjil") }
    var bobotSumatif by remember(current) { mutableStateOf((current?.bobotSumatifTp ?: 0.7f).toString()) }
    var bobotSas by remember(current) { mutableStateOf((current?.bobotSas ?: 0.3f).toString()) }
    var kktp by remember(current) { mutableStateOf((current?.kktpDefault ?: 75).toString()) }

    var showSaved by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AppTopBar(title = "Pengaturan Kelas & Mapel", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionTitle("Identitas")
            OutlinedTextField(
                value = namaSekolah,
                onValueChange = { namaSekolah = it },
                label = { Text("Nama Sekolah") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = kelasFase,
                onValueChange = { kelasFase = it },
                label = { Text("Kelas / Fase (contoh: VII-A / Fase D)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = mataPelajaran,
                onValueChange = { mataPelajaran = it },
                label = { Text("Mata Pelajaran") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = tahunAjaran,
                onValueChange = { tahunAjaran = it },
                label = { Text("Tahun Ajaran (contoh: 2025/2026)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Semester dropdown
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = semester,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Semester") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("Ganjil", "Genap").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                semester = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            SectionTitle("Bobot Nilai Akhir")
            Text(
                text = "Nilai Akhir = (Rata-rata Sumatif TP × Bobot Sumatif) + (SAS × Bobot SAS)",
                style = MaterialTheme.typography.bodySmall
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = bobotSumatif,
                    onValueChange = { bobotSumatif = it },
                    label = { Text("Bobot Sumatif TP (0-1)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = bobotSas,
                    onValueChange = { bobotSas = it },
                    label = { Text("Bobot SAS (0-1)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
            OutlinedTextField(
                value = kktp,
                onValueChange = { kktp = it },
                label = { Text("KKTP Default") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val settings = AppSettings(
                        id = 1,
                        namaSekolah = namaSekolah.trim(),
                        kelasFase = kelasFase.trim(),
                        mataPelajaran = mataPelajaran.trim(),
                        tahunAjaran = tahunAjaran.trim(),
                        semester = semester,
                        bobotSumatifTp = bobotSumatif.toFloatOrNull() ?: 0.7f,
                        bobotSas = bobotSas.toFloatOrNull() ?: 0.3f,
                        kktpDefault = kktp.toIntOrNull() ?: 75
                    )
                    viewModel.saveSettings(settings)
                    showSaved = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Simpan Pengaturan")
            }

            if (showSaved) {
                Text(
                    text = "✓ Pengaturan berhasil disimpan",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}
