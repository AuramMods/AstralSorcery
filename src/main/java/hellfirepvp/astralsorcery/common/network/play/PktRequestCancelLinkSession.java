/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.linking.session.LinkSessionHelper;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktRequestCancelLinkSession
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktRequestCancelLinkSession extends PlayPacketHandler.ToServer<PktRequestCancelLinkSession.Request> {

    static final StreamCodec<FriendlyByteBuf, Request> CODEC = StreamCodec.unit(Request.INSTANCE);

    public static final PktRequestCancelLinkSession HANDLER = new PktRequestCancelLinkSession();

    private PktRequestCancelLinkSession() {
        super(Request.class);
    }

    public static Request cancelSession() {
        return Request.INSTANCE;
    }

    @Override
    public StreamCodec<FriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, PacketContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sPlayer) {
                LinkSessionHelper.stopSession(sPlayer);
            }
        });
    }

    public static class Request {

        private static final Request INSTANCE = new Request();

        private Request() {}

    }
}
