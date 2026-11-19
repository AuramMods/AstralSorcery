/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.tile.base.BaseTileBlock;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.tile.TileFocusRelay;
import hellfirepvp.astralsorcery.common.util.InteractUtil;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocusRelayBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocusRelayBlock extends BaseTileBlock<TileFocusRelay> {

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 2, 14);

    public FocusRelayBlock(Properties properties) {
        super(properties, TileEntitiesAS.FOCUS_RELAY);
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
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide() || !level.isEmptyBlock(pos.above())) return InteractionResult.SUCCESS;

        return MiscUtil.getTileAt(level, pos, TileFocusRelay.class, true).map(relay -> {
            ItemStack held = player.getItemInHand(hand);

            InteractUtil.tryPlaceItemIntoBlock(held, player, level, pos);
            return InteractionResult.SUCCESS;
        }).orElse(InteractionResult.PASS);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);

        MiscUtil.getTileAt(level, pos, TileFocusRelay.class, true).ifPresent(relay -> {
            if (!level.isEmptyBlock(pos.above())) {
                ItemStack in = relay.getTileData().getInventory().getStackInSlot(0);
                if (!in.isEmpty()) {
                    ItemUtil.dropItem(level, pos, in.copy());
                    relay.getTileData().getInventory().setStackInSlot(0, ItemStack.EMPTY);
                }
            }
        });
    }
}
