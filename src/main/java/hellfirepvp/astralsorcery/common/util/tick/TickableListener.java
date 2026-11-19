/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.tick;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TickableListener
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface TickableListener extends Tickable {

    default void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        this.tick();
    }

    default void onLevelTick(LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        this.tick();
    }

    default void onClientTick(ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        this.tick();
    }

    default void onServerTick(ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        this.tick();
    }
}
