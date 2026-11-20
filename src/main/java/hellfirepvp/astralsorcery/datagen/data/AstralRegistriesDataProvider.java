/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data;

import hellfirepvp.astralsorcery.common.lib.DamageTypesAS;
import hellfirepvp.astralsorcery.datagen.data.world.AstralWorldGenProvider;
import hellfirepvp.astralsorcery.datagen.data.world.structure.AstralStructureProvider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralRegistriesDataProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralRegistriesDataProvider {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, AstralRegistriesDataProvider::generateDamageTypes)
            .add(Registries.CONFIGURED_FEATURE, AstralWorldGenProvider::generateConfiguredFeatures)
            .add(Registries.PLACED_FEATURE, AstralWorldGenProvider::generatePlacedFeatures)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, AstralWorldGenProvider::generateBiomeModifiers)
            .add(Registries.STRUCTURE, AstralStructureProvider::generateStructures)
            .add(Registries.STRUCTURE_SET, AstralStructureProvider::generateStructureSets)
            .add(Registries.TEMPLATE_POOL, AstralStructureProvider::generateStructurePools)
            .add(Registries.PROCESSOR_LIST, AstralStructureProvider::generateStructureProcessorLists);

    public static RegistrySetBuilder getRegistryBuilder() {
        return BUILDER;
    }

    private static void generateDamageTypes(BootstapContext<DamageType> context) {
        DamageTypesAS.STELLAR.register(context);
    }
}
