# Architecture Documentation

## Project Structure

The app follows a clean architecture pattern with clear separation of concerns:

```
app/src/main/java/com/alf/app_root_music_player/
├── data/
│   ├── model/           # Data models (Game, Soundtrack, SoundEffect)
│   └── repository/      # Data access layer (GameRepository, ResourceResolver)
├── audio/               # Audio playback management
│   ├── AudioManager.kt  # Handles soundtrack and sound effects playback
│   └── AudioState.kt    # Audio state data class
├── ui/
│   ├── navigation/      # Navigation setup
│   │   └── NavGraph.kt  # Navigation routes and graph
│   ├── screens/         # Screen composables and ViewModels
│   │   ├── GameSelectionScreen.kt
│   │   ├── GameSelectionViewModel.kt
│   │   ├── GamePlayerScreen.kt
│   │   └── GamePlayerViewModel.kt
│   └── theme/           # Theme configuration
└── MainActivity.kt      # App entry point
```

## Data Flow

1. **Data Loading**: `GameRepository` loads `games.json` from assets and parses it using kotlinx.serialization
2. **Resource Resolution**: Resource names from JSON are resolved to Android resource IDs at runtime
3. **State Management**: ViewModels use StateFlow to manage UI state
4. **Audio Playback**: `AudioManager` handles all audio operations (soundtracks via MediaPlayer, sound effects via SoundPool)

## Key Components

### Data Layer

- **GameRepository**: Singleton-like repository that loads and caches game data
- **ResourceResolver**: Utility to convert resource names to resource IDs
- **Data Models**: Serializable data classes for Game, Soundtrack, and SoundEffect

### Audio Layer

- **AudioManager**: Centralized audio management
  - MediaPlayer for soundtracks (looping, fade transitions)
  - SoundPool for sound effects (simultaneous playback)
  - Progress tracking via coroutines

### UI Layer

- **Navigation**: Navigation Compose with type-safe routes
- **Screens**: Composable screens with their respective ViewModels
- **State Management**: MVVM pattern with StateFlow

## Design Decisions

1. **JSON-based Configuration**: Games are defined in `games.json` for easy extension without code changes
2. **Resource Name Resolution**: Resource IDs are resolved at runtime to allow JSON-based configuration
3. **Fade Transitions**: Soundtrack changes use smooth fade in/out for better UX
4. **Separate Audio Systems**: Soundtracks (MediaPlayer) and sound effects (SoundPool) use different systems optimized for their use cases
5. **MVVM Architecture**: Clear separation between UI and business logic

## Dependencies

- **Navigation Compose**: Multi-screen navigation
- **kotlinx.serialization**: JSON parsing
- **Jetpack Compose**: Modern UI framework
- **Material 3**: Design system
- **ViewModel**: State management
- **Coroutines & Flow**: Asynchronous operations

