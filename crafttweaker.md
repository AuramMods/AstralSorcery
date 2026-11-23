# CraftTweaker on Forge 1.20.1

Astral Sorcery uses Forge's recipe manager for the processing types below.
The same serializers load datapack recipes and CraftTweaker's JSON recipe
actions. Astral's custom recipe code uses its own input containers and can
have block, fluid, lumen, or entity results. Its recipe preview must never
call `assemble`, which can create output in the world.

## Processing and actor inventory

| Recipe type or feature | Native operation | Inputs and results | Server-side attribution |
| --- | --- | --- | --- |
| `altar_crafting` | `TileAltar`, `ActiveAltarRecipe` | Altar and relay layouts, additional items, fluid, lumen and starlight into items or output modifiers | Initiating wand user's UUID is already stored with the active recipe |
| `focal_combine` | `ActiveFocalCombineRecipe` | Dropped items and optional constellation into multiple item stacks | Dropped-item provenance must agree across selected entities |
| `focal_transmutation` | `ActiveTransmutationHandler` | Block-state predicates and optional focused starlight into weighted block states | Runs from a focal point or beam. A natural focal point can be ownerless |
| `infusion` | `TileInfuser`, `ActiveInfusionRecipe` | Item and surrounding fluid sources or a chalice into an item | Wand user is known when starting. Current active state lacks that UUID |
| `lightwell` | `TileLightwell` | Item catalyst into a fluid with production and shatter multipliers | A player can insert a catalyst. Production and automation also run without an actor |
| `lumen_generation` | `TileLumenArray`, `TileLumenAlchemyArray` | Catalyst and optional combination lumen into another lumen | Array data already records a placed machine's owner |
| `lumen_crystallization` | `TileLumenCrystallizer` | Catalyst and stored lumen into a lumen crystal | Automatic processing needs explicit handling when no actor is present |
| `liquid_starlight` | `ActiveLiquidStarlightRecipe` | Main and secondary dropped items, duration and consumption policy into output modifiers | Use the actual dropped-item contributors |
| `liquid_interaction` | `LiquidInteractionRecipe` | Two sized fluid reactants, consumption odds and weight into items or entities | Fluid contact is an autonomous world operation |
| Player and item attunement | `AttunementRecipe`, `TileAttunementAltar` | Eligible player or dropped item and the configured constellation into attunement | Use the participant or item provenance |
| Chisel processing | `ItemEntityChiselAttackable` and its starmetal, crystal and artifact implementations | Dropped item and chisel into stardust, a crystal split, or artifact shards | Actual attacker |
| Crystal clusters | `TileCelestialCrystalCluster`, `TileGemCrystalCluster`, `TileLumenCrystalCluster` | Growth and harvesting into crystal-related items | Growth can be autonomous. Harvest can have a player |
| Wand and gateway dyeing | `RecipeChangeColor` | The matching item and a dye into a recolored item | Vanilla crafting |

## Integration checks

Run `./gradlew runGameTestServer` for the base Forge profile. Run
`./gradlew -PcraftTweakerIntegration=true runGameTestServer` to exercise
CraftTweaker 14.0.59 with the checked-in ZenCode fixture and verify its recipe
in the server's live recipe manager. The optional
`-PgameStagesIntegration=true` profile adds GameStages 15.0.2 and Bookshelf
20.2.15 independently. With both flags enabled, the run directories and
scripts stay separate from the base profile.

CraftTweaker binds its tag managers to the current reload's tag results,
runs scripts against that reload's recipe map, and applies recipe actions
before the world starts using the resulting resources. Custom registry
tag mutations must reach the Forge tag results used by the matcher.
An independent script-side tag map would not satisfy that requirement.
Auram's `minecraft/scripts/stages.zs` already uses ItemStages and
RecipeStages. Its stage names can also be checked by GameStages directly.
