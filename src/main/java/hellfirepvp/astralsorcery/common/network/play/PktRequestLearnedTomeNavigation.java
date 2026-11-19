/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.research.ResearchHelper;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktRequestLearnedTomeNavigation
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktRequestLearnedTomeNavigation extends PlayPacketHandler.ToServer<PktRequestLearnedTomeNavigation.Request> {

    static final Request INSTANCE = new Request();
    static final StreamCodec<FriendlyByteBuf, PktRequestLearnedTomeNavigation.Request> CODEC = StreamCodec.unit(INSTANCE);

    public static final PktRequestLearnedTomeNavigation HANDLER = new PktRequestLearnedTomeNavigation();

    private PktRequestLearnedTomeNavigation() {
        super(Request.class);
    }

    public static Request request() {
        return INSTANCE;
    }

    @Override
    public StreamCodec<FriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, PacketContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sPlayer) {
                ResearchHelper.setLearnedToNavigateTome(sPlayer);
            }
        });
    }

    public static record Request() {}
}
