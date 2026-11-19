/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.tile.base.FluidLitTileBlock;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.util.InteractUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ChaliceBlock
 * Created by HellFirePvP
 * Date: 11.04.2026
 */
public class ChaliceBlock extends FluidLitTileBlock<TileChalice> {

    private static final VoxelShape SHAPE = Shapes.box(2D / 16D, 0D / 16D, 2D / 16D, 14D / 16D, 14D / 16D, 14D / 16D);

    public ChaliceBlock(Properties properties) {
        super(properties, TileEntitiesAS.CHALICE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        return MiscUtil.getTileAt(level, pos, TileChalice.class, true).map(chalice -> {
            ItemStack held = player.getItemInHand(hand);
            return MiscUtil.firstNonNull(
                    () -> InteractUtil.tryTransferFluidFromBlock(held, player, level, pos, newStack -> InteractUtil.handleContainerReplacement(player, newStack, hand)),
                    () -> InteractUtil.tryTransferFluidIntoBlock(held, player, level, pos, newStack -> InteractUtil.handleContainerReplacement(player, newStack, hand))
            ).orElse(InteractionResult.SUCCESS);
        }).orElse(InteractionResult.PASS);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return MiscUtil.getTileAt(level, pos, TileChalice.class, false)
                .map(chalice -> Mth.ceil(chalice.getTankFillPercentage() * 15F))
                .orElse(0);
    }

    @Override
    protected BlockEntityTicker<TileChalice> createTicker() {
        return this.ticker();
    }
}
