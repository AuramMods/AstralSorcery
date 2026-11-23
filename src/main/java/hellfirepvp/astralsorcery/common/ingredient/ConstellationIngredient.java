package hellfirepvp.astralsorcery.common.ingredient;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;

import java.util.Comparator;
import java.util.List;

public record ConstellationIngredient(Either<BaseConstellation, TagKey<BaseConstellation>> selector) {

    public static final Codec<ConstellationIngredient> CODEC = Codec.either(
            RegistriesAS.codec(RegistriesAS.KEY_CONSTELLATIONS), TagKey.hashedCodec(RegistriesAS.KEY_CONSTELLATIONS))
            .xmap(ConstellationIngredient::new, ConstellationIngredient::selector);
    public static final StreamCodec<FriendlyByteBuf, ConstellationIngredient> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public static ConstellationIngredient of(BaseConstellation constellation) {
        return new ConstellationIngredient(Either.left(constellation));
    }

    public static ConstellationIngredient of(TagKey<BaseConstellation> tag) {
        return new ConstellationIngredient(Either.right(tag));
    }

    public boolean matches(BaseConstellation constellation) {
        return constellation != null && this.selector.map(constellation::equals, tag -> {
            var registry = RegistriesAS.REGISTRY_CONSTELLATIONS.get();
            return registry.getResourceKey(constellation).flatMap(registry::getHolder).filter(holder -> holder.is(tag)).isPresent();
        });
    }

    public List<BaseConstellation> values() {
        return this.selector.map(List::of, tag -> {
            var registry = RegistriesAS.REGISTRY_CONSTELLATIONS.get();
            return registry.getTag(tag).stream().flatMap(holders -> holders.stream()).map(Holder::value)
                    .sorted(Comparator.comparing(registry::getKey)).toList();
        });
    }

    public Component displayName() {
        return this.selector.map(BaseConstellation::getColoredName, tag -> Component.literal("#" + tag.location()));
    }
}
