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

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RegistryEntryValue
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public final class RegistryEntryValue<T> {

    private final ResourceKey<Registry<T>> registryKey;
    private final ForgeConfigSpec.ConfigValue<String> cfg;

    private RegistryEntryValue(ResourceKey<Registry<T>> registryKey, ForgeConfigSpec.ConfigValue<String> cfg) {
        this.registryKey = registryKey;
        this.cfg = cfg;
    }

    public static <T> RegistryEntryValue<T> defineConfig(ForgeConfigSpec.Builder builder, ResourceKey<Registry<T>> registryKey, String path, ResourceLocation defaultKey) {
        ForgeConfigSpec.ConfigValue<String> cfg = builder.define(path, defaultKey.toString(), obj -> {
            if (!(obj instanceof String str)) return false;
            ResourceLocation resKey = ResourceLocation.tryParse(str);
            return resKey != null;
        });
        return new RegistryEntryValue<>(registryKey, cfg);
    }

    public Optional<T> getConfiguredValue(RegistryAccess registryAccess) {
        String key = this.cfg.get();
        ResourceLocation resKey = ResourceLocation.tryParse(key);
        if (resKey == null) return Optional.empty(); //Should be caught by validator beforehand
        Registry<T> registry = registryAccess.registryOrThrow(this.registryKey);
        return registry.getOptional(resKey);
    }
}
