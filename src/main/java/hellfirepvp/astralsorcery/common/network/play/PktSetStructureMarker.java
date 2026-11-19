/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.tile.TileStructureMarker;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktSetStructureMarker
 * Created by HellFirePvP
 * Date: 07.10.2026 / 10:27
 */
public class PktSetStructureMarker extends PlayPacketHandler.ToServer<PktSetStructureMarker.Request> {

    static final StreamCodec<FriendlyByteBuf, Request> CODEC = StreamCodec.composite(
            ByteBufCodecs.BLOCK_POS, Request::pos,
            ByteBufCodecs.RESOURCE_LOCATION, Request::markerId,
            Request::new);

    public static final PktSetStructureMarker HANDLER = new PktSetStructureMarker();

    private PktSetStructureMarker() {
        super(Request.class);
    }

    public static Request setMarker(BlockPos pos, ResourceLocation markerId) {
        return new Request(pos, markerId);
    }

    @Override
    public StreamCodec<FriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, PacketContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer sPlayer) || !sPlayer.canUseGameMasterBlocks()) {
                return;
            }
            MiscUtil.getTileAt(sPlayer.level(), payload.pos(), TileStructureMarker.class, false)
                    .ifPresent(marker -> {
                        marker.getTileData().setMarkerId(payload.markerId());
                        marker.markForUpdate();
                    });
        });
    }

    public record Request(BlockPos pos, ResourceLocation markerId) {}
}
