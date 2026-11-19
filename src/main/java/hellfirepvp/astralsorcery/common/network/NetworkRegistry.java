/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network;

import hellfirepvp.astralsorcery.common.network.play.*;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkRegistry.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: NetworkRegistry
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public final class NetworkRegistry {

    public static final String NET_VERSION = "forge-1";

    private static final SimpleChannel CHANNEL = ChannelBuilder.named(AstralSorcery.key("play"))
            .networkProtocolVersion(() -> NET_VERSION)
            .clientAcceptedVersions(NET_VERSION::equals)
            .serverAcceptedVersions(NET_VERSION::equals)
            .simpleChannel();

    private NetworkRegistry() {}

    public static void registerPackets() {
        List<PlayPacketHandler<?>> handlers = List.of(
                PktSyncPlayerProgress.HANDLER,
                PktSyncResearchNodes.HANDLER,
                PktSyncData.HANDLER,
                PktUpdateLinkSession.HANDLER,
                PktPlayVisualEffect.HANDLER,
                PktSyncModifierSource.HANDLER,
                PktSyncPerkTree.HANDLER,
                PktSyncPerkLevels.HANDLER,
                PktSyncPerkActivity.HANDLER,
                PktSyncCustomDestroyProgress.HANDLER,
                PktSyncAuxiliaryLightManager.HANDLER,
                PktSyncLumenBindingTypes.HANDLER,
                PktPlayStructurePreview.HANDLER,
                PktOpenClientScreen.HANDLER,
                PktAdjustAstrolabeAngle.HANDLER,
                PktDiscoverConstellation.HANDLER,
                PktRequestSocketPerkItem.HANDLER,
                PktRequestCancelLinkSession.HANDLER,
                PktRequestGatewayTeleport.HANDLER,
                PktRequestLearnedTomeNavigation.HANDLER,
                PktSetStructureMarker.HANDLER,
                PktRequestSeed.HANDLER,
                PktRequestUnlockPerk.HANDLER,
                PktRequestPerkSealAction.HANDLER,
                PktRequestRemovePerk.HANDLER
        );
        for (int discriminator = 0; discriminator < handlers.size(); discriminator++) {
            handlers.get(discriminator).register(CHANNEL, discriminator);
        }
    }

    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }

    public static void sendToPlayer(ServerPlayer player, Object packet) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToAllPlayers(Object packet) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
    }

    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos pos, Object packet) {
        LevelChunk chunk = level.getChunkSource().getChunkNow(pos.x, pos.z);
        if (chunk != null) {
            CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
        }
    }

    public static void sendToPlayersNear(ServerLevel level, @Nullable ServerPlayer excluded,
                                         double x, double y, double z, double range, Object packet) {
        PacketDistributor.TargetPoint target = new PacketDistributor.TargetPoint(excluded, x, y, z, range, level.dimension());
        CHANNEL.send(PacketDistributor.NEAR.with(() -> target), packet);
    }
}
