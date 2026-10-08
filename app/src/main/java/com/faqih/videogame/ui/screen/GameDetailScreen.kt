package com.faqih.videogame.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.faqih.videogame.viewmodel.GameDetailUiState
import com.faqih.videogame.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    viewModel: GameViewModel,
    gameId: Int,
    onBack: () -> Unit
) {

    val detailUiState by viewModel.detailUiState.collectAsState()

    LaunchedEffect(gameId) {
        viewModel.loadGameDetail(gameId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Detail Game")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Hijau
                    titleContentColor = MaterialTheme.colorScheme.onPrimary // Putih
                ),
                navigationIcon = {
                    IconButton (onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        when (val state = detailUiState) {

            is GameDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is GameDetailUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.loadGameDetail(gameId) }
                    ) {
                        Text(text = "Coba Lagi")
                    }
                }
            }

            is GameDetailUiState.Success -> {

                val game = state.game

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Judul game
                    Text(
                        text = game.name ?: "Tanpa Judul",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rating
                    DetailInfoRow(
                        label = "Rating",
                        value = game.rating?.let {
                            String.format("%.1f / 5.0", it)
                        } ?: "Belum ada rating"
                    )

                    // Tanggal rilis
                    DetailInfoRow(
                        label = "Tanggal Rilis",
                        value = game.released ?: "Tidak diketahui"
                    )

                    // Metacritic
                    DetailInfoRow(
                        label = "Metacritic",
                        value = game.metacritic?.toString() ?: "-"
                    )

                    // Genre
                    DetailInfoRow(
                        label = "Genre",
                        value = game.genres
                            ?.mapNotNull { it.name }
                            ?.joinToString(", ")
                            ?: "-"
                    )

                    // Platform
                    DetailInfoRow(
                        label = "Platform",
                        value = game.platforms
                            ?.mapNotNull { it.platform?.name }
                            ?.joinToString(", ")
                            ?: "-"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Deskripsi
                    Text(
                        text = "Deskripsi",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = game.description_raw?.trim()
                            ?.takeIf { it.isNotEmpty() }
                            ?: "Deskripsi tidak tersedia.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun DetailInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.35f),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            modifier = Modifier.weight(0.65f),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
