# NXLauncher 🇦🇲

**NXLauncher v1.0.0** — ժամանակակից Android launcher՝ Minecraft: Java Edition-ը Android սարքերում իրական գործարկելու համար։

NXLauncher-ը ստեղծվում է Հայաստանից՝ հիմնվելով այս repository-ում արդեն առկա բաց կոդով Amethyst/PojavLauncher launcher-ի codebase-ի վրա։ Նախագծում պահպանվում են upstream-ի և օգտագործվող third-party բաղադրիչների անհրաժեշտ license-ները, credits-ը և attribution-ը։

## 🎮 Հիմնական հնարավորություններ

- Իրական Minecraft: Java Edition launch
- Microsoft account մուտք
- Minecraft version/profile կառավարում
- Minecraft տարբերակների ներբեռնում և տեղադրում
- Java Runtime Manager
- RAM allocation և անվտանգ ավտոմատ RAM կարգավորում
- Renderer և performance կարգավորումներ
- Fabric, Forge և NeoForge support
- Mods / modpacks կառավարում
- Worlds և game files կառավարում
- Resource Packs
- Custom controls և gamepad support
- Crash logs և crash recovery
- Safe fallback կարգավորումներ՝ crash-երի դեպքում
- GitHub Actions-ով Android APK build
- GitHub Releases-ի միջոցով թարմացումների տարածում

## ✨ NXLauncher Design

NXLauncher-ի interface-ը նախատեսված է ժամանակակից և premium Android experience-ի համար՝ NXLauncher branding-ով և նախագծի սեփական logo-ով։ Գլխավոր գործարկման գործողությունը ներկայացվում է հայերեն **«ԽԱՂԱԼ»** կոճակով։

Launcher-ի branding-ը ներառում է՝

**NX LAUNCHER**

**ՊԱՏՐԱՍՏՎԵԼ Է ՀԱՅԱՍՏԱՆՈՒՄ**

**ՍՏԵՂԾՈՂԸ Nyrox_YT**

## ⚡ Performance

NXLauncher-ը նպատակ ունի հնարավորինս performance-oriented լինել՝ օգտագործելով անվտանգ performance-first կարգավորումներ։ Նախագիծը նախատեսված է high-refresh gameplay-ի համար և կարող է նպատակադրել մինչև **120 FPS**՝ համապատասխան սարքերում։

120 FPS-ը **չի երաշխավորվում բոլոր սարքերում**։ Իրական FPS-ը կախված է հեռախոսի CPU/GPU-ից, Minecraft-ի տարբերակից, renderer-ից, resolution-ից, render distance-ից, shaders-ից և տեղադրված mods-ից։

RAM-ի և performance-ի կարգավորումները չպետք է օգտագործեն unsafe արժեքներ։ Crash-ի դեպքում launcher-ը պետք է հնարավորություն տա վերադառնալ ավելի անվտանգ կարգավորումների։

## 🛠️ Build APK

Android release build-ը կատարվում է GitHub Actions workflow-ի միջոցով։ Նպատակային release artifact-ի անունն է՝

`NXLauncher-v1.0.0.apk`

Build-ի համար օգտագործվում է այս repository-ի Android/Gradle project-ը։ Signed release-ի համար անհրաժեշտ signing secrets/API configuration-ը պետք է կարգավորվի repository-ի GitHub settings-ում, եթե տվյալ release workflow-ը պահանջում է դրանք։

## 📦 Open-source հիմք և Credits

NXLauncher-ը չի փորձում ներկայացնել upstream launcher-ի codebase-ը որպես ամբողջությամբ զրոյից գրված նախագիծ։ Այն զարգացվում և branded է այս repository-ում առկա open-source launcher implementation-ի հիման վրա։

Պարտադիր license-ներն ու attribution-ը պահպանվում են։ Մանրամասների համար տես՝

- `LICENSE`
- `app_pojavlauncher/src/main/assets/licenses`
- repository-ի upstream/third-party credits

## 🇦🇲 Նախագծի մասին

NXLauncher-ը հայկական նախաձեռնություն է, որի նպատակն է Android-ում Minecraft Java Edition-ի համար ստեղծել ժամանակակից, արագ և օգտագործողի համար հարմար launcher experience։

**NX LAUNCHER — ՊԱՏՐԱՍՏՎԵԼ Է ՀԱՅԱՍՏԱՆՈՒՄ**

**ՍՏԵՂԾՈՂԸ՝ Nyrox_YT**
