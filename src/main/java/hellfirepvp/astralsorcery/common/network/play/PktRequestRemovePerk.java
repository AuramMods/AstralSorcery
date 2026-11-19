/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.client.screen.tome.TomePerkTreeScreen;
import hellfirepvp.astralsorcery.common.item.PerkNullifierItem;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.perk.data.PerkTree;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.perk.PerkAllocation;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktRequestRemovePerk
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktRequestRemovePerk extends PlayPacketHandler.BiDirectional<PktRequestRemovePerk.Request> {

    static final StreamCodec<FriendlyByteBuf, PktRequestRemovePerk.Request> CODEC = StreamCodec.composite(
            ByteBufCodecs.RESOURCE_LOCATION,
            PktRequestRemovePerk.Request::perkKey,
            PktRequestRemovePerk.Request::new
    );

    public static final PktRequestRemovePerk HANDLER = new PktRequestRemovePerk();

    private PktRequestRemovePerk() {
        super(Request.class);
    }

    public static PktRequestRemovePerk.Request remove(ResourceLocation perkKey) {
        return new PktRequestRemovePerk.Request(perkKey);
    }

    @Override
    public StreamCodec<FriendlyByteBuf, PktRequestRemovePerk.Request> codec() {
        return CODEC;
    }

    @Override
    public void handleClient(Request payload, PacketContext context) {
        context.enqueueWork(() -> {
            PerkTree.getInstance().getPerk(LogicalSide.CLIENT, payload.perkKey()).ifPresent(perk -> {
                if (Minecraft.getInstance().screen instanceof TomePerkTreeScreen perkTreeScreen) {

                }
            });
        });
    }

    @Override
    public void handleServer(Request payload, PacketContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sPlayer) {
                PerkTree.getInstance().getPerk(LogicalSide.SERVER, payload.perkKey()).ifPresent(perk -> {
                    PlayerProgress progress = ResearchManager.getProgress(sPlayer, LogicalSide.SERVER);
                    if (perk.mayRemovePerk(progress, sPlayer)) {
                        if (PerkNullifierItem.consumePerkNullifier(sPlayer, false) &&
                                ResearchHelper.removePerk(sPlayer, perk, PerkAllocation.unlock())) {
                            context.reply(remove(payload.perkKey()));
                        }
                    }
                });
            }
        });
    }

    public static record Request(ResourceLocation perkKey) {}
}
