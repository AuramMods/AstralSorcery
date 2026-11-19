/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.visual.VisualEffectTypes;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.util.RandomSource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktPlayVisualEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktPlayVisualEffect extends PlayPacketHandler.ToClient<PktPlayVisualEffect.Request> {

    static final StreamCodec<FriendlyByteBuf, PktPlayVisualEffect.Request> CODEC =
            StreamCodec.composite(VisualEffectTypes.STREAM_CODEC, Request::effect, Request::new);

    public static final PktPlayVisualEffect HANDLER = new PktPlayVisualEffect();

    private PktPlayVisualEffect() {
        super(Request.class);
    }

    public static Request playEffect(VisualEffectTypes.Effect effect) {
        return new Request(effect);
    }

    @Override
    public StreamCodec<FriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, PacketContext context) {
        context.enqueueWork(() -> {
            payload.effect().playEffect(RandomSource.create());
        });
    }

    public static record Request(VisualEffectTypes.Effect effect) {}

}
