# CraftTweaker on Forge 1.20.1

This reference targets Minecraft 1.20.1 with Forge 47.4.10 and Java 17.
Install Astral Sorcery, Curios, and ObserverLib. Install CraftTweaker
14.0.59 to use the ZenCode examples. GameStages and Bookshelf are only
needed for stage rules. Put scripts in the server or single-player
instance's `scripts/` directory, then restart or run `/reload`.

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
| `infusion` | `TileInfuser`, `ActiveInfusionRecipe` | Item and surrounding fluid sources or a chalice into an item | Initiating wand user's UUID is persisted with the active recipe |
| `lightwell` | `TileLightwell` | Item catalyst into a fluid with production and shatter multipliers | Placed machine owner |
| `lumen_generation` | `TileLumenArray`, `TileLumenAlchemyArray` | Catalyst and optional combination lumen into another lumen | Array data already records a placed machine's owner |
| `lumen_crystallization` | `TileLumenCrystallizer` | Catalyst, stored lumen and liquid starlight into a growing lumen crystal cluster | Placed machine owner |
| `liquid_starlight` | `ActiveLiquidStarlightRecipe` | Main and secondary dropped items, duration and consumption policy into output modifiers | Use the actual dropped-item contributors |
| `liquid_interaction` | `LiquidInteractionRecipe` | Two nearby chalice fluids, consumption odds and weight into items or entities | Shared owner of the two chalices |
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

## GameStages

Install GameStages 15.0.2 and Bookshelf 20.2.15 on the server. Import
`mods.astralsorcery.AstralStages` and use
`require(ruleId, target, selector, stages, any=false, allowAutomation=false)`.
For each matching rule, the actor needs every listed stage by default, or
one listed stage when `any` is true. All applicable rules must pass. A
second call with the same namespaced rule ID replaces that rule.
`getRules()` lists the resulting definitions and `remove(ruleId)` removes one.
Adding a rule without GameStages produces an error in the CraftTweaker log.

| Target | Namespaced selector |
| --- | --- |
| `recipe` | An exact recipe ID, such as `astralsorcery:lightwell/water_ice` |
| `recipe_type` | An Astral processing type, such as `astralsorcery:infusion` |
| `feature` | A feature ID from the table below, or an attunement, chisel or cluster operation ID |
| `output_lumen_tag` | A lumen tag matched by the output type of a generation or crystallization recipe |
| `constellation_tag` | A constellation tag matched by an attunement participant's constellation or a required focal or altar-focus constellation |

| Feature ID under `astralsorcery:` | Server action |
| --- | --- |
| `altar_upgrade` | An altar recipe that can set another altar block |
| `perk_allocation` | A player's packet request to unlock a perk |
| `perk_socketing` | Inserting a perk gem. Removing a gem stays available. |
| `gateway_travel` | A player's teleport request |
| `resonating_wand` | Starting altar or infusion recipes, structure previews, creative structure placement, and crystal ore detection |
| `illumination_wand`, `architect_wand`, `exchange_wand`, `blink_wand`, `grappling_wand` | Using the corresponding wand, including the blink release |
| `artifact_interaction` | Player attacks and dropped artifact processing |
| `lumen_binding`, `lumen_use` | Applying a lumen binding in liquid starlight and using an active equipment binding |

Altar and infusion use the initiating player. Lightwells, arrays and
crystallizers use the placed machine's owner. Chalice reactions use a
shared owner. Dropped-item recipes use a common recorded thrower across
the items they match. Focal transmutation and natural cluster growth
run without an attributable actor. Blocks saved before ownership was
recorded can be ownerless. Set `allowAutomation` for a rule when you
want ownerless processing or direct fake-player actions to proceed.
Mixed provenance, offline owners, and players missing a required stage
remain restricted. Astral does not consult nearby players' stages.

When CraftTweaker is absent, the same rules can be defined in a datapack
under `data/NAMESPACE/astral_stage_rules/PATH.json`. The file's ID is the
rule ID. Set `target`, `selector`, and a `stages` array. Optional
`any` and `allow_automation` fields default to false. Forge
`conditions` can make a file conditional on GameStages. CraftTweaker
can override or remove these rules by ID during the same reload.

