package hellfirepvp.astralsorcery.common.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.base.IRuntimeAction;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.recipe.StageRules;
import net.minecraft.resources.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

import java.util.List;
import java.util.Locale;

@ZenRegister
@ZenCodeType.Name("mods.astralsorcery.AstralStages")
public final class AstralStages {

    private AstralStages() {}

    @ZenCodeType.Method
    public static void require(String name, String target, String selector, String[] stages,
                               @ZenCodeType.OptionalBoolean boolean any, @ZenCodeType.OptionalBoolean boolean allowAutomation) {
        var id = AstralRecipeManager.exactId(name);
        var kind = StageRules.Target.valueOf(target.toUpperCase(Locale.ROOT));
        var rule = new StageRules.Rule(id, kind, AstralRecipeManager.exactId(selector), List.of(stages), any, allowAutomation);
        CraftTweakerAPI.apply(new StageAction(id, rule));
    }

    @ZenCodeType.Method
    public static void remove(String name) {
        CraftTweakerAPI.apply(new StageAction(AstralRecipeManager.exactId(name), null));
    }

    @ZenCodeType.Method
    public static List<String> getRules() {
        return StageRules.configured(AstralProcessing.loadingRecipes()).values().stream()
                .map(StageRules.Rule::description).sorted().toList();
    }

    private record StageAction(ResourceLocation id, StageRules.Rule rule) implements IRuntimeAction {
        @Override
        public void apply() {
            var recipes = AstralProcessing.loadingRecipes();
            if (this.rule == null) {
                StageRules.remove(recipes, this.id);
            } else {
                StageRules.set(recipes, this.rule);
            }
        }

        @Override
        public String describe() {
            return this.rule == null ? "Removing Astral stage rule " + this.id : "Configuring Astral stage rule " + this.rule.description();
        }

        @Override
        public String systemName() {
            return AstralSorcery.MODID;
        }
    }
}
