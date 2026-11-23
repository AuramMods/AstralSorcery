#modloaded gamestages

import mods.astralsorcery.AstralStages;

AstralStages.require("astraltest:lightwell_recipe", "recipe", "astraltest:lightwell", ["astral_first", "astral_second"], false, true);
AstralStages.require("astraltest:lightwell_smoke", "recipe", "crafttweaker:astral_lightwell_smoke", ["astral_first"]);
AstralStages.require("astraltest:fluid_reactions", "recipe_type", "astralsorcery:liquid_interaction", ["astral_first"]);
AstralStages.require("astraltest:grapple", "feature", "astralsorcery:grappling_wand", ["astral_grapple", "astral_master"], true);
AstralStages.require("astraltest:gateway", "feature", "astralsorcery:gateway_travel", ["astral_master"]);
AstralStages.remove("astraltest:gateway");
AstralStages.require("astraltest:lumens", "output_lumen_tag", "astraltest:light", ["astral_first"]);
AstralStages.require("astralsorcery:gametest_gateway", "feature", "astralsorcery:gateway_travel", ["astral_master"]);
<recipetype:astralsorcery:lightwell>.addRecipe("astral_permitted_fallback", <item:minecraft:sponge>, <fluid:minecraft:water>, 4.0f, 0.0f);
