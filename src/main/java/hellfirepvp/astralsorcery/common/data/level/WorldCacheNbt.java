/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;

final class WorldCacheNbt {

    private WorldCacheNbt() {}

    static <T> void write(CompoundTag target, Codec<T> codec, T value) {
        Tag encoded = codec.encodeStart(ops(), value).getOrThrow(false, message -> {});
        target.merge((CompoundTag) encoded);
    }

    static <T> T read(CompoundTag source, Codec<T> codec) {
        return codec.parse(ops(), source).getOrThrow(false, message -> {});
    }

    private static DynamicOps<Tag> ops() {
        return RegistryOps.create(NbtOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
    }
}
