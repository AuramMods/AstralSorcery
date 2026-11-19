/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import hellfirepvp.astralsorcery.common.structure.observer.CompoundObserverProviderStructure;
import hellfirepvp.observerlib.api.ChangeSubscriber;
import hellfirepvp.observerlib.api.ObserverHelper;
import hellfirepvp.observerlib.api.ObserverProvider;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
import hellfirepvp.observerlib.api.util.StructureBlockArray;
import hellfirepvp.observerlib.common.change.ObserverProviderStructure;
import hellfirepvp.observerlib.common.registry.RegistryStructures;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ObserverRegistryObject
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record ObserverRegistryObject(RegistryObject<ObserverProvider> observer) {

    public boolean isProviderFor(ChangeSubscriber<?> subscriber) {
        return this.observer().getId().equals(subscriber.getObserver().getProviderRegistryName());
    }

    public Component getObserverName() {
        return Component.translatable(Util.makeDescriptionId("observer_provider", this.observer().getId()));
    }

    public ChangeSubscriber<?> createSubscriber(Level level, BlockPos pos) {
        return ObserverHelper.getHelper().observeArea(level, pos, this.observer().get());
    }

    public static Optional<StructureBlockArray> extractRequiredStructure(ObserverProvider observerProvider) {
        return extractRequiredStructures(observerProvider).stream().findFirst();
    }

    public static List<StructureBlockArray> extractRequiredStructures(ObserverProvider observerProvider) {
        if (observerProvider instanceof CompoundObserverProviderStructure compoundProvider) {
            return compoundProvider.getStructures().stream()
                    .filter(StructureBlockArray.class::isInstance)
                    .map(StructureBlockArray.class::cast)
                    .toList();
        }
        if (observerProvider instanceof ObserverProviderStructure) {
            MatchableStructure structure = RegistryStructures.getStructure(observerProvider.getRegistryName());
            if (structure instanceof StructureBlockArray blocks) {
                return List.of(blocks);
            }
        }
        return List.of();
    }

    public Optional<StructureBlockArray> findStructureForPreview(Level level, BlockPos center) {
        ObserverProvider provider = this.observer().get();
        List<StructureBlockArray> structures = extractRequiredStructures(provider);
        if (provider instanceof CompoundObserverProviderStructure) {
            return structures.stream().filter(structure -> !structure.matches(level, center)).findFirst();
        }
        return structures.stream().findFirst();
    }
}
