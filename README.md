# NXLauncher

**NXLauncher v1.0.0** — Android launcher for Minecraft: Java Edition.

This repository is a branded distribution based on the existing open-source Amethyst/PojavLauncher Android launcher code already present here. The upstream implementation and required third-party license/attribution materials are retained.

## Features

- Real Minecraft Java Edition launch pipeline
- Microsoft account authentication
- Minecraft versions and profiles
- Java Runtime management
- RAM allocation
- Renderer and performance settings
- Fabric, Forge and NeoForge support
- Mod/modpack management
- Custom controls and gamepad support
- Game files, worlds and resource-pack access
- Crash logs and recovery activities
- GitHub Actions APK builds

## Performance

NXLauncher uses performance-first defaults where safe. The launcher targets high-refresh gameplay, including a 120 FPS target on capable devices, but does not guarantee 120 FPS on every phone. Performance depends on hardware, Minecraft version, renderer, resolution, render distance, shaders and mods.

## Build

The `NXLauncher Android CI` workflow builds the release artifact as:

`NXLauncher-v1.0.0.apk`

Required GitHub secrets for signing/API integrations must be configured in repository settings when a signed release build requires them.

## License and attribution

See `LICENSE` and the third-party license files under `app_pojavlauncher/src/main/assets/licenses`. Upstream credits and required attribution are intentionally preserved.
