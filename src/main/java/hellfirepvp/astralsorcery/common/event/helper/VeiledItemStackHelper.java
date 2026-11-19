/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.helper;

import hellfirepvp.astralsorcery.common.component.VeiledItemStackComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VeiledItemStackHelper
 * Created by HellFirePvP
 * Date: 24.10.2026 / 21:55
 */
public class VeiledItemStackHelper {

    private VeiledItemStackHelper() {}

    public static void attachListeners(IEventBus bus) {
        bus.addListener(VeiledItemStackHelper::onPlayerTick);
    }

    private static void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer sPlayer)) return;

        Inventory inventory = sPlayer.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            removeVeilIfExpired(inventory.getItem(slot), sPlayer.level());
        }
    }

    public static ItemStack getDisplayedStack(ItemStack stack) {
        VeiledItemStackComponent veil = DataComponentsAS.VEILED.get(stack);
        if (veil == null) {
            return stack;
        }
        ItemStack appearance = veil.appearance();
        appearance.setCount(stack.getCount());
        return appearance;
    }

    public static Component markUnknownVeil(ItemStack stack, Component name) {
        VeiledItemStackComponent veil = DataComponentsAS.VEILED.get(stack);
        if (veil != null && !veil.known()) {
            return Component.translatable("component.veiled.item_name", name);
        }
        return name;
    }

    public static boolean removeVeilIfExpired(ItemStack stack, Level level) {
        VeiledItemStackComponent veil = DataComponentsAS.VEILED.get(stack);
        if (veil != null && veil.isExpired(level)) {
            DataComponentsAS.VEILED.remove(stack);
            return true;
        }
        return false;
    }
}
