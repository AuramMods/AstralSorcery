package hellfirepvp.astralsorcery.gametest;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.bracket.custom.RecipeTypeBracketHandler;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.component.LumenComponent;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.recipe.CustomRecipe;
import hellfirepvp.astralsorcery.common.recipe.lightwell.LightwellRecipe;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenGenerationRecipe;
import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@GameTestHolder(AstralSorcery.MODID)
@PrefixGameTestTemplate(false)
public final class ForgeCompatibilityTests {

    private static final String TEMPLATE = "gametest/empty";

    @GameTest(template = TEMPLATE)
    public static void craftTweakerAddsNativeLightwellRecipe(GameTestHelper test) {
        if (ModList.get().isLoaded("crafttweaker")) {
            var id = new ResourceLocation("crafttweaker", "astral_lightwell_smoke");
            Recipe<?> recipe = test.getLevel().getRecipeManager().byKey(id).orElseThrow();
            test.assertTrue(recipe instanceof LightwellRecipe lightwell
                            && lightwell.getInput().test(new ItemStack(Items.SNOW_BLOCK))
                            && lightwell.getGeneratedFluid() == Fluids.WATER,
                    "The real ZenCode script must add a native, usable Lightwell recipe");
        }
        test.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void craftTweakerManagesEveryAstralRecipeType(GameTestHelper test) {
        if (ModList.get().isLoaded("crafttweaker")) {
            var recipes = test.getLevel().getRecipeManager();
            var types = Map.of(
                    "altar", RecipeTypesAS.ALTAR_CRAFTING_TYPE.get(),
                    "focal", RecipeTypesAS.FOCAL_COMBINE_TYPE.get(),
                    "transmutation", RecipeTypesAS.FOCAL_TRANSMUTATION_TYPE.get(),
                    "infusion", RecipeTypesAS.INFUSION_TYPE.get(),
                    "lightwell", RecipeTypesAS.LIGHTWELL_TYPE.get(),
                    "interaction", RecipeTypesAS.LIQUID_INTERACTION_TYPE.get(),
                    "starlight", RecipeTypesAS.LIQUID_STARLIGHT_TYPE.get(),
                    "crystallization", RecipeTypesAS.LUMEN_CRYSTALLIZATION_TYPE.get(),
                    "generation", RecipeTypesAS.LUMEN_GENERATION_TYPE.get(),
                    "alchemy", RecipeTypesAS.LUMEN_GENERATION_TYPE.get());
            types.forEach((name, type) -> {
                var id = ResourceLocation.tryParse("astraltest:" + name);
                var original = ResourceLocation.tryParse("crafttweaker:astral_" + name);
                var result = recipes.byKey(id);
                test.assertTrue(result.isPresent() && result.get().getType() == type, "Missing script recipe " + id);
                test.assertTrue(recipes.byKey(original).isEmpty(), "Script recipe was not removed " + original);
                assertRecipeRoundTrip(test, result.orElseThrow());
            });
            var lightwell = (LightwellRecipe) recipes.byKey(ResourceLocation.tryParse("astraltest:lightwell")).orElseThrow();
            test.assertTrue(lightwell.getGeneratedFluid() == Fluids.LAVA, "Script replacement must retain its namespaced ID");
            var ice = (LightwellRecipe) recipes.byKey(AstralSorcery.key("lightwell/water_ice")).orElseThrow();
            test.assertTrue(ice.getProductionMultiplier() == 1.5F, "The built-in recipe must be replaced at its original ID");
            var generation = (LumenGenerationRecipe) recipes.byKey(ResourceLocation.tryParse("astraltest:generation")).orElseThrow();
            var alchemy = (LumenGenerationRecipe) recipes.byKey(ResourceLocation.tryParse("astraltest:alchemy")).orElseThrow();
            test.assertTrue(generation.getLumenCombinationInputs().isEmpty()
                    && alchemy.getLumenCombinationInputs().getOrDefault(LumenAS.SOLYN.get(), 0) == 10,
                    "The native lumen recipes retain the configured production modes");
            for (Recipe<?> recipe : recipes.getRecipes()) {
                if (recipe instanceof CustomRecipe<?, ?>) {
                    var manager = RecipeTypeBracketHandler.getOrDefault(recipe.getType());
                    String command = CraftTweakerAPI.getRegistry().<Recipe<?>>getRecipeHandlerFor(recipe).dumpToCommandString(manager, recipe);
                    test.assertTrue(command.startsWith(manager.getCommandString() + ".setJsonRecipe(\"" + recipe.getId() + "\""),
                            "CraftTweaker must dump the complete native recipe at its original ID " + recipe.getId());
                }
            }
        }
        test.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void packetContextLoadsOnDedicatedServer(GameTestHelper test) throws IllegalAccessException {
        MethodHandles.lookup().ensureInitialized(PlayPacketHandler.PacketContext.class);
        test.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void lumenStackPersistence(GameTestHelper test) {
        var lumenType = new LumenComponent(LumenAS.SOLYN);
        ItemStack crystal = ItemsAS.LUMEN_CRYSTAL.toStack(4);
        test.assertTrue(DataComponentsAS.LUMEN.getOrDefault(crystal, LumenComponent.EMPTY).equals(LumenComponent.EMPTY),
                "A new crystal must have the empty lumen default");
        DataComponentsAS.LUMEN.set(crystal, lumenType);

        ItemStack restored = ItemStack.of(crystal.save(new CompoundTag()));
        test.assertTrue(ItemStack.matches(crystal, restored), "The native save must retain crystal identity and count");
        test.assertTrue(DataComponentsAS.LUMEN.getOrDefault(restored, LumenComponent.EMPTY).lumen().value() == LumenAS.SOLYN.get(),
                "The saved ID must resolve to the registered lumen");

        ItemStack split = restored.split(1);
        DataComponentsAS.LUMEN.set(split, new LumenComponent(LumenAS.CALDOR));
        test.assertTrue(restored.getCount() == 3 && DataComponentsAS.LUMEN.getOrDefault(restored, LumenComponent.EMPTY).equals(lumenType),
                "Splitting a stack must keep the remaining crystals' lumen");
        test.assertTrue(!ItemStack.isSameItemSameTags(restored, split), "Crystals with different lumens must stay distinct");

        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            ByteBufCodecs.ITEM_STACK.encode(buffer, restored);
            ItemStack synced = ByteBufCodecs.ITEM_STACK.decode(buffer);
            test.assertTrue(ItemStack.matches(restored, synced) && !buffer.isReadable(), "The Forge item packet must retain all stack data");
        } finally {
            buffer.release();
        }
        test.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void recipeWireRoundTrips(GameTestHelper test) {
        Set<RecipeType<?>> expectedTypes = Set.of(
                RecipeTypesAS.ALTAR_CRAFTING_TYPE.get(), RecipeTypesAS.FOCAL_COMBINE_TYPE.get(),
                RecipeTypesAS.FOCAL_TRANSMUTATION_TYPE.get(), RecipeTypesAS.LUMEN_GENERATION_TYPE.get(),
                RecipeTypesAS.LUMEN_CRYSTALLIZATION_TYPE.get(), RecipeTypesAS.LIGHTWELL_TYPE.get(),
                RecipeTypesAS.INFUSION_TYPE.get(), RecipeTypesAS.LIQUID_STARLIGHT_TYPE.get(),
                RecipeTypesAS.LIQUID_INTERACTION_TYPE.get());
        Set<RecipeType<?>> testedTypes = new HashSet<>();
        for (Recipe<?> recipe : test.getLevel().getRecipeManager().getRecipes()) {
            if (expectedTypes.contains(recipe.getType())) {
                assertRecipeRoundTrip(test, recipe);
                testedTypes.add(recipe.getType());
            }
        }
        test.assertTrue(testedTypes.equals(expectedTypes), "All nine Astral recipe types must load in the actual Forge recipe manager");
        test.assertTrue(test.getLevel().getRecipeManager().byKey(AstralSorcery.key("altar/altar_radiance")).isPresent(),
                "The endgame altar recipe must keep its original ID");
        test.succeed();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Recipe<?>> void assertRecipeRoundTrip(GameTestHelper test, T recipe) {
        RecipeSerializer<T> serializer = (RecipeSerializer<T>) recipe.getSerializer();
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        FriendlyByteBuf reencoded = new FriendlyByteBuf(Unpooled.buffer());
        try {
            serializer.toNetwork(buffer, recipe);
            int size = buffer.readableBytes();
            T synced = serializer.fromNetwork(recipe.getId(), buffer);
            test.assertTrue(synced.getId().equals(recipe.getId()) && synced.getType() == recipe.getType() && !buffer.isReadable(),
                    "The complete recipe packet must decode to " + recipe.getId());
            serializer.toNetwork(reencoded, synced);
            test.assertTrue(size == reencoded.readableBytes(), "The decoded recipe must retain its packet fields for " + recipe.getId());
            if (recipe instanceof LumenGenerationRecipe original && synced instanceof LumenGenerationRecipe copy) {
                test.assertTrue(original.getLumenCombinationInputs().equals(copy.getLumenCombinationInputs())
                                && original.getProducedLumen() == copy.getProducedLumen()
                                && original.getProducedLumenAmount() == copy.getProducedLumenAmount(),
                        "The synchronized lumen recipe must preserve its ingredients, quantities and result for " + recipe.getId());
            }
        } finally {
            buffer.release();
            reencoded.release();
        }
    }

    @GameTest(template = TEMPLATE)
    public static void chaliceFluidPersistence(GameTestHelper test) {
        BlockPos pos = new BlockPos(1, 1, 1);
        test.setBlock(pos, BlocksAS.CHALICE.get());
        var tile = test.getBlockEntity(pos);
        var down = tile.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.DOWN);
        test.assertTrue(down.isPresent(), "The chalice must expose Forge fluid storage on its bottom face");
        test.assertTrue(!tile.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.UP).isPresent(),
                "The chalice must restrict direct fluid access on its top face");

        FluidStack starlight = new FluidStack(FluidsAS.LIQUID_STARLIGHT.getSource().get(), 750);
        starlight.getOrCreateTag().putString("test_variant", "tagged");
        var accessibleTank = down.orElseThrow(IllegalStateException::new);
        test.assertTrue(accessibleTank.fill(starlight, IFluidHandler.FluidAction.EXECUTE) == 750,
                "Fluid automation must accept the full amount");
        CompoundTag save = tile.saveWithoutMetadata();
        test.setBlock(pos, Blocks.AIR);
        test.assertTrue(!down.isPresent(), "Removing the chalice must invalidate its capability");

        test.setBlock(pos, BlocksAS.CHALICE.get());
        var restored = test.getBlockEntity(pos);
        restored.load(save);
        var restoredTank = restored.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.DOWN).orElseThrow(IllegalStateException::new);
        FluidStack drained = restoredTank.drain(300, IFluidHandler.FluidAction.EXECUTE);
        test.assertTrue(drained.getAmount() == 300 && drained.isFluidEqual(starlight),
                "A partial drain after loading must retain fluid type and NBT");
        test.assertTrue(restoredTank.getFluidInTank(0).getAmount() == 450, "The exact remaining fluid amount must persist");
        test.succeed();
    }
}
