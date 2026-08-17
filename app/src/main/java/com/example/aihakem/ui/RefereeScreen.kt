package com.example.aihakem.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefereeScreen(viewModel: RefereeViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    var topic by remember { mutableStateOf("") }
    var nameA by remember { mutableStateOf("Ahmet") }
    var statementA by remember { mutableStateOf("") }
    var nameB by remember { mutableStateOf("Mehmet") }
    var statementB by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("⚖️ AI Tartışma Hakemi") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val state = uiState) {
                is UiState.Idle, is UiState.Error -> {
                    if (state is UiState.Error) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Tartışma Konusu (İsteğe Bağlı)") },
                        placeholder = { Text("Örn: Hafta sonu planı / Bulaşık sırası") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    HorizontalDivider()

                    // 1. Taraf
                    Text(
                        text = "1. Tarafın İfadesi",
                        style = MaterialTheme.typography.titleMedium
                    )
                    OutlinedTextField(
                        value = nameA,
                        onValueChange = { nameA = it },
                        label = { Text("1. Kişinin İsmi") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = statementA,
                        onValueChange = { statementA = it },
                        label = { Text("$nameA ne diyor?") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    HorizontalDivider()

                    // 2. Taraf
                    Text(
                        text = "2. Tarafın İfadesi",
                        style = MaterialTheme.typography.titleMedium
                    )
                    OutlinedTextField(
                        value = nameB,
                        onValueChange = { nameB = it },
                        label = { Text("2. Kişinin İsmi") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = statementB,
                        onValueChange = { statementB = it },
                        label = { Text("$nameB ne diyor?") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            viewModel.analyzeDispute(topic, nameA, statementA, nameB, statementB)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Kararı Açıkla & Gerekçelendir")
                    }
                }

                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(text = "Hakem taraf tutmadan ifadeleri inceliyor...")
                        }
                    }
                }

                is UiState.Success -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = state.resultText,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.reset() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Yeni Bir Tartışma Değerlendir")
                    }
                }
            }
        }
    }
}
