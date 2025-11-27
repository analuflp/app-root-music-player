# Adding New Games Guide

This guide explains how to add a new game to the app.

## Step 1: Add Audio Files

1. Place your soundtrack audio files (MP3 format) in `app/src/main/res/raw/`
2. Place your sound effect audio files in `app/src/main/res/raw/`
3. Use lowercase names with underscores (e.g., `main_theme.mp3`, `jump_sound.mp3`)

## Step 2: Add Cover Art and Icons

1. Place game icon in `app/src/main/res/drawable/` (recommended size: 64x64dp or larger)
2. Place soundtrack cover art in `app/src/main/res/drawable/`
3. Use lowercase names with underscores (e.g., `game_icon.png`, `cover_main.png`)

## Step 3: Update games.json

Edit `app/src/main/assets/games.json` and add a new game object:

```json
{
  "id": "your-game-id",
  "name": "Your Game Name",
  "iconResName": "your_game_icon",
  "soundtracks": [
    {
      "id": "main-theme",
      "title": "Main Theme",
      "audioResName": "main_theme",
      "coverResName": "cover_main"
    },
    {
      "id": "battle-theme",
      "title": "Battle Theme",
      "audioResName": "battle_theme",
      "coverResName": "cover_battle"
    }
  ],
  "soundEffects": [
    {
      "id": "jump",
      "title": "Jump",
      "audioResName": "jump_sound"
    },
    {
      "id": "collect",
      "title": "Collect Item",
      "audioResName": "collect_sound"
    }
  ]
}
```

### Field Descriptions

- **id**: Unique identifier for the game (lowercase, use hyphens)
- **name**: Display name shown in the UI
- **iconResName**: Resource name of the game icon (without extension)
- **soundtracks**: Array of soundtrack objects
  - **id**: Unique identifier for the soundtrack
  - **title**: Display name
  - **audioResName**: Resource name of the audio file (without extension)
  - **coverResName**: Resource name of the cover art (without extension)
- **soundEffects**: Array of sound effect objects
  - **id**: Unique identifier for the sound effect
  - **title**: Display name
  - **audioResName**: Resource name of the audio file (without extension)

## Step 4: Resource Name Rules

- Use lowercase letters only
- Use underscores to separate words
- Do NOT include file extensions (.mp3, .png, etc.)
- Resource names must match the actual resource file names (without extension)

## Example: Complete Game Entry

```json
{
  "id": "super-adventure",
  "name": "Super Adventure",
  "iconResName": "super_adventure_icon",
  "soundtracks": [
    {
      "id": "overworld",
      "title": "Overworld Theme",
      "audioResName": "super_adventure_overworld",
      "coverResName": "cover_overworld"
    },
    {
      "id": "boss",
      "title": "Boss Battle",
      "audioResName": "super_adventure_boss",
      "coverResName": "cover_boss"
    }
  ],
  "soundEffects": [
    {
      "id": "coin",
      "title": "Coin Collect",
      "audioResName": "coin_collect"
    },
    {
      "id": "powerup",
      "title": "Power-up",
      "audioResName": "powerup_sound"
    }
  ]
}
```

## File Structure Example

After adding a new game, your resources should look like:

```
app/src/main/res/
├── raw/
│   ├── super_adventure_overworld.mp3
│   ├── super_adventure_boss.mp3
│   ├── coin_collect.mp3
│   └── powerup_sound.mp3
└── drawable/
    ├── super_adventure_icon.png
    ├── cover_overworld.png
    └── cover_boss.png
```

## Testing

1. Build and run the app
2. Verify the new game appears in the game selection grid
3. Test soundtrack playback (should loop and fade between tracks)
4. Test sound effects (should play simultaneously without pausing soundtracks)

## Notes

- Soundtracks automatically loop when playing
- Only one soundtrack can play at a time
- Multiple sound effects can play simultaneously
- Sound effects do not pause or interrupt soundtracks
- Resource names are case-sensitive

