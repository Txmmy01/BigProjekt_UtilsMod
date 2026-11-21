# UtilsMod

A client-side Fabric utility mod for Minecraft 26.2.

**Author:** Txmmy01  
**Version:** 2.1.0  
**Mod ID:** utilsmod

## v1.0.0

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

## Added for v2.0.0

- Fullbright: overrides the client lightmap so dark areas are fully illuminated without changing block textures
- Clear Lava: removes camera-submersion fog while the camera is in lava
- Clear Water: removes camera-submersion fog while the camera is underwater
- Independent module toggles in the GUI
- Persistent visual module settings in `config/utilsmod-visual.properties`

## Added for v2.1.0

- Armor HUD: compact four-slot durability bars
- Potion Bars: compact effect names, remaining time and duration bars
- FPS HUD: current client FPS
- CPS Counter: left and right mouse clicks per second
- Independent HUD toggles with persistent settings in `config/utilsmod-hud.properties`
- HUD elements are only rendered while in-world and no screen is open

## Build

The project targets Minecraft 26.2, Fabric Loader, Fabric API and Java 25.

Build with Gradle 9.8.2:

```bash
gradle --no-daemon build
```

The mod JAR is written to `build/libs/`. GitHub Actions builds pushes to `main` and pull requests targeting `main`, and uploads the JAR as a workflow artifact.

## Validation

A successful build confirms compilation and packaging only. An in-game visual test is required to validate rendering behavior in a Minecraft 26.2 client.
