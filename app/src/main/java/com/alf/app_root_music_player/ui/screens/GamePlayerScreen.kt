package com.alf.app_root_music_player.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alf.app_root_music_player.components.formatTime

@Composable
fun GamePlayerScreen(
    gameId: String,
    onBackClick: () -> Unit,
    viewModel: GamePlayerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(gameId) {
        viewModel.loadGame(gameId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }
            uiState.error != null -> {
                Text(
                    text = "Error: ${uiState.error}",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            uiState.game != null -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Top bar with back button and game name
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = uiState.game!!.name,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // Soundtracks section
                        if (uiState.game!!.soundtracks.isNotEmpty()) {
                            Text(
                                text = "Soundtracks",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            uiState.game!!.soundtracks.forEach { soundtrack ->
                                SoundtrackCard(
                                    soundtrack = soundtrack,
                                    isCurrent = uiState.currentSoundtrack?.id == soundtrack.id,
                                    isPlaying = uiState.currentSoundtrack?.id == soundtrack.id && uiState.isPlaying,
                                    currentPosition = if (uiState.currentSoundtrack?.id == soundtrack.id) uiState.currentPosition else 0,
                                    duration = if (uiState.currentSoundtrack?.id == soundtrack.id) uiState.duration else 0,
                                    onSelect = { viewModel.selectSoundtrack(soundtrack) },
                                    onPlayPause = { viewModel.togglePlayPause() },
                                    onSeek = { position -> viewModel.seekTo(position) }
                                )
                            }
                        }

                        // Sound Effects section
                        if (uiState.game!!.soundEffects.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Sound Effects",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            // Use a regular grid layout instead of LazyVerticalGrid to avoid nested scrolling
                            val soundEffects = uiState.game!!.soundEffects
                            val rows = (soundEffects.size + 2) / 3 // Calculate number of rows needed
                            for (row in 0 until rows) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    for (col in 0 until 3) {
                                        val index = row * 3 + col
                                        if (index < soundEffects.size) {
                                            Box(modifier = Modifier.weight(1f)) {
                                                SoundEffectCard(
                                                    soundEffect = soundEffects[index],
                                                    onPlay = { viewModel.playSoundEffect(soundEffects[index]) }
                                                )
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                                if (row < rows - 1) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SoundtrackCard(
    soundtrack: com.alf.app_root_music_player.data.model.Soundtrack,
    isCurrent: Boolean,
    isPlaying: Boolean,
    currentPosition: Int,
    duration: Int,
    onSelect: () -> Unit,
    onPlayPause: () -> Unit,
    onSeek: (Int) -> Unit
) {
    val calculatedProgress = if (duration > 0) currentPosition.toFloat() / duration else 0f
    var sliderProgress by remember { mutableFloatStateOf(calculatedProgress) }
    var isDragging by remember { mutableStateOf(false) }
    
    // Update slider position when position changes (but not during dragging)
    LaunchedEffect(currentPosition, duration) {
        if (duration > 0 && !isDragging) {
            val newProgress = currentPosition.toFloat() / duration
            sliderProgress = newProgress
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) Color(0xFF1E1E1E) else Color(0xFF121212)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(IntrinsicSize.Min)
        ) {
            if (soundtrack.coverResId != 0) {
                Image(
                    painter = painterResource(id = soundtrack.coverResId),
                    contentDescription = soundtrack.title,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(
                    text = soundtrack.title,
                    color = Color.White,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1
                )

                if (isCurrent) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            formatTime(currentPosition),
                            color = Color.LightGray,
                            fontSize = 10.sp
                        )
                        Slider(
                            value = sliderProgress,
                            onValueChange = { newProgress ->
                                isDragging = true
                                sliderProgress = newProgress
                                val newPosition = (newProgress * duration).toInt()
                                onSeek(newPosition)
                            },
                            onValueChangeFinished = {
                                isDragging = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            enabled = true,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF4A90E2), // Match active track color
                                activeTrackColor = Color(0xFF4A90E2), // Blue for played portion
                                inactiveTrackColor = Color(0xFF3A3A3A) // Dark gray for unplayed portion
                            )
                        )
                        Text(
                            formatTime(duration),
                            color = Color.LightGray,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    IconButton(
                        onClick = onPlayPause,
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SoundEffectCard(
    soundEffect: com.alf.app_root_music_player.data.model.SoundEffect,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPlay),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF121212)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (soundEffect.emoji.isNotEmpty()) {
                Text(
                    text = soundEffect.emoji,
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = soundEffect.title,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

