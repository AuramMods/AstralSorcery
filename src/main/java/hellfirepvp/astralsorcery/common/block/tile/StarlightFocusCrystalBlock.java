/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.tile.base.BaseTickTileBlock;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.tile.TileStarlightFocusCrystal;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StarlightFocusCrystalBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StarlightFocusCrystalBlock extends BaseTickTileBlock<TileStarlightFocusCrystal> {

    private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 16, 12);

    private final Type type;

    public StarlightFocusCrystalBlock(Properties properties, Type type) {
        this(properties, type, TileEntitiesAS.STARLIGHT_FOCUS_CRYSTAL);
    }

    protected StarlightFocusCrystalBlock(Properties properties, Type type, TileRegistryObject<TileStarlightFocusCrystal> tileType) {
        super(properties, tileType);
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected BlockEntityTicker<TileStarlightFocusCrystal> createTicker() {
        return this.ticker();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public enum Type {

        ROCK_CRYSTAL,
        CELESTIAL_CRYSTAL

    }
}
