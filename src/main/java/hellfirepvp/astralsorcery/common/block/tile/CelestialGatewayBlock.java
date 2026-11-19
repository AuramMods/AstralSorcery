/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.tile.base.BaseTickTileBlock;
import hellfirepvp.astralsorcery.common.component.ColorComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.ColorReference;
import hellfirepvp.astralsorcery.common.util.ColorUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CelestialGatewayBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CelestialGatewayBlock extends BaseTickTileBlock<TileCelestialGateway> {

    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 1, 15);

    public static final float defaultDestroySpeed = 2.5F;

    public CelestialGatewayBlock(Properties properties) {
        super(properties, TileEntitiesAS.CELESTIAL_GATEWAY);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);

        ColorComponent cmp = DataComponentsAS.COLOR.get(stack);
        if (cmp != null) {
            cmp.reference().asDyeColor().ifPresent(dyeColor -> {
                tooltipComponents.add(ColorUtil.getColorName(dyeColor).withStyle(style -> style.withColor(dyeColor.getTextColor())));
            });
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        MiscUtil.getTileAt(level, pos, TileCelestialGateway.class, true).ifPresent(gateway -> {
            if (gateway.hasCustomName()) {
                stack.setHoverName(gateway.getCustomName());
            }
            if (gateway.getTileData().getColor() != null) {
                DataComponentsAS.COLOR.set(stack, new ColorComponent(ColorReference.Dye.of(gateway.getTileData().getColor())));
            }
        });
        return stack;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (placer instanceof ServerPlayer sPlayer) {
            MiscUtil.getTileAt(level, pos, TileCelestialGateway.class, true).ifPresent(tile -> {
                if (stack.hasCustomHoverName()) {
                    tile.getTileData().setCustomName(stack.getHoverName());
                }
                Optional.ofNullable(DataComponentsAS.COLOR.get(stack))
                        .flatMap(cmp -> cmp.reference().asDyeColor())
                        .ifPresent(dyeColor -> tile.getTileData().setColor(dyeColor));
                tile.getTileData().markForUpdate();
            });
        }
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return MiscUtil.getTileAt(level, pos, TileCelestialGateway.class, true).map(gateway -> {
            var data = gateway.getTileData();
            if (data.isLocked() && data.hasOwner() && !data.isOwner(player)) {
                return 0F;
            }
            int hardnessMultiplier = ForgeHooks.isCorrectToolForDrops(state, player) ? 30 : 100;
            return player.getDigSpeed(state, pos) / defaultDestroySpeed / hardnessMultiplier;
        }).orElse(0F);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!this.canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canSupportRigidBlock(level, pos.below());
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected BlockEntityTicker<TileCelestialGateway> createTicker() {
        return this.ticker();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
