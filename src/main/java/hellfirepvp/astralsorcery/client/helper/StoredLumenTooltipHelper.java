/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.helper;

import com.mojang.datafixers.util.Either;
import hellfirepvp.astralsorcery.common.component.IdentifierComponent;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.util.tooltip.StoredLumenDisplayTooltip;
import net.minecraft.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.client.event.RenderTooltipEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StoredLumenTooltipHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StoredLumenTooltipHelper {

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(StoredLumenTooltipHelper::onTooltipGather);
    }

    private static void onTooltipGather(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        if (DataComponentsAS.STORED_LUMEN.has(stack) && DataComponentsAS.IDENTIFIER.has(stack)) {
            IdentifierComponent idCmp = DataComponentsAS.IDENTIFIER.getOrDefault(stack, IdentifierComponent.NONE);
            StoredLumenComponent cmp = DataComponentsAS.STORED_LUMEN.getOrDefault(stack, StoredLumenComponent.EMPTY);
            if (idCmp.id().equals(Util.NIL_UUID)) return;
            String idStr = idCmp.id().toString();

            var tooltip = event.getTooltipElements();
            int index = -1;
            for (int i = 0; i < tooltip.size(); i++) {
                var element = tooltip.get(i);

                if (element.map(txt -> txt.getString().equals(idStr), ttCmp -> false)) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                tooltip.set(index, Either.right(StoredLumenDisplayTooltip.of(stack.copy(), idCmp, cmp)));
            }
        }
    }
}
