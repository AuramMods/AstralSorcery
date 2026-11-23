/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.config.server;

import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.toml.TomlParser;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.config.BaseConfiguration;
import hellfirepvp.astralsorcery.common.config.ConfigEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GeneralConfig
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class GeneralConfig extends ConfigEntry {

    public static final GeneralConfig CONFIG = new GeneralConfig();
    private static final String GIVE_TOME_ON_JOIN = "giveTomeOnJoin";

    public ForgeConfigSpec.IntValue dayLength;

    private GeneralConfig() {
        super("general");
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        dayLength = cfgBuilder
                .comment("Defines the length of a day (both daytime & nighttime obviously) for the mod's internal logic. NOTE: This does NOT CHANGE HOW LONG A DAY IN MC IS! It is only to provide potential compatibility for mods that do provide such functionality.")
                .translation(translationKey("dayLength"))
                .defineInRange("dayLength", 24000, 1000, 400_000);

        cfgBuilder
                .comment("If set to 'true', the player will receive an AstralSorcery Tome when they join the server for the first time.")
                .translation(translationKey(GIVE_TOME_ON_JOIN))
                .define(GIVE_TOME_ON_JOIN, false);
    }

    public boolean givesTomeOnJoin(MinecraftServer server) {
        return readTomeOnJoin(server.getWorldPath(LevelResource.ROOT)
                .resolve("serverconfig").resolve(BaseConfiguration.SERVER_FILE_NAME));
    }

    boolean readTomeOnJoin(Path file) {
        // Require the active world's explicit opt-in before the one-time inventory grant.
        try (var reader = Files.newBufferedReader(file)) {
            return Boolean.TRUE.equals(new TomlParser().parse(reader).get(List.of(this.getPath(), GIVE_TOME_ON_JOIN)));
        } catch (NoSuchFileException ignored) {
            return false;
        } catch (IOException | ParsingException ex) {
            AstralSorcery.LOG.warn("Skipping the login Tome grant because {} could not be read", file, ex);
            return false;
        }
    }
}
