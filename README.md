# skibidi-debug-1.0

Meteor Client addon — Skibidi Debug
MC 1.21.1 · Fabric · Java 21

## Modules
- **MusicHub** — plays local .wav/.au files in-game; volume, shuffle, loop, pause, skip

## Build (Termux)
```bash
cd skibidi-debug-1.0
chmod +x gradlew
./gradlew build --no-daemon
# jar → build/libs/skibidi-debug-1.0.jar
```

## Install
Drop jar into `.minecraft/mods/` alongside Meteor Client.

## Music folder
Default: ~/music — drop .wav or .au files there, set path in module settings.
