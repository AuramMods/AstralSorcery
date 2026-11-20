/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.worldgen.feature.RockCrystalOreFeature;
import hellfirepvp.astralsorcery.common.worldgen.feature.RockCrystalOreFeatureConfiguration;
import hellfirepvp.astralsorcery.common.worldgen.placement.RiverbedPlacement;
import hellfirepvp.astralsorcery.common.worldgen.structure.TemplateStructure;
import hellfirepvp.astralsorcery.common.worldgen.structure.processor.FlareLightColorizationProcessor;
import hellfirepvp.astralsorcery.common.worldgen.structure.processor.FlareLightExtinguishProcessor;
import hellfirepvp.astralsorcery.common.worldgen.structure.processor.FocalPointRegisterProcessor;
import hellfirepvp.astralsorcery.common.worldgen.structure.processor.StructureMarkerProcessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: WorldGenAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class WorldGenAS {

    public static final DeferredRegister<Feature<?>> FEATURE_REGISTER =
            DeferredRegister.create(Registries.FEATURE, AstralSorcery.MODID);
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_REGISTER =
            DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, AstralSorcery.MODID);
    public static final DeferredRegister<StructureType<?>> STRUCTURE_REGISTER =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, AstralSorcery.MODID);
    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSOR_REGISTER =
            DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, AstralSorcery.MODID);

    public static final RegistryObject<RockCrystalOreFeature> ROCK_CRYSTAL_ORE =
            FEATURE_REGISTER.register("rock_crystal_ore", () -> new RockCrystalOreFeature(RockCrystalOreFeatureConfiguration.CODEC));

    public static final RegistryObject<PlacementModifierType<RiverbedPlacement>> RIVERBED_PLACEMENT =
            PLACEMENT_REGISTER.register("riverbed_placement", () -> () -> RiverbedPlacement.CODEC.codec());

    public static final RegistryObject<StructureType<TemplateStructure>> TEMPLATE_STRUCTURE =
            STRUCTURE_REGISTER.register("template_structure", () -> () -> TemplateStructure.CODEC);

    public static final RegistryObject<StructureProcessorType<FocalPointRegisterProcessor>> FOCAL_POINT_REGISTER_PROCESSOR =
            STRUCTURE_PROCESSOR_REGISTER.register("focal_point_register_processor", () -> () -> FocalPointRegisterProcessor.CODEC);
    public static final RegistryObject<StructureProcessorType<StructureMarkerProcessor>> STRUCTURE_MARKER_PROCESSOR =
            STRUCTURE_PROCESSOR_REGISTER.register("structure_marker_processor", () -> () -> StructureMarkerProcessor.CODEC);
    public static final RegistryObject<StructureProcessorType<FlareLightExtinguishProcessor>> FLARE_LIGHT_EXTINGUISH_PROCESSOR =
            STRUCTURE_PROCESSOR_REGISTER.register("flare_light_extinguish_processor", () -> () -> FlareLightExtinguishProcessor.CODEC);
    public static final RegistryObject<StructureProcessorType<FlareLightColorizationProcessor>> FLARE_LIGHT_COLORIZATION_PROCESSOR =
            STRUCTURE_PROCESSOR_REGISTER.register("flare_light_colorization_processor", () -> () -> FlareLightColorizationProcessor.CODEC);

}
