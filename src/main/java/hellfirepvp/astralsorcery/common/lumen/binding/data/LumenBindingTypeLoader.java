/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.binding.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.reload.RegistryJsonDataLoader;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.binding.LumenBindingType;
import hellfirepvp.astralsorcery.common.util.data.MapStream;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingTypeLoader
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingTypeLoader extends RegistryJsonDataLoader {

    private static final LumenBindingTypeLoader INSTANCE = new LumenBindingTypeLoader();

    private final Map<ResourceLocation, LumenBindingType> bindings = new HashMap<>();
    private final Map<ResourceKey<Lumen>, ResourceLocation> lumenApplicationMapping = new HashMap<>();

    private final Map<ResourceLocation, LumenBindingType> clientBindings = new HashMap<>();
    private final Map<ResourceKey<Lumen>, ResourceLocation> clientLumenApplicationMapping = new HashMap<>();

    private LumenBindingTypeLoader() {
        super("lumen_binding");
    }

    public static LumenBindingTypeLoader getInstance() {
        return INSTANCE;
    }

    public Optional<ResourceLocation> getLumenBindingType(LogicalSide side, ResourceLocation lumenKey) {
        return this.getLumenBindingType(side, ResourceKey.create(RegistriesAS.KEY_LUMEN, lumenKey));
    }

    public Optional<ResourceLocation> getLumenBindingType(LogicalSide side, ResourceKey<Lumen> lumenKey) {
        return Optional.ofNullable(this.getLumenApplicationMapping(side).get(lumenKey));
    }

    public Optional<LumenBindingType> getBindingType(LogicalSide side, ResourceLocation bindingKey) {
        return Optional.ofNullable(this.getBindings(side).get(bindingKey));
    }

    public Map<ResourceLocation, LumenBindingType> getBindings(LogicalSide side) {
        return switch (side) {
            case CLIENT -> Map.copyOf(this.clientBindings);
            case SERVER -> Map.copyOf(this.bindings);
        };
    }

    public Map<ResourceKey<Lumen>, ResourceLocation> getLumenApplicationMapping(LogicalSide side) {
        return switch (side) {
            case CLIENT -> Map.copyOf(this.clientLumenApplicationMapping);
            case SERVER -> Map.copyOf(this.lumenApplicationMapping);
        };
    }

    public void updateClientBindings(Map<ResourceLocation, LumenBindingType> bindings, Map<ResourceKey<Lumen>, ResourceLocation> lumenApplicationMapping) {
        this.clientBindings.clear();
        this.clientBindings.putAll(bindings);
        this.clientLumenApplicationMapping.clear();
        this.clientLumenApplicationMapping.putAll(lumenApplicationMapping);
    }

    public void clearClientBindings() {
        this.clientBindings.clear();
        this.clientLumenApplicationMapping.clear();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> dataMap, RegistryAccess registries) {
        Map<ResourceLocation, JsonObject> loadableFiles = MapStream.of(dataMap)
                .filterValue(JsonElement::isJsonObject)
                .mapValue(JsonElement::getAsJsonObject)
                .toMap();
        Map<ResourceLocation, JsonObject> mappings = MapStream.of(loadableFiles)
                .filterKey(key -> key.getPath().startsWith("_"))
                .toMap();
        Map<ResourceLocation, JsonObject> bindingTypes = MapStream.of(loadableFiles)
                .filterKey(key -> !key.getPath().startsWith("_"))
                .toMap();

        AstralSorcery.LOG.info("Loading {} lumen binding types and {} mapping files.", bindingTypes.size(), mappings.size());

        this.bindings.clear();
        this.lumenApplicationMapping.clear();
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registries);
        mappings.forEach((key, obj) -> {
            var result = LumenBindingTypeMapping.CODEC.parse(ops, obj);
            result.error().ifPresent(error -> AstralSorcery.LOG.warn("Failed to load lumen binding type mapping {}: {}", key, error.message()));
            result.result().ifPresent(mapping -> this.lumenApplicationMapping.putAll(mapping.getMapping()));
        });

        bindingTypes.forEach((key, obj) -> {
            var result = LumenBindingType.CODEC.parse(ops, obj);
            result.error().ifPresent(error -> AstralSorcery.LOG.warn("Failed to load lumen binding type {}: {}", key, error.message()));
            result.result().ifPresent(bindingType -> this.bindings.put(key, bindingType));
        });
    }
}
