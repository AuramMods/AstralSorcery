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
