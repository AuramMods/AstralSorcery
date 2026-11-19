/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.config.value;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RegistryEntryListValue
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public final class RegistryEntryListValue<T> {

    private final ResourceKey<Registry<T>> registryKey;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> cfg;

    private RegistryEntryListValue(ResourceKey<Registry<T>> registryKey, ForgeConfigSpec.ConfigValue<List<? extends String>> cfg) {
        this.registryKey = registryKey;
        this.cfg = cfg;
    }

    public static <T> RegistryEntryListValue<T> defineConfig(ForgeConfigSpec.Builder builder, ResourceKey<Registry<T>> registryKey, String path, List<ResourceLocation> defaultList, ResourceLocation defaultNewElement) {
        ForgeConfigSpec.ConfigValue<List<? extends String>> cfg = builder.defineListAllowEmpty(path, defaultList.stream().map(ResourceLocation::toString).toList(), defaultNewElement::toString, obj -> {
            if (!(obj instanceof String str)) return false;
            ResourceLocation resKey = ResourceLocation.tryParse(str);
            return resKey != null;
        });
        return new RegistryEntryListValue<>(registryKey, cfg);
    }

    public List<T> getConfiguredValue(RegistryAccess registryAccess) {
        Registry<T> registry = registryAccess.registryOrThrow(this.registryKey);
        List<T> elements = new ArrayList<>();

        for (String key : this.cfg.get()) {
            ResourceLocation resKey = ResourceLocation.tryParse(key);
            if (resKey == null) continue; //Should be caught by validator beforehand
            registry.getOptional(resKey).ifPresent(elements::add);
        }

        return elements;
    }
}
