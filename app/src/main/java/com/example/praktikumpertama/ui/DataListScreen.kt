package com.example.praktikumpertama.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.praktikumpertama.viewmodel.DataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataListScreen(navController: NavHostController, viewModel: DataViewModel) {
    val dataList by viewModel.dataList.observeAsState(emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var isBottomSheetVisible by remember { mutableStateOf(false) }
    var selectedYear by remember { mutableStateOf<Int?>(null) }

    val filteredList = dataList
        .filter { item ->
            item.namaProvinsi.contains(searchQuery, ignoreCase = true) ||
                    item.namaKabupatenKota.contains(searchQuery, ignoreCase = true)
        }
        .filter { item ->
            selectedYear == null || item.tahun == selectedYear
        }

    val availableYears = dataList.map { it.tahun }.distinct().sortedDescending() // Ambil semua tahun unik

    Column(modifier = Modifier.fillMaxSize()) {
        // **1️⃣ Row Search Bar & Back Button**
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
            }

            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                placeholder = { Text("Cari data...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // **2️⃣ Row untuk Filter**
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { isBottomSheetVisible = true }) {
                Text(text = "Tahun: ${selectedYear ?: "Semua"}")
            }

            Button(onClick = { /* Tambahkan logika untuk sorting */ }) {
                Text(text = "Sort")
            }
        }

        // **3️⃣ Bottom Sheet untuk Filter Tahun**
        if (isBottomSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { isBottomSheetVisible = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Pilih Tahun",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    availableYears.forEach { year ->
                        Button(
                            onClick = {
                                selectedYear = year
                                isBottomSheetVisible = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = year.toString())
                        }
                    }

                    Button(
                        onClick = {
                            selectedYear = null
                            isBottomSheetVisible = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Semua Tahun")
                    }
                }
            }
        }

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No Data Available", style = MaterialTheme.typography.headlineMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Provinsi: ${item.namaProvinsi} (${item.kodeProvinsi})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Kabupaten/Kota: ${item.namaKabupatenKota} (${item.kodeKabupatenKota})",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Total: ${item.total} ${item.satuan}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tahun: ${item.tahun}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        navController.navigate("edit/${item.id}")
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(text = "Edit")
                                }

                                Button(
                                    onClick = {
                                        navController.navigate("hapus/${item.id}") // Navigasi ke rute hapus
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(text = "Hapus")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}