/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.worldgen.structure;

import hellfirepvp.astralsorcery.common.lib.WorldGenAS;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public final class IgnoreWaterloggingPoolPiece extends PoolElementStructurePiece {

    private static final ThreadLocal<Boolean> IGNORE_WATERLOGGING = new ThreadLocal<>();

    public IgnoreWaterloggingPoolPiece(StructureTemplateManager templates, StructurePoolElement element, BlockPos pos,
                                       int groundLevelDelta, Rotation rotation, BoundingBox bounds) {
        super(templates, element, pos, groundLevelDelta, rotation, bounds);
    }

    public IgnoreWaterloggingPoolPiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(context, tag);
    }

    @Override
    public StructurePieceType getType() {
        return WorldGenAS.IGNORE_WATERLOGGING_POOL_PIECE.get();
    }

    public static boolean ignoresWaterlogging() {
        return Boolean.TRUE.equals(IGNORE_WATERLOGGING.get());
    }

    @Override
    public void place(WorldGenLevel level, StructureManager structures, ChunkGenerator generator, RandomSource random,
                      BoundingBox bounds, BlockPos pivot, boolean keepJigsaws) {
        // Forge 1.20.1 does not pass a liquid setting through the pool element placement API.
        if (ignoresWaterlogging()) {
            super.place(level, structures, generator, random, bounds, pivot, keepJigsaws);
            return;
        }

        IGNORE_WATERLOGGING.set(true);
        try {
            super.place(level, structures, generator, random, bounds, pivot, keepJigsaws);
        } finally {
            IGNORE_WATERLOGGING.remove();
        }
    }
}
