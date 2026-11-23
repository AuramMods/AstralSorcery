package hellfirepvp.astralsorcery.gametest;

import com.mojang.authlib.GameProfile;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.ObserversAS;
import hellfirepvp.astralsorcery.common.lumen.ILumenHandler;
import hellfirepvp.astralsorcery.common.lumen.transfer.LumenNetworkHelper;
import hellfirepvp.astralsorcery.common.recipe.StageRules;
import hellfirepvp.astralsorcery.common.recipe.focal.drop.ActiveFocalCombineRecipe;
import hellfirepvp.astralsorcery.common.recipe.focal.drop.FocalCombineCraftingInput;
import hellfirepvp.astralsorcery.common.recipe.focal.place.ActiveTransmutationHandler;
import hellfirepvp.astralsorcery.common.recipe.liquid.ActiveLiquidStarlightRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.tile.TileFocusRelay;
import hellfirepvp.astralsorcery.common.tile.TileInfuser;
import hellfirepvp.astralsorcery.common.tile.TileLumenAlchemyArray;
import hellfirepvp.astralsorcery.common.tile.TileLumenArray;
import hellfirepvp.astralsorcery.common.tile.TileLumenCrystallizer;
import hellfirepvp.astralsorcery.common.util.RecipeFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@GameTestHolder(AstralSorcery.MODID)
@PrefixGameTestTemplate(false)
public final class ScriptedProcessingTests {

    private static final String TEMPLATE = "gametest/empty";
    private static final BlockPos ORIGIN = new BlockPos(1, 2, 1);

