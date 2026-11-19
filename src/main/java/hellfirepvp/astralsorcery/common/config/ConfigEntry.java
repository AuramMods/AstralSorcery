/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.config;

import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ConfigEntry
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ConfigEntry implements Consumer<ForgeConfigSpec.Builder> {

    private final Set<ConfigEntry> subSections = new HashSet<>();
    private final String path;

    private String subPath = "";
    private ModConfig.Type configType;

    public ConfigEntry(String section) {
        this.path = section.toLowerCase(Locale.ROOT);
    }

    public ConfigEntry newSubSection(ConfigEntry entry) {
        entry.subPath = this.getPath();
        entry.setConfigType(this.configType);
        this.subSections.add(entry);
        return entry;
    }

    final void setConfigType(ModConfig.Type type) {
        this.configType = type;
        this.subSections.forEach(section -> section.setConfigType(type));
    }

    public final void up(Consumer<ConfigEntry> fn) {
        fn.accept(this);
        this.subSections.forEach(entry -> entry.up(fn));
    }

    @Override
    public final void accept(ForgeConfigSpec.Builder builder) {
        this.createEntries(builder);

        for (ConfigEntry section : subSections) {
            List<String> splitPath = BaseConfiguration.DOT_SPLITTER.splitToList(section.getPath());
            builder.push(splitPath);
            section.accept(builder);
            builder.pop(splitPath.size());
        }
    }

    protected String translationKey(String key) {
        return String.format("config.%s.%s.%s", this.configType.extension(), this.getFullPath(), key.toLowerCase(Locale.ROOT));
    }

    public abstract void createEntries(ForgeConfigSpec.Builder cfgBuilder);

    public void onReload() {}

    public String getPath() {
        return this.path;
    }

    public String getFullPath() {
        return this.subPath.isEmpty() ? getPath() : String.format("%s.%s", this.subPath, getPath());
    }
}
