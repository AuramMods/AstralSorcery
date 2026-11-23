package hellfirepvp.astralsorcery.common.recipe;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.WeakHashMap;

public final class ProcessingRules {

    // A reload stages actions against a fresh recipe manager. The game reads rules
    // from the manager it adopted, even if a new reload is running or has failed.
    private static final Map<RecipeManager, Map<Operation, Rule>> RULES = new WeakHashMap<>();
    private static final Rule NONE = new Rule(true, null, null, ItemStack.EMPTY, 1);

    private ProcessingRules() {}

    public enum Operation {
        PLAYER_ATTUNEMENT("player_attunement", false, true, false, false),
        ITEM_ATTUNEMENT("item_attunement", true, true, true, false),
        CHISEL_STARMETAL("chisel_starmetal", true, false, true, true),
        CHISEL_CRYSTAL("chisel_crystal", true, false, false, true),
        CHISEL_ARTIFACT("chisel_artifact", true, false, false, true),
        CELESTIAL_CLUSTER_GROWTH("celestial_cluster_growth", false, false, false, true),
        GEM_CLUSTER_GROWTH("gem_cluster_growth", false, false, false, true),
        LUMEN_CLUSTER_GROWTH("lumen_cluster_growth", false, false, false, true);

        public final ResourceLocation id;
        public final boolean acceptsInputFilter;
        public final boolean acceptsConstellationFilter;
        public final boolean acceptsOutputOverride;
        public final boolean acceptsRateMultiplier;

        Operation(String id, boolean acceptsInputFilter, boolean acceptsConstellationFilter,
                  boolean acceptsOutputOverride, boolean acceptsRateMultiplier) {
            this.id = AstralSorcery.key(id);
            this.acceptsInputFilter = acceptsInputFilter;
            this.acceptsConstellationFilter = acceptsConstellationFilter;
            this.acceptsOutputOverride = acceptsOutputOverride;
            this.acceptsRateMultiplier = acceptsRateMultiplier;
        }

        public static Operation fromId(ResourceLocation id) {
            return Arrays.stream(values()).filter(operation -> operation.id.equals(id)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown Astral processing operation " + id));
        }
    }

    public record Rule(boolean enabled, @Nullable Ingredient input, @Nullable TagKey<BaseConstellation> constellationTag,
                       ItemStack output, float rateMultiplier) {

        public Rule {
            output = output.copy();
            if (!Float.isFinite(rateMultiplier) || rateMultiplier < 0) {
                throw new IllegalArgumentException("rateMultiplier must be a finite, non-negative number");
            }
        }

        public boolean allows(ItemStack stack) {
            return this.enabled && (this.input == null || this.input.test(stack));
        }

        public boolean allows(BaseConstellation constellation) {
            if (!this.enabled) {
                return false;
            }
            if (this.constellationTag == null) {
                return true;
            }
            var registry = RegistriesAS.REGISTRY_CONSTELLATIONS.get();
            return registry.getResourceKey(constellation).flatMap(registry::getHolder)
                    .filter(holder -> holder.is(this.constellationTag)).isPresent();
        }

        @Override
        public ItemStack output() {
            return this.output.copy();
        }

        public float chance(float base) {
            return Math.min(1, base * this.rateMultiplier);
        }

        public boolean roll(RandomSource random, int denominator) {
            return this.enabled && this.rateMultiplier > 0
                    && random.nextInt(Math.max(1, Mth.ceil(denominator / this.rateMultiplier))) == 0;
        }
    }

    public static Rule get(Level level, Operation operation) {
        return get(level.getRecipeManager(), operation);
    }

    public static synchronized Rule get(RecipeManager manager, Operation operation) {
        return RULES.getOrDefault(manager, Collections.emptyMap()).getOrDefault(operation, NONE);
    }

    public static synchronized Map<Operation, Rule> configured(RecipeManager manager) {
        return Map.copyOf(RULES.getOrDefault(manager, Collections.emptyMap()));
    }

    public static synchronized void set(RecipeManager manager, Operation operation, Rule rule) {
        if (rule.input() != null && !operation.acceptsInputFilter
                || rule.constellationTag() != null && !operation.acceptsConstellationFilter
                || !rule.output().isEmpty() && !operation.acceptsOutputOverride
                || rule.rateMultiplier() != 1 && !operation.acceptsRateMultiplier) {
            throw new IllegalArgumentException("Unsupported rule field for Astral processing operation " + operation.id);
        }
        RULES.computeIfAbsent(manager, key -> new EnumMap<>(Operation.class)).put(operation, rule);
    }

    public static synchronized void clear(RecipeManager manager, Operation operation) {
        var rules = RULES.get(manager);
        if (rules != null) {
            rules.remove(operation);
        }
    }
}
