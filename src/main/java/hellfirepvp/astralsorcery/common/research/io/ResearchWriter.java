/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.io;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import net.minecraft.ReportedException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchWriter
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ResearchWriter {

    @Nonnull
    public static PlayerProgress loadFromFile(UUID playerUUID) {
        File playerFile = ResearchFile.getPlayerFile(playerUUID);
        try {
            PlayerProgress progress = readFile(playerFile);
            if (progress != null) {
                return progress;
            }
        } catch (IOException | ReportedException exception) {
            AstralSorcery.LOG.error("Failed loading player progress for {}.", playerUUID, exception);
        }

        File backupFile = ResearchFile.getPlayerBackupFile(playerUUID);
        try {
            PlayerProgress progress = readFile(backupFile);
            if (progress != null) {
                try {
                    Files.copy(backupFile.toPath(), playerFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException exception) {
                    AstralSorcery.LOG.error("Failed restoring player progress for {}.", playerUUID, exception);
                }
                return progress;
            }
        } catch (IOException | ReportedException exception) {
            AstralSorcery.LOG.error("Failed loading backup player progress for {}.", playerUUID, exception);
        }
        return PlayerProgress.blankProgress();
    }

    @Nullable
    private static PlayerProgress readFile(File file) throws IOException {
        CompoundTag tag = NbtIo.read(file);
        if (tag == null) {
            return null;
        }
        var decoded = PlayerProgress.SAVE_CODEC.parse(NbtOps.INSTANCE, tag);
        return decoded.result().orElseThrow(() -> new IOException(decoded.error()
                .map(error -> error.message()).orElse("Invalid player progress")));
    }

    public static void saveToFile(UUID playerUUID, PlayerProgress progress) {
        var encoded = PlayerProgress.SAVE_CODEC.encodeStart(NbtOps.INSTANCE, progress);
        if (!(encoded.result().orElse(null) instanceof CompoundTag tag)) {
            AstralSorcery.LOG.error("Failed encoding player progress for {}: {}", playerUUID,
                    encoded.error().map(error -> error.message()).orElse("Expected a compound tag"));
            return;
        }

        var playerFile = ResearchFile.getPlayerFile(playerUUID).toPath();
        var backupFile = ResearchFile.getPlayerBackupFile(playerUUID).toPath();
        try {
            var temporary = Files.createTempFile(playerFile.getParent(), playerFile.getFileName().toString(), ".tmp");
            try {
                NbtIo.write(tag, temporary.toFile());
                try {
                    Files.copy(playerFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
                } catch (NoSuchFileException ignored) {
                    // A new player has no previous save to back up.
                } catch (IOException exception) {
                    AstralSorcery.LOG.error("Failed backing up player progress for {}.", playerUUID, exception);
                }

                try {
                    Files.move(temporary, playerFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                } catch (AtomicMoveNotSupportedException ignored) {
                    Files.move(temporary, playerFile, StandardCopyOption.REPLACE_EXISTING);
                }
            } finally {
                Files.deleteIfExists(temporary);
            }
        } catch (IOException exception) {
            AstralSorcery.LOG.error("Failed saving player progress for {}.", playerUUID, exception);
        }
    }

    public static void wipeFiles(UUID playerUuid) {
        ResearchFile.getPlayerFile(playerUuid).delete();
        ResearchFile.getPlayerBackupFile(playerUuid).delete();
        ResearchIOThread.cancelSave(playerUuid);
    }
}
