package hellfirepvp.astralsorcery.common.recipe;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.tile.AltarBlock;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.integration.gamestages.GameStagesBridge;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarRecipe;
import hellfirepvp.astralsorcery.common.recipe.altar.output.AltarOutputSetBlock;
import hellfirepvp.astralsorcery.common.recipe.focal.drop.FocalCombineRecipe;
import hellfirepvp.astralsorcery.common.recipe.focal.place.FocalTransmutationRecipe;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenCrystallizationRecipe;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenGenerationRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class StageRules {

    private static final Map<RecipeManager, Map<ResourceLocation, Rule>> RULES = new WeakHashMap<>();
    private static final String GAME_STAGES = "gamestages";

    private StageRules() {}

    public static ResourceLocation selectorId(String name) {
        ResourceLocation id = ResourceLocation.tryParse(name);
        if (id == null || name.indexOf(':') <= 0 || id.getPath().isEmpty()) {
            throw new IllegalArgumentException("Expected a namespaced stage rule selector, got '" + name + "'");
        }
        return id;
    }

    public enum Target {
        RECIPE, RECIPE_TYPE, FEATURE, OUTPUT_LUMEN_TAG, CONSTELLATION_TAG
    }

    public enum Feature {
        ALTAR_UPGRADE("altar_upgrade"),
        PERK_ALLOCATION("perk_allocation"),
        PERK_SOCKETING("perk_socketing"),
        GATEWAY_TRAVEL("gateway_travel"),
        RESONATING_WAND("resonating_wand"),
        ILLUMINATION_WAND("illumination_wand"),
        ARCHITECT_WAND("architect_wand"),
        EXCHANGE_WAND("exchange_wand"),
        BLINK_WAND("blink_wand"),
        GRAPPLING_WAND("grappling_wand"),
        ARTIFACT_INTERACTION("artifact_interaction"),
        LUMEN_BINDING("lumen_binding"),
        LUMEN_USE("lumen_use");

        private final ResourceLocation id;

        Feature(String id) {
            this.id = AstralSorcery.key(id);
        }

        public ResourceLocation id() {
            return this.id;
        }
    }

    public record Rule(ResourceLocation id, Target target, ResourceLocation selection,
                       List<String> stages, boolean any, boolean automation) {

        public Rule {
            stages = List.copyOf(stages);
            if (!ModList.get().isLoaded(GAME_STAGES)) {
                throw new IllegalStateException("Install GameStages 15.0.2 and Bookshelf 20.2.15 to configure Astral stage rule " + id);
            }
            if (stages.isEmpty() || stages.stream().anyMatch(stage -> !GameStagesBridge.validStage(stage))) {
                throw new IllegalArgumentException("Expected one or more valid GameStages names for rule " + id + ": " + stages);
            }
            if (target == Target.FEATURE
                    && Arrays.stream(Feature.values()).noneMatch(feature -> feature.id().equals(selection))
                    && Arrays.stream(ProcessingRules.Operation.values()).noneMatch(operation -> operation.id.equals(selection))) {
                throw new IllegalArgumentException("Unknown Astral feature " + selection);
            }
            if (target == Target.RECIPE_TYPE
                    && (!AstralSorcery.MODID.equals(selection.getNamespace()) || !BuiltInRegistries.RECIPE_TYPE.containsKey(selection))) {
                throw new IllegalArgumentException("Unknown Astral processing recipe type " + selection);
            }
        }

        boolean accepts(StageActor actor) {
            return actor.player() == null ? !actor.attributed() && this.automation
                    : GameStagesBridge.hasStages(actor.player(), this.stages, this.any);
        }

        public String description() {
            return this.id + " " + this.target + " " + this.selection + " " + (this.any ? "any" : "all")
                    + " " + this.stages + " automation=" + this.automation;
        }
    }

    public static synchronized void set(RecipeManager recipes, Rule rule) {
        var rules = new HashMap<>(RULES.getOrDefault(recipes, Map.of()));
        rules.put(rule.id(), rule);
        RULES.put(recipes, Map.copyOf(rules));
    }

    public static synchronized void remove(RecipeManager recipes, ResourceLocation id) {
        var rules = new HashMap<>(RULES.getOrDefault(recipes, Map.of()));
        rules.remove(id);
        RULES.put(recipes, Map.copyOf(rules));
    }

    public static synchronized Map<ResourceLocation, Rule> configured(RecipeManager recipes) {
        return RULES.getOrDefault(recipes, Map.of());
    }

    public static boolean hasRules(Level level) {
        return !level.isClientSide() && !configured(level.getRecipeManager()).isEmpty();
    }

    public static boolean isCurrent(Level level, CustomRecipe<?, ?> recipe) {
        return level.getRecipeManager().byKey(recipe.getId()).orElse(null) == recipe;
    }

    public static boolean allowsRecipe(Level level, CustomRecipe<?, ?> recipe, StageActor actor) {
        return deniedRecipe(level, recipe, actor) == null;
    }

    public static boolean allowsFeature(Level level, ResourceLocation feature, StageActor actor) {
        return allowsFeature(level, feature, null, actor);
    }

    public static boolean allowsFeature(Level level, ResourceLocation feature, @Nullable BaseConstellation constellation, StageActor actor) {
        return denied(level, null, feature, constellation, actor) == null;
    }

    public static boolean checkFeature(ServerPlayer player, Feature feature) {
        Rule failure = denied(player.level(), null, feature.id(), null, StageActor.fromPlayer(player));
        if (failure == null) return true;
        inform(player, failure);
        return false;
    }

    public static boolean checkRecipe(ServerPlayer player, CustomRecipe<?, ?> recipe) {
        Rule failure = deniedRecipe(player.level(), recipe, StageActor.fromPlayer(player));
        if (failure == null) return true;
        inform(player, failure);
        return false;
    }

    private static void inform(ServerPlayer player, Rule rule) {
        player.displayClientMessage(Component.translatable(rule.any ? "message.astralsorcery.stage_any" : "message.astralsorcery.stage_all",
                String.join(", ", rule.stages)), true);
    }

    @Nullable
    private static Rule deniedRecipe(Level level, CustomRecipe<?, ?> recipe, StageActor actor) {
        Rule failure = denied(level, recipe, null, null, actor);
        if (failure == null && hasRules(level) && recipe instanceof AltarRecipe altar
                && altar.getOutputModifiers().stream()
                    .filter(AltarOutputSetBlock.class::isInstance)
                    .map(AltarOutputSetBlock.class::cast)
                    .flatMap(modifier -> modifier.getBlockStates().stream())
                    .anyMatch(result -> result.getData().getBlock() instanceof AltarBlock)) {
            failure = denied(level, null, Feature.ALTAR_UPGRADE.id(), null, actor);
        }
        return failure;
    }

    @Nullable
    private static Rule denied(Level level, @Nullable CustomRecipe<?, ?> recipe, @Nullable ResourceLocation feature,
                               @Nullable BaseConstellation constellation, StageActor actor) {
        if (level.isClientSide()) return null;
        var rules = configured(level.getRecipeManager());
        if (rules.isEmpty()) return null;
        return rules.values().stream().filter(rule -> matches(rule, recipe, feature, constellation) && !rule.accepts(actor))
                .min(Comparator.comparing(Rule::id)).orElse(null);
    }

    private static boolean matches(Rule rule, @Nullable CustomRecipe<?, ?> recipe, @Nullable ResourceLocation feature,
                                   @Nullable BaseConstellation constellation) {
        return switch (rule.target) {
            case FEATURE -> rule.selection.equals(feature);
            case RECIPE -> recipe != null && rule.selection.equals(recipe.getId());
            case RECIPE_TYPE -> recipe != null && rule.selection.equals(recipe.getRecipeType().holder().getId());
            case OUTPUT_LUMEN_TAG -> {
                Lumen lumen = recipe instanceof LumenGenerationRecipe generation ? generation.getProducedLumen()
                        : recipe instanceof LumenCrystallizationRecipe crystallization ? crystallization.getLumenToCrystallize() : null;
                yield lumen != null && inTag(RegistriesAS.KEY_LUMEN, lumen, rule.selection);
            }
            case CONSTELLATION_TAG -> {
                if (constellation != null) {
                    yield inTag(RegistriesAS.KEY_CONSTELLATIONS, constellation, rule.selection);
                }
                List<BaseConstellation> requirements = recipe instanceof AltarRecipe altar ? altar.getFocusConstellation().stream().toList()
                        : recipe instanceof FocalCombineRecipe focal ? focal.getRequiredConstellations()
                        : recipe instanceof FocalTransmutationRecipe focal ? focal.getRequiredConstellations() : List.of();
                yield requirements.stream().anyMatch(cst -> inTag(RegistriesAS.KEY_CONSTELLATIONS, cst, rule.selection));
            }
        };
    }

    private static <T> boolean inTag(ResourceKey<Registry<T>> key, T entry, ResourceLocation tag) {
        Registry<T> registry = RegistriesAS.registry(key);
        return registry.getResourceKey(entry).flatMap(registry::getHolder)
                .filter(holder -> holder.is(TagKey.create(key, tag))).isPresent();
    }
}
