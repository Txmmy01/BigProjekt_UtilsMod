# UtilsMod

A modular Fabric client mod for Minecraft 26.2.

**Author:** Txmmy01  
**Version:** 1.0.0  
**Mod ID:** utilsmod

## Overview

UtilsMod is built around a clean, modern client-side experience. The first release is focused on BlockGlow: configurable block highlights that preserve the original block textures and can be seen through walls.

## Planned for v1.0.0

- BlockGlow module
- Individual block selection
- Per-block highlight colors
- Configurable render distance and glow intensity
- Dimension-aware settings
- Persistent client configuration
- An animated, dark interface with orange accents
- A configurable keybind that opens without pausing the game

## Development

The project targets Minecraft 26.2, Fabric Loader, Fabric API and Java 25.

Build with Gradle 9.8.2:

```bash
gradle build
```

The built mod JAR is written to `build/libs/`.

## Status

The project foundation is being set up. BlockGlow and the user interface are the next implementation stages.
