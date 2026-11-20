/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.tags;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralBiomeTagsProvider
 * Created by HellFirePvP
 * Date: 07.10.2026 / 14:03
 */
public class AstralBiomeTagsProvider extends TagsProvider<Biome> {

    public AstralBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.BIOME, provider, AstralSorcery.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(TagsAS.Biomes.FOREST).addTag(BiomeTags.IS_FOREST);
        this.tag(TagsAS.Biomes.MOUNTAIN)
                .addTag(BiomeTags.IS_MOUNTAIN)
                .addTag(Tags.Biomes.IS_MOUNTAIN)
                .addOptionalTag(common("is_mountain/peak"))
                .addOptionalTag(common("is_mountain/slope"));
        this.tag(TagsAS.Biomes.SAVANNA).addTag(BiomeTags.IS_SAVANNA);
        this.tag(TagsAS.Biomes.DESERT).addTag(Tags.Biomes.IS_DESERT);
        this.tag(TagsAS.Biomes.SNOWY)
                .add(Biomes.SNOWY_BEACH)
                .add(Biomes.SNOWY_PLAINS)
                .add(Biomes.ICE_SPIKES)
                .add(Biomes.SNOWY_TAIGA)
                .add(Biomes.GROVE)
                .add(Biomes.SNOWY_SLOPES)
                .add(Biomes.JAGGED_PEAKS)
                .add(Biomes.FROZEN_PEAKS);

        this.tag(TagsAS.Biomes.FOCAL_POINT_BIOMES)
                .addTag(BiomeTags.IS_OVERWORLD)
                .addOptionalTag(ResourceLocation.fromNamespaceAndPath("forge", "is_overworld"))
                .addOptionalTag(common("is_overworld"));
        this.tag(TagsAS.Biomes.OBLITERATION_BIOMES)
                .addTag(BiomeTags.IS_OVERWORLD)
                .addTag(BiomeTags.IS_END)
                .addOptionalTag(ResourceLocation.fromNamespaceAndPath("forge", "is_overworld"))
                .addOptionalTag(ResourceLocation.fromNamespaceAndPath("forge", "is_end"))
                .addOptionalTag(common("is_overworld"))
                .addOptionalTag(common("is_end"));
        this.tag(TagsAS.Biomes.DIG_SITE_BIOMES)
                .addTag(TagsAS.Biomes.FOREST)
                .addTag(TagsAS.Biomes.MOUNTAIN)
                .addTag(TagsAS.Biomes.SAVANNA)
                .addTag(TagsAS.Biomes.DESERT);
        this.tag(TagsAS.Biomes.MOON_DIAL_BIOMES)
                .addTag(TagsAS.Biomes.DESERT);
        this.tag(TagsAS.Biomes.COLUMN_BIOMES)
                .addTag(BiomeTags.IS_OVERWORLD)
                .addOptionalTag(ResourceLocation.fromNamespaceAndPath("forge", "is_overworld"))
                .addOptionalTag(common("is_overworld"));
        this.tag(TagsAS.Biomes.ROTUNDA_BIOMES)
                .addTag(Tags.Biomes.IS_PEAK)
                .addOptionalTag(common("is_mountain/peak"));
    }

    private static ResourceLocation common(String path) {
        return ResourceLocation.fromNamespaceAndPath("c", path);
    }
}