Stage changes take effect at the next server action. A stage change
pauses an active altar or infusion until it can resume. If a reload
removes or changes an active altar recipe, a reserved altar pauses
until the same definition is restored. Focal and liquid-starlight
progress can restart with inputs intact. A failed manual action can show
the required stages in the action bar. JEI and the Tome can still show
recipes regardless of which stages the local player has.


## Examples

### Add a recipe in each processing family

Use distinct paths if you change these recipes or add other examples to the
same script. Lava and water quantities in the chalice reaction are in mB.
The alchemy array consumes ten Solyn units to produce twenty Caldor units
on a successful attempt.

```zenscript
import crafttweaker.api.ingredient.IIngredient;
import mods.astralsorcery.recipe.LumenCost;

val altar = <recipetype:astralsorcery:altar_crafting>;
altar.addRecipe("astral_altar", "illumination", ["S"], {"S": <item:minecraft:sponge> as IIngredient}, [<item:minecraft:clay_ball>, <item:minecraft:brick>], 80);
val focal = <recipetype:astralsorcery:focal_combine>;
focal.addRecipe("astral_focal", [<item:minecraft:sponge>, <item:minecraft:brick>], [<item:minecraft:clay_ball>], 80);
val transmutation = <recipetype:astralsorcery:focal_transmutation>;
transmutation.addRecipe("astral_transmutation", <block:minecraft:sponge>, <block:minecraft:clay>.defaultState, 80);
val infusion = <recipetype:astralsorcery:infusion>;
infusion.addRecipe("astral_infusion", <item:minecraft:sponge>, <fluid:minecraft:water>, <item:minecraft:clay_ball>, 80);
val lightwell = <recipetype:astralsorcery:lightwell>;
lightwell.addRecipe("astral_lightwell", <item:minecraft:sponge>, <fluid:minecraft:water>);
val interaction = <recipetype:astralsorcery:liquid_interaction>;
interaction.addRecipe("astral_interaction", <fluid:minecraft:water> * 1000, <fluid:minecraft:lava> * 1000, <item:minecraft:clay_ball>);
val starlight = <recipetype:astralsorcery:liquid_starlight>;
starlight.addRecipe("astral_starlight", <item:minecraft:sponge>, 1, <item:minecraft:clay_ball>, 80);
val crystallization = <recipetype:astralsorcery:lumen_crystallization>;
crystallization.addRecipe("astral_crystallization", <item:minecraft:sponge>, "astralsorcery:solyn", 20);
val generation = <recipetype:astralsorcery:lumen_generation>;
generation.addRecipe("astral_generation", <item:minecraft:sponge>, "astralsorcery:solyn", 20);
generation.addAlchemyRecipe("astral_alchemy", <item:minecraft:sponge>, "astralsorcery:caldor", 20, [new LumenCost("astralsorcery:solyn", 10)]);
```

### Keep a built-in recipe ID

The existing water-from-ice Lightwell recipe keeps its original ID and
produces more water per tick. Tome or pack references to that ID still resolve.

```zenscript
val lightwell = <recipetype:astralsorcery:lightwell>;
lightwell.replaceJsonRecipe("astralsorcery:lightwell/water_ice", {
    catalyst_color: -11310593,
    input: {item: "minecraft:ice"},
    generated_fluid: "minecraft:water",
    production_multiplier: 1.5,
    shatter_multiplier: 0.04
});
```

### Group Astral ingredients and configure hard-coded processing

This example creates and edits three registry tags. The tagged Lightwell
inputs match lumen-bearing or property-bearing crystals, and the focal
combination requires a constellation in `astraltest:bright`. The starmetal
chisel example keeps the normal damage and ingot break behavior and drops
flint on success.

