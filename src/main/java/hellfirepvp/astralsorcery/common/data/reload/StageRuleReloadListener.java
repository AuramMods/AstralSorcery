package hellfirepvp.astralsorcery.common.data.reload;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.recipe.StageRules;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

@Mod.EventBusSubscriber(modid = AstralSorcery.MODID)
public final class StageRuleReloadListener extends SimpleJsonResourceReloadListener {

    private final RecipeManager recipes;
    private final ICondition.IContext conditions;

    private StageRuleReloadListener(RecipeManager recipes, ICondition.IContext conditions) {
        super(new Gson(), "astral_stage_rules");
        this.recipes = recipes;
        this.conditions = conditions;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onReload(AddReloadListenerEvent event) {
        event.addListener(new StageRuleReloadListener(event.getServerResources().getRecipeManager(), event.getConditionContext()));
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> definitions, ResourceManager resources, ProfilerFiller profiler) {
        definitions.forEach((id, definition) -> {
            try {
                var json = definition.getAsJsonObject();
                if (!CraftingHelper.processConditions(json, "conditions", this.conditions)) return;
                var target = StageRules.Target.valueOf(GsonHelper.getAsString(json, "target").toUpperCase(Locale.ROOT));
                var selector = StageRules.selectorId(GsonHelper.getAsString(json, "selector"));
                var stages = new ArrayList<String>();
                GsonHelper.getAsJsonArray(json, "stages").forEach(value -> stages.add(value.getAsString()));
                StageRules.set(this.recipes, new StageRules.Rule(id, target, selector, stages,
                        GsonHelper.getAsBoolean(json, "any", false),
                        GsonHelper.getAsBoolean(json, "allow_automation", false)));
            } catch (RuntimeException error) {
                AstralSorcery.LOG.error("Invalid Astral stage rule {}: {}", id, error.getMessage());
            }
        });
    }
}
