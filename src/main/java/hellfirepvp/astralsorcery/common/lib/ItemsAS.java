/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.common.util.data.RegisteredItemLike;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.component.ColorComponent;
import hellfirepvp.astralsorcery.common.item.*;
import hellfirepvp.astralsorcery.common.item.base.BlockItemCustom;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import hellfirepvp.astralsorcery.common.item.block.*;
import hellfirepvp.astralsorcery.common.item.crystal.AttunedCelestialCrystalItem;
import hellfirepvp.astralsorcery.common.item.crystal.AttunedRockCrystalItem;
import hellfirepvp.astralsorcery.common.item.crystal.CelestialCrystalItem;
import hellfirepvp.astralsorcery.common.item.crystal.RockCrystalItem;
import hellfirepvp.astralsorcery.common.item.dust.IlluminationPowderItem;
import hellfirepvp.astralsorcery.common.item.dust.NocturnalPowderItem;
import hellfirepvp.astralsorcery.common.item.dust.VividPowderItem;
import hellfirepvp.astralsorcery.common.item.tool.*;
import hellfirepvp.astralsorcery.common.item.wand.ArchitectWandItem;
import hellfirepvp.astralsorcery.common.item.wand.BlinkWandItem;
import hellfirepvp.astralsorcery.common.item.wand.ExchangeWandItem;
import hellfirepvp.astralsorcery.common.item.wand.GrapplingWandItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.SimpleTier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ItemsAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ItemsAS {

    public static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, AstralSorcery.MODID);

    public static final Tier CRYSTAL_TOOL_TIER = new SimpleTier(BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            741, 4F, 1F, 26, () -> Ingredient.EMPTY);

    //------------------------------- ITEMS -------------------------------//

    public static RegisteredItemLike<TomeItem> TOME = register("tome", TomeItem::new);
    public static RegisteredItemLike<AstrolabeItem> ASTROLABE = register("astrolabe", AstrolabeItem::new);
    public static RegisteredItemLike<WandItem> WAND = register("wand", WandItem::new);
    public static RegisteredItemLike<ChiselItem> CHISEL = register("chisel", ChiselItem::new);
    public static RegisteredItemLike<LinkingToolItem> LINKING_TOOL = register("linking_tool", LinkingToolItem::new);
    public static RegisteredItemLike<IlluminationWandItem> ILLUMINATION_WAND = register("illumination_wand", IlluminationWandItem::new);
    public static RegisteredItemLike<ArchitectWandItem> ARCHITECT_WAND = register("architect_wand", ArchitectWandItem::new);
    public static RegisteredItemLike<ExchangeWandItem> EXCHANGE_WAND = register("exchange_wand", ExchangeWandItem::new);
    public static RegisteredItemLike<BlinkWandItem> BLINK_WAND = register("blink_wand", BlinkWandItem::new);
    public static RegisteredItemLike<GrapplingWandItem> GRAPPLING_WAND = register("grappling_wand", GrapplingWandItem::new);
    public static RegisteredItemLike<KnowledgeShareItem> KNOWLEDGE_SHARE = register("knowledge_share", KnowledgeShareItem::new);
    public static RegisteredItemLike<ConstellationPaperItem> CONSTELLATION_PAPER = register("constellation_paper", ConstellationPaperItem::new);
    public static RegisteredItemLike<ArtifactItem> ARTIFACT = register("artifact", ArtifactItem::new);
    public static RegisteredItemLike<ArtifactShardItem> ARTIFACT_SHARD = register("artifact_shard", ArtifactShardItem::new);

    public static RegisteredItemLike<Item> AQUAMARINE = registerSimple("aquamarine");
    public static RegisteredItemLike<Item> GLASS_LENS = registerSimple("glass_lens");
    public static RegisteredItemLike<Item> RESONATING_GEM = registerSimple("resonating_gem");
    public static RegisteredItemLike<Item> PARCHMENT = registerSimple("parchment");
    public static RegisteredItemLike<Item> STARDUST = registerSimple("stardust");
    public static RegisteredItemLike<StarmetalIngotItem> STARMETAL_INGOT = register("starmetal_ingot", StarmetalIngotItem::new);
    public static RegisteredItemLike<Item> RAW_STARMETAL = registerSimple("raw_starmetal");
    public static RegisteredItemLike<PerkSealItem> PERK_SEAL = register("perk_seal", PerkSealItem::new);
    public static RegisteredItemLike<PerkNullifierItem> PERK_NULLIFIER = register("perk_nullifier", PerkNullifierItem::new);
    public static RegisteredItemLike<DynamismGemItem> DYNAMISM_GEM_SKY = register("dynamism_gem_sky", () -> new DynamismGemItem(DynamismGemItem.GemType.SKY));
    public static RegisteredItemLike<DynamismGemItem> DYNAMISM_GEM_DAY = register("dynamism_gem_day", () -> new DynamismGemItem(DynamismGemItem.GemType.DAY));
    public static RegisteredItemLike<DynamismGemItem> DYNAMISM_GEM_NIGHT = register("dynamism_gem_night", () -> new DynamismGemItem(DynamismGemItem.GemType.NIGHT));

    public static RegisteredItemLike<IlluminationPowderItem> ILLUMINATION_POWDER = register("illumination_powder", IlluminationPowderItem::new);
    public static RegisteredItemLike<NocturnalPowderItem> NOCTURNAL_POWDER = register("nocturnal_powder", NocturnalPowderItem::new);
    public static RegisteredItemLike<VividPowderItem> VIVID_POWDER = register("vivid_powder", VividPowderItem::new);
    public static RegisteredItemLike<ShiftingStarItem> SHIFTING_STAR = register("shifting_star", ShiftingStarItem::new);
    public static RegisteredItemLike<ShiftingStarItem> SHIFTING_STAR_AEVITAS = register("shifting_star_aevitas", () -> new ShiftingStarItem(ConstellationsAS.AEVITAS));
    public static RegisteredItemLike<ShiftingStarItem> SHIFTING_STAR_ARMARA = register("shifting_star_armara", () -> new ShiftingStarItem(ConstellationsAS.ARMARA));
    public static RegisteredItemLike<ShiftingStarItem> SHIFTING_STAR_DISCIDIA = register("shifting_star_discidia", () -> new ShiftingStarItem(ConstellationsAS.DISCIDIA));
    public static RegisteredItemLike<ShiftingStarItem> SHIFTING_STAR_EVORSIO = register("shifting_star_evorsio", () -> new ShiftingStarItem(ConstellationsAS.EVORSIO));
    public static RegisteredItemLike<ShiftingStarItem> SHIFTING_STAR_VICIO = register("shifting_star_vicio", () -> new ShiftingStarItem(ConstellationsAS.VICIO));
    public static RegisteredItemLike<AkashicSingularityItem> AKASHIC_SINGULARITY = register("akashic_singularity", AkashicSingularityItem::new);

    public static RegisteredItemLike<LumenCrystalItem> LUMEN_CRYSTAL = register("lumen_crystal", LumenCrystalItem::new);
    public static RegisteredItemLike<RockCrystalItem> ROCK_CRYSTAL = register("rock_crystal", RockCrystalItem::new);
    public static RegisteredItemLike<CelestialCrystalItem> CELESTIAL_CRYSTAL = register("celestial_crystal", CelestialCrystalItem::new);
    public static RegisteredItemLike<AttunedRockCrystalItem> ATTUNED_ROCK_CRYSTAL = register("attuned_rock_crystal", AttunedRockCrystalItem::new);
    public static RegisteredItemLike<AttunedCelestialCrystalItem> ATTUNED_CELESTIAL_CRYSTAL = register("attuned_celestial_crystal", AttunedCelestialCrystalItem::new);

    public static RegisteredItemLike<CrystalAxeItem> CRYSTAL_AXE = register("crystal_axe", CrystalAxeItem::new);
    public static RegisteredItemLike<CrystalPickaxeItem> CRYSTAL_PICKAXE = register("crystal_pickaxe", CrystalPickaxeItem::new);
    public static RegisteredItemLike<CrystalShovelItem> CRYSTAL_SHOVEL = register("crystal_shovel", CrystalShovelItem::new);
    public static RegisteredItemLike<CrystalSwordItem> CRYSTAL_SWORD = register("crystal_sword", CrystalSwordItem::new);
    public static RegisteredItemLike<IridescentCrystalAxeItem> IRIDESCENT_CRYSTAL_AXE = register("iridescent_crystal_axe", IridescentCrystalAxeItem::new);
    public static RegisteredItemLike<IridescentCrystalPickaxeItem> IRIDESCENT_CRYSTAL_PICKAXE = register("iridescent_crystal_pickaxe", IridescentCrystalPickaxeItem::new);
    public static RegisteredItemLike<IridescentCrystalShovelItem> IRIDESCENT_CRYSTAL_SHOVEL = register("iridescent_crystal_shovel", IridescentCrystalShovelItem::new);
    public static RegisteredItemLike<IridescentCrystalSwordItem> IRIDESCENT_CRYSTAL_SWORD = register("iridescent_crystal_sword", IridescentCrystalSwordItem::new);
    public static RegisteredItemLike<EnchantmentAmuletItem> ENCHANTMENT_AMULET = register("enchantment_amulet", EnchantmentAmuletItem::new);
    public static RegisteredItemLike<StardewItem> STARDEW = register("stardew", StardewItem::new);

    //------------------------------- BLOCK -------------------------------//

    public static RegisteredItemLike<BlockItem> BLOCK_MARBLE_ARCH               = registerBlockItem(BlocksAS.MARBLE_ARCH);
    public static RegisteredItemLike<BlockItem> BLOCK_MARBLE_BRICKS             = registerBlockItem(BlocksAS.MARBLE_BRICKS);
    public static RegisteredItemLike<BlockItem> BLOCK_MARBLE_CHISELED           = registerBlockItem(BlocksAS.MARBLE_CHISELED);
    public static RegisteredItemLike<BlockItem> BLOCK_MARBLE_ENGRAVED           = registerBlockItem(BlocksAS.MARBLE_ENGRAVED);
    public static RegisteredItemLike<BlockItem> BLOCK_MARBLE_PILLAR             = registerBlockItem(BlocksAS.MARBLE_PILLAR);
    public static RegisteredItemLike<BlockItem> BLOCK_MARBLE_RAW                = registerBlockItem(BlocksAS.MARBLE_RAW);
    public static RegisteredItemLike<BlockItem> BLOCK_MARBLE_RUNED              = registerBlockItem(BlocksAS.MARBLE_RUNED);
    public static RegisteredItemLike<BlockItem> BLOCK_MARBLE_SLAB               = registerBlockItem(BlocksAS.MARBLE_SLAB);
    public static RegisteredItemLike<BlockItem> BLOCK_MARBLE_STAIRS             = registerBlockItem(BlocksAS.MARBLE_STAIRS);

    public static RegisteredItemLike<BlockItem> BLOCK_SOOTY_MARBLE_ARCH         = registerBlockItem(BlocksAS.SOOTY_MARBLE_ARCH);
    public static RegisteredItemLike<BlockItem> BLOCK_SOOTY_MARBLE_BRICKS       = registerBlockItem(BlocksAS.SOOTY_MARBLE_BRICKS);
    public static RegisteredItemLike<BlockItem> BLOCK_SOOTY_MARBLE_CHISELED     = registerBlockItem(BlocksAS.SOOTY_MARBLE_CHISELED);
    public static RegisteredItemLike<BlockItem> BLOCK_SOOTY_MARBLE_ENGRAVED     = registerBlockItem(BlocksAS.SOOTY_MARBLE_ENGRAVED);
    public static RegisteredItemLike<BlockItem> BLOCK_SOOTY_MARBLE_PILLAR       = registerBlockItem(BlocksAS.SOOTY_MARBLE_PILLAR);
    public static RegisteredItemLike<BlockItem> BLOCK_SOOTY_MARBLE_RAW          = registerBlockItem(BlocksAS.SOOTY_MARBLE_RAW);
    public static RegisteredItemLike<BlockItem> BLOCK_SOOTY_MARBLE_RUNED        = registerBlockItem(BlocksAS.SOOTY_MARBLE_RUNED);
    public static RegisteredItemLike<BlockItem> BLOCK_SOOTY_MARBLE_SLAB         = registerBlockItem(BlocksAS.SOOTY_MARBLE_SLAB);
    public static RegisteredItemLike<BlockItem> BLOCK_SOOTY_MARBLE_STAIRS       = registerBlockItem(BlocksAS.SOOTY_MARBLE_STAIRS);

    public static RegisteredItemLike<BlockItem> BLOCK_INFUSED_WOOD_RAW          = registerBlockItem(BlocksAS.INFUSED_WOOD_RAW);
    public static RegisteredItemLike<BlockItem> BLOCK_INFUSED_WOOD_ARCH         = registerBlockItem(BlocksAS.INFUSED_WOOD_ARCH);
    public static RegisteredItemLike<BlockItem> BLOCK_INFUSED_WOOD_COLUMN       = registerBlockItem(BlocksAS.INFUSED_WOOD_COLUMN);
    public static RegisteredItemLike<BlockItem> BLOCK_INFUSED_WOOD_ENGRAVED     = registerBlockItem(BlocksAS.INFUSED_WOOD_ENGRAVED);
    public static RegisteredItemLike<BlockItem> BLOCK_INFUSED_WOOD_ENRICHED     = registerBlockItem(BlocksAS.INFUSED_WOOD_ENRICHED);
    public static RegisteredItemLike<BlockItem> BLOCK_INFUSED_WOOD_INFUSED      = registerBlockItem(BlocksAS.INFUSED_WOOD_INFUSED);
    public static RegisteredItemLike<BlockItem> BLOCK_INFUSED_WOOD_PLANKS       = registerBlockItem(BlocksAS.INFUSED_WOOD_PLANKS);
    public static RegisteredItemLike<BlockItem> BLOCK_INFUSED_WOOD_SLAB         = registerBlockItem(BlocksAS.INFUSED_WOOD_SLAB);
    public static RegisteredItemLike<BlockItem> BLOCK_INFUSED_WOOD_STAIRS       = registerBlockItem(BlocksAS.INFUSED_WOOD_STAIRS);

    public static RegisteredItemLike<BlockItem> BLOCK_AQUAMARINE_SHALE_ORE      = registerBlockItem(BlocksAS.AQUAMARINE_SHALE);
    public static RegisteredItemLike<BlockItem> BLOCK_ROCK_CRYSTAL_ORE          = registerBlockItem(BlocksAS.ROCK_CRYSTAL_ORE);
    public static RegisteredItemLike<BlockItem> BLOCK_STARMETAL_ORE             = registerBlockItem(BlocksAS.STARMETAL_ORE);
    public static RegisteredItemLike<BlockItem> BLOCK_RAW_STARMETAL_BLOCK       = registerBlockItem(BlocksAS.RAW_STARMETAL_BLOCK);

    public static RegisteredItemLike<BlockItem> BLOCK_GLIMMER_AMARANTH          = registerBlockItem(BlocksAS.GLIMMER_AMARANTH);
    public static RegisteredItemLike<BlockItem> BLOCK_HYACINTH                  = registerBlockItem(BlocksAS.HYACINTH);
    public static RegisteredItemLike<BlockItem> BLOCK_IRIS                      = registerBlockItem(BlocksAS.IRIS);
    public static RegisteredItemLike<BlockItem> BLOCK_ORCHID                    = registerBlockItem(BlocksAS.ORCHID);
    public static RegisteredItemLike<BlockItem> BLOCK_PROTEA                    = registerBlockItem(BlocksAS.PROTEA);
    public static RegisteredItemLike<BlockItem> BLOCK_THISTLE                   = registerBlockItem(BlocksAS.THISTLE);

    public static RegisteredItemLike<BlockItem> BLOCK_ALTAR_ILLUMINATION        = registerBlockItem(BlocksAS.ALTAR_ILLUMINATION);
    public static RegisteredItemLike<BlockItem> BLOCK_ALTAR_RESONANCE           = registerBlockItem(BlocksAS.ALTAR_RESONANCE);
    public static RegisteredItemLike<BlockItem> BLOCK_ALTAR_LUMINANCE           = registerBlockItem(BlocksAS.ALTAR_LUMINANCE);
    public static RegisteredItemLike<BlockItem> BLOCK_ALTAR_RADIANCE            = registerBlockItem(BlocksAS.ALTAR_RADIANCE);

    public static RegisteredItemLike<BlockItem> BLOCK_FOCUS_RELAY               = registerBlockItem(BlocksAS.FOCUS_RELAY);
    public static RegisteredItemLike<BlockItem> BLOCK_CELESTIAL_CRYSTAL_CLUSTER = registerBlockItem(BlocksAS.CELESTIAL_CRYSTAL_CLUSTER, CelestialCrystalClusterBlockItem::new);
    public static RegisteredItemLike<BlockItem> BLOCK_GEM_CRYSTAL_CLUSTER       = registerBlockItem(BlocksAS.GEM_CRYSTAL_CLUSTER, GemCrystalClusterBlockItem::new);
    public static RegisteredItemLike<BlockItem> BLOCK_LUMEN_CRYSTAL_CLUSTER     = registerBlockItem(BlocksAS.LUMEN_CRYSTAL_CLUSTER, LumenCrystalClusterBlockItem::new);

    public static RegisteredItemLike<BlockItem> BLOCK_LUMEN_ARRAY               = registerBlockItem(BlocksAS.LUMEN_ARRAY);
    public static RegisteredItemLike<BlockItem> BLOCK_LUMEN_ALCHEMY_ARRAY       = registerBlockItem(BlocksAS.LUMEN_ALCHEMY_ARRAY);
    public static RegisteredItemLike<BlockItem> BLOCK_LUMEN_FILAMENT            = registerBlockItem(BlocksAS.LUMEN_FILAMENT);
    public static RegisteredItemLike<BlockItem> BLOCK_LUMEN_CRYSTALLIZER        = registerBlockItem(BlocksAS.LUMEN_CRYSTALLIZER);
    public static RegisteredItemLike<BlockItem> BLOCK_LIGHTWELL                 = registerBlockItem(BlocksAS.LIGHTWELL);
    public static RegisteredItemLike<BlockItem> BLOCK_INFUSER                   = registerBlockItem(BlocksAS.INFUSER);
    public static RegisteredItemLike<BlockItem> BLOCK_CHALICE                   = registerBlockItem(BlocksAS.CHALICE);
    public static RegisteredItemLike<BlockItem> BLOCK_ATTUNEMENT_ALTAR          = registerBlockItem(BlocksAS.ATTUNEMENT_ALTAR);
    public static RegisteredItemLike<BlockItem> BLOCK_TREE_BEACON               = registerBlockItem(BlocksAS.TREE_BEACON);
    public static RegisteredItemLike<BlockItem> BLOCK_CELESTIAL_GATEWAY         = registerBlockItem(BlocksAS.CELESTIAL_GATEWAY,
            ItemsAS::registerColorDefaults);

    public static RegisteredItemLike<BlockItem> BLOCK_LENS                      = registerBlockItem(BlocksAS.LENS, LensBlockItem::new);
    public static RegisteredItemLike<BlockItem> BLOCK_PRISM                     = registerBlockItem(BlocksAS.PRISM, PrismBlockItem::new);
    public static RegisteredItemLike<BlockItem> BLOCK_STARLIGHT_FOCUS_ROCK_CRYSTAL =
            registerBlockItem(BlocksAS.STARLIGHT_FOCUS_ROCK_CRYSTAL, StarlightFocusCrystalBlockItem::new);
    public static RegisteredItemLike<BlockItem> BLOCK_STARLIGHT_FOCUS_CELESTIAL_CRYSTAL =
            registerBlockItem(BlocksAS.STARLIGHT_FOCUS_CELESTIAL_CRYSTAL, StarlightFocusCrystalBlockItem::new);
    public static RegisteredItemLike<BlockItem> BLOCK_STELLAR_FILAMENT = registerBlockItem(BlocksAS.STELLAR_FILAMENT);
    public static RegisteredItemLike<BlockItem> BLOCK_CAVE_ILLUMINATOR = registerBlockItem(BlocksAS.CAVE_ILLUMINATOR,
            ItemsAS::registerColorDefaults);

    public static RegisteredItemLike<BlockItem> BLOCK_STRUCTURE_MARKER = registerBlockItem(BlocksAS.STRUCTURE_MARKER, BlockItem::new);

    //----------------------------- REGISTER ------------------------------//

    private static BlockItem registerColorDefaults(Block block) {
        BlockItem item = new BlockItemCustom(block, new Item.Properties());
        DataComponentsAS.COLOR.setDefault(item, ColorComponent.DEFAULT_YELLOW);
        return item;
    }

    private static RegisteredItemLike<BlockItem> registerBlockItem(RegisteredItemLike<? extends Block> block) {
        return registerBlockItem(block, BlockItemCustom::new);
    }

    private static <T extends BlockItem, B extends Block> RegisteredItemLike<T> registerBlockItem(RegisteredItemLike<B> block, Function<B, T> blockItem) {
        return register(block.getId().getPath(), () -> blockItem.apply(block.get()));
    }

    private static RegisteredItemLike<BlockItem> registerBlockItem(RegisteredItemLike<? extends Block> block, BiFunction<Block, Item.Properties, BlockItem> ctor) {
        Item.Properties blockItemProperties = new Item.Properties();
        ResourceLocation key = block.getId();
        return register(key.getPath(), () -> ctor.apply(block.get(), blockItemProperties));
    }

    private static RegisteredItemLike<Item> registerSimple(String name) {
        return register(name, () -> new ItemCustom(new Item.Properties()));
    }

    private static <T extends Item> RegisteredItemLike<T> register(String name, Supplier<T> item) {
        return new RegisteredItemLike<>(ITEM_REGISTER.register(name, item));
    }
}
