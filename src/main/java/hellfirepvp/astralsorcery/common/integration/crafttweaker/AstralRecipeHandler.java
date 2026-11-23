package hellfirepvp.astralsorcery.common.integration.crafttweaker;

import com.blamejared.crafttweaker.api.recipe.component.IDecomposedRecipe;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker.api.util.StringUtil;
import hellfirepvp.astralsorcery.common.recipe.CustomRecipe;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarRecipe;
import hellfirepvp.astralsorcery.common.recipe.focal.drop.FocalCombineRecipe;
import hellfirepvp.astralsorcery.common.recipe.focal.place.FocalTransmutationRecipe;
import hellfirepvp.astralsorcery.common.recipe.infusion.InfusionRecipe;
import hellfirepvp.astralsorcery.common.recipe.lightwell.LightwellRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.LiquidInteractionRecipe;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenCrystallizationRecipe;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenGenerationRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Optional;

@IRecipeHandler.For(AltarRecipe.class)
@IRecipeHandler.For(FocalCombineRecipe.class)
@IRecipeHandler.For(FocalTransmutationRecipe.class)
@IRecipeHandler.For(InfusionRecipe.class)
@IRecipeHandler.For(LightwellRecipe.class)
@IRecipeHandler.For(LiquidInteractionRecipe.class)
@IRecipeHandler.For(LiquidStarlightRecipe.class)
@IRecipeHandler.For(LumenCrystallizationRecipe.class)
@IRecipeHandler.For(LumenGenerationRecipe.class)
public final class AstralRecipeHandler implements IRecipeHandler<CustomRecipe<?, ?>> {

    @Override
    public String dumpToCommandString(IRecipeManager<? super CustomRecipe<?, ?>> manager, CustomRecipe<?, ?> recipe) {
        return manager.getCommandString() + ".setJsonRecipe(" + StringUtil.quoteAndEscape(recipe.getId()) +
                ", " + StringUtil.quoteAndEscape(AstralRecipeManager.encode(recipe).toString()) + ");";
    }

    @Override
    public <U extends Recipe<?>> boolean doesConflict(IRecipeManager<? super CustomRecipe<?, ?>> manager,
                                                      CustomRecipe<?, ?> first, U second) {
        return second instanceof CustomRecipe<?, ?> astral
                && AstralRecipeManager.encode(first).equals(AstralRecipeManager.encode(astral));
    }

    @Override
    public Optional<IDecomposedRecipe> decompose(IRecipeManager<? super CustomRecipe<?, ?>> manager, CustomRecipe<?, ?> recipe) {
        return Optional.empty();
    }

    @Override
    public Optional<CustomRecipe<?, ?>> recompose(IRecipeManager<? super CustomRecipe<?, ?>> manager, ResourceLocation name,
                                                  IDecomposedRecipe recipe) {
        return Optional.empty();
    }
}
