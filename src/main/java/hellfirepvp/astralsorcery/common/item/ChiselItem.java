/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ChiselItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ChiselItem extends ItemCustom {

    public ChiselItem() {
        super(new Properties()
                .durability(64));
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 3;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.UNBREAKING || enchantment == Enchantments.BLOCK_FORTUNE;
    }

    public static void damageAfterUse(ItemStack chisel, ServerPlayer player, int amount) {
        chisel.hurtAndBreak(amount, player, owner -> {
            owner.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            ForgeEventFactory.onPlayerDestroyItem(owner, chisel, InteractionHand.MAIN_HAND);
        });
    }
}
