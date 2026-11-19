/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResolvedDeferredHolder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ResolvedDeferredHolder<R, T extends R> implements Supplier<T> {

    private final RegistryObject<T> holder;
    private final T value;

    private ResolvedDeferredHolder(RegistryObject<T> holder, T value) {
        this.holder = holder;
        this.value = value;
    }

    public static <R, T extends R> ResolvedDeferredHolder<R, T> of(RegistryObject<T> holder, T value) {
        return new ResolvedDeferredHolder<>(holder, value);
    }

    public static <R, T extends R> ResolvedDeferredHolder<R, T> of(RegistryObject<T> holder, Supplier<T> value) {
        return of(holder, value.get());
    }

    public RegistryObject<T> getHolder() {
        return this.holder;
    }

    @Override
    public T get() {
        return this.value;
    }
}
