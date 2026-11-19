/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.component.AstrolabeAngleComponent;
import hellfirepvp.astralsorcery.common.item.AstrolabeItem;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktAdjustAstrolabeAngle
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktAdjustAstrolabeAngle extends PlayPacketHandler.ToServer<PktAdjustAstrolabeAngle.Request> {

    static final StreamCodec<FriendlyByteBuf, PktAdjustAstrolabeAngle.Request> CODEC =
            StreamCodec.composite(ByteBufCodecs.FLOAT, Request::newAngle, Request::new);

    public static final PktAdjustAstrolabeAngle HANDLER = new PktAdjustAstrolabeAngle();

    private PktAdjustAstrolabeAngle() {
        super(Request.class);
    }

    public static Request adjustAngle(float newAngle) {
        return new Request(newAngle);
    }

    @Override
    public StreamCodec<FriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, PacketContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sPlayer && AstrolabeItem.isUsingAstrolabe(sPlayer)) {
                ItemStack useStack = sPlayer.getItemInHand(sPlayer.getUsedItemHand());
                if (useStack.is(ItemsAS.ASTROLABE.get())) {
                    AstrolabeAngleComponent existing = DataComponentsAS.ASTROLABE_ANGLE.getOrDefault(useStack, AstrolabeAngleComponent.DEFAULT);
                    DataComponentsAS.ASTROLABE_ANGLE.set(useStack, new AstrolabeAngleComponent(payload.newAngle(), existing.matchesAll()));
                }
            }
        });
    }

    public static record Request(float newAngle) {}

}
