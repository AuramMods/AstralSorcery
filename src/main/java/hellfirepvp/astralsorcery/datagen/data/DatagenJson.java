/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.RegistryOps;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public final class DatagenJson {

    private DatagenJson() {}

    public static <T> CompletableFuture<?> saveStable(CachedOutput output, HolderLookup.Provider registries,
                                                      Codec<T> codec, T value, Path file) {
        var json = codec.encodeStart(RegistryOps.create(JsonOps.INSTANCE, registries), value)
                .getOrThrow(false, error -> {
                    throw new IllegalArgumentException("Cannot generate " + file + ": " + error);
                });
        return DataProvider.saveStable(output, json, file);
    }
}
