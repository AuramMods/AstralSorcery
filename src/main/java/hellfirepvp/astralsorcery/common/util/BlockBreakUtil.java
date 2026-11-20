/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.lumen.binding.effect.LumenBindingCollectDropsEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BlockBreakUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class BlockBreakUtil {

    public static Result breakBlockWithoutPlayer(ServerLevel sLevel, BlockPos pos, ItemStack heldItem, boolean bypassHarvestCheck) {
        ServerPlayer fakePlayer = MiscUtil.getAstralFakePlayer(sLevel);
        ItemStack previousTool = fakePlayer.getMainHandItem();
        ItemStack tool = heldItem.copy();
        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, tool);
        try {
            BlockState state = sLevel.getBlockState(pos);
            if (state.getBlock() instanceof GameMasterBlock && !fakePlayer.canUseGameMasterBlocks()) {
                return Result.failure();
            }

            int experience = ForgeHooks.onBlockBreakEvent(sLevel, GameType.SURVIVAL, fakePlayer, pos);
            if (experience < 0 || tool.onBlockStartBreak(pos, fakePlayer)
                    || (!bypassHarvestCheck && !state.canHarvestBlock(sLevel, pos, fakePlayer))) {
                return Result.failure();
            }

            BlockEntity blockEntity = sLevel.getBlockEntity(pos);
            return BlockUtil.captureLevelChanges(sLevel, () -> BlockUtil.collectDrops(sLevel, drops -> {
                tool.copy().mineBlock(sLevel, state, pos, fakePlayer);
                boolean removed = state.onDestroyedByPlayer(sLevel, pos, fakePlayer, true, Fluids.EMPTY.defaultFluidState());
                if (!removed) {
                    return BlockUtil.ChangeResult.revert(Result.failure());
                }
                state.getBlock().destroy(sLevel, pos, state);
                dropPlayerResources(sLevel, fakePlayer, () ->
                        state.getBlock().playerDestroy(sLevel, fakePlayer, pos, state, blockEntity, tool));
                return BlockUtil.ChangeResult.apply(Result.success(drops, experience));
            })).value();
        } catch (Exception exc) {
            return Result.failure();
        } finally {
            fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, previousTool);
        }
    }

    public static void dropPlayerResources(ServerLevel level, ServerPlayer player, Runnable harvest) {
        List<Entity> drops = BlockUtil.collectDrops(level, captured -> {
            harvest.run();
            return captured;
        });
        List<ItemEntity> items = drops.stream().filter(ItemEntity.class::isInstance).map(ItemEntity.class::cast).toList();
        LumenBindingCollectDropsEffect.collectDrops(player, items);
        drops.stream().filter(entity -> !entity.isRemoved()).forEach(level::addFreshEntity);
    }

    public static class Result {

        private final boolean success;
        private final List<ItemEntity> capturedDrops = new ArrayList<>();
        private int droppedExperience = 0;

        private Result(boolean success) {
            this.success = success;
        }

        private static Result failure() {
            return new Result(false);
        }

        private static Result success(List<Entity> drops, int experience) {
            Result result = new Result(true);
            result.droppedExperience = experience;
            for (Entity drop : drops) {
                if (drop instanceof ItemEntity item) {
                    result.capturedDrops.add(item);
                } else if (drop instanceof ExperienceOrb orb) {
                    result.droppedExperience += orb.getValue();
                }
            }
            return result;
        }

        public boolean isSuccess() {
            return this.success;
        }

        public List<ItemEntity> getCapturedDrops() {
            return this.capturedDrops;
        }

        public int getDroppedExperience() {
            return this.droppedExperience;
        }

        public void dropResultsInWorld(ServerLevel sLevel, BlockState brokenState, BlockPos pos, ItemStack tool) {
            for (ItemEntity entity : this.getCapturedDrops()) {
                //Emulating Block#popResource position calc
                double height = (double) EntityType.ITEM.getHeight() / 2.0;
                double x = pos.getX() + 0.5 + Mth.nextDouble(sLevel.random, -0.25, 0.25);
                double y = pos.getY() + 0.5 + Mth.nextDouble(sLevel.random, -0.25, 0.25) - height;
                double z = pos.getZ() + 0.5 + Mth.nextDouble(sLevel.random, -0.25, 0.25);
                entity.setPos(x, y, z);
                sLevel.addFreshEntity(entity);
            }
            if (this.getDroppedExperience() > 0) {
                brokenState.getBlock().popExperience(sLevel, pos, this.getDroppedExperience());
            }
        }
    }
}
