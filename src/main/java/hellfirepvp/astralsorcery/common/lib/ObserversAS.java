/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.focal.observer.FocusCrystalFilamentProvider;
import hellfirepvp.astralsorcery.common.structure.*;
import hellfirepvp.astralsorcery.common.structure.observer.CompoundObserverProviderStructure;
import hellfirepvp.astralsorcery.common.util.data.ObserverRegistryObject;
import hellfirepvp.observerlib.api.ObserverProvider;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
import hellfirepvp.observerlib.api.util.StructureBlockArray;
import hellfirepvp.observerlib.common.change.ObserverProviderStructure;
import hellfirepvp.observerlib.common.registry.RegistryProviders;
import hellfirepvp.observerlib.common.registry.RegistryStructures;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ObserversAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ObserversAS {

    public static final DeferredRegister<ObserverProvider> OBSERVER_REGISTER =
            DeferredRegister.create(RegistryProviders.REGISTRY_NAME, AstralSorcery.MODID);
    public static final DeferredRegister<MatchableStructure> STRUCTURE_REGISTER =
            DeferredRegister.create(RegistryStructures.REGISTRY_NAME, AstralSorcery.MODID);

    public static final Codec<ObserverProvider> PROVIDER_CODEC = ResourceLocation.CODEC.comapFlatMap(id -> {
        ObserverProvider provider = RegistryProviders.getProvider(id);
        return provider == null
                ? DataResult.error(() -> "Unknown observer provider " + id)
                : DataResult.success(provider);
    }, ObserverProvider::getRegistryName);

    public static final ObserverRegistryObject STRUCTURE_EMPTY = registerStructure("empty", StructureBlockArray::new);
    public static final ObserverRegistryObject STRUCTURE_ALTAR_T2 =
            registerResonanceAltar();
    public static final ObserverRegistryObject STRUCTURE_ALTAR_T3 =
            registerStructure("altar_t3", PatternAltarT3::new);
    public static final ObserverRegistryObject STRUCTURE_ALTAR_T4 =
            registerStructure("altar_t4", PatternAltarT4::new);
    public static final ObserverRegistryObject STRUCTURE_INFUSER =
            registerStructure("infuser", PatternInfuser::new);
    public static final ObserverRegistryObject STRUCTURE_ATTUNEMENT_ALTAR =
            registerStructure("attunement_altar", PatternAttunementAltar::new);
    public static final ObserverRegistryObject STRUCTURE_CELESTIAL_GATEWAY =
            registerStructure("celestial_gateway", PatternCelestialGateway::new);

    public static final ObserverRegistryObject FOCUS_CRYSTAL_FILAMENTS =
            register("focus_crystal_filaments", FocusCrystalFilamentProvider::new);

    private static ObserverRegistryObject registerResonanceAltar() {
        var standard = STRUCTURE_REGISTER.register("altar_t2", () -> new PatternAltarT2(AstralSorcery.key("altar_t2")));
        var expanded = STRUCTURE_REGISTER.register("altar_t2_expanded",
                () -> new PatternAltarT2Expanded(AstralSorcery.key("altar_t2_expanded")));
        return register("altar_t2", id -> new CompoundObserverProviderStructure(id, () -> List.of(standard.get(), expanded.get())));
    }

    private static ObserverRegistryObject registerStructure(String name, Function<ResourceLocation, StructureBlockArray> factory) {
        ResourceLocation id = AstralSorcery.key(name);
        STRUCTURE_REGISTER.register(name, () -> factory.apply(id));
        return register(name, ObserverProviderStructure::new);
    }

    private static ObserverRegistryObject register(String name, Function<ResourceLocation, ObserverProvider> factory) {
        ResourceLocation id = AstralSorcery.key(name);
        return new ObserverRegistryObject(OBSERVER_REGISTER.register(name, () -> factory.apply(id)));
    }

    public static Optional<ObserverRegistryObject> getByName(ResourceLocation id) {
        return OBSERVER_REGISTER.getEntries().stream()
                .filter(entry -> entry.getId().equals(id))
                .findFirst()
                .map(ObserverRegistryObject::new);
    }
}
