package com.rekapnilai.merdeka.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.rekapnilai.merdeka.ui.components.AppTopBar
import com.rekapnilai.merdeka.ui.components.SectionTitle
import com.rekapnilai.merdeka.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var message by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AppTopBar(title = "Backup & Export", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionTitle("Export Data")
            Text(
                "Semua data tersimpan lokal di perangkat. Export untuk cadangan atau pindah HP.",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        try {
                            val file = viewModel.exportJson()
                            shareFile(context, file, "application/json")
                            message = "Berhasil export: ${file.name}"
                        } catch (e: Exception) {
                            message = "Gagal export: ${e.message}"
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLoading
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Export Backup JSON (Lengkap)")
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        isLoading = true
                        try {
                            val file = viewModel.exportSiswaCsv()
                            shareFile(context, file, "text/csv")
                            message = "Berhasil export daftar siswa: ${file.name}"
                        } catch (e: Exception) {
                            message = "Gagal: ${e.message}"
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLoading
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Export Daftar Siswa (CSV)")
            }

            HorizontalDivider()

            SectionTitle("Import Data")
            Text(
                "Fitur import dari file JSON/CSV akan tersedia di versi berikutnya. Saat ini gunakan export JSON sebagai cadangan penuh.",
                style = MaterialTheme.typography.bodySmall
            )

            // Placeholder for future import via SAF
            OutlinedButton(
                onClick = {
                    message = "Gunakan file manager untuk menyimpan file JSON yang sudah di-export. Fitur import langsung sedang dikembangkan."
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.Upload, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Import dari File (Segera)")
            }

            if (message != null) {
                Card {
                    Text(message!!, modifier = Modifier.padding(16.dp))
                }
            }

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

private fun shareFile(context: android.content.Context, file: File, mimeType: String) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Bagikan / Simpan"))
}
