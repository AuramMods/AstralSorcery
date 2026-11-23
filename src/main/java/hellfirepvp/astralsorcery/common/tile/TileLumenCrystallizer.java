/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.google.common.collect.Iterables;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenCrystallizationRecipeInput;
import hellfirepvp.astralsorcery.common.tile.base.TileDataOwned;
import hellfirepvp.astralsorcery.common.recipe.StageActor;
import hellfirepvp.astralsorcery.common.recipe.StageRules;
import hellfirepvp.astralsorcery.common.recipe.ProcessingRules;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.common.block.tile.LumenCrystalClusterBlock;
import hellfirepvp.astralsorcery.common.event.RecipeEvent;
import hellfirepvp.astralsorcery.common.lib.*;
import hellfirepvp.astralsorcery.common.lumen.ILumenHandler;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import hellfirepvp.astralsorcery.common.lumen.capability.LumenHandlerView;
import hellfirepvp.astralsorcery.common.lumen.capability.LumenHandlerViewFactory;
import hellfirepvp.astralsorcery.common.lumen.capability.LumenStackList;
import hellfirepvp.astralsorcery.common.lumen.transfer.LumenRequestHelper;
import hellfirepvp.astralsorcery.common.recipe.infusion.ActiveInfusionRecipe;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenCrystallizationRecipe;
import hellfirepvp.astralsorcery.common.tile.base.TileCapabilities;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityLumenDisplay;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.*;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.LazyRecipeHolder;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.inventory.FilteredInventoryView;
import hellfirepvp.astralsorcery.common.util.inventory.FilteredInventoryViewFactory;
import hellfirepvp.astralsorcery.common.util.inventory.InventoryStackList;
import hellfirepvp.astralsorcery.common.util.inventory.InventoryView;
import hellfirepvp.astralsorcery.common.util.tank.FluidContainerList;
import hellfirepvp.astralsorcery.common.util.tank.FluidTankView;
import hellfirepvp.astralsorcery.common.util.tank.FluidTankViewFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import hellfirepvp.astralsorcery.common.recipe.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileLumenCrystallizer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileLumenCrystallizer extends TileEntityTick<TileLumenCrystallizer.Data> implements TileEntityLumenDisplay {

    private static final int PASSIVE_LUMEN_DRAIN = 2, PASSIVE_LIQUID_STARLIGHT_DRAIN = 2;
    private LumenCrystallizationRecipe activeRecipe = null;

    public TileLumenCrystallizer(BlockPos pos, BlockState blockState) {
        super(TileEntitiesAS.LUMEN_CRYSTALLIZER, pos, blockState);
    }

    @Override
    public void serverTick(ServerLevel level) {
        super.serverTick(level);

        this.doCraftingCycle(level);

        if (this.getTileData().getTicksExisted() % 20 == 0) {
            FluidStack stack = this.getTileData().getContainedFluid();
            if (!stack.isEmpty()) {
                this.setLight(level, stack.getFluid().getFluidType().getLightLevel(stack));
            } else {
                this.setLight(level, 0);
            }
        }
    }

    private void doCraftingCycle(ServerLevel level) {
        var rule = ProcessingRules.get(level, ProcessingRules.Operation.LUMEN_CLUSTER_GROWTH);
        if (!rule.enabled() || rule.rateMultiplier() == 0) return;
        ItemStack catalyst = this.getTileData().getInventory().getStackInSlot(0);
        if (catalyst.isEmpty() && level.isEmptyBlock(this.getBlockPos().above())) {
            this.activeRecipe = null;
            return;
        }
        TileLumenCrystalCluster cluster = MiscUtil.getTileAt(level, this.getBlockPos().above(), TileLumenCrystalCluster.class, false).orElse(null);

        StageActor actor = StageActor.fromOwner(level, this.getTileData().getOwnerId());
        if (!StageRules.allowsFeature(level, ProcessingRules.Operation.LUMEN_CLUSTER_GROWTH.id, actor)) return;
        if (this.activeRecipe != null && (!StageRules.isCurrent(level, this.activeRecipe)
                || !StageRules.allowsRecipe(level, this.activeRecipe, actor))) {
            this.activeRecipe = null;
        }
        if (this.activeRecipe == null) {
            var input = cluster == null ? LumenCrystallizationRecipeInput.of(catalyst)
                    : LumenCrystallizationRecipeInput.of(cluster.getTileData().getLumen());
            this.activeRecipe = RecipeFinder.of(level).findCrystallizationRecipe(input, actor).map(RecipeHolder::value).orElse(null);
        }
        if (this.activeRecipe == null) return;
        if (!catalyst.isEmpty() && !level.isEmptyBlock(this.getBlockPos().above())) {
            this.breakCatalyst();
            return;
        }
        boolean byCatalyst = cluster == null;

        LumenStack containedLumen = this.getTileData().getContainedLumen();
        if (!containedLumen.isEmpty() && !containedLumen.is(this.activeRecipe.getLumenToCrystallize())) {
            this.getTileData().getLumenContents().clear();
            this.getTileData().markForUpdate();
        }

        boolean canCraft = false;
        BlockState above = level.getBlockState(this.getBlockPos().above());
        if (above.isAir()) {
            canCraft = true;
        }
        if (above.is(BlocksAS.LUMEN_CRYSTAL_CLUSTER.get())) {
            if (cluster != null && above.getValue(LumenCrystalClusterBlock.STAGE) < 4) {
                canCraft = cluster.getTileData().getLumen().equals(this.activeRecipe.getLumenToCrystallize());
            }
        }
        if (!canCraft) return;

        int lumenToProvide = this.activeRecipe.getLumenConsumedPerOperation() + 100;
        int storedLumen = this.getTileData().getLumenContents()
                .getLumenStack(this.activeRecipe.getLumenToCrystallize())
                .map(LumenStack::getAmount)
                .orElse(0);

        if (this.getTileData().getTicksExisted() % 80 == 0) {
            if (storedLumen <= lumenToProvide) {
                int maxCapacity = this.getTileData().getLumenHandler().getCapacity(this.activeRecipe.getLumenToCrystallize());
                LumenStack drainStack = this.activeRecipe.getLumenToCrystallize().stack(Math.min(200, maxCapacity - storedLumen));
                if (drainStack.getAmount() > 0) {
                    LumenRequestHelper.requestRelayed(level, this.getBlockPos(), drainStack).ifPresent(chain -> {
                        LumenStack stored = LumenUtil.tryChainTransfer(this.getTileData().getLumenHandler(), level, chain, drainStack, ILumenHandler.Action.EXECUTE);
                        if (!stored.isEmpty()) {
                            chain.playTransferEffect(level, stored.getLumen());
                        }
                    });
                }
            }
        }

        int drainAmt = PASSIVE_LUMEN_DRAIN;
        LumenStack drained = this.getTileData().getLumenHandler().drain(this.activeRecipe.getLumenToCrystallize(), drainAmt, ILumenHandler.Action.SIMULATE);
        if (drained.getAmount() < drainAmt) return;

        int liquidAmt = PASSIVE_LIQUID_STARLIGHT_DRAIN;
        if (byCatalyst) liquidAmt *= 2;
        FluidStack requested = FluidsAS.LIQUID_STARLIGHT.stack(liquidAmt);
        FluidStack drainedFluid = this.getTileData().getFluidTank().getWithoutFilters(tank -> tank.drain(requested, IFluidHandler.FluidAction.SIMULATE));
        if (drainedFluid.getAmount() < liquidAmt) return;

        if (this.getTileData().getTicksExisted() % 20 == 0) {
            this.getTileData().getLumenHandler().drain(this.activeRecipe.getLumenToCrystallize(), drainAmt, ILumenHandler.Action.EXECUTE);
            this.getTileData().getFluidTank().withoutFilters(tank -> tank.drain(requested, IFluidHandler.FluidAction.EXECUTE));
        }

        if (!catalyst.isEmpty() && this.activeRecipe.getCatalystShatterMultiplier() > 0) {
            int chance = Mth.ceil(20 * 60 * (1F / this.activeRecipe.getCatalystShatterMultiplier()));
            if (this.rand.nextInt(Math.max(chance, 1)) == 0) {
                this.breakCatalyst();
                return;
            }
        }

        if (storedLumen >= lumenToProvide) {
            if (above.isAir()) {
                if (rule.roll(this.rand, 20 * 60)) {
                    BlockState toSet = BlocksAS.LUMEN_CRYSTAL_CLUSTER.get().defaultBlockState();
                    level.setBlock(this.getBlockPos().above(), toSet, Block.UPDATE_ALL);
                    MiscUtil.getTileAt(level, this.getBlockPos().above(), TileLumenCrystalCluster.class, true).ifPresent(newCluster -> {
                        newCluster.getTileData().setLumen(this.activeRecipe.getLumenToCrystallize());
                        newCluster.getTileData().markForUpdate();
                    });
                    this.getTileData().getInventory().clearInventory();
                    this.getTileData().markForUpdate();
                    LumenUtil.drain(this.getTileData().getLumenHandler(),
                            this.activeRecipe.getLumenToCrystallize().stack(this.activeRecipe.getLumenConsumedPerOperation()),
                            ILumenHandler.Action.EXECUTE);

                    MinecraftForge.EVENT_BUS.post(new RecipeEvent.LumenCrystallization(this.activeRecipe, this, toSet));
                }
            } else {
                if (rule.roll(this.rand, 20 * 60 * 5)) {
                    int stage = above.getValue(LumenCrystalClusterBlock.STAGE);
                    BlockState toSet = above.setValue(LumenCrystalClusterBlock.STAGE, Math.min(4, stage + 1));
                    level.setBlock(this.getBlockPos().above(), toSet, Block.UPDATE_ALL);
                    LumenUtil.drain(this.getTileData().getLumenHandler(),
                            this.activeRecipe.getLumenToCrystallize().stack(this.activeRecipe.getLumenConsumedPerOperation()),
                            ILumenHandler.Action.EXECUTE);

                    MinecraftForge.EVENT_BUS.post(new RecipeEvent.LumenCrystallization(this.activeRecipe, this, toSet));
                }
            }
        }
    }

    public void breakCatalyst() {
        InventoryView inv = this.getTileData().getInventory();
        if (!inv.getStackInSlot(0).isEmpty()) {
            inv.setStackInSlot(0, ItemStack.EMPTY);
            ServerSoundHelper.playSoundAround(SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, this.getLevel(), this.getBlockPos(), 1F, 1F);
        }
        this.activeRecipe = null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(Level level) {
        super.clientTick(level);

        Lumen transmittedLumen = LumenAS.NONE.get();

        LumenStack stored = Iterables.getFirst(this.getTileData().getLumenContents().getLumenStacks(), LumenStack.EMPTY);
        if (!stored.isEmpty()) {
            transmittedLumen = stored.getLumen();
        }

        if (!this.getTileData().getInventory().getStackInSlot(0).isEmpty() && !this.getTileData().getContainedFluid().isEmpty()) {
            Vector3 effectPos = new Vector3(this).add(0.1F + rand.nextFloat() * 0.8F, 0.96F, 0.1F + rand.nextFloat() * 0.8F);
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(effectPos)
                    .color(FXColorFunction.constant(rand.nextInt(3) == 0 ? ColorWrapper.WHITE : transmittedLumen.getColor(level, effectPos)))
                    .setScale(0.1F + rand.nextFloat() * 0.1F)
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .setGravity(Vector3.y(0.0004F))
                    .setMaxAge(20 + rand.nextInt(10));
        }

        for (int i = 0; i < 2; i++) {
            Vector3 pos = Vector3.atBottomCenter(this).addY(0.31F);
            pos = VectorUtil.withRandomOffset(pos, rand, 0.05F);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .color(FXColorFunction.constant(rand.nextInt(3) == 0 ? ColorWrapper.WHITE : transmittedLumen.getColor(level, pos)))
                    .setScale(0.09F + rand.nextFloat() * 0.1F)
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .setGravity(Vector3.y(0.0009F))
                    .setMaxAge(20 + rand.nextInt(8));
        }

        if (transmittedLumen != LumenAS.NONE.get() && rand.nextInt(20) == 0) {
            Vector3 pos = VectorUtil.withRandomOffset(Vector3.atCenter(this), rand, 0.3F);

            EffectHelper.of(EffectTemplatesAS.LUMEN_PARTICLE)
                    .spawn(pos)
                    .setSprite(TexturesAS.ATLAS_LUMEN, RegistriesAS.REGISTRY_LUMEN.get().getKey(transmittedLumen))
                    .setAlpha(0.3F)
                    .color(FXColorFunction.constant(transmittedLumen.getColor(level, pos)))
                    .setGravity(Vector3.y(0.00007F));
        }
    }

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }

    public static class Data extends TileEntityTick.Data
            implements TileDataOwned, TileCapabilities.ItemStorage, TileCapabilities.FluidStorage, TileCapabilities.LumenStorage {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> crystallizerFields(inst).apply(inst, Data::new));

        protected static <T extends Data> Products.P9<RecordCodecBuilder.Mu<T>, Long, Boolean, Map<BlockPos, Boolean>, FluidContainerList, InventoryStackList, LumenStackList, Lumen, Long, Optional<UUID>> crystallizerFields(RecordCodecBuilder.Instance<T> instance) {
            var tick = TileEntityTick.Data.tickFields(instance);
            return instance.group(tick.t1(), tick.t2(), tick.t3(),
                    CodecUtil.defaulted(FluidContainerList.CODEC, "fluidContents", FluidContainerList::create, Data::getFluidContents),
                    CodecUtil.defaulted(InventoryStackList.CODEC, "inventoryContents", InventoryStackList::create, Data::getInventoryContents),
                    CodecUtil.defaulted(LumenStackList.CODEC, "lumenContents", LumenStackList::create, Data::getLumenContents),
                    CodecUtil.lenientDefaulted(RegistriesAS.codec(RegistriesAS.KEY_LUMEN), "recentlyTransmittedLumen", LumenAS.NONE, Data::getRecentlyTransmittedLumen),
                    CodecUtil.defaulted(Codec.LONG, "transmittedLumenGameTime", () -> 0L, Data::getTransmittedLumenGameTime),
                    TileDataOwned.<T>ownerField()
            );
        }

        private UUID ownerId;
        protected final FluidContainerList fluidContents;
        protected final InventoryStackList inventoryContents;
        protected final LumenStackList lumenContents;
        protected Lumen recentlyTransmittedLumen;
        protected long transmittedLumenGameTime;

        protected Data(long ticksExisted,
                       boolean hasStructure,
                       Map<BlockPos, Boolean> skyObstructions,
                       FluidContainerList fluidContents,
                       InventoryStackList inventoryContents,
                       LumenStackList lumenContents,
                       Lumen recentlyTransmittedLumen,
                       long transmittedLumenGameTime, Optional<UUID> ownerId) {
            super(ticksExisted, hasStructure, skyObstructions);
            this.fluidContents = fluidContents;
            this.inventoryContents = inventoryContents;
            this.lumenContents = lumenContents;
            this.recentlyTransmittedLumen = recentlyTransmittedLumen;
            this.transmittedLumenGameTime = transmittedLumenGameTime;
            this.ownerId = ownerId.orElse(null);
        }

        @Override
        public UUID getOwnerId() {
            return this.ownerId;
        }

        @Override
        public void setOwnerId(UUID ownerId) {
            this.ownerId = ownerId;
        }

        public FluidContainerList getFluidContents() {
            return this.fluidContents;
        }

        public InventoryStackList getInventoryContents() {
            return this.inventoryContents;
        }

        public LumenStackList getLumenContents() {
            return this.lumenContents;
        }

        public FluidStack getContainedFluid() {
            return this.fluidContents.getTank(0).getContent();
        }

        public LumenStack getContainedLumen() {
            return Iterables.getFirst(this.getLumenContents().getLumenStacks(), LumenStack.EMPTY);
        }

        protected Optional<RecipeHolder<LumenCrystallizationRecipe>> findMatchingRecipe(Level level, ItemStack stack) {
            return RecipeFinder.of(level).findCrystallizationRecipe(stack);
        }

        protected Optional<RecipeHolder<LumenCrystallizationRecipe>> findMatchingRecipe(Level level, Lumen lumen) {
            return RecipeFinder.of(level).findCrystallizationRecipe(lumen);
        }

        protected FluidTankViewFactory newFluidTank() {
            return FluidTankViewFactory.builder(1)
                    .tankCapacity(tank -> 2000)
                    .inputFilter((tank, stack, existing) -> stack.getFluid().getFluidType() == FluidsAS.LIQUID_STARLIGHT.getFluidType())
                    .extractFilter((tank, stack, existing) -> false)
                    .accessibleSides(Direction.DOWN);
        }

        @Override
        public FluidTankView getFluidTank() {
            return this.newFluidTank().createTileView(this, this.fluidContents);
        }


        protected FilteredInventoryViewFactory newInventoryHandler() {
            return FilteredInventoryViewFactory.filteredBuilder(1)
                    .stackSizeLimiter((slot, stack) -> 1)
                    .extractFilter((slot, amount, existing) -> false)
                    .inputFilter((slot, toAdd, existing) -> {
                        if (!existing.isEmpty()) return false;
                        var match = this.findMatchingRecipe(this.getLevel(), toAdd);
                        return match.isPresent() &&
                                (this.getContainedLumen().isEmpty() || match.get().value().getLumenToCrystallize().equals(this.getContainedLumen().getLumen()));
                    })
                    .accessibleSides(Direction.DOWN);
        }

        @Override
        public FilteredInventoryView getInventory() {
            return this.newInventoryHandler().createTileView(this, this.inventoryContents);
        }

        protected LumenHandlerViewFactory newLumenHandler() {
            return LumenHandlerViewFactory.builder()
                    .accessibleSides(Direction.DOWN)
                    .tankCapacity(lumen -> 2000)
                    .inputFilter((toAdd, existing) ->
                            this.getContainedLumen().isEmpty() || this.getContainedLumen().isSameLumen(toAdd));
        }

        @Override
        public LumenHandlerView getLumenHandler() {
            return this.newLumenHandler().createTileView(this, this.getLumenContents());
        }

        public Lumen getRecentlyTransmittedLumen() {
            return this.recentlyTransmittedLumen;
        }

        public long getTransmittedLumenGameTime() {
            return this.transmittedLumenGameTime;
        }

        public void setTransmittedLumen(Lumen lumen, long gameTime) {
            this.recentlyTransmittedLumen = lumen;
            this.transmittedLumenGameTime = gameTime;
        }
    }
}
