/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.data.ResearchNodeLoader;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktSyncResearchNodes
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktSyncResearchNodes extends PlayPacketHandler.ToClient<PktSyncResearchNodes.Request> {

    static final StreamCodec<FriendlyByteBuf, PktSyncResearchNodes.Request> CODEC =
            ResearchNode.SYNC_CODEC.apply(ByteBufCodecs.list()).map(Request::new, Request::nodes);

    public static final PktSyncResearchNodes HANDLER = new PktSyncResearchNodes();

    private PktSyncResearchNodes() {
        super(Request.class);
    }

    public static Request newRequest() {
        return new Request(ResearchNodeLoader.getInstance().getNodes());
    }

    @Override
    public StreamCodec<FriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, PacketContext context) {
        context.enqueueWork(() -> ResearchNodeLoader.getInstance().updateServerNodes(payload.nodes()));
    }

    public static record Request(List<ResearchNode> nodes) {}
}
