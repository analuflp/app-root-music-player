package com.alf.app_root_music_player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alf.app_root_music_player.composable.MiniMusicCard
import com.alf.app_root_music_player.player.PlayerUiState
import com.alf.app_root_music_player.player.playerViewModel

@Composable
fun MiniMusicPlayerScreen(viewModel: playerViewModel) {
    val context = LocalContext.current
    val uiState: PlayerUiState by viewModel.uiState.collectAsStateWithLifecycle()

    val musics = uiState.musics
    val currentIndex = uiState.currentIndex
    val isPlaying = uiState.isPlaying

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "Mini Player Root",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(musics) { index, music ->
                val isCurrent = index == currentIndex
                MiniMusicCard(
                    musicTitle = music.title,
                    coverResId = music.coverResId,
                    isCurrent = isCurrent,
                    isPlaying = isCurrent && isPlaying,
                    onPlayPauseClick = {
                        if (isCurrent) {
                            // mesma música → só dá play/pause
                            viewModel.onPlayPauseClicked(context)
                        } else {
                            // clicou em outro card → toca essa
                            viewModel.onMusicSelected(index, context)
                        }
                    },
                    onNextClick = { viewModel.onNextClicked(context) },
                    onPreviousClick = { viewModel.onPreviousClicked(context) },
                    onShuffleClick = {
                        // por enquanto não faz nada, é só visual
                        // depois podemos implementar shuffle de verdade :)
                    }
                )
            }
        }
    }
}
