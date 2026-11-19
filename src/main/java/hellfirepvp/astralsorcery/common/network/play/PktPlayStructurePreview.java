/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.client.util.structure.StructurePreviewHelper;
import hellfirepvp.astralsorcery.common.lib.ObserversAS;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ObserverRegistryObject;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktPlayStructurePreview
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktPlayStructurePreview extends PlayPacketHandler.ToClient<PktPlayStructurePreview.Request> {

    static final StreamCodec<FriendlyByteBuf, PktPlayStructurePreview.Request> CODEC = StreamCodec.composite(
            ByteBufCodecs.BLOCK_POS,
            Request::tilePos,
            ByteBufCodecs.RESOURCE_LOCATION,
            PktPlayStructurePreview.Request::observerKey,
            PktPlayStructurePreview.Request::new);

    public static final PktPlayStructurePreview HANDLER = new PktPlayStructurePreview();

    private PktPlayStructurePreview() {
        super(Request.class);
    }

    public static Request showPreview(BlockPos tilePos, ObserverRegistryObject registryObject) {
        return new Request(tilePos, registryObject.observer().getId());
    }

    @Override
    public StreamCodec<FriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, PacketContext context) {
        context.enqueueWork(() -> {
            ObserversAS.getByName(payload.observerKey()).ifPresent(obj -> {
                Player clientPlayer = context.player();
                Level level = clientPlayer.level();
                TileEntityTick<?> tile = MiscUtil.getTileAt(level, payload.tilePos(), TileEntityTick.class, true).orElse(null);
                if (tile == null) return;

                MatchableStructure neededStructure = obj.findStructureForPreview(level, payload.tilePos()).orElse(null);
                if (neededStructure == null) return;

                StructurePreviewHelper.newPreview(level, payload.tilePos(), neededStructure)
                        .persistIf((lvl, playerPos) -> {
                            TileEntityTick<?> atTile = MiscUtil.getTileAt(lvl, payload.tilePos(), TileEntityTick.class, true).orElse(null);
                            if (atTile == null) return false;

                            return atTile.getRequiredObserver() != null &&
                                    atTile.getRequiredObserver().equals(obj) &&
                                    !neededStructure.matches(lvl, payload.tilePos());
                        })
                        .showBar(obj.getObserverName())
                        .createAndDisplay();
            });
        });
    }

    public record Request(BlockPos tilePos, ResourceLocation observerKey) {}
}
