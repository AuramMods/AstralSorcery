/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.builder;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.recipe.focal.place.FocalTransmutationRecipe;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.datagen.data.AstralFinishedRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalTransmutationRecipeBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocalTransmutationRecipeBuilder implements RecipeBuilder {

    private String group = "";
    private final Map<String, CriterionTriggerInstance> criteria = new LinkedHashMap<>();

    private int duration = 100;
    private ColorWrapper color = ColorWrapper.WHITE;
    private final List<BlockPredicate> inputPredicates = new ArrayList<>();
    private Ingredient inputDisplay = Ingredient.EMPTY;
    private final List<WeightedEntry.Wrapper<BlockState>> outputStates = new ArrayList<>();
    private BaseConstellation requiredConstellation = null;
    private boolean requiresFocusedStarlight = false;

    private FocalTransmutationRecipeBuilder() {}

    public static FocalTransmutationRecipeBuilder builder() {
        return new FocalTransmutationRecipeBuilder();
    }

    public FocalTransmutationRecipeBuilder duration(int craftingTickTime) {
        this.duration = craftingTickTime;
        return this;
    }

    public FocalTransmutationRecipeBuilder color(ColorWrapper color) {
        this.color = color;
        return this;
    }

    public FocalTransmutationRecipeBuilder input(BlockPredicate input) {
        this.inputPredicates.add(input);
        return this;
    }

    public FocalTransmutationRecipeBuilder inputDisplay(ItemLike displayStack) {
        return this.inputDisplay(Ingredient.of(displayStack));
    }

    public FocalTransmutationRecipeBuilder inputDisplay(ItemStack displayStack) {
        return this.inputDisplay(Ingredient.of(displayStack));
    }

    public FocalTransmutationRecipeBuilder inputDisplay(TagKey<Item> displayTag) {
        return this.inputDisplay(Ingredient.of(displayTag));
    }

    public FocalTransmutationRecipeBuilder inputDisplay(Ingredient display) {
        this.inputDisplay = display;
        return this;
    }

    public FocalTransmutationRecipeBuilder outputs(Block output, int weight) {
        return this.outputs(output.defaultBlockState(), weight);
    }

    public FocalTransmutationRecipeBuilder outputs(BlockState output, int weight) {
        this.outputStates.add(WeightedEntry.wrap(output, weight));
        return this;
    }

    public FocalTransmutationRecipeBuilder requiresConstellation(BaseConstellation constellation) {
        this.requiredConstellation = constellation;
        return this;
    }

    public FocalTransmutationRecipeBuilder requiresFocusedStarlight() {
        this.requiresFocusedStarlight = true;
        return this;
    }

    @Override
    public FocalTransmutationRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public FocalTransmutationRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public Item getResult() {
        return this.outputStates.stream()
                .map(WeightedEntry.Wrapper::getData)
                .map(state -> state.getBlock().asItem())
                .filter(item -> item != Items.AIR)
                .findFirst()
                .orElse(Items.AIR);
    }

    @Override
    public void save(Consumer<FinishedRecipe> recipeOutput, ResourceLocation id) {
        id = AstralSorcery.key(id.getPath());
        ResourceLocation recipeId = id.withPrefix("focal_transmutation/");
        Advancement.Builder advancementBuilder = AstralFinishedRecipe.advancement(recipeId);
        this.criteria.forEach(advancementBuilder::addCriterion);

        FocalTransmutationRecipe recipe = new FocalTransmutationRecipe(
                this.duration,
                this.color,
                this.inputPredicates,
                this.inputDisplay,
                this.outputStates,
                Optional.ofNullable(this.requiredConstellation),
                this.requiresFocusedStarlight
        );
        recipe.setGroup(this.group);
        recipeOutput.accept(new AstralFinishedRecipe<>(recipeId, recipe, FocalTransmutationRecipe.CODEC,
                id.withPrefix("recipes/"), advancementBuilder));
    }
}