```zenscript
import mods.astralsorcery.AstralProcessing;
import mods.astralsorcery.AstralTags;

val lumens = AstralTags.lumenTag("astraltest:light");
lumens.add(AstralTags.lumen("astralsorcery:solyn"), AstralTags.lumen("astralsorcery:caldor"));
lumens.remove(AstralTags.lumen("astralsorcery:caldor"));
val constellations = AstralTags.constellationTag("astraltest:bright");
constellations.add(AstralTags.constellation("astralsorcery:aevitas"));
constellations.manager.clear(constellations);
AstralTags.replaceConstellations(constellations, AstralTags.constellation("astralsorcery:vicio"));
val crystalProperties = AstralTags.crystalPropertyTag("astraltest:properties");
crystalProperties.add(AstralTags.crystalProperty("astralsorcery:size"));

val lightwell = <recipetype:astralsorcery:lightwell>;
if lumens.contains(AstralTags.lumen("astralsorcery:solyn")) {
    lightwell.addRecipe("lumen_tag", AstralTags.lumenCrystal(lumens), <fluid:minecraft:water>);
}
lightwell.addRecipe("crystal_property_tag", AstralTags.crystalPropertyIngredient(<item:astralsorcery:rock_crystal>, crystalProperties, 2), <fluid:minecraft:water>);
<recipetype:astralsorcery:focal_combine>.addRecipe("focal_tag", [<item:minecraft:sponge>], [<item:minecraft:clay_ball>], 80, -1, "#astraltest:bright");

AstralProcessing.configure("astralsorcery:player_attunement", false);
AstralProcessing.remove("astralsorcery:player_attunement");
AstralProcessing.configure("astralsorcery:item_attunement", true, <item:minecraft:stick>, <item:minecraft:blaze_rod>, 1.0f, constellations);
AstralProcessing.configure("astralsorcery:chisel_starmetal", true, null, <item:minecraft:flint>, 3.0f);
AstralProcessing.configure("astralsorcery:chisel_crystal", false);
AstralProcessing.configure("astralsorcery:chisel_artifact", false);
AstralProcessing.configure("astralsorcery:gem_cluster_growth", false);
```


### Configure relay grids and an entity reaction

The altar example uses slot 2 of its 3×3 grid and the top-left relay of its
5×5 layout. Keep the relay center blank. The second recipe reacts water
from two nearby chalices and spawns a chicken. It consumes 1,000 mB and
1,500 mB and competes with other permitted matches by weight.

```zenscript
<recipetype:astralsorcery:altar_crafting>.addJsonRecipe("astral_relay_gameplay", {
    required_type: "illumination",
    grid: {
        key: {
            S: {type: "item", ingredient: {item: "minecraft:sponge"}},
            R: {type: "item", ingredient: {item: "minecraft:redstone"}}
        },
        pattern: [" S ", "   ", "   "],
        relay_pattern: ["R    ", "     ", "     ", "     ", "     "]
    },
    outputs: [{id: "minecraft:emerald", Count: 1}, {id: "minecraft:amethyst_shard", Count: 1}],
    base_focus_shatter_chance: 0.0,
    duration: 20,
    only_night: false,
    may_chain: false,
    required_starlight: [],
    required_lumen: [],
    required_fluid: [],
    required_additional_inputs: [],
    effects: [],
    output_modifiers: []
});

<recipetype:astralsorcery:liquid_interaction>.addJsonRecipe("astral_entity_reaction", {
    reactant_a: {ingredient: {fluid: "minecraft:water"}, amount: 1000},
    reactant_b: {ingredient: {fluid: "minecraft:water"}, amount: 1500},
    chance_consume_a: 1.0,
    chance_consume_b: 1.0,
    weight: 10,
    result: {type: "astralsorcery:spawn_entity", entity_type: "minecraft:chicken"}
});
```

### Combine Astral stage rules with Auram's existing stage mods

The ItemStages restriction governs Astral items. RecipeStages handles the
mod's vanilla crafting recipes. AstralStages handles the specific
Lightwell recipe and gateway travel. Both Astral rules use the same
`astralsorcery` stage. The Lightwell example explicitly permits ownerless
automation. Update or remove the rule by its fully qualified ID.

```zenscript
import mods.astralsorcery.AstralStages;
import mods.itemstages.ItemStages;
import mods.recipestages.Recipes;

ItemStages.createModRestriction("astralsorcery", "astralsorcery")
    .preventPickup(false)
    .preventInventory(false)
    .hiddenName("Unfamiliar starlight");
Recipes.setRecipeStageByMod("astralsorcery", "astralsorcery");

AstralStages.require("auram:lightwell_ice", "recipe", "astralsorcery:lightwell/water_ice", ["astralsorcery"], false, true);
AstralStages.require("auram:gateways", "feature", "astralsorcery:gateway_travel", ["astralsorcery"]);
```
