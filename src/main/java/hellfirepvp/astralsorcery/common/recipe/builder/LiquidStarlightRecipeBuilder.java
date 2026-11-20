/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.builder;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.output.LiquidStarlightRecipeOutputModifier;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.datagen.data.AstralFinishedRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import hellfirepvp.astralsorcery.common.ingredient.SizedIngredient;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidStarlightRecipeBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LiquidStarlightRecipeBuilder implements RecipeBuilder {

    private String group = "";
    private final Map<String, CriterionTriggerInstance> criteria = new LinkedHashMap<>();

    private final String recipeName;
    private final SizedIngredient input;
    private final List<SizedIngredient> otherInputs;

    private int duration = 60;
    private int randomAdditionalDuration = 20;
    private ColorWrapper color = ColorWrapper.WHITE;
    private final List<LiquidStarlightRecipeOutputModifier> outputModifiers = new ArrayList<>();
    private boolean consumesLiquid = false;
    private boolean consumesInputs = true;

    private LiquidStarlightRecipeBuilder(String recipeName, SizedIngredient input, List<SizedIngredient> otherInputs) {
        this.recipeName = recipeName;
        this.input = input;
        this.otherInputs = otherInputs;
    }

    public static LiquidStarlightRecipeBuilder builder(String recipeName, SizedIngredient input) {
        return builder(recipeName, input, List.of());
    }

    public static LiquidStarlightRecipeBuilder builder(String recipeName, SizedIngredient input, List<SizedIngredient> otherInputs) {
        return new LiquidStarlightRecipeBuilder(recipeName, input, otherInputs);
    }

    public LiquidStarlightRecipeBuilder duration(int ticks) {
        this.duration = ticks;
        return this;
    }

    public LiquidStarlightRecipeBuilder randomAdditionalDuration(int ticks) {
        this.randomAdditionalDuration = ticks;
        return this;
    }

    public LiquidStarlightRecipeBuilder color(ColorWrapper color) {
        this.color = color;
        return this;
    }

    public LiquidStarlightRecipeBuilder addOutputModifier(LiquidStarlightRecipeOutputModifier modifier) {
        this.outputModifiers.add(modifier);
        return this;
    }

    public LiquidStarlightRecipeBuilder consumesLiquid() {
        this.consumesLiquid = true;
        return this;
    }

    public LiquidStarlightRecipeBuilder doesntConsumeInputs() {
        this.consumesInputs = false;
        return this;
    }

    @Override
    public LiquidStarlightRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public LiquidStarlightRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
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
        ResourceLocation recipeId = id.withPrefix("liquid_starlight/");
        Advancement.Builder advancementBuilder = AstralFinishedRecipe.advancement(recipeId);
        this.criteria.forEach(advancementBuilder::addCriterion);

        LiquidStarlightRecipe recipe = new LiquidStarlightRecipe(
                this.input,
                this.otherInputs,
                this.duration,
                this.randomAdditionalDuration,
                this.color,
                this.outputModifiers,
                this.consumesLiquid,
                this.consumesInputs
        );
        recipe.setGroup(this.group);
        recipeOutput.accept(new AstralFinishedRecipe<>(recipeId, recipe, LiquidStarlightRecipe.CODEC,
                id.withPrefix("recipes/"), advancementBuilder));
    }
}
