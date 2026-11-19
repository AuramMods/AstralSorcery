/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure.observer;

import hellfirepvp.observerlib.api.ObserverProvider;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CompoundObserverProviderStructure
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CompoundObserverProviderStructure extends ObserverProvider {

    private final Supplier<List<MatchableStructure>> structures;

    public CompoundObserverProviderStructure(ResourceLocation id, Supplier<List<MatchableStructure>> structures) {
        super(id);
        this.structures = structures;
    }

    public List<MatchableStructure> getStructures() {
        return List.copyOf(this.structures.get());
    }

    @Nonnull
    @Override
    public CompoundChangeObserverStructure provideObserver() {
        return new CompoundChangeObserverStructure(this.getRegistryName(), this.getStructures());
    }
}
