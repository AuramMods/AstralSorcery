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

## Recipe managers

Every row with a recipe type uses the standard bracket
`<recipetype:astralsorcery:TYPE>`. Put your script under `scripts/` and run
`/reload` on the server. Use `/ct recipes` to dump recipe definitions.

| Method | Behavior |
| --- | --- |
| `addRecipe(path, …)`, `addAlchemyRecipe(path, …)` | Add at `crafttweaker:PATH`. Duplicate IDs are rejected. |
| `addJsonRecipe(path, data as MapData)` | Add using the native datapack schema. Omit `type` or use the manager's own ID. |
| `replaceJsonRecipe(id, data as MapData)` | Replace a recipe of this type at its exact namespaced ID. Missing IDs and IDs of another type are rejected. |
| `setJsonRecipe(id, json as string)` | Add or replace a recipe of this type at its exact namespaced ID. Recipe dumps use this form. |
| `getRecipeIds()`, `getRecipeJson(id)` | List namespaced IDs in sorted order or serialize a single live definition. |
| `removeByName(id, …)`, `removeAll()` | Remove recipes from this manager. |

`getRecipesByOutput(ingredient)` and `remove(ingredient)` match explicit
item results, including any of the listed altar and focal outputs. For
lightwell fluid, transmutation block, liquid interaction entity, and lumen
outputs use each manager's `getIdsByOutputFluid`, `getIdsByOutputBlock`,
`getIdsByOutputEntity`, or `getIdsByOutputLumen`. Use the returned IDs with
`removeByName`. Output modifiers that mutate a supplied item or a block
depend on the operation's live input. Inspect their definitions by ID.

The native fields and modifiers are illustrated by the bundled datapacks in
`data/astralsorcery/recipes/` inside the JAR. Dump a working recipe before
editing an advanced field. This covers relay grids, additional item, fluid,
and lumen inputs, weighted block outputs, fluid tags and entity outcomes,
and item state. Add and replace actions take effect in script execution
order during reload. The vanilla crafting manager handles
`astralsorcery:illumination_wand_change_color` and
`astralsorcery:celestial_gateway_change_color` by ID.

### Typed additions

Call `addRecipe` with the arguments in the table below. An argument shown
with a default can be omitted. Durations are in game ticks, chances are in
the range `0.0` to `1.0`, and colors are signed ARGB integers. Import
`crafttweaker.api.ingredient.IIngredient` to cast an altar key's item
values to `IIngredient`. Import `mods.astralsorcery.recipe.LumenCost` for
`new LumenCost(lumenId, amount)`.

| Manager | Arguments after `path` |
| --- | --- |
| `altar_crafting` | `tier, pattern, key, outputs, duration, onlyNight=false, mayChain=false`. Tiers are `illumination`, `resonance`, `luminance` and `radiance`. The typed layout starts in the top left of the 3×3 grid. Use the JSON form for 5×5 relay layouts, resource costs and effects. |
| `focal_combine` | `inputs, outputs, duration, color=-1, constellation=""`. An empty constellation leaves it unrestricted. |
| `focal_transmutation` | `inputBlock, outputBlockState, duration, color=-1, constellation="", requiresFocusedStarlight=false`. The typed result has weight 1. The JSON form accepts weighted lists and block predicates. |
| `infusion` | `input, fluid, output, duration, consumptionChance=1.0, consumeMultiple=false, acceptChalice=true`. The fluid argument selects the type. The native infuser controls the amount consumed. |
| `lightwell` | `input, outputFluid, productionMultiplier=1.0, shatterMultiplier=1.0, catalystColor=-1`. The fluid argument selects the type. The machine determines the amount it produces. |
| `liquid_interaction` | `firstFluidStack, secondFluidStack, result, consumeFirst=1.0, consumeSecond=1.0, weight=1`. Fluid stacks supply quantities in mB. The JSON form also supports an entity result. |
| `liquid_starlight` | `input, count, result, duration, extraTicks=0, color=-1, consumesLiquid=false`. This form consumes its input and drops the listed item. Use JSON for secondary inputs and other output modifiers. |
| `lumen_crystallization` | `input, lumenId, cost, shatterMultiplier=1.0`. Cost is in lumen units per operation. |
| `lumen_generation` | `input, lumenId, amount, attemptMultiplier=1.0, starlightCost=1.0, shatterMultiplier=1.0`. Call `addAlchemyRecipe` with a nonempty `LumenCost[]` after `amount` for a combination. |

