/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.builder;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenGenerationRecipe;
import hellfirepvp.astralsorcery.datagen.data.AstralFinishedRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenGenerationRecipeBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenGenerationRecipeBuilder implements RecipeBuilder {

    private String group = "";
    private final Map<String, CriterionTriggerInstance> criteria = new LinkedHashMap<>();

    private final Ingredient input;
    private final Lumen producedLumen;
    private final String recipeIdSuffix;

    private int producedLumenAmount = 1;
    private float productionAttemptMultiplier = 1F;
    private float attemptStarlightConsumption = 1F;
    private float catalystShatterMultiplier = 1F;
    private final Map<Lumen, Integer> lumenCombinationInputs = new LinkedHashMap<>();

    private LumenGenerationRecipeBuilder(Ingredient input, Lumen producedLumen, String recipeIdSuffix) {
        this.input = input;
        this.producedLumen = producedLumen;
        this.recipeIdSuffix = recipeIdSuffix;
    }

    public static LumenGenerationRecipeBuilder builder(Ingredient input, Lumen producedLumen) {
        return builder(input, producedLumen, "");
    }

    public static LumenGenerationRecipeBuilder builder(Ingredient input, Lumen producedLumen, String recipeIdSuffix) {
        return new LumenGenerationRecipeBuilder(input, producedLumen, recipeIdSuffix);
    }

    public LumenGenerationRecipeBuilder producedLumenAmount(int amount) {
        this.producedLumenAmount = amount;
        return this;
    }

    public LumenGenerationRecipeBuilder productionAttemptMultiplier(float multiplier) {
        this.productionAttemptMultiplier = multiplier;
        return this;
    }

    public LumenGenerationRecipeBuilder attemptStarlightConsumption(float consumption) {
        this.attemptStarlightConsumption = consumption;
        return this;
    }

    public LumenGenerationRecipeBuilder catalystShatterMultiplier(float multiplier) {
        this.catalystShatterMultiplier = multiplier;
        return this;
    }

    public LumenGenerationRecipeBuilder addLumenCombinationInput(Lumen lumen, int amount) {
        this.lumenCombinationInputs.put(lumen, amount);
        return this;
    }

    @Override
    public LumenGenerationRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public LumenGenerationRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public Item getResult() {
        return Items.AIR;
    }

    @Override
    public void save(Consumer<FinishedRecipe> recipeOutput) {
        ResourceLocation id = RegistriesAS.REGISTRY_LUMEN.get().getKey(this.producedLumen);
        this.save(recipeOutput, id.withSuffix(this.recipeIdSuffix));
    }

    @Override
    public void save(Consumer<FinishedRecipe> recipeOutput, ResourceLocation id) {
        id = AstralSorcery.key(id.getPath());
        ResourceLocation recipeId = id.withPrefix("lumen/");
        Advancement.Builder advancementBuilder = AstralFinishedRecipe.advancement(recipeId);
        this.criteria.forEach(advancementBuilder::addCriterion);

        LumenGenerationRecipe recipe = new LumenGenerationRecipe(
                this.input,
                this.producedLumen,
                this.producedLumenAmount,
                this.productionAttemptMultiplier,
                this.attemptStarlightConsumption,
                this.catalystShatterMultiplier,
                this.lumenCombinationInputs
        );
        recipe.setGroup(this.group);
        recipeOutput.accept(new AstralFinishedRecipe<>(recipeId, recipe, LumenGenerationRecipe.CODEC,
                id.withPrefix("recipes/"), advancementBuilder));
    }
}
