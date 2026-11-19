/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.reload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;

import java.util.Map;

public abstract class RegistryJsonDataLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String directory;

    protected RegistryJsonDataLoader(String directory) {
        this.directory = directory;
    }

    public final void registerReloadListener(AddReloadListenerEvent event) {
        RegistryAccess registries = event.getRegistryAccess();
        event.addListener(new SimpleJsonResourceReloadListener(GSON, this.directory) {
            @Override
            protected void apply(Map<ResourceLocation, JsonElement> data, ResourceManager resources, ProfilerFiller profiler) {
                RegistryJsonDataLoader.this.apply(data, registries);
            }
        });
    }

    protected abstract void apply(Map<ResourceLocation, JsonElement> data, RegistryAccess registries);
}
