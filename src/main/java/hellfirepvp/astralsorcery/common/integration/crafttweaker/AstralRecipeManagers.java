package hellfirepvp.astralsorcery.common.integration.crafttweaker;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.ingredient.IngredientBridge;
import hellfirepvp.astralsorcery.common.ingredient.SizedFluidIngredient;
import hellfirepvp.astralsorcery.common.ingredient.SizedIngredient;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarRecipe;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.recipe.focal.drop.FocalCombineRecipe;
import hellfirepvp.astralsorcery.common.recipe.focal.place.FocalTransmutationRecipe;
import hellfirepvp.astralsorcery.common.recipe.infusion.InfusionRecipe;
import hellfirepvp.astralsorcery.common.recipe.lightwell.LightwellRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.LiquidInteractionRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.result.LiquidInteractionResultDropItem;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.result.LiquidInteractionResultSpawnEntity;
import hellfirepvp.astralsorcery.common.recipe.liquid.output.LiquidStarlightOutputDropItem;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenCrystallizationRecipe;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenGenerationRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class AstralRecipeManagers {

    private AstralRecipeManagers() {}

    @ZenRegister
    @ZenCodeType.Name("mods.astralsorcery.recipe.Altar")
    public static final class Altar extends AstralRecipeManager {

        public Altar() {
            super(RecipeTypesAS.ALTAR_CRAFTING_TYPE);
        }

        @ZenCodeType.Method
        public void addRecipe(String name, String tier, String[] pattern, Map<String, IIngredient> key, IItemStack[] outputs,
                              int duration, @ZenCodeType.OptionalBoolean boolean onlyNight,
                              @ZenCodeType.OptionalBoolean boolean mayChain) {
            Map<Character, IngredientBridge> ingredients = new HashMap<>();
            key.forEach((symbol, ingredient) -> {
                if (symbol.length() != 1 || Character.isWhitespace(symbol.charAt(0))) {
                    throw new IllegalArgumentException("Altar key must be a single non-space character: " + symbol);
                }
                ingredients.put(symbol.charAt(0), IngredientBridge.of(input(ingredient)));
            });
            if (pattern.length < 1 || pattern.length > 3
                    || Arrays.stream(pattern).anyMatch(row -> row.length() < 1 || row.length() > 3)
                    || Arrays.stream(pattern).allMatch(String::isBlank)) {
                throw new IllegalArgumentException("Expected a non-empty altar layout within a 3×3 grid");
            }
            var altarTier = TileAltar.AltarType.valueOf(tier.toUpperCase(Locale.ROOT));
            String[] rows = {"   ", "   ", "   "};
            for (int row = 0; row < pattern.length; row++) {
                rows[row] = (pattern[row] + "   ").substring(0, 3);
            }
            var grid = AltarRecipeGrid.create(ingredients, List.of(rows), List.of("     ", "     ", "     ", "     ", "     "));
            this.add(name, new AltarRecipe(altarTier, grid, outputs(outputs), Optional.empty(), 0,
                    positive(duration, "duration"), onlyNight, mayChain, Set.of(), List.of(), List.of(),
                    List.of(), Set.of(), List.of()));
        }
    }

    @ZenRegister
    @ZenCodeType.Name("mods.astralsorcery.recipe.FocalCombine")
    public static final class FocalCombine extends AstralRecipeManager {

        public FocalCombine() {
            super(RecipeTypesAS.FOCAL_COMBINE_TYPE);
        }

        @ZenCodeType.Method
        public void addRecipe(String name, IIngredient[] inputs, IItemStack[] outputs, int duration,
                              @ZenCodeType.OptionalInt(-1) int color, @ZenCodeType.OptionalString String constellation) {
            if (inputs.length == 0) {
                throw new IllegalArgumentException("Focal combination needs at least one input");
            }
            this.add(name, new FocalCombineRecipe(positive(duration, "duration"), ColorWrapper.transparent(color),
                    Arrays.stream(inputs).map(AstralRecipeManagers::input).toList(), outputs(outputs),
                    constellation(constellation)));
        }
    }

    @ZenRegister
    @ZenCodeType.Name("mods.astralsorcery.recipe.FocalTransmutation")
    public static final class FocalTransmutation extends AstralRecipeManager {

        public FocalTransmutation() {
            super(RecipeTypesAS.FOCAL_TRANSMUTATION_TYPE);
        }

        @ZenCodeType.Method
        public void addRecipe(String name, Block inputBlock, BlockState result, int duration,
                              @ZenCodeType.OptionalInt(-1) int color, @ZenCodeType.OptionalString String constellation,
                              @ZenCodeType.OptionalBoolean boolean requiresFocusedStarlight) {
            this.add(name, new FocalTransmutationRecipe(positive(duration, "duration"), ColorWrapper.transparent(color),
                    List.of(BlockPredicate.matchesBlocks(inputBlock)), Ingredient.of(inputBlock),
                    List.of(WeightedEntry.wrap(result, 1)), constellation(constellation), requiresFocusedStarlight));
        }

        @ZenCodeType.Method
        public List<String> getIdsByOutputBlock(Block block) {
            return this.findIds(recipe -> ((FocalTransmutationRecipe) recipe).getOutputStates().stream()
                    .anyMatch(output -> output.getData().is(block)));
        }
    }

    @ZenRegister
    @ZenCodeType.Name("mods.astralsorcery.recipe.Infusion")
    public static final class Infusion extends AstralRecipeManager {

        public Infusion() {
            super(RecipeTypesAS.INFUSION_TYPE);
        }

        @ZenCodeType.Method
        public void addRecipe(String name, IIngredient input, IFluidStack fluid, IItemStack output, int duration,
                              @ZenCodeType.OptionalFloat(1) float consumptionChance,
                              @ZenCodeType.OptionalBoolean boolean consumeMultiple,
                              @ZenCodeType.OptionalBoolean(true) boolean acceptChalice) {
            this.add(name, new InfusionRecipe(input(input), fluid.getFluid(), positive(duration, "duration"), output(output),
                    chance(consumptionChance, "consumptionChance"), consumeMultiple, acceptChalice));
        }
    }

    @ZenRegister
    @ZenCodeType.Name("mods.astralsorcery.recipe.Lightwell")
    public static final class Lightwell extends AstralRecipeManager {

        public Lightwell() {
            super(RecipeTypesAS.LIGHTWELL_TYPE);
        }

        @ZenCodeType.Method
        public void addRecipe(String name, IIngredient input, IFluidStack output,
                              @ZenCodeType.OptionalFloat(1) float productionMultiplier,
                              @ZenCodeType.OptionalFloat(1) float shatterMultiplier,
                              @ZenCodeType.OptionalInt(-1) int catalystColor) {
            this.add(name, new LightwellRecipe(ColorWrapper.transparent(catalystColor), input(input), output.getFluid(),
                    multiplier(productionMultiplier, "productionMultiplier"), multiplier(shatterMultiplier, "shatterMultiplier")));
        }

        @ZenCodeType.Method
        public List<String> getIdsByOutputFluid(IFluidStack fluid) {
            return this.findIds(recipe -> ((LightwellRecipe) recipe).getGeneratedFluid() == fluid.getFluid());
        }
    }

    @ZenRegister
    @ZenCodeType.Name("mods.astralsorcery.recipe.LiquidInteraction")
    public static final class LiquidInteraction extends AstralRecipeManager {

        public LiquidInteraction() {
            super(RecipeTypesAS.LIQUID_INTERACTION_TYPE);
        }

        @ZenCodeType.Method
        public void addRecipe(String name, IFluidStack first, IFluidStack second, IItemStack result,
                              @ZenCodeType.OptionalFloat(1) float consumeFirst, @ZenCodeType.OptionalFloat(1) float consumeSecond,
                              @ZenCodeType.OptionalInt(1) int weight) {
            this.add(name, new LiquidInteractionRecipe(SizedFluidIngredient.of(first.getImmutableInternal()),
                    SizedFluidIngredient.of(second.getImmutableInternal()), chance(consumeFirst, "consumeFirst"),
                    chance(consumeSecond, "consumeSecond"), positive(weight, "weight"),
                    new LiquidInteractionResultDropItem(output(result))));
        }

        @ZenCodeType.Method
        public List<String> getIdsByOutputEntity(String entity) {
            var entityType = registered(BuiltInRegistries.ENTITY_TYPE, entity);
            return this.findIds(recipe -> ((LiquidInteractionRecipe) recipe).getResult()
                    instanceof LiquidInteractionResultSpawnEntity result && result.getEntityType() == entityType);
        }
    }

    @ZenRegister
    @ZenCodeType.Name("mods.astralsorcery.recipe.LiquidStarlight")
    public static final class LiquidStarlight extends AstralRecipeManager {

        public LiquidStarlight() {
            super(RecipeTypesAS.LIQUID_STARLIGHT_TYPE);
        }

        @ZenCodeType.Method
        public void addRecipe(String name, IIngredient input, int count, IItemStack result, int duration,
                              @ZenCodeType.OptionalInt int extraTicks, @ZenCodeType.OptionalInt(-1) int color,
                              @ZenCodeType.OptionalBoolean boolean consumesLiquid) {
            if (extraTicks < 0) {
                throw new IllegalArgumentException("extraTicks cannot be negative");
            }
            this.add(name, new LiquidStarlightRecipe(new SizedIngredient(input(input), positive(count, "count")), List.of(),
                    positive(duration, "duration"), extraTicks, ColorWrapper.transparent(color),
                    List.of(LiquidStarlightOutputDropItem.create(output(result))), consumesLiquid, true));
        }
    }

    @ZenRegister
    @ZenCodeType.Name("mods.astralsorcery.recipe.LumenCrystallization")
    public static final class LumenCrystallization extends AstralRecipeManager {

        public LumenCrystallization() {
            super(RecipeTypesAS.LUMEN_CRYSTALLIZATION_TYPE);
        }

        @ZenCodeType.Method
        public void addRecipe(String name, IIngredient input, String lumen, int cost,
                              @ZenCodeType.OptionalFloat(1) float shatterMultiplier) {
            this.add(name, new LumenCrystallizationRecipe(input(input), registered(RegistriesAS.REGISTRY_LUMEN.get(), lumen),
                    multiplier(shatterMultiplier, "shatterMultiplier"), positive(cost, "cost")));
        }

        @ZenCodeType.Method
        public List<String> getIdsByOutputLumen(String lumen) {
            var type = registered(RegistriesAS.REGISTRY_LUMEN.get(), lumen);
            return this.findIds(recipe -> ((LumenCrystallizationRecipe) recipe).getLumenToCrystallize() == type);
        }
    }

    @ZenRegister
    @ZenCodeType.Name("mods.astralsorcery.recipe.LumenGeneration")
    public static final class LumenGeneration extends AstralRecipeManager {

        public LumenGeneration() {
            super(RecipeTypesAS.LUMEN_GENERATION_TYPE);
        }

        @ZenCodeType.Method
        public void addRecipe(String name, IIngredient input, String lumen, int amount,
                              @ZenCodeType.OptionalFloat(1) float attemptMultiplier,
                              @ZenCodeType.OptionalFloat(1) float starlightCost,
                              @ZenCodeType.OptionalFloat(1) float shatterMultiplier) {
            this.addRecipeWithInputs(name, input, lumen, amount, Map.of(), attemptMultiplier, starlightCost, shatterMultiplier);
        }

        @ZenCodeType.Method
        public void addAlchemyRecipe(String name, IIngredient input, String lumen, int amount, LumenCost[] combination,
                                     @ZenCodeType.OptionalFloat(1) float attemptMultiplier,
                                     @ZenCodeType.OptionalFloat(1) float starlightCost,
                                     @ZenCodeType.OptionalFloat(1) float shatterMultiplier) {
            if (combination.length == 0) {
                throw new IllegalArgumentException("An alchemy recipe needs at least one lumen input");
            }
            Map<Lumen, Integer> inputs = new HashMap<>();
            for (LumenCost cost : combination) {
                if (inputs.putIfAbsent(cost.lumen, cost.amount) != null) {
                    throw new IllegalArgumentException("An alchemy recipe cannot request the same lumen twice");
                }
            }
            this.addRecipeWithInputs(name, input, lumen, amount, Map.copyOf(inputs), attemptMultiplier, starlightCost, shatterMultiplier);
        }

        @ZenCodeType.Method
        public List<String> getIdsByOutputLumen(String lumen) {
            var type = registered(RegistriesAS.REGISTRY_LUMEN.get(), lumen);
            return this.findIds(recipe -> ((LumenGenerationRecipe) recipe).getProducedLumen() == type);
        }

        private void addRecipeWithInputs(String name, IIngredient input, String lumen, int amount, Map<Lumen, Integer> combination,
                                         float attemptMultiplier, float starlightCost, float shatterMultiplier) {
            this.add(name, new LumenGenerationRecipe(input(input), registered(RegistriesAS.REGISTRY_LUMEN.get(), lumen), positive(amount, "amount"),
                    multiplier(attemptMultiplier, "attemptMultiplier"), multiplier(starlightCost, "starlightCost"),
                    multiplier(shatterMultiplier, "shatterMultiplier"), combination));
        }
    }

    @ZenRegister
    @ZenCodeType.Name("mods.astralsorcery.recipe.LumenCost")
    public static final class LumenCost {

        private final Lumen lumen;
        private final int amount;

        @ZenCodeType.Constructor
        public LumenCost(String id, int amount) {
            this.lumen = registered(RegistriesAS.REGISTRY_LUMEN.get(), id);
            this.amount = positive(amount, "lumen cost");
        }
    }

    private static List<ItemStack> outputs(IItemStack[] stacks) {
        if (stacks.length == 0) {
            throw new IllegalArgumentException("Expected at least one output");
        }
        return Arrays.stream(stacks).map(AstralRecipeManagers::output).toList();
    }

    private static ItemStack output(IItemStack stack) {
        ItemStack result = stack.getImmutableInternal();
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Expected a non-empty output item");
        }
        return result;
    }

    private static Ingredient input(IIngredient ingredient) {
        Ingredient result = ingredient.asVanillaIngredient();
        if (result == Ingredient.EMPTY) {
            throw new IllegalArgumentException("Expected a non-empty ingredient");
        }
        return result;
    }

    private static Optional<BaseConstellation> constellation(String id) {
        return id.isEmpty() ? Optional.empty() : Optional.of(registered(RegistriesAS.REGISTRY_CONSTELLATIONS.get(), id));
    }

    private static <T> T registered(Registry<T> registry, String name) {
        return registry.getOptional(AstralRecipeManager.exactId(name)).orElseThrow(() ->
                new IllegalArgumentException("Unknown " + registry.key().location() + " entry " + name));
    }

    private static int positive(int value, String field) {
        if (value < 1) {
            throw new IllegalArgumentException(field + " must be positive");
        }
        return value;
    }

    private static float multiplier(float value, String field) {
        if (!Float.isFinite(value) || value < 0) {
            throw new IllegalArgumentException(field + " must be a finite, non-negative number");
        }
        return value;
    }

    private static float chance(float value, String field) {
        if (multiplier(value, field) > 1) {
            throw new IllegalArgumentException(field + " must be between 0 and 1");
        }
        return value;
    }
}