## Astral ingredient tags

Import `mods.astralsorcery.AstralTags`. Its `lumen(id)`,
`constellation(id)`, and `crystalProperty(id)` functions return typed,
registered entries with an `id` getter. Use the corresponding `lumenTag(id)`,
`constellationTag(id)`, or `crystalPropertyTag(id)` for a typed tag.
All names must include the namespace. The tag methods `add(...)`,
`remove(...)`, `contains(value)`, and `elements()` use CraftTweaker's
registry tag interface. Add to a new tag to create it. Use
`tag.manager.clear(tag)` on an existing tag, or
`AstralTags.replaceLumens`, `replaceConstellations`, and
`replaceCrystalProperties` to set the full membership.

`AstralTags.lumenCrystal(tag)` matches lumen crystals of the tagged types.
`AstralTags.crystalPropertyIngredient(input, tag, minimumTier=1)` matches
the base ingredient when at least one tagged crystal property meets the
tier. Use either as an item ingredient in a typed addition. In native JSON,
their ingredient types are `astralsorcery:lumen_tag` with a `tag` field and
`astralsorcery:crystal_property_tag` with `base`, `tag`, and optional
`minimum_tier` fields. The focal combine and transmutation constellation
argument accepts a tag as `"#namespace:path"`. An empty tag has no matches.

Forge datapacks can supply the same tags under
`data/NAMESPACE/tags/astralsorcery/lumen/`,
`data/NAMESPACE/tags/astralsorcery/constellations/`, and
`data/NAMESPACE/tags/astralsorcery/crystal_properties/`. Use existing
item, fluid, and block tags where the native fields accept those types.
Stack-state predicates such as `astralsorcery:stack_data`,
`astralsorcery:is_flag_set`, and `astralsorcery:has_stored_lumen`
are available through the native JSON format.

## Attunement, chisel and cluster rules

Import `mods.astralsorcery.AstralProcessing` and use
`configure(operation, enabled, input=null, output=null, rateMultiplier=1.0, constellations=null)`.
Each call replaces the operation's full rule. Use `isEnabled(operation)`
and `configuredOperations()` during script loading, and `remove(operation)`
to restore native defaults. Rules are evaluated on the server.

| Operation ID under `astralsorcery:` | Configurable fields beyond `enabled` |
| --- | --- |
| `player_attunement` | A constellation tag. Native research, discovery and perk requirements still apply. |
| `item_attunement` | A custom ingredient replaces the native attuneable item tag. A constellation tag limits the process. An output override uses the input count and writes the chosen constellation to the new item. |
| `chisel_starmetal` | An additional filter on the dropped ingot, the dropped output stack, and the success-rate multiplier. |
| `chisel_crystal` | An additional filter and the split-rate multiplier. The native crystal property split still runs. |
| `chisel_artifact` | An additional filter and the success-rate multiplier. The native shard still carries artifact data. |
| `celestial_cluster_growth` | Rate multiplier for growth and erosion of starmetal ore beneath it. |
| `gem_cluster_growth` | Rate multiplier for growth and late-stage shrinkage. |
| `lumen_cluster_growth` | Rate multiplier for the crystallizer's cluster creation and growth. Disabling this step pauses processing before the crystallizer draws resources. |

Cluster harvest uses the three matching vanilla loot tables under
`data/astralsorcery/loot_tables/blocks/`. Override those in a datapack
to edit harvest results. Cluster formation and crystal growth in liquid
starlight use `liquid_starlight` recipes and their output modifiers.
