# Astral Sorcery

Astral Sorcery is a magic mod focused around harnessing the powers of
starlight and the constellations. This branch is a Minecraft 1.20.1 Forge
backport of HellFirePvP's [Astral Sorcery](https://github.com/HellFirePvP/AstralSorcery).
See the original [license](LICENSE) for the terms of use.

## Building the Forge backport

Run builds and checks on the local machine with its Gradle cache. Keep
GitHub Actions out of the build and release process.

Use JDK 17. Build ObserverLib once, then build Astral Sorcery with the
checked-in Gradle wrapper.

```sh
./scripts/build-observerlib.sh
./gradlew build
```

For a release, run `./gradlew build reobfJarJar` and use the reobfuscated
runtime jar from `build/libs/`.

The ObserverLib script fetches the pinned upstream Forge source, applies
the tracked build patch, and builds and reobfuscates the mod. It stores the
result in the worktree's `.gradle/observerlib/maven` repository, including
the separate ObserverLib jar required to run the port. The script uses
`curl`, `unzip`, and `git`.

The development run directories are under `run/`. Use `runServer` for a
headless dedicated server, `runGameTestServer` for GameTests, and `runData`
for data generation. The `runClient` and `runClient2` profiles start with
the narrator and audio muted. Run client verification in a virtual
display to keep it off the host desktop. Pass `-PintegrationMods=true`
to include JEI and Jade in the development run classpath.

## Pack customization

See the [CraftTweaker and GameStages reference](crafttweaker.md) for ZenCode
recipe managers, Astral ingredient tags, processing rules, and stage gates.

## Starlight receivers

Addons can expose the `BlockStarlightReceiver` capability on a block entity.
Use the Linking Tool to connect a crystal or lens to that block. The capability
receives each source's server-side packet after the usual splitting and
transmission losses. Native network nodes and block transmutation retain their
existing behavior.

## Links

- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/astral-sorcery)
- [Discord](https://discord.gg/MhWMfR8HUf)
