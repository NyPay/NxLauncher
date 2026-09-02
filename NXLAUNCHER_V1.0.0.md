# NXLauncher v1.0.0

NXLauncher is an Android Minecraft: Java Edition launcher built from the existing open-source Amethyst/PojavLauncher codebase in this repository.

## v1.0.0 goals

- NXLauncher branding and application identity
- Real Minecraft Java Edition runtime and launch pipeline
- Microsoft account authentication already provided by the underlying launcher
- Minecraft version installation and profile management
- Java runtime management and safe RAM allocation
- Renderer selection and performance-oriented configuration
- Fabric / Forge / NeoForge installation support provided by the base project
- Mods/modpack, worlds, resource-pack and game-file management through the existing launcher architecture
- Custom controls and gamepad support
- Crash logs and recovery activities
- GitHub Actions APK build

## Performance policy

NXLauncher targets a smooth, performance-first experience. A 120 FPS target is not a guarantee: actual FPS depends on the device CPU/GPU, Minecraft version, renderer, resolution, render distance, shaders and installed mods. RAM defaults must remain conservative enough to avoid starving Android and causing crashes.

## Licensing

This project retains the upstream license, attribution and third-party license materials. Required credits must not be removed. NXLauncher branding does not claim ownership of upstream components.

## APK

The CI workflow produces `NXLauncher-v1.0.0.apk` as a GitHub Actions artifact. A GitHub Release can attach the same APK after a successful build.
