# UtilsMod

A client-side Fabric utility mod for Minecraft 26.2.

**Author:** Txmmy01  
**Version:** 1.0.0  
**Mod ID:** utilsmod

## Implemented in v1.0.0

- Custom dark interface with orange accents
- Right Shift keybind to open and close the GUI
- GUI stays open without pausing the game
- BlockGlow for diamond, emerald, gold and iron ores, spawners and chests
- Per-block enable switches and individual color selection
- Per-dimension block selections
- Render distance presets from 8 to 32 blocks
- Highlight intensity presets from 0.5 to 2.0
- Searchable block list
- Persistent settings in `config/utilsmod.properties`
- Through-wall outlines rendered as always-on-top gizmos; original block textures are not modified

## Build

The project targets Minecraft 26.2, Fabric Loader, Fabric API and Java 25.

Build with Gradle 9.8.2:

```bash
gradle --no-daemon build
```

The mod JAR is written to `build/libs/`. GitHub Actions builds the project on every push to `main` and uploads the JAR as a workflow artifact.

## Validation

The Java 25 / Gradle 9.8.2 GitHub Actions build completed successfully for the current implementation. An in-game visual and multiplayer smoke test still needs to be performed in a Minecraft 26.2 client.
