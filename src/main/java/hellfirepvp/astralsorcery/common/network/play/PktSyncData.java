/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.data.sync.ClientSyncData;
import hellfirepvp.astralsorcery.common.data.sync.ClientSyncDiffData;
import hellfirepvp.astralsorcery.common.data.sync.SyncData;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataManager;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktSyncData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktSyncData extends PlayPacketHandler.ToClient<PktSyncData.Request> {

    static final StreamCodec<FriendlyByteBuf, PktSyncData.Request> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            Request::syncAll,
            ClientSyncData.STREAM_CODEC.apply(ByteBufCodecs.list()),
            Request::syncPayload,
            ClientSyncDiffData.STREAM_CODEC.apply(ByteBufCodecs.list()),
            Request::syncDiffPayload,
            Request::new);

    public static final PktSyncData HANDLER = new PktSyncData();

    private PktSyncData() {
        super(Request.class);
    }

    public static Request syncAll(SyncDataManager dataManager) {
        return Request.all(RegistriesAS.REGISTRY_SYNC_DATA_TYPES.get().stream()
                .map(type -> dataManager.getData(type).syncAllData())
                .collect(Collectors.toUnmodifiableList()));
    }

    public static Request syncDiff(SyncDataManager dataManager, Collection<SyncData.Type<?, ?, ?, ?>> types) {
        return Request.diff(types.stream()
                .map(type -> dataManager.getData(type).syncDiffData())
                .collect(Collectors.toUnmodifiableList()));
    }

    @Override
    public StreamCodec<FriendlyByteBuf, PktSyncData.Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, PacketContext context) {
        context.enqueueWork(() -> {
            if (payload.syncAll()) {
                payload.syncPayload().forEach(syncData -> {
                    syncData.updateClientData(MiscUtil.cast(SyncDataManager.getInstance().getClientData(syncData.type())));
                });
            } else {
                payload.syncDiffPayload().forEach(syncDiffData -> {
                    syncDiffData.updateClientData(MiscUtil.cast(SyncDataManager.getInstance().getClientData(syncDiffData.type())));
                });
            }
        });
    }

    public static record Request(boolean syncAll, List<ClientSyncData<?>> syncPayload, List<ClientSyncDiffData<?>> syncDiffPayload) {

        public static Request all(List<ClientSyncData<?>> syncPayload) {
            return new Request(true, syncPayload, List.of());
        }

        public static Request diff(List<ClientSyncDiffData<?>> syncDiffPayload) {
            return new Request(false, List.of(), syncDiffPayload);
        }

    }
}
