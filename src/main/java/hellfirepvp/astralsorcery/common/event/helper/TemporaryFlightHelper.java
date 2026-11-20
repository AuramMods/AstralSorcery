/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.helper;

import hellfirepvp.astralsorcery.common.util.tick.TimeoutList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TemporaryFlightHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TemporaryFlightHelper {

    private static final TimeoutList<ServerPlayer> temporaryFlight = new TimeoutList<>(TemporaryFlightHelper::releaseFlight);

    private TemporaryFlightHelper() {}

    public static void attachListeners(IEventBus bus) {
        bus.addListener(temporaryFlight::onServerTick);
        bus.addListener(TemporaryFlightHelper::onDisconnect);
        bus.addListener(TemporaryFlightHelper::onClone);
        bus.addListener(TemporaryFlightHelper::onServerStopped);
    }

    private static void onDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        removePlayer(event.getEntity());
    }

    private static void onClone(PlayerEvent.Clone event) {
        removePlayer(event.getOriginal());
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        temporaryFlight.clear();
    }

    private static void removePlayer(Player player) {
        if (player instanceof ServerPlayer serverPlayer && temporaryFlight.remove(serverPlayer)) {
            releaseFlight(serverPlayer);
        }
    }

    private static void releaseFlight(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator()) return;

        Abilities abilities = player.getAbilities();
        abilities.mayfly = false;
        abilities.flying = false;
        updateAbilities(player);
    }

    private static void updateAbilities(ServerPlayer player) {
        if (player.connection != null && player.connection.isAcceptingMessages()) {
            player.onUpdateAbilities();
        }
    }

    public static void removeTemporaryFlightFromSave(ServerPlayer player, CompoundTag playerTag) {
        if (temporaryFlight.contains(player) && !player.isCreative() && !player.isSpectator()) {
            CompoundTag abilities = playerTag.getCompound("abilities");
            abilities.putBoolean("mayfly", false);
            abilities.putBoolean("flying", false);
        }
    }

    public static boolean allowFlight(Player player) {
        return allowFlight(player, 20);
    }

    public static boolean allowFlight(Player player, int timeout) {
        if (!(player instanceof ServerPlayer serverPlayer) || player.isCreative() || player.isSpectator()) {
            return false;
        }

        Abilities abilities = player.getAbilities();
        if (abilities.mayfly && !temporaryFlight.contains(serverPlayer)) {
            return false;
        }

        temporaryFlight.setOrAddTimeout(timeout, serverPlayer);
        if (!abilities.mayfly) {
            abilities.mayfly = true;
            updateAbilities(serverPlayer);
        }
        return true;
    }
}