    @GameTest(template = TEMPLATE)
    public static void scriptedFocalTransmutationChangesBlock(GameTestHelper test) {
        if (ModList.get().isLoaded("crafttweaker")) {
            test.setBlock(ORIGIN, Blocks.SPONGE);
            BlockPos target = test.absolutePos(ORIGIN);
            var level = test.getLevel();
            var constellation = ConstellationsAS.VICIO.get();
            for (int tick = 0; tick < 80; tick++) {
                ActiveTransmutationHandler.receiveStarlight(level, target, constellation, 1, false);
            }
            test.assertBlockPresent(Blocks.CLAY, ORIGIN);
        }
        test.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void scriptedFocalCombinationConsumesDroppedInputs(GameTestHelper test) {
        if (ModList.get().isLoaded("crafttweaker")) {
            var level = test.getLevel();
            var constellation = ConstellationsAS.VICIO.get();
            var pos = test.absolutePos(ORIGIN).getCenter();
            var catalyst = new ItemEntity(level, pos.x, pos.y, pos.z, new ItemStack(Items.SPONGE));
            level.addFreshEntity(catalyst);

            var recipe = RecipeFinder.of(level).findFocalCombineRecipe(constellation, List.of(catalyst)).orElseThrow();
            var input = new FocalCombineCraftingInput(constellation, level, List.of(catalyst));
            var active = ActiveFocalCombineRecipe.create(recipe, constellation, input);
            for (int tick = 0; tick < recipe.value().getDuration(); tick++) active.tick();
            test.assertTrue(active.isFinished() && active.finish(active.match(level).orElseThrow(), level),
                    "The native focal combination must complete");
            test.assertTrue(catalyst.getItem().isEmpty() && hasDrop(test, ORIGIN, Items.CLAY_BALL),
                    "Focal combination must consume the selected entity and drop its script output");
        }
        test.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void scriptedLiquidStarlightProcessesDroppedItem(GameTestHelper test) {
        if (ModList.get().isLoaded("crafttweaker")) {
            test.setBlock(ORIGIN, FluidsAS.LIQUID_STARLIGHT.getFluidBlock().get());
            var level = test.getLevel();
            var pos = test.absolutePos(ORIGIN).getCenter();
            var input = new ItemEntity(level, pos.x, pos.y, pos.z, new ItemStack(Items.SPONGE));
            level.addFreshEntity(input);
            for (int tick = 0; tick < 81; tick++) ActiveLiquidStarlightRecipe.tryProgressCraft(input);
            test.assertTrue(input.getItem().isEmpty() && hasDrop(test, ORIGIN, Items.CLAY_BALL),
                    "Liquid starlight must consume its catalyst and drop the scripted result");
        }
        test.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void scriptedChaliceReactionSpawnsAnEntity(GameTestHelper test) {
        if (ModList.get().isLoaded("crafttweaker") && !ModList.get().isLoaded("gamestages")) {
            BlockPos second = ORIGIN.east();
            test.setBlock(ORIGIN, BlocksAS.CHALICE.get());
            test.setBlock(second, BlocksAS.CHALICE.get());
            var firstChalice = (TileChalice) test.getBlockEntity(ORIGIN);
            var secondChalice = (TileChalice) test.getBlockEntity(second);
            firstChalice.getTankView().fill(new FluidStack(Fluids.WATER, 1000), FluidAction.EXECUTE);
            secondChalice.getTankView().fill(new FluidStack(Fluids.WATER, 1500), FluidAction.EXECUTE);

            for (int tick = 0; tick < 65; tick++) firstChalice.serverTick(test.getLevel());
            test.assertTrue(firstChalice.getContainedFluid().isEmpty() && secondChalice.getContainedFluid().isEmpty(),
                    "Chalice reaction must consume the exact fluid volumes");
            test.assertTrue(!test.getLevel().getEntitiesOfClass(Chicken.class,
                            new AABB(test.absolutePos(ORIGIN)).inflate(2)).isEmpty(),
                    "The weighted chalice reaction must spawn its configured entity");
        }
        test.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void scriptedAltarConsumesRelayAndProducesBothItems(GameTestHelper test) {
        if (ModList.get().isLoaded("crafttweaker")) {
            var level = test.getLevel();
            test.setBlock(ORIGIN, BlocksAS.ALTAR_ILLUMINATION.get());
            var altar = (TileAltar) test.getBlockEntity(ORIGIN);
            var relayPos = test.absolutePos(ORIGIN).offset(TileAltar.getRelayGridOffsets().get(0));
            level.setBlockAndUpdate(relayPos, BlocksAS.FOCUS_RELAY.get().defaultBlockState());
            var relay = (TileFocusRelay) level.getBlockEntity(relayPos);
            relay.getTileData().getInventory().setStackInSlot(0, new ItemStack(Items.REDSTONE));
            altar.getTileData().getAltarInventory().setStackInSlot(1, new ItemStack(Items.SPONGE));
            UUID actor = UUID.randomUUID();
            var recipe = altar.findMatchingRecipe(level, actor).orElseThrow();
            altar.startCrafting(recipe, actor);
            if (ModList.get().isLoaded("gamestages")) {
                for (int tick = 0; tick < 5; tick++) altar.serverTick(level);
                var recipes = level.getRecipeManager();
                var gateId = AstralSorcery.key("gametest_active_altar");
                StageRules.set(recipes, new StageRules.Rule(gateId, StageRules.Target.RECIPE,
                        recipe.id(), List.of("astral_altar_stage"), false, false));
                try {
                    var active = altar.getTileData().getActiveRecipe().orElseThrow();
                    int progress = active.getProgressTick();
                    var save = altar.saveWithoutMetadata();
                    altar.load(save);
                    var restored = altar.getTileData().getActiveRecipe().orElseThrow();
                    test.assertTrue(restored != active && restored.getPlayerUUID().equals(actor)
                                    && restored.getProgressTick() == progress
                                    && restored.getRecipeId().equals(recipe.id()),
                            "Reloading an active altar must preserve its recipe, actor and progress");
                    for (int tick = 0; tick < 25; tick++) altar.serverTick(level);
                    test.assertTrue(altar.getTileData().getActiveRecipe().isPresent()
                                    && altar.getTileData().getAltarInventory().getStackInSlot(1).is(Items.SPONGE)
                                    && relay.getTileData().getInventory().getStackInSlot(0).is(Items.REDSTONE)
                                    && !hasDrop(test, ORIGIN.above(), Items.EMERALD),
                            "An in-flight gate must pause the altar without modifying its inputs or creating output");
                } finally {
                    StageRules.remove(recipes, gateId);
                }
            }
            for (int tick = 0; tick < 25; tick++) altar.serverTick(level);
            test.assertTrue(altar.getTileData().getAltarInventory().getStackInSlot(1).isEmpty()
                    && relay.getTileData().getInventory().getStackInSlot(0).isEmpty(),
                    "The scripted altar must use its configured main and relay slots");
            test.assertTrue(countDrops(test, ORIGIN.above(), Items.EMERALD) == 1
                            && countDrops(test, ORIGIN.above(), Items.AMETHYST_SHARD) == 1,
                    "Both outputs must drop exactly once");
        }
        test.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void scriptedInfusionRunsInItsStructure(GameTestHelper test) {
        if (ModList.get().isLoaded("crafttweaker")) {
            var level = test.getLevel();
            var pos = test.absolutePos(ORIGIN);
            test.setBlock(ORIGIN, BlocksAS.INFUSER.get());
            var structure = ObserversAS.STRUCTURE_INFUSER.findStructureForPreview(level, pos).orElseThrow();
            structure.getContents().forEach((offset, block) ->
                    level.setBlock(pos.offset(offset), block.getDescriptiveState(0), Block.UPDATE_ALL));
            for (BlockPos offset : TileInfuser.getLiquidOffsets()) {
                level.setBlockAndUpdate(pos.offset(offset), Blocks.WATER.defaultBlockState());
            }
            var infuser = (TileInfuser) level.getBlockEntity(pos);
            infuser.getTileData().getInventory().setStackInSlot(0, new ItemStack(Items.SPONGE));
            var player = new ServerPlayer(level.getServer(), level, new GameProfile(UUID.randomUUID(), "astral_infusion"));
            var recipe = infuser.findMatchingRecipe(player).orElseThrow();
            infuser.startCrafting(player, recipe);
            for (int tick = 0; tick < 85; tick++) infuser.serverTick(level);
            test.assertTrue(infuser.getTileData().getInventory().getStackInSlot(0).isEmpty()
                    && hasDrop(test, ORIGIN.above(), Items.CLAY_BALL), "The infuser must craft the scripted recipe");
        }
        test.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 260)
    public static void scriptedLumenArraysProduceBothModes(GameTestHelper test) {
        if (!ModList.get().isLoaded("crafttweaker") || ModList.get().isLoaded("gamestages")) {
            test.succeed();
            return;
        }
        var level = test.getLevel();
        BlockPos alchemyPos = ORIGIN.east(3);
        test.setBlock(ORIGIN, BlocksAS.LUMEN_ARRAY.get());
        test.setBlock(alchemyPos, BlocksAS.LUMEN_ALCHEMY_ARRAY.get());
        var array = (TileLumenArray) test.getBlockEntity(ORIGIN);
        var alchemy = (TileLumenAlchemyArray) test.getBlockEntity(alchemyPos);
        array.getTileData().getFluidTank().fill(FluidsAS.LIQUID_STARLIGHT.stack(1000), FluidAction.EXECUTE);
        array.getTileData().getInventory().setStackInSlot(0, new ItemStack(Items.GOLD_NUGGET));
        alchemy.getTileData().getFluidTank().fill(FluidsAS.LIQUID_STARLIGHT.stack(1000), FluidAction.EXECUTE);
        alchemy.getTileData().getInventory().setStackInSlot(0, new ItemStack(Items.GOLD_NUGGET));

        AtomicBoolean seeded = new AtomicBoolean();
        test.succeedWhen(() -> {
            test.assertTrue(array.getTileData().getLumenHandler().getContainedLumen(LumenAS.SOLYN.get()).isPresent(),
                    "The regular array must generate lumen");
            var source = LumenNetworkHelper.getNode(level, test.absolutePos(ORIGIN)).orElseThrow();
            if (seeded.compareAndSet(false, true)) {
                array.getTileData().getLumenHandler().fill(LumenAS.SOLYN.get().stack(3000), ILumenHandler.Action.EXECUTE);
                LumenNetworkHelper.setNodeProvidedLumenTypes(level, source, Set.of(LumenAS.SOLYN.get()));
            }
            test.assertTrue(alchemy.getTileData().getLumenHandler().getContainedLumen(LumenAS.CALDOR.get()).isPresent(),
                    "The alchemy array must consume linked Solyn and produce Caldor");
        });
    }

    @GameTest(template = TEMPLATE)
    public static void scriptedCrystallizerFormsLumenCluster(GameTestHelper test) {
        if (ModList.get().isLoaded("crafttweaker") && !ModList.get().isLoaded("gamestages")) {
            var level = test.getLevel();
            test.setBlock(ORIGIN, BlocksAS.LUMEN_CRYSTALLIZER.get());
            var crystallizer = (TileLumenCrystallizer) test.getBlockEntity(ORIGIN);
            crystallizer.getTileData().getInventory().setStackInSlot(0, new ItemStack(Items.SPONGE));
            crystallizer.getTileData().getLumenHandler().fill(LumenAS.SOLYN.get().stack(600), ILumenHandler.Action.EXECUTE);
            crystallizer.getTileData().getFluidTank().fill(FluidsAS.LIQUID_STARLIGHT.stack(1000), FluidAction.EXECUTE);
            crystallizer.serverTick(level);
            test.assertBlockPresent(BlocksAS.LUMEN_CRYSTAL_CLUSTER.get(), ORIGIN.above());
        }
        test.succeed();
    }

    private static boolean hasDrop(GameTestHelper test, BlockPos pos, Item item) {
        return countDrops(test, pos, item) > 0;
    }

    private static int countDrops(GameTestHelper test, BlockPos pos, Item item) {
        return test.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(test.absolutePos(pos)).inflate(1.5),
                entity -> entity.getItem().is(item)).stream().mapToInt(entity -> entity.getItem().getCount()).sum();
    }
}
