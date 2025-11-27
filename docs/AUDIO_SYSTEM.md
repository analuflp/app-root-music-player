# Audio System Documentation

## Overview

The app uses two separate audio systems optimized for different use cases:

1. **MediaPlayer** - For soundtracks (background music)
2. **SoundPool** - For sound effects (short, one-shot sounds)

## Soundtrack System (MediaPlayer)

### Features

- **Looping**: Soundtracks automatically loop when they finish
- **Fade Transitions**: Smooth fade in/out when switching between soundtracks (500ms duration)
- **Progress Tracking**: Real-time position and duration tracking
- **Single Playback**: Only one soundtrack can play at a time

### How It Works

1. When a soundtrack is selected, the current one (if any) fades out
2. The new soundtrack fades in from silence
3. Progress is tracked via coroutines (updates every 200ms)
4. Soundtracks loop indefinitely until stopped or replaced

### Implementation Details

- Located in `AudioManager.playSoundtrack()`
- Uses `MediaPlayer.setVolume()` for fade transitions
- Volume is controlled in 20 steps over 500ms for smooth transitions

## Sound Effect System (SoundPool)

### Features

- **Simultaneous Playback**: Up to 10 sound effects can play at once
- **Low Latency**: Optimized for quick response times
- **One-Shot**: Sound effects play once and don't loop
- **Non-Blocking**: Sound effects don't pause or interrupt soundtracks

### How It Works

1. Sound effects are preloaded when a game is selected
2. When triggered, they play immediately
3. Multiple sound effects can overlap
4. Sound effects are unloaded when no longer needed

### Implementation Details

- Located in `AudioManager.playSoundEffect()`
- Uses `SoundPool` with `AudioAttributes` configured for game audio
- Preloading happens in `GamePlayerViewModel.loadGame()`
- Sound effects are cached in a map for quick access

## Audio State Management

The `AudioState` data class tracks:

```kotlin
data class AudioState(
    val currentSoundtrack: Soundtrack? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Int = 0,
    val duration: Int = 0
)
```

This state is exposed via `StateFlow` and observed by the UI for real-time updates.

## Resource Management

### Loading

- Soundtracks are loaded on-demand when selected
- Sound effects are preloaded when a game is loaded
- Resources are resolved from names to IDs at runtime

### Cleanup

- MediaPlayer is released when switching soundtracks or when ViewModel is cleared
- SoundPool is released when the app is destroyed
- All resources are properly cleaned up to prevent memory leaks

## Threading

- All audio operations run on background threads via coroutines
- UI updates are posted to the main thread via StateFlow
- Fade transitions use coroutines with delays for smooth animation

## Best Practices

1. **Soundtrack Selection**: Always fade out the current soundtrack before starting a new one
2. **Sound Effects**: Preload frequently used sound effects for better performance
3. **Resource Cleanup**: Always release MediaPlayer and SoundPool when done
4. **Error Handling**: Handle cases where resources might not be found

## Future Enhancements

Potential improvements:

- Volume controls for soundtracks and sound effects separately
- Seek functionality for soundtracks
- Playlist support for soundtracks
- Sound effect volume normalization
- Background playback support

