/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.component;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record LumenComponent(ResourceKey<Lumen> key) {

    public static final LumenComponent EMPTY = new LumenComponent(LumenAS.NONE.getKey());

    public static final Codec<LumenComponent> CODEC = ResourceLocation.CODEC.xmap(
            id -> new LumenComponent(ResourceKey.create(RegistriesAS.KEY_LUMEN, id)), component -> component.key().location());
    public static final StreamCodec<ByteBuf, LumenComponent> STREAM_CODEC =
            ByteBufCodecs.resourceKey(RegistriesAS.KEY_LUMEN).map(LumenComponent::new, LumenComponent::key);

    public LumenComponent(LumenAS.DeferredLumen<?> lumen) {
        this(lumen.getKey());
    }

    public LumenComponent(Holder<Lumen> lumen) {
        this(lumen.unwrapKey().orElseGet(() -> RegistriesAS.REGISTRY_LUMEN.get().getResourceKey(lumen.value()).orElseThrow()));
    }

    public static Optional<LumenComponent> of(Lumen lumen) {
        return RegistriesAS.REGISTRY_LUMEN.get().getResourceKey(lumen).map(LumenComponent::new);
    }

    public Holder<Lumen> lumen() {
        return RegistriesAS.REGISTRY_LUMEN.get().getHolderOrThrow(this.key);
    }
}
