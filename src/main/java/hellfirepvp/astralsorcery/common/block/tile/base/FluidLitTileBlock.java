/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile.base;

import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.lighting.LightEngine;

/** Stores fluid light on the block so it is available to lighting and chunk serialization. */
public abstract class FluidLitTileBlock<T extends TileEntityTick<?>> extends BaseTickTileBlock<T> {

    public static final IntegerProperty FLUID_LIGHT = IntegerProperty.create("fluid_light", 0, LightEngine.MAX_LEVEL);

    protected FluidLitTileBlock(Properties properties, TileRegistryObject<T> type) {
        super(properties.lightLevel(state -> state.getValue(FLUID_LIGHT)), type);
        this.registerDefaultState(this.defaultBlockState().setValue(FLUID_LIGHT, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FLUID_LIGHT);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }
}
