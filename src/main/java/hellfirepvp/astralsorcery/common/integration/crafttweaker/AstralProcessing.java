package hellfirepvp.astralsorcery.common.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.base.IRuntimeAction;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.tag.type.KnownTag;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.recipe.ProcessingRules;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import org.openzen.zencode.java.ZenCodeType;

import java.util.List;

@ZenRegister
@ZenCodeType.Name("mods.astralsorcery.AstralProcessing")
public final class AstralProcessing {

    private AstralProcessing() {}

    @ZenCodeType.Method
    public static void configure(String operation, boolean enabled,
                           @ZenCodeType.Optional @ZenCodeType.Nullable IIngredient input,
                           @ZenCodeType.Optional @ZenCodeType.Nullable IItemStack output,
                           @ZenCodeType.OptionalFloat(1) float rateMultiplier,
                           @ZenCodeType.Optional @ZenCodeType.Nullable KnownTag<BaseConstellation> constellations) {
        var type = ProcessingRules.Operation.fromId(AstralRecipeManager.exactId(operation));
        var rule = new ProcessingRules.Rule(enabled, input == null ? null : input.asVanillaIngredient(),
                constellations == null ? null : TagKey.create(RegistriesAS.KEY_CONSTELLATIONS, constellations.id()),
                output == null ? ItemStack.EMPTY : output.getImmutableInternal(), rateMultiplier);
        CraftTweakerAPI.apply(new ProcessingAction(type, rule));
    }

    @ZenCodeType.Method
    public static void remove(String operation) {
        CraftTweakerAPI.apply(new ProcessingAction(ProcessingRules.Operation.fromId(AstralRecipeManager.exactId(operation)), null));
    }

    @ZenCodeType.Method
    public static List<String> configuredOperations() {
        return ProcessingRules.configured(loadingRecipes()).keySet().stream().map(operation -> operation.id.toString()).sorted().toList();
    }

    @ZenCodeType.Method
    public static boolean isEnabled(String operation) {
        return ProcessingRules.get(loadingRecipes(), ProcessingRules.Operation.fromId(AstralRecipeManager.exactId(operation))).enabled();
    }

    static RecipeManager loadingRecipes() {
        return CraftTweakerAPI.getAccessibleElementsProvider().server().resources().getRecipeManager();
    }

    private record ProcessingAction(ProcessingRules.Operation operation, ProcessingRules.Rule rule) implements IRuntimeAction {
        @Override
        public void apply() {
            RecipeManager recipes = loadingRecipes();
            if (this.rule == null) {
                ProcessingRules.clear(recipes, this.operation);
            } else {
                ProcessingRules.set(recipes, this.operation, this.rule);
            }
        }

        @Override
        public String describe() {
            return (this.rule == null ? "Restoring default " : "Configuring ") + this.operation.id;
        }

        @Override
        public String systemName() {
            return AstralSorcery.MODID;
        }
    }
}
