/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.item.TomeItem;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;

public class PktOpenClientScreen extends PlayPacketHandler.ToClient<PktOpenClientScreen.Request> {

    static final StreamCodec<FriendlyByteBuf, Request> CODEC =
            StreamCodec.composite(CodecUtil.enumStreamCodec(ScreenType.class), Request::screenType, Request::new);

    public static final PktOpenClientScreen HANDLER = new PktOpenClientScreen();

    private PktOpenClientScreen() {
        super(Request.class);
    }

    public static Request openScreen(ScreenType screenType) {
        return new Request(screenType);
    }

    @Override
    public StreamCodec<FriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, PacketContext context) {
        context.enqueueWork(() -> {
            switch (payload.screenType()) {
                case TOME -> TomeItem.openTomeScreen();
            }
        });
    }

    public record Request(ScreenType screenType) {}

    public enum ScreenType {
        TOME
    }
}
