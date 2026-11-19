/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.research.ResearchMessageHelper;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktDiscoverConstellation
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktDiscoverConstellation extends PlayPacketHandler.ToServer<PktDiscoverConstellation.Request> {

    static final StreamCodec<FriendlyByteBuf, PktDiscoverConstellation.Request> CODEC =
            StreamCodec.composite(ByteBufCodecs.registry(RegistriesAS.KEY_CONSTELLATIONS), Request::constellation, Request::new);

    public static final PktDiscoverConstellation HANDLER = new PktDiscoverConstellation();

    private PktDiscoverConstellation() {
        super(Request.class);
    }

    public static Request discover(BaseConstellation constellation) {
        return new Request(constellation);
    }

    @Override
    public StreamCodec<FriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, PacketContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sPlayer) {
                if (ResearchHelper.discoverConstellation(sPlayer, payload.constellation())) {
                    ResearchMessageHelper.sendConstellationDiscovery(sPlayer, payload.constellation());
                }
            }
        });
    }

    public record Request(BaseConstellation constellation) {}

}
