/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.enchantment.ScorchingHeatEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EnchantmentsAS {

    public static final DeferredRegister<Enchantment> ENCHANTMENT_REGISTER =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, AstralSorcery.MODID);

    public static final RegistryObject<Enchantment> SCORCHING_HEAT =
            ENCHANTMENT_REGISTER.register("scorching_heat", ScorchingHeatEnchantment::new);
}
