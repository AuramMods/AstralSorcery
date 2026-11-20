/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.tags;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static hellfirepvp.astralsorcery.datagen.data.tags.AstralBlockTagsProvider.cTag;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralItemTagsProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralItemTagsProvider extends ItemTagsProvider {

    public AstralItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, AstralSorcery.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        copyCommonTag(TagsAS.Blocks.ORES_STARMETAL, TagsAS.Items.ORES_STARMETAL);
        copyCommonTag(TagsAS.Blocks.STORAGE_BLOCKS_RAW_STARMETAL, TagsAS.Items.STORAGE_BLOCKS_RAW_STARMETAL);
        copy(TagsAS.Blocks.INFUSED_WOOD, TagsAS.Items.INFUSED_WOOD);
        copyCommonTag(TagsAS.Blocks.MARBLE, TagsAS.Items.MARBLE);
        copy(TagsAS.Blocks.SOOTY_MARBLE, TagsAS.Items.SOOTY_MARBLE);

        addCommonItem(Tags.Items.GEMS, TagsAS.Items.GEMS_AQUAMARINE, ItemsAS.AQUAMARINE.asItem());

        addCommonItem(Tags.Items.INGOTS, TagsAS.Items.INGOTS_STARMETAL, ItemsAS.STARMETAL_INGOT.asItem());
        addCommonItem(Tags.Items.RAW_MATERIALS, TagsAS.Items.RAW_MATERIALS_STARMETAL, ItemsAS.RAW_STARMETAL.asItem());

        tag(ItemTags.LECTERN_BOOKS)
                .add(ItemsAS.TOME.asItem());

        tag(TagsAS.Items.CRYSTAL)
                .addTag(TagsAS.Items.CELESTIAL_CRYSTAL)
                .addTag(TagsAS.Items.ATTUNED_CRYSTAL)
                .add(ItemsAS.ROCK_CRYSTAL.asItem());
        tag(TagsAS.Items.CELESTIAL_CRYSTAL)
                .add(ItemsAS.CELESTIAL_CRYSTAL.asItem())
                .add(ItemsAS.ATTUNED_CELESTIAL_CRYSTAL.asItem());
        tag(TagsAS.Items.ROCK_CRYSTAL)
                .add(ItemsAS.ROCK_CRYSTAL.asItem())
                .add(ItemsAS.ATTUNED_ROCK_CRYSTAL.asItem());
        tag(TagsAS.Items.ATTUNED_CRYSTAL)
                .add(ItemsAS.ATTUNED_ROCK_CRYSTAL.asItem())
                .add(ItemsAS.ATTUNED_CELESTIAL_CRYSTAL.asItem());

        tag(TagsAS.Items.FUNCTIONAL_ALTAR_CONSTELLATION_ITEM)
                .addTag(TagsAS.Items.ATTUNED_CRYSTAL);
        tag(TagsAS.Items.FUNCTIONAL_ATTUNEABLE_ITEM)
                .add(ItemsAS.ROCK_CRYSTAL.asItem())
                .add(ItemsAS.CELESTIAL_CRYSTAL.asItem());
        tag(TagsAS.Items.FUNCTIONAL_PERKTREE_SOCKETABLE_ITEM)
                .add(ItemsAS.DYNAMISM_GEM_SKY.asItem())
                .add(ItemsAS.DYNAMISM_GEM_DAY.asItem())
                .add(ItemsAS.DYNAMISM_GEM_NIGHT.asItem());

        tag(TagsAS.Items.CURIOS_NECKLACE)
                .add(ItemsAS.ENCHANTMENT_AMULET.asItem());

        tag(TagsAS.Items.SHARP_WEAPON_ENCHANTABLE)
                .addTag(ItemTags.SWORDS)
                .addTag(ItemTags.AXES);
        tag(TagsAS.Items.WEAPON_ENCHANTABLE)
                .addTag(TagsAS.Items.SHARP_WEAPON_ENCHANTABLE);
        tag(TagsAS.Items.MINING_LOOT_ENCHANTABLE)
                .addTag(ItemTags.AXES)
                .addTag(ItemTags.PICKAXES)
                .addTag(ItemTags.SHOVELS)
                .addTag(ItemTags.HOES);
        tag(TagsAS.Items.MINING_ENCHANTABLE)
                .addTag(TagsAS.Items.MINING_LOOT_ENCHANTABLE)
                .add(Items.SHEARS);
        tag(TagsAS.Items.BOW_ENCHANTABLE)
                .addTag(Tags.Items.TOOLS_BOWS);

        tag(ItemTags.AXES)
                .add(ItemsAS.CRYSTAL_AXE.asItem())
                .add(ItemsAS.IRIDESCENT_CRYSTAL_AXE.asItem());
        tag(ItemTags.PICKAXES)
                .add(ItemsAS.CRYSTAL_PICKAXE.asItem())
                .add(ItemsAS.IRIDESCENT_CRYSTAL_PICKAXE.asItem());
        tag(ItemTags.SHOVELS)
                .add(ItemsAS.CRYSTAL_SHOVEL.asItem())
                .add(ItemsAS.IRIDESCENT_CRYSTAL_SHOVEL.asItem());
        tag(ItemTags.SWORDS)
                .add(ItemsAS.CRYSTAL_SWORD.asItem())
                .add(ItemsAS.IRIDESCENT_CRYSTAL_SWORD.asItem());

        tag(ItemTags.SMALL_FLOWERS)
                .add(ItemsAS.BLOCK_GLIMMER_AMARANTH.get())
                .add(ItemsAS.BLOCK_HYACINTH.get())
                .add(ItemsAS.BLOCK_IRIS.get())
                .add(ItemsAS.BLOCK_ORCHID.get())
                .add(ItemsAS.BLOCK_PROTEA.get())
                .add(ItemsAS.BLOCK_THISTLE.get());
    }

    private void copyCommonTag(TagKey<Block> blockTag, TagKey<Item> itemTag) {
        TagKey<Item> shared = cTag(itemTag);
        copy(cTag(blockTag), shared);
        tag(itemTag).addTag(shared);
    }

    private void addCommonItem(TagKey<Item> parent, TagKey<Item> tag, Item item) {
        TagKey<Item> shared = cTag(tag);
        tag(parent).addTag(tag);
        tag(tag).addTag(shared);
        tag(cTag(parent)).addTag(shared);
        tag(shared).add(item);
    }
}
