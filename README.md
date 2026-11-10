# DonutSpy

Client-side Fabric utility mod for Minecraft 26.3.

Features being ported from 0.4.1:
- Sound event history and category detection
- Sound radar and world markers
- Player-list session join/leave audit
- Local singleplayer-only loaded-chunk ore analysis
- Configurable HUD and settings

The ore analysis feature is intentionally restricted to integrated singleplayer. DonutSpy does not scan hidden ores on multiplayer servers.

## Build

Requires JDK 25 and Gradle 9.8.2. Run:

```sh
gradle wrapper --gradle-version 9.8.2
./gradlew build
```
