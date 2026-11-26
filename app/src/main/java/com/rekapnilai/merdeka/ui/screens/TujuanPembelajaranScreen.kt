package com.rekapnilai.merdeka.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rekapnilai.merdeka.data.model.TujuanPembelajaran
import com.rekapnilai.merdeka.ui.components.AppTopBar
import com.rekapnilai.merdeka.ui.components.EmptyState
import com.rekapnilai.merdeka.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TujuanPembelajaranScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val tpList by viewModel.tujuanPembelajaran.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingTp by remember { mutableStateOf<TujuanPembelajaran?>(null) }

    Scaffold(
        topBar = { AppTopBar(title = "Tujuan Pembelajaran (TP)", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingTp = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah TP")
            }
        }
    ) { padding ->
        if (tpList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                EmptyState("Belum ada Tujuan Pembelajaran.\nTap tombol + untuk menambah.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tpList, key = { it.id }) { tp ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tp.kodeTp,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Row {
                                    IconButton(onClick = {
                                        editingTp = tp
                                        showDialog = true
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }
                                    IconButton(onClick = { viewModel.deleteTp(tp) }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Hapus",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                            Text(text = tp.deskripsi)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "KKTP: ${tp.targetKktp}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        TpDialog(
            initial = editingTp,
            nextUrutan = (tpList.maxOfOrNull { it.urutan } ?: 0) + 1,
            onDismiss = { showDialog = false },
            onSave = { kode, deskripsi, kktp, urutan ->
                if (editingTp != null) {
                    viewModel.updateTp(
                        editingTp!!.copy(
                            kodeTp = kode,
                            deskripsi = deskripsi,
                            targetKktp = kktp,
                            urutan = urutan
                        )
                    )
                } else {
                    viewModel.addTp(kode, deskripsi, kktp, urutan)
                }
                showDialog = false
            }
        )
    }
}

@Composable
private fun TpDialog(
    initial: TujuanPembelajaran?,
    nextUrutan: Int,
    onDismiss: () -> Unit,
    onSave: (kode: String, deskripsi: String, kktp: Int, urutan: Int) -> Unit
) {
    var kode by remember { mutableStateOf(initial?.kodeTp ?: "TP.${nextUrutan}") }
    var deskripsi by remember { mutableStateOf(initial?.deskripsi ?: "") }
    var kktp by remember { mutableStateOf((initial?.targetKktp ?: 75).toString()) }
    var urutan by remember { mutableStateOf((initial?.urutan ?: nextUrutan).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Tambah TP" else "Edit TP") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = kode,
                    onValueChange = { kode = it },
                    label = { Text("Kode TP") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi Tujuan Pembelajaran") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                OutlinedTextField(
                    value = kktp,
                    onValueChange = { kktp = it },
                    label = { Text("Target KKTP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = urutan,
                    onValueChange = { urutan = it },
                    label = { Text("Urutan") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        kode.trim(),
                        deskripsi.trim(),
                        kktp.toIntOrNull() ?: 75,
                        urutan.toIntOrNull() ?: nextUrutan
                    )
                },
                enabled = kode.isNotBlank() && deskripsi.isNotBlank()
            ) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
