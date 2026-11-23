# Astral Sorcery 2.0.3 on Forge 1.20.1

The port uses commit `0ec3ff0fc` on `1.21.1` as its gameplay baseline. It lives on
`codex/forge-1.20.1` and targets fresh Minecraft 1.20.1 worlds. The distribution
keeps HellFirePvP's attribution and the repository license.

## Build

Use Java 17 and the checked-in Gradle 8.8 wrapper. ForgeGradle 6.0.54 uses the
official Minecraft 1.20.1 mappings and Forge 47.4.10, the version pinned by
Auram. ObserverLib is built from
upstream revision `053aa0076f9e0f4948e6f99cd8195c0fd6d1ed59` with the tracked
Forge patch.

```sh
./scripts/build-observerlib.sh
JAVA_TOOL_OPTIONS=-Djava.awt.headless=true ./gradlew runData
JAVA_TOOL_OPTIONS=-Djava.awt.headless=true ./gradlew build runGameTestServer reobfJarJar
```

Install `build/libs/astralsorcery-2.0.3+forge.1.20.1.jar` on the client and
server. Install Curios and the separate `observerlib-1.8.0-forge-1.20.1.jar`
from the repository under `.gradle/observerlib/maven`. The Astral Sorcery
jar contains the original license and embeds MixinExtras Forge 0.5.0.

The clean checkout run rebuilt ObserverLib, generated data and built the
production jar with pinned dependencies. All four Forge GameTests passed.
They check native stack persistence and split identity, recipe and packet
codecs, dedicated-side packet class loading, and sided fluid access and
invalidation. Separate clean builds rebuilt both jars from cached
compilation outputs. The restored Astral Sorcery jar matched the
uncached jar entry by entry and started on the dedicated server. The
ObserverLib jars matched byte for byte.

The Auram release production build used Forge 47.4.10, JEI 15.20.0.129
and Jade 11.13.2+forge. Its JEI subtype support uses the registry IDs
shared by Auram's JEI version and newer JEI 15 versions.

## Content comparison

The compared source registration IDs matched for 71 block declarations,
51 item declarations, 12 entity declarations, 22 block entity declarations,
32 sound declarations and 15 lumen declarations. The native Forge recipe
manager loaded all nine Astral recipe types. The data inventory matched
the baseline's 222 Astral recipes, 20 vanilla recipe additions, 53 research
nodes, 364 perks, 13 gameplay advancements, 32 artifact conditions, four
artifact effects and ten artifact penalties. Two independent runs emitted
the same 1,444 generated resource files. Forge recipe unlock
advancements use category-qualified IDs to preserve distinct rewards.

## Tested profiles

The gameplay profiles used Astral Sorcery 2.0.3, Minecraft 1.20.1, Forge 47.4.28,
Curios 5.14.1+1.20.1 and the pinned ObserverLib 1.8.0 build.

| Profile | Additional installed mods | Verification |
| --- | --- | --- |
| Minimal client and dedicated server | None | Two clients joined the same survival world, saw the altar upgrades and rejoined against the saved world after restarting the server. |
| Integrated single-player | JEI 15.74.0.234, MezzConfig 0.7.1, Jade 11.13.3+forge and Embeddium 0.3.31+mc1.20.1 | Loaded all eight Astral JEI categories and their catalysts on join, rejoin and datapack reload. Displayed Lightwell, Altar Crafting and Lumen Generation recipes and showed 2,300 of 4,000 Aevitas lumen through Jade's server data provider. Reloaded client resources in-world. |
| Pack dedicated server | Create 6.0.8, KubeJS 2001.6.5-build.26, Rhino 2001.2.3-build.10, Architectury 9.2.14, YUNG's Better Strongholds 1.20-Forge-4.0.3, YUNG's API 1.20-Forge-4.0.6, Draconic Evolution 3.1.2.621, Brandon's Core 3.2.1.302, CodeChickenLib 4.4.0.528, JEI 15.74.0.234, MezzConfig 0.7.1 and Jade 11.13.3+forge | Located `betterstrongholds:stronghold`, applied one KubeJS Lightwell recipe replacement and the Aquamarine tag addition, transferred liquid starlight through a powered Create pump, and supplied a Lumen Array with a hopper. The array produced Aevitas lumen. |

