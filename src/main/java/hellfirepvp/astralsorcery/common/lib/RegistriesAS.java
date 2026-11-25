/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.artifact.ArtifactCondition;
import hellfirepvp.astralsorcery.common.artifact.ArtifactEffect;
import hellfirepvp.astralsorcery.common.artifact.ArtifactType;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.data.sync.SyncData;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.binding.effect.LumenBindingEffect;
import hellfirepvp.astralsorcery.common.lumen.binding.usage.LumenBindingUsage;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeLimiter;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.reader.PerkAttributeTypeReader;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSourceProvider;
import hellfirepvp.astralsorcery.common.perk.tree.PerkDataType;
import hellfirepvp.astralsorcery.common.perk.tree.PerkType;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.recipe.altar.effect.AltarEffect;
import hellfirepvp.astralsorcery.common.recipe.altar.output.AltarRecipeOutputModifier;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.result.LiquidInteractionResult;
import hellfirepvp.astralsorcery.common.recipe.liquid.output.LiquidStarlightRecipeOutputModifier;
import hellfirepvp.astralsorcery.common.research.condition.ResearchNodeCondition;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import hellfirepvp.astralsorcery.common.starlight.api.provider.TransmissionNodeProvider;
import hellfirepvp.astralsorcery.common.focal.node.FocalPointNode;
import hellfirepvp.astralsorcery.common.worldgen.structure.marker.StructureMarkerReplacement;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RegistriesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RegistriesAS {

    public static final ResourceKey<Registry<BaseConstellation>> KEY_CONSTELLATIONS = registryKey("constellations");
    public static final ResourceKey<Registry<Lumen>> KEY_LUMEN = registryKey("lumen");
    public static final ResourceKey<Registry<CrystalProperty>> KEY_CRYSTAL_PROPERTIES = registryKey("crystal_properties");
    public static final ResourceKey<Registry<TransmissionNodeProvider<?>>> KEY_TRANSMISSION_NODES = registryKey("transmission_nodes");
    public static final ResourceKey<Registry<AltarEffect>> KEY_ALTAR_EFFECTS = registryKey("altar_effects");
    public static final ResourceKey<Registry<PerkType<?>>> KEY_PERK_TYPES = registryKey("perk_types");
    public static final ResourceKey<Registry<PerkDataType<?>>> KEY_PERK_DATA_TYPES = registryKey("perk_data_types");
    public static final ResourceKey<Registry<PerkAttributeTypeReader.Type>> KEY_PERK_ATTRIBUTE_TYPE_READERS = registryKey("perk_attribute_type_readers");
    public static final ResourceKey<Registry<PerkAttributeType>> KEY_PERK_ATTRIBUTE_TYPES = registryKey("perk_attribute_types");
    public static final ResourceKey<Registry<PerkAttributeConverter>> KEY_PERK_CONVERTERS = registryKey("perk_converters");
    public static final ResourceKey<Registry<PerkAttributeModifier>> KEY_PERK_CUSTOM_MODIFIERS = registryKey("perk_custom_modifiers");
    public static final ResourceKey<Registry<ModifierSourceProvider<?>>> KEY_PERK_MODIFIER_SOURCES = registryKey("perk_modifier_sources");
    public static final ResourceKey<Registry<PerkAttributeLimiter.Limit>> KEY_PERK_ATTRIBUTE_LIMITS = registryKey("perk_attribute_limits");

    public static final ResourceKey<Registry<TomePage.TomePageType<?>>> KEY_TOME_PAGE_TYPES = registryKey("tome_page_types");
    public static final ResourceKey<Registry<SyncData.Type<?, ?, ?, ?>>> KEY_SYNC_DATA_TYPES = registryKey("sync_data_types");
    public static final ResourceKey<Registry<FocalPointNode.Type<?>>> KEY_FOCAL_NODE_TYPES = registryKey("focal_node_types");
    public static final ResourceKey<Registry<ResearchNodeCondition.Type<?>>> KEY_RESEARCH_NODE_CONDITION_TYPES = registryKey("research_node_condition_types");
    public static final ResourceKey<Registry<AltarRecipeOutputModifier.Type<?>>> KEY_ALTAR_OUTPUT_MODIFIER_TYPES = registryKey("altar_output_modifier_types");
    public static final ResourceKey<Registry<LiquidStarlightRecipeOutputModifier.Type<?>>> KEY_LIQUID_STARLIGHT_OUTPUT_MODIFIER_TYPES = registryKey("liquid_starlight_output_modifier_types");
    public static final ResourceKey<Registry<LiquidInteractionResult.Type<?>>> KEY_LIQUID_INTERACTION_RESULT_TYPES = registryKey("liquid_interaction_result_types");
    public static final ResourceKey<Registry<PerkRequirement.Type<?>>> KEY_PERK_REQUIREMENT_TYPES = registryKey("perk_requirement_types");
    public static final ResourceKey<Registry<ArtifactType>> KEY_ARTIFACT_TYPES = registryKey("artifact_types");
    public static final ResourceKey<Registry<ArtifactCondition.Type<?>>> KEY_ARTIFACT_CONDITION_TYPES = registryKey("artifact_condition_types");
    public static final ResourceKey<Registry<ArtifactEffect.Type<?>>> KEY_ARTIFACT_EFFECT_TYPES = registryKey("artifact_effect_types");
    public static final ResourceKey<Registry<LumenBindingUsage.Type<?>>> KEY_LUMEN_BINDING_USAGE_TYPES = registryKey("lumen_binding_usage_types");
    public static final ResourceKey<Registry<LumenBindingEffect.Type<?>>> KEY_LUMEN_BINDING_EFFECT_TYPES = registryKey("lumen_binding_effect_types");
    public static final ResourceKey<Registry<StructureMarkerReplacement.Type<?>>> KEY_STRUCTURE_MARKER_REPLACEMENT_TYPES = registryKey("structure_marker_replacement_types");

    public static final Supplier<Registry<BaseConstellation>> REGISTRY_CONSTELLATIONS = () -> registry(KEY_CONSTELLATIONS);
    public static final Supplier<Registry<Lumen>> REGISTRY_LUMEN = () -> registry(KEY_LUMEN);
    public static final Supplier<Registry<CrystalProperty>> REGISTRY_CRYSTAL_PROPERTIES = () -> registry(KEY_CRYSTAL_PROPERTIES);
    public static final Supplier<Registry<TransmissionNodeProvider<?>>> REGISTRY_TRANSMISSION_NODES = () -> registry(KEY_TRANSMISSION_NODES);
    public static final Supplier<Registry<AltarEffect>> REGISTRY_ALTAR_EFFECTS = () -> registry(KEY_ALTAR_EFFECTS);
    public static final Supplier<Registry<PerkType<?>>> REGISTRY_PERK_TYPES = () -> registry(KEY_PERK_TYPES);
    public static final Supplier<Registry<PerkDataType<?>>> REGISTRY_PERK_DATA_TYPES = () -> registry(KEY_PERK_DATA_TYPES);
    public static final Supplier<Registry<PerkAttributeTypeReader.Type>> REGISTRY_PERK_ATTRIBUTE_TYPE_READERS = () -> registry(KEY_PERK_ATTRIBUTE_TYPE_READERS);
    public static final Supplier<Registry<PerkAttributeType>> REGISTRY_PERK_ATTRIBUTE_TYPES = () -> registry(KEY_PERK_ATTRIBUTE_TYPES);
    public static final Supplier<Registry<PerkAttributeConverter>> REGISTRY_PERK_CONVERTERS = () -> registry(KEY_PERK_CONVERTERS);
    public static final Supplier<Registry<PerkAttributeModifier>> REGISTRY_PERK_CUSTOM_MODIFIERS = () -> registry(KEY_PERK_CUSTOM_MODIFIERS);
    public static final Supplier<Registry<ModifierSourceProvider<?>>> REGISTRY_PERK_MODIFIER_SOURCES = () -> registry(KEY_PERK_MODIFIER_SOURCES);
    public static final Supplier<Registry<PerkAttributeLimiter.Limit>> REGISTRY_PERK_ATTRIBUTE_LIMITS = () -> registry(KEY_PERK_ATTRIBUTE_LIMITS);

    public static final Supplier<Registry<TomePage.TomePageType<?>>> REGISTRY_TOME_PAGE_TYPES = () -> registry(KEY_TOME_PAGE_TYPES);
    public static final Supplier<Registry<SyncData.Type<?, ?, ?, ?>>> REGISTRY_SYNC_DATA_TYPES = () -> registry(KEY_SYNC_DATA_TYPES);
    public static final Supplier<Registry<FocalPointNode.Type<?>>> REGISTRY_FOCAL_NODE_TYPES = () -> registry(KEY_FOCAL_NODE_TYPES);
    public static final Supplier<Registry<ResearchNodeCondition.Type<?>>> REGISTRY_RESEARCH_NODE_CONDITION_TYPES = () -> registry(KEY_RESEARCH_NODE_CONDITION_TYPES);
    public static final Supplier<Registry<AltarRecipeOutputModifier.Type<?>>> REGISTRY_ALTAR_OUTPUT_MODIFIER_TYPES = () -> registry(KEY_ALTAR_OUTPUT_MODIFIER_TYPES);
    public static final Supplier<Registry<LiquidStarlightRecipeOutputModifier.Type<?>>> REGISTRY_LIQUID_STARLIGHT_OUTPUT_MODIFIER_TYPES = () -> registry(KEY_LIQUID_STARLIGHT_OUTPUT_MODIFIER_TYPES);
    public static final Supplier<Registry<LiquidInteractionResult.Type<?>>> REGISTRY_LIQUID_INTERACTION_RESULT_TYPES = () -> registry(KEY_LIQUID_INTERACTION_RESULT_TYPES);
    public static final Supplier<Registry<PerkRequirement.Type<?>>> REGISTRY_PERK_REQUIREMENT_TYPES = () -> registry(KEY_PERK_REQUIREMENT_TYPES);
    public static final Supplier<Registry<ArtifactType>> REGISTRY_ARTIFACT_TYPES = () -> registry(KEY_ARTIFACT_TYPES);
    public static final Supplier<Registry<ArtifactCondition.Type<?>>> REGISTRY_ARTIFACT_CONDITION_TYPES = () -> registry(KEY_ARTIFACT_CONDITION_TYPES);
    public static final Supplier<Registry<ArtifactEffect.Type<?>>> REGISTRY_ARTIFACT_EFFECT_TYPES = () -> registry(KEY_ARTIFACT_EFFECT_TYPES);
    public static final Supplier<Registry<LumenBindingUsage.Type<?>>> REGISTRY_LUMEN_BINDING_USAGE_TYPES = () -> registry(KEY_LUMEN_BINDING_USAGE_TYPES);
    public static final Supplier<Registry<LumenBindingEffect.Type<?>>> REGISTRY_LUMEN_BINDING_EFFECT_TYPES = () -> registry(KEY_LUMEN_BINDING_EFFECT_TYPES);
    public static final Supplier<Registry<StructureMarkerReplacement.Type<?>>> REGISTRY_STRUCTURE_MARKER_REPLACEMENT_TYPES = () -> registry(KEY_STRUCTURE_MARKER_REPLACEMENT_TYPES);

    public static void createRegistries(NewRegistryEvent event) {
        create(event, KEY_CONSTELLATIONS);
        create(event, KEY_LUMEN);
        create(event, KEY_CRYSTAL_PROPERTIES);
        create(event, KEY_TRANSMISSION_NODES, false);
        create(event, KEY_ALTAR_EFFECTS);
        create(event, KEY_PERK_TYPES);
        create(event, KEY_PERK_DATA_TYPES);
        create(event, KEY_PERK_ATTRIBUTE_TYPE_READERS);
        event.create(new RegistryBuilder<PerkAttributeType>().setName(KEY_PERK_ATTRIBUTE_TYPES.location()).hasTags()
                .onAdd((owner, stage, id, key, value, previous) -> {
                    if (stage == RegistryManager.ACTIVE) PerkAttributeType.invalidateVanillaAttributeIndex();
                }).onClear((owner, stage) -> {
                    if (stage == RegistryManager.ACTIVE) PerkAttributeType.invalidateVanillaAttributeIndex();
                }).onBake((owner, stage) -> {
                    if (stage == RegistryManager.ACTIVE) PerkAttributeType.beginVanillaAttributeIndex();
                }));
        create(event, KEY_PERK_CONVERTERS);
        create(event, KEY_PERK_CUSTOM_MODIFIERS);
        create(event, KEY_PERK_MODIFIER_SOURCES);
        create(event, KEY_PERK_ATTRIBUTE_LIMITS);

        create(event, KEY_TOME_PAGE_TYPES);
        create(event, KEY_SYNC_DATA_TYPES);
        create(event, KEY_FOCAL_NODE_TYPES);
        create(event, KEY_RESEARCH_NODE_CONDITION_TYPES);
        create(event, KEY_ALTAR_OUTPUT_MODIFIER_TYPES);
        create(event, KEY_LIQUID_STARLIGHT_OUTPUT_MODIFIER_TYPES);
        create(event, KEY_LIQUID_INTERACTION_RESULT_TYPES);
        create(event, KEY_PERK_REQUIREMENT_TYPES);
        create(event, KEY_ARTIFACT_TYPES);
        create(event, KEY_ARTIFACT_CONDITION_TYPES);
        create(event, KEY_ARTIFACT_EFFECT_TYPES);
        create(event, KEY_LUMEN_BINDING_USAGE_TYPES);
        create(event, KEY_LUMEN_BINDING_EFFECT_TYPES);
        create(event, KEY_STRUCTURE_MARKER_REPLACEMENT_TYPES, false);
    }

    private static <T> void create(NewRegistryEvent event, ResourceKey<Registry<T>> key) {
        create(event, key, true);
    }

    private static <T> void create(NewRegistryEvent event, ResourceKey<Registry<T>> key, boolean sync) {
        RegistryBuilder<T> builder = new RegistryBuilder<T>().setName(key.location()).hasTags();
        if (!sync) {
            builder.disableSync();
        }
        event.create(builder);
    }

    public static <T> Registry<T> registry(ResourceKey<Registry<T>> key) {
        return GameData.getWrapper(key, Lifecycle.stable());
    }

    public static <T> Codec<T> codec(ResourceKey<Registry<T>> key) {
        return ExtraCodecs.lazyInitializedCodec(() -> registry(key).byNameCodec());
    }

    public static <T> Codec<Holder<T>> holderCodec(ResourceKey<Registry<T>> key) {
        return ExtraCodecs.lazyInitializedCodec(() -> registry(key).holderByNameCodec());
    }

    private static <T> ResourceKey<Registry<T>> registryKey(String name) {
        return ResourceKey.createRegistryKey(AstralSorcery.key(name));
    }
}
