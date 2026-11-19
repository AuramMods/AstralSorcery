/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tick;

import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.SidedHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkTickHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkTickHelper {

    private PerkTickHelper() {}

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(PerkTickHelper::onPlayerTick);
    }

    private static void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        LogicalSide side = SidedHelper.getSide(player);
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (prog.isValid()) {
            for (AbstractPerk<?> perk : prog.getPerkData().getEffectGrantingPerks()) {
                if (perk instanceof TickablePerk tickPerk) {
                    tickPerk.tick(player, side);
                }
            }
        }
    }
}
