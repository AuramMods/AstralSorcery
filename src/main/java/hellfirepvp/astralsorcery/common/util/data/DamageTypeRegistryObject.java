/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DamageTypeRegistryObject
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record DamageTypeRegistryObject(ResourceKey<DamageType> id, Supplier<DamageType> damageType) {

    public ResourceLocation idLocation() {
        return this.id.location();
    }

    public DamageSource source(Level level) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(this.id));
    }

    public void register(BootstapContext<DamageType> context) {
        AstralSorcery.assertDataGeneration();
        context.register(this.id(), this.damageType().get());
    }
}
