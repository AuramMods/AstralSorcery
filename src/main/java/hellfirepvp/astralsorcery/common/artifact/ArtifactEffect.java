/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.entity.item.ItemEntityArtifact;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ArtifactEffect {

    public static final Codec<ArtifactEffect> CODEC = RegistriesAS.codec(RegistriesAS.KEY_ARTIFACT_EFFECT_TYPES)
            .dispatch(ArtifactEffect::unwrapType, type -> type.codec().codec());
    public static final StreamCodec<FriendlyByteBuf, ArtifactEffect> STREAM_CODEC = ByteBufCodecs.registry(RegistriesAS.KEY_ARTIFACT_EFFECT_TYPES)
            .dispatch(ArtifactEffect::unwrapType, ArtifactEffect.Type::streamCodec);

    private final ResourceLocation id;

    protected ArtifactEffect(ResourceLocation id) {
        this.id = id;
    }

    public final ResourceLocation getId() {
        return this.id;
    }

    public abstract DeferredType<?> getType();

    public Type<?> unwrapType() {
        return this.getType().holder().get();
    }

    public abstract void applyEffect(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifact);

    public record Type<T extends ArtifactEffect>(MapCodec<T> codec, StreamCodec<FriendlyByteBuf, T> streamCodec) {}

    public record DeferredType<T extends ArtifactEffect>(RegistryObject<ArtifactEffect.Type<T>> holder) {}

    @FunctionalInterface
    public interface Provider extends Function<ResourceLocation, ArtifactEffect> {}
}
