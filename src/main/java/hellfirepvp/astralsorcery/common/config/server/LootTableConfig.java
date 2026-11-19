/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.config.server;

import hellfirepvp.astralsorcery.common.config.ConfigEntry;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LootTableConfig
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LootTableConfig extends ConfigEntry {

    public static final LootTableConfig CONFIG = new LootTableConfig();

    public ForgeConfigSpec.ConfigValue<List<? extends String>> constellationPaperLootTables;

    public ForgeConfigSpec.ConfigValue<List<? extends String>> artifactShardLootTables;

    private LootTableConfig() {
        super("loot");
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        this.constellationPaperLootTables = cfgBuilder
                .comment("Loot tables that get constellation paper added to them on load")
                .translation(translationKey("constellationPaperLootTables"))
                .defineListAllowEmpty("constellationPaperLootTables", this.getDefaultList(), s -> s instanceof String str && ResourceLocation.tryParse(str) != null);
        this.artifactShardLootTables = cfgBuilder
                .comment("Loot tables that the artifact shard loot table item can generate. Temporary use until a proper feature is added")
                .translation(translationKey("artifactShardLootTables"))
                .defineListAllowEmpty("artifactShardLootTables", this.getDefaultArtifactShardList(), s -> s instanceof String str && ResourceLocation.tryParse(str) != null);
    }

    private List<String> getDefaultList() {
        return Stream.of(
                BuiltInLootTables.STRONGHOLD_LIBRARY,
                BuiltInLootTables.STRONGHOLD_CORRIDOR,
                BuiltInLootTables.DESERT_PYRAMID,
                BuiltInLootTables.JUNGLE_TEMPLE,
                BuiltInLootTables.WOODLAND_MANSION,
                BuiltInLootTables.ABANDONED_MINESHAFT,
                BuiltInLootTables.BURIED_TREASURE,
                BuiltInLootTables.SHIPWRECK_MAP,
                BuiltInLootTables.END_CITY_TREASURE,
                BuiltInLootTables.SIMPLE_DUNGEON
        ).map(ResourceLocation::toString).toList();
    }

    private List<String> getDefaultArtifactShardList() {
        return Stream.of(
                BuiltInLootTables.DESERT_PYRAMID,
                BuiltInLootTables.JUNGLE_TEMPLE,
                BuiltInLootTables.WOODLAND_MANSION,
                BuiltInLootTables.ABANDONED_MINESHAFT,
                BuiltInLootTables.BURIED_TREASURE,
                BuiltInLootTables.SHIPWRECK_SUPPLY
        ).map(ResourceLocation::toString).toList();
    }

    public boolean canAddConstellationPaper(ResourceLocation lootTableId) {
        return this.constellationPaperLootTables.get().contains(lootTableId.toString());
    }

    public ResourceLocation getRandomArtifactShardLootTable(RandomSource rand) {
        List<? extends String> list = this.artifactShardLootTables.get();
        if (list.isEmpty()) {
            return BuiltInLootTables.DESERT_PYRAMID;
        }
        return MiscUtil.getRandomEntry(list, rand).map(ResourceLocation::tryParse).orElse(BuiltInLootTables.DESERT_PYRAMID);
    }
}
