/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.perk.PerkLevelManager;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktSyncPerkLevels
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktSyncPerkLevels extends PlayPacketHandler.ToClient<PktSyncPerkLevels.Request> {

    static final StreamCodec<FriendlyByteBuf, PktSyncPerkLevels.Request> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            Request::maxPerkLevel,
            Request::new
    );

    public static final PktSyncPerkLevels HANDLER = new PktSyncPerkLevels();

    private PktSyncPerkLevels() {
        super(Request.class);
    }

    public static PktSyncPerkLevels.Request sync(ServerPlayer sPlayer) {
        return new Request(PerkLevelManager.getInstance().getMaxLevel(LogicalSide.SERVER, sPlayer));
    }

    @Override
    public StreamCodec<FriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, PacketContext context) {
        context.enqueueWork(() -> {
            PerkLevelManager.getInstance().initializeClientLevels(payload.maxPerkLevel());
        });
    }

    public static record Request(int maxPerkLevel) {}

}