The survival test used fixtures for the multiblock structures and ingredients.
The player picked up the Illumination Altar from a focal point recipe and
placed it through the client. The Resonating Wand started the Resonance,
Luminance and Radiance upgrades through the live altar interaction.
The final recipe drew all five lumen types and liquid starlight, captured
the dropped items, and received Armara starlight through a Focus
Crystal and Lens linked in-game. The player received the Radiance research
tier and retained it after a Nether round trip and respawn. The Tome perk
screen allocated Added Armor and a repeated click left the same armor
value. A real gateway selection carried the player between two named
observatories. The server completed a datapack reload with both clients
connected, and a reloaded chunk restored the Chalice's exact 5,432 mB.
When the player joined the final production jar after restarting,
the Radiance Altar, known recipes and effective armor value matched
the saved session.

The Curios client equipped a Resplendent Prism in the necklace slot and
kept it in the saved inventory. With a deterministic modifier fixture,
the same equipped item raised a Sharpness I sword to effective Sharpness
III after rejoining. The pack server's runtime probe accepted the vanilla
diamond sword for dynamic enchantments and excluded the registered
Wyvern, Draconic and Chaotic swords.

The server runs used `--nogui` and disposable save directories. Client runs
used Xvfb and Mesa llvmpipe with an OpenAL null output device. Every test
profile started with the narrator and all sound categories disabled. The
macOS builds and native servers used Java 17.0.17. The Linux clients and
the integrated server used Java 17.0.21.

The minimal client rendered the focus crystal, starlight beam and
gateway overlay in Fabulous mode. Tome navigation was exercised at
GUI scales 1 and 2. At 960 by 640, the scale-2 layout retains the book
and fits its bookmark hitboxes within the window. Truncated bookmark
labels show their full title on hover.

## Target-version details

- Forge 1.20.1 persists Astral item components as typed namespaced item NBT
  and uses Forge common tags such as `forge:ingots/starmetal`.
- Vanilla 1.20.1 supplies ordinary spawners. The artifact spawn-limiter
  applies to those spawners.
- Forge 1.20.1 exposes a shared `mayfly` ability flag. Temporary focal flight
  preserves flight already granted when it starts. An independent grant
  during the same window can share the flag.
- The Simulated block movement exclusion still writes
  `simulated:non_movable`. The Forge counterpart's source uses that tag.

## Local validation evidence

- `build/reference/final-clean-forge-1.20.1/` contains the clean bootstrap,
  data generation, reobfuscation, GameTest and resource manifest results.
- `build/forge-port-multiplayer/server-native/` contains the live survival,
  reload, gateway, respawn and restart logs. The two client directories
  next to it contain the rendered screenshots and client logs.
- `build/forge-port-client-optional/` contains the JEI, Jade, Curios and
  Embeddium evidence.
- `build/forge-port-pack-server/` contains the automation, KubeJS,
  Better Strongholds and Draconic Evolution evidence.

## CraftTweaker and GameStages on Forge 1.20.1

Run `./gradlew runGameTestServer` for the base profile. Repeat with
`-PcraftTweakerIntegration=true`, `-PgameStagesIntegration=true`, and
both flags together. Each profile passed all 19 registered GameTests.
Optional-mod assertions run in the profiles that supply the corresponding
integration. The script fixtures live in
`src/gameTest/scripts/crafttweaker/`.

The tests exercise all nine ZenCode managers and their native processing
paths, including a two-output altar with relay inputs, a weighted chalice
reaction that spawns an entity, and both lumen generation modes. They also
cover custom ingredient matching, inactive and ownerless rules, and a
running altar that saves and restores its progress while paused and
consumes its ingredients and produces its outputs once after resuming.
Two live resource reloads remove and restore a recipe, tag membership, and
a stage rule. A CraftTweaker-only run with an intentional stage rule
reported the required GameStages and Bookshelf versions in its script log.

An isolated Forge 47.4.10 server and a muted client on a virtual display
tested the reobfuscated jar with Auram's CraftTweaker, GameStages, ItemStages,
RecipeStages, JEI, and Jade versions. Auram's existing `scripts/stages.zs`
ran alongside the Astral rules. A live reload changed the built-in
Lightwell altar recipe's output count from one to two at the same recipe
ID. The open Tome, JEI, and a client-side custom tag assertion picked up the
reload. A perk allocation packet was denied with the missing-stage
message, then the same perk activated and used one point after a native
GameStages grant. The player's stage and allocated perk persisted across
a dedicated server restart and client rejoin. The logs and screenshots are under
`build/reference/crafttweaker/` in the verification worktree.
