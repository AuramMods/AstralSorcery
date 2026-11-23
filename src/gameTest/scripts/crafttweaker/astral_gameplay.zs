import mods.astralsorcery.AstralProcessing;
import mods.astralsorcery.recipe.LumenCost;

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

AstralProcessing.configure("astralsorcery:lumen_cluster_growth", true, null, null, 6000.0f);
<recipetype:astralsorcery:lumen_generation>.addRecipe("astral_array_gameplay", <item:minecraft:gold_nugget>, "astralsorcery:solyn", 20, 400.0f, 0.0f, 0.0f);
<recipetype:astralsorcery:lumen_generation>.addAlchemyRecipe("astral_alchemy_gameplay", <item:minecraft:gold_nugget>, "astralsorcery:caldor", 20, [new LumenCost("astralsorcery:solyn", 10)], 400.0f, 0.0f, 0.0f);
