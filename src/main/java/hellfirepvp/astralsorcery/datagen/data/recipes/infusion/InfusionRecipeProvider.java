/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipes.infusion;

import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.recipe.builder.InfusionRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: InfusionRecipeProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class InfusionRecipeProvider extends RecipeProvider {

    private InfusionRecipeProvider(PackOutput output) {
        super(output);
    }

    public static void registerRecipes(Consumer<FinishedRecipe> recipeOutput) {
        InfusionRecipeBuilder.builder(Ingredient.of(TagsAS.Items.GEMS_AQUAMARINE), FluidsAS.LIQUID_STARLIGHT.getSource(), ItemsAS.RESONATING_GEM.toStack())
                .setConsumptionChance(0.03F)
                .save(recipeOutput);
        InfusionRecipeBuilder.builder(Ingredient.of(ItemsAS.BLOCK_INFUSED_WOOD_RAW), FluidsAS.LIQUID_STARLIGHT.getSource(), ItemsAS.BLOCK_INFUSED_WOOD_INFUSED.toStack())
                .setConsumptionChance(0.015F)
                .save(recipeOutput);
        InfusionRecipeBuilder.builder(Ingredient.of(Tags.Items.GLASS_PANES), FluidsAS.LIQUID_STARLIGHT.getSource(), ItemsAS.GLASS_LENS.toStack())
                .setConsumptionChance(0.01F)
                .save(recipeOutput);
    }
}
