/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.block.tile.base.FluidLitTileBlock;
import hellfirepvp.astralsorcery.common.network.NetworkRegistry;
import hellfirepvp.astralsorcery.common.network.play.PktSyncAuxiliaryLightManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;

/**
 * Synchronizes a machine's fluid emission with clients tracking its chunk.
 *
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SyncedAuxiliaryLightManager
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public final class SyncedAuxiliaryLightManager {

    private SyncedAuxiliaryLightManager() {}

    public static void setLightAt(ServerLevel level, BlockPos pos, int value) {
        int light = Mth.clamp(value, 0, LightEngine.MAX_LEVEL);
        if (updateLight(level, pos, light)) {
            NetworkRegistry.sendToPlayersTrackingChunk(level, new ChunkPos(pos),
                    PktSyncAuxiliaryLightManager.updateLight(pos, light));
        }
    }

    public static void applyClientLight(Level level, BlockPos pos, int light) {
        if (level.isClientSide() && level.hasChunkAt(pos)) {
            updateLight(level, pos, Mth.clamp(light, 0, LightEngine.MAX_LEVEL));
        }
    }

    private static boolean updateLight(Level level, BlockPos pos, int light) {
        BlockState state = level.getBlockState(pos);
        var lightProperty = FluidLitTileBlock.FLUID_LIGHT;
        if (!state.hasProperty(lightProperty) || state.getValue(lightProperty) == light) {
            return false;
        }

        return level.setBlock(pos, state.setValue(lightProperty, light), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
    }
}
