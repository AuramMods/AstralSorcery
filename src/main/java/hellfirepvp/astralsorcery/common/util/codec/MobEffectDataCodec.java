/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Optional;

/** Reads the effect fields used by Astral's datapacks into native Forge effect instances. */
public final class MobEffectDataCodec {

    private static final MapCodec<Details> DETAILS = createDetailsCodec(
            ExtraCodecs.lazyInitializedCodec(() -> MobEffectDataCodec.DETAILS.codec()));

    public static final Codec<MobEffectInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.MOB_EFFECT.byNameCodec().fieldOf("id").forGetter(MobEffectInstance::getEffect),
            DETAILS.forGetter(Details::of)
    ).apply(instance, (effect, details) -> details.create(effect)));

    private MobEffectDataCodec() {}

    private static MapCodec<Details> createDetailsCodec(Codec<Details> hiddenCodec) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.intRange(0, 255).optionalFieldOf("amplifier", 0).forGetter(Details::amplifier),
                Codec.INT.optionalFieldOf("duration", 0).forGetter(Details::duration),
                Codec.BOOL.optionalFieldOf("ambient", false).forGetter(Details::ambient),
                Codec.BOOL.optionalFieldOf("show_particles", true).forGetter(Details::showParticles),
                Codec.BOOL.optionalFieldOf("show_icon").forGetter(details -> Optional.of(details.showIcon())),
                hiddenCodec.optionalFieldOf("hidden_effect").forGetter(Details::hidden)
        ).apply(instance, (amplifier, duration, ambient, particles, icon, hidden) ->
                new Details(amplifier, duration, ambient, particles, icon.orElse(particles), hidden)));
    }

    private record Details(int amplifier, int duration, boolean ambient, boolean showParticles, boolean showIcon,
                           Optional<Details> hidden) {
        private static Details of(MobEffectInstance effect) {
            return new Details(effect.getAmplifier(), effect.getDuration(), effect.isAmbient(), effect.isVisible(),
                    effect.showIcon(), Optional.ofNullable(effect.hiddenEffect).map(Details::of));
        }

        private MobEffectInstance create(MobEffect effect) {
            return new MobEffectInstance(effect, this.duration, this.amplifier, this.ambient, this.showParticles,
                    this.showIcon, this.hidden.map(details -> details.create(effect)).orElse(null), effect.createFactorData());
        }
    }
}
