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

altar.setJsonRecipe("astraltest:altar", altar.getRecipeJson("crafttweaker:astral_altar"));
focal.setJsonRecipe("astraltest:focal", focal.getRecipeJson("crafttweaker:astral_focal"));
transmutation.setJsonRecipe("astraltest:transmutation", transmutation.getRecipeJson("crafttweaker:astral_transmutation"));
infusion.setJsonRecipe("astraltest:infusion", infusion.getRecipeJson("crafttweaker:astral_infusion"));
lightwell.setJsonRecipe("astraltest:lightwell", lightwell.getRecipeJson("crafttweaker:astral_lightwell"));
interaction.setJsonRecipe("astraltest:interaction", interaction.getRecipeJson("crafttweaker:astral_interaction"));
starlight.setJsonRecipe("astraltest:starlight", starlight.getRecipeJson("crafttweaker:astral_starlight"));
crystallization.setJsonRecipe("astraltest:crystallization", crystallization.getRecipeJson("crafttweaker:astral_crystallization"));
generation.setJsonRecipe("astraltest:generation", generation.getRecipeJson("crafttweaker:astral_generation"));
generation.setJsonRecipe("astraltest:alchemy", generation.getRecipeJson("crafttweaker:astral_alchemy"));

altar.removeByName("crafttweaker:astral_altar");
focal.removeByName("crafttweaker:astral_focal");
transmutation.removeByName("crafttweaker:astral_transmutation");
infusion.removeByName("crafttweaker:astral_infusion");
lightwell.removeByName("crafttweaker:astral_lightwell");
interaction.removeByName("crafttweaker:astral_interaction");
starlight.removeByName("crafttweaker:astral_starlight");
crystallization.removeByName("crafttweaker:astral_crystallization");
generation.removeByName("crafttweaker:astral_generation", "crafttweaker:astral_alchemy");

lightwell.replaceJsonRecipe("astraltest:lightwell", {
    catalyst_color: -1,
    input: {item: "minecraft:sponge"},
    generated_fluid: "minecraft:lava",
    production_multiplier: 1.0,
    shatter_multiplier: 0.0
});

lightwell.replaceJsonRecipe("astralsorcery:lightwell/water_ice", {
    catalyst_color: -11310593,
    input: {item: "minecraft:ice"},
    generated_fluid: "minecraft:water",
    production_multiplier: 1.5,
    shatter_multiplier: 0.04
});
