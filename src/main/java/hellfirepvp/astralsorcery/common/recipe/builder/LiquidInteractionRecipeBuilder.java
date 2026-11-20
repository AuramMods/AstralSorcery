/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.builder;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.LiquidInteractionRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.result.LiquidInteractionResult;
import hellfirepvp.astralsorcery.datagen.data.AstralFinishedRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import hellfirepvp.astralsorcery.common.ingredient.SizedFluidIngredient;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidInteractionRecipeBuilder
 * Created by HellFirePvP
 * Date: 11.04.2026
 */
public class LiquidInteractionRecipeBuilder implements RecipeBuilder {

    private String group = "";
    private final Map<String, CriterionTriggerInstance> criteria = new LinkedHashMap<>();

    private final String recipeName;
    private final SizedFluidIngredient reactantA;
    private final SizedFluidIngredient reactantB;
    private final LiquidInteractionResult result;

    private float chanceConsumeA = 1.0F;
    private float chanceConsumeB = 1.0F;
    private int weight = 1;

    private LiquidInteractionRecipeBuilder(String recipeName, SizedFluidIngredient reactantA, SizedFluidIngredient reactantB, LiquidInteractionResult result) {
        this.recipeName = recipeName;
        this.reactantA = reactantA;
        this.reactantB = reactantB;
        this.result = result;
    }

    public static LiquidInteractionRecipeBuilder builder(String recipeName, SizedFluidIngredient reactantA, SizedFluidIngredient reactantB, LiquidInteractionResult result) {
        return new LiquidInteractionRecipeBuilder(recipeName, reactantA, reactantB, result);
    }

    public LiquidInteractionRecipeBuilder chanceConsumeA(float chance) {
        this.chanceConsumeA = chance;
        return this;
    }

    public LiquidInteractionRecipeBuilder chanceConsumeB(float chance) {
        this.chanceConsumeB = chance;
        return this;
    }

    public LiquidInteractionRecipeBuilder weight(int weight) {
        this.weight = weight;
        return this;
    }

    @Override
    public LiquidInteractionRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public LiquidInteractionRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName == null ? "" : groupName;
        return this;
    }

    @Override
    public Item getResult() {
        return Items.AIR;
    }

    @Override
    public void save(Consumer<FinishedRecipe> recipeOutput) {
        this.save(recipeOutput, AstralSorcery.key(this.recipeName));
    }

    @Override
    public void save(Consumer<FinishedRecipe> recipeOutput, ResourceLocation id) {
        id = AstralSorcery.key(id.getPath());
        ResourceLocation recipeId = id.withPrefix("liquid_interaction/");
        Advancement.Builder advancementBuilder = AstralFinishedRecipe.advancement(recipeId);
        this.criteria.forEach(advancementBuilder::addCriterion);

        LiquidInteractionRecipe recipe = new LiquidInteractionRecipe(
                this.reactantA,
                this.reactantB,
                this.chanceConsumeA,
                this.chanceConsumeB,
                this.weight,
                this.result);
        recipe.setGroup(this.group);
        recipeOutput.accept(new AstralFinishedRecipe<>(recipeId, recipe, LiquidInteractionRecipe.CODEC,
                advancementBuilder));
    }
}
