/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.recipe.RecipeHolder;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarRecipe;
import hellfirepvp.astralsorcery.common.recipe.focal.drop.FocalCombineCraftingInput;
import hellfirepvp.astralsorcery.common.recipe.focal.drop.FocalCombineRecipe;
import hellfirepvp.astralsorcery.common.recipe.focal.place.FocalTransmutationCraftingInput;
import hellfirepvp.astralsorcery.common.recipe.focal.place.FocalTransmutationRecipe;
import hellfirepvp.astralsorcery.common.recipe.lightwell.LightwellRecipe;
import hellfirepvp.astralsorcery.common.recipe.lightwell.LightwellRecipeInput;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipeInput;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenCrystallizationRecipe;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenCrystallizationRecipeInput;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenGenerationRecipeInput;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenGenerationRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RecipeFinder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RecipeFinder {

    private final Level level;
    private final RecipeManager mgr;

    private RecipeFinder(Level level, RecipeManager mgr) {
        this.level = level;
        this.mgr = mgr;
    }

    @OnlyIn(Dist.CLIENT)
    public static RecipeFinder clientFinder() {
        Level level = Minecraft.getInstance().level;
        if (level == null) throw new IllegalArgumentException("Cannot resolve recipes outside of a level.");
        return of(level);
    }

    public static RecipeFinder of(Level level) {
        return of(level, level.getRecipeManager());
    }

    public static RecipeFinder of(Level level, RecipeManager mgr) {
        return new RecipeFinder(level, mgr);
    }

    public Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> findSmeltingRecipe(ItemStack inputStack) {
        SimpleContainer input = new SimpleContainer(inputStack);
        for (RecipeType<? extends AbstractCookingRecipe> type :
                List.of(RecipeType.SMELTING, RecipeType.BLASTING, RecipeType.SMOKING, RecipeType.CAMPFIRE_COOKING)) {
            var recipe = this.mgr.getRecipeFor(type, input, this.level);
            if (recipe.isPresent()) {
                return recipe.map(RecipeHolder::of);
            }
        }
        return Optional.empty();
    }

    public Optional<RecipeHolder<FocalTransmutationRecipe>> findFocalTransmutationRecipe(BaseConstellation cst, BlockPos transmutationPos, boolean isFocused) {
        FocalTransmutationCraftingInput input = new FocalTransmutationCraftingInput(cst, this.level, transmutationPos, isFocused);
        return this.mgr.getRecipeFor(RecipeTypesAS.FOCAL_TRANSMUTATION_TYPE.get(), input, this.level).map(RecipeHolder::of);
    }

    public Optional<RecipeHolder<FocalCombineRecipe>> findFocalCombineRecipe(BaseConstellation cst, List<ItemEntity> combinableItems) {
        FocalCombineCraftingInput input = new FocalCombineCraftingInput(cst, this.level, combinableItems);
        return this.mgr.getRecipeFor(RecipeTypesAS.FOCAL_COMBINE_TYPE.get(), input, this.level).map(RecipeHolder::of);
    }

    public Optional<RecipeHolder<AltarRecipe>> findAltarRecipe(AltarCraftingInput input) {
        return this.mgr.getRecipeFor(RecipeTypesAS.ALTAR_CRAFTING_TYPE.get(), input, this.level).map(RecipeHolder::of);
    }

    public Optional<RecipeHolder<LumenGenerationRecipe>> findLumenGenerationRecipe(ItemStack inputStack, Lumen generatedLumen, boolean useCombinationRecipes) {
        LumenGenerationRecipeInput input = LumenGenerationRecipeInput.create(inputStack);
        if (generatedLumen.equals(LumenAS.NONE.get())) {
            return this.mgr.getRecipesFor(RecipeTypesAS.LUMEN_GENERATION_TYPE.get(), input, this.level).stream()
                    .filter(recipeHolder -> useCombinationRecipes != recipeHolder.getLumenCombinationInputs().isEmpty())
                    .findFirst().map(RecipeHolder::of);
        }
        return this.mgr.getRecipesFor(RecipeTypesAS.LUMEN_GENERATION_TYPE.get(), input, this.level).stream()
                .filter(recipeHolder -> useCombinationRecipes != recipeHolder.getLumenCombinationInputs().isEmpty())
                .filter(recipeHolder -> recipeHolder.getProducedLumen().equals(generatedLumen))
                .findFirst().map(RecipeHolder::of);
    }

    public Optional<RecipeHolder<LumenGenerationRecipe>> findLumenGenerationRecipeByOutput(Lumen generatedLumen) {
        return this.mgr.getAllRecipesFor(RecipeTypesAS.LUMEN_GENERATION_TYPE.get()).stream()
                .filter(recipeHolder -> recipeHolder.getProducedLumen().equals(generatedLumen))
                .findFirst().map(RecipeHolder::of);
    }

    public Optional<RecipeHolder<LightwellRecipe>> findLightwellRecipe(ItemStack inputStack, FluidStack existingFluid) {
        LightwellRecipeInput input = LightwellRecipeInput.of(inputStack, existingFluid);
        return this.mgr.getRecipeFor(RecipeTypesAS.LIGHTWELL_TYPE.get(), input, this.level).map(RecipeHolder::of);
    }

    public Optional<RecipeHolder<LiquidStarlightRecipe>> findLiquidStarlightRecipe(ItemEntity itemEntity) {
        LiquidStarlightRecipeInput input = LiquidStarlightRecipeInput.of(itemEntity);
        return this.mgr.getRecipesFor(RecipeTypesAS.LIQUID_STARLIGHT_TYPE.get(), input, itemEntity.level()).stream()
                .min(Comparator.comparingInt((LiquidStarlightRecipe recipe) -> recipe.getOtherInputs().size())
                        .thenComparing(LiquidStarlightRecipe::getId))
                .map(RecipeHolder::of);
    }

    public Optional<RecipeHolder<LumenCrystallizationRecipe>> findCrystallizationRecipe(ItemStack stack) {
        LumenCrystallizationRecipeInput input = LumenCrystallizationRecipeInput.of(stack);
        return this.mgr.getRecipeFor(RecipeTypesAS.LUMEN_CRYSTALLIZATION_TYPE.get(), input, this.level).map(RecipeHolder::of);
    }

    public Optional<RecipeHolder<LumenCrystallizationRecipe>> findCrystallizationRecipe(Lumen lumen) {
        LumenCrystallizationRecipeInput input = LumenCrystallizationRecipeInput.of(lumen);
        return this.mgr.getRecipeFor(RecipeTypesAS.LUMEN_CRYSTALLIZATION_TYPE.get(), input, this.level).map(RecipeHolder::of);
    }

    public <I extends Container, T extends Recipe<I>> Optional<RecipeHolder<T>> findRecipe(RecipeType<T> type, ResourceLocation recipeId) {
        return this.mgr.getAllRecipesFor(type).stream()
                .filter(recipe -> recipe.getId().equals(recipeId))
                .findFirst().map(RecipeHolder::of);
    }

    public <I extends Container, T extends Recipe<I>> Optional<ResourceLocation> getRecipeId(T recipe) {
        RecipeType<T> type = MiscUtil.cast(recipe.getType());
        return this.mgr.getAllRecipesFor(type).stream()
                .filter(candidate -> candidate == recipe)
                .map(Recipe::getId)
                .findFirst();
    }
}
