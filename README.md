# UtilsMod

A client-side Fabric utility mod for Minecraft 26.2.

**Author:** Txmmy01  
**Version:** 1.0.0  
**Mod ID:** utilsmod

## v1.0.0 scope

- Custom dark interface with orange accents
- Right Shift keybind
- Interface does not pause the game
- BlockGlow with per-block selection and colors
- Configurable render distance and highlight intensity
- Dimension-specific settings
- Persistent client configuration

## Build

The project targets Minecraft 26.2 and Java 25.

Build with Gradle 9.8.2:

```bash
gradle --no-daemon build
```

The built mod JAR is written to `build/libs/`.

## Status

The interface foundation is present. v1.0.0 is not complete until BlockGlow is implemented and the build is verified.
