# Root Music Player

A multi-game music player app for Android that allows you to play soundtracks and sound effects from multiple games.

## Features

- 🎮 **Multi-Game Support**: Select from multiple games, each with their own soundtracks and sound effects
- 🎵 **Soundtrack Playback**: Play looping background music with smooth fade transitions
- 🔊 **Sound Effects**: Trigger one-shot sound effects that play simultaneously without interrupting soundtracks
- 🎨 **Modern UI**: Built with Jetpack Compose and Material 3
- 📱 **Easy Extension**: Add new games by simply updating a JSON file

## Architecture

The app follows a clean architecture pattern:

- **Data Layer**: JSON-based game configuration with resource resolution
- **Audio Layer**: Separate systems for soundtracks (MediaPlayer) and sound effects (SoundPool)
- **UI Layer**: MVVM with Jetpack Compose and Navigation

See [ARCHITECTURE.md](docs/ARCHITECTURE.md) for detailed architecture documentation.

## Adding New Games

To add a new game:

1. Add audio files to `res/raw/`
2. Add icons and cover art to `res/drawable/`
3. Update `assets/games.json` with the new game data

See [ADDING_GAMES.md](docs/ADDING_GAMES.md) for a complete step-by-step guide.

## Audio System

The app uses two audio systems:

- **MediaPlayer**: For soundtracks (looping, fade transitions)
- **SoundPool**: For sound effects (simultaneous playback, low latency)

See [AUDIO_SYSTEM.md](docs/AUDIO_SYSTEM.md) for detailed audio system documentation.

## Building

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Build and run

## Requirements

- Android SDK 23 (Android 6.0) or higher
- Kotlin 2.0.21
- Gradle 8.13.1

## Dependencies

- Jetpack Compose
- Navigation Compose
- kotlinx.serialization
- Material 3
- ViewModel & Lifecycle

## License

This project is for educational purposes.

