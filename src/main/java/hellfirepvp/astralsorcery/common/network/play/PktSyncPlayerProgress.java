/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.SyncPlayerProgress;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktSyncPlayerProgress
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktSyncPlayerProgress extends PlayPacketHandler.ToClient<PktSyncPlayerProgress.Request> {

    static final StreamCodec<FriendlyByteBuf, Request> CODEC = StreamCodec.composite(
            SyncPlayerProgress.SYNC_CODEC,
            Request::progress,
            Request::new
    );

    public static final PktSyncPlayerProgress HANDLER = new PktSyncPlayerProgress();

    private PktSyncPlayerProgress() {
        super(Request.class);
    }

    public static Request newRequest(PlayerProgress progress) {
        return new Request(SyncPlayerProgress.sync(progress));
    }

    @Override
    public StreamCodec<FriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, PacketContext context) {
        context.enqueueWork(() -> ResearchManager.setClientProgress(payload.progress.load()));
    }

    public static record Request(SyncPlayerProgress progress) {}
}
