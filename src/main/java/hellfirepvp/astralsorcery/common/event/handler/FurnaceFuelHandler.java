/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.handler;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

public final class FurnaceFuelHandler {

    private FurnaceFuelHandler() {}

    public static void attachListeners(IEventBus bus) {
        bus.addListener(EventPriority.LOWEST, FurnaceFuelHandler::onFuelBurnTime);
    }

    private static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        ItemStack fuel = event.getItemStack();
        if (fuel.is(BlocksAS.INFUSED_WOOD_SLAB.asItem())) {
            event.setBurnTime(150);
        } else if (fuel.is(BlocksAS.INFUSED_WOOD_STAIRS.asItem()) || fuel.is(TagsAS.Items.INFUSED_WOOD)) {
            event.setBurnTime(300);
        }
    }
}
