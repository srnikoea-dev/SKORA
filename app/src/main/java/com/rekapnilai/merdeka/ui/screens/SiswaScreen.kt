package com.rekapnilai.merdeka.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rekapnilai.merdeka.data.model.Siswa
import com.rekapnilai.merdeka.ui.components.AppTopBar
import com.rekapnilai.merdeka.ui.components.EmptyState
import com.rekapnilai.merdeka.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiswaScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onSiswaClick: (Long) -> Unit
) {
    val siswaList by viewModel.filteredSiswa.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingSiswa by remember { mutableStateOf<Siswa?>(null) }

    Scaffold(
        topBar = { AppTopBar(title = "Daftar Siswa", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingSiswa = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Siswa")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Cari nama atau NIS/NISN...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            if (siswaList.isEmpty()) {
                EmptyState(
                    if (searchQuery.isBlank()) "Belum ada siswa.\nTap + untuk menambah."
                    else "Tidak ditemukan."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(siswaList, key = { it.id }) { siswa ->
                        Card(
                            onClick = { onSiswaClick(siswa.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${siswa.nomor}",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(40.dp)
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
                                IconButton(onClick = {
                                    editingSiswa = siswa
                                    showDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = { viewModel.deleteSiswa(siswa) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Hapus",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        SiswaDialog(
            initial = editingSiswa,
            nextNomor = (siswaList.maxOfOrNull { it.nomor } ?: 0) + 1,
            onDismiss = { showDialog = false },
            onSave = { nomor, nis, nama ->
                if (editingSiswa != null) {
                    viewModel.updateSiswa(
                        editingSiswa!!.copy(nomor = nomor, nisNisn = nis, nama = nama)
                    )
                } else {
                    viewModel.addSiswa(nomor, nis, nama)
                }
                showDialog = false
            }
        )
    }
}

@Composable
private fun SiswaDialog(
    initial: Siswa?,
    nextNomor: Int,
    onDismiss: () -> Unit,
    onSave: (nomor: Int, nis: String, nama: String) -> Unit
) {
    var nomor by remember { mutableStateOf((initial?.nomor ?: nextNomor).toString()) }
    var nis by remember { mutableStateOf(initial?.nisNisn ?: "") }
    var nama by remember { mutableStateOf(initial?.nama ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Tambah Siswa" else "Edit Siswa") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nomor,
                    onValueChange = { nomor = it },
                    label = { Text("No Urut") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nis,
                    onValueChange = { nis = it },
                    label = { Text("NIS / NISN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Siswa") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        nomor.toIntOrNull() ?: nextNomor,
                        nis.trim(),
                        nama.trim()
                    )
                },
                enabled = nama.isNotBlank()
            ) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
