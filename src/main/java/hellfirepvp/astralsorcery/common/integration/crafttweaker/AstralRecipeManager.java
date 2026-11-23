package hellfirepvp.astralsorcery.common.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.base.IRuntimeAction;
import com.blamejared.crafttweaker.api.action.recipe.ActionRemoveRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.data.MapData;
import com.blamejared.crafttweaker.api.data.visitor.DataToJsonStringVisitor;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.recipe.CustomRecipe;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarRecipe;
import hellfirepvp.astralsorcery.common.recipe.focal.drop.FocalCombineRecipe;
import hellfirepvp.astralsorcery.common.recipe.focal.place.FocalTransmutationRecipe;
import hellfirepvp.astralsorcery.common.recipe.infusion.InfusionRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.LiquidInteractionRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.result.LiquidInteractionResultDropItem;
import hellfirepvp.astralsorcery.common.recipe.liquid.output.LiquidStarlightOutputDropItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.apache.logging.log4j.Logger;
import org.openzen.zencode.java.ZenCodeType;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@ZenRegister
@ZenCodeType.Name("mods.astralsorcery.recipe.AstralRecipeManager")
public abstract class AstralRecipeManager implements IRecipeManager<Recipe<?>> {

    private final Supplier<? extends RecipeType<?>> recipeType;

    protected AstralRecipeManager(Supplier<? extends RecipeType<?>> recipeType) {
        this.recipeType = recipeType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final RecipeType<Recipe<?>> getRecipeType() {
        return (RecipeType<Recipe<?>>) this.recipeType.get();
    }

    @Override
    @ZenCodeType.Method
    public final void addJsonRecipe(String name, MapData data) {
        this.writeJson(addId(name), data.accept(DataToJsonStringVisitor.INSTANCE), Operation.ADD);
    }

    @ZenCodeType.Method
    public final void replaceJsonRecipe(String id, MapData data) {
        this.writeJson(exactId(id), data.accept(DataToJsonStringVisitor.INSTANCE), Operation.REPLACE);
    }

    @ZenCodeType.Method
    public final void setJsonRecipe(String id, String json) {
        this.writeJson(exactId(id), json, Operation.SET);
    }

    @ZenCodeType.Method
    public final String getRecipeJson(String id) {
        Recipe<?> recipe = this.getRecipeList().get(exactId(id));
        if (!(recipe instanceof CustomRecipe<?, ?> customRecipe)) {
            throw new IllegalArgumentException("No " + this.getBracketResourceLocation() + " recipe named " + id);
        }
        return encode(customRecipe).toString();
    }

    @ZenCodeType.Method
    public final List<String> getRecipeIds() {
        return this.findIds(recipe -> true);
    }

    @Override
    @ZenCodeType.Method
    public final List<Recipe<?>> getRecipesByOutput(IIngredient output) {
        return this.getRecipes().values().stream().filter(recipe -> matchesOutput(recipe, output)).toList();
    }

    @Override
    @ZenCodeType.Method
    public final void remove(IIngredient output) {
        CraftTweakerAPI.apply(new ActionRemoveRecipe<>(this, recipe -> matchesOutput(recipe, output),
                action -> "item output matching " + output.getCommandString()));
    }

    protected final void add(String name, CustomRecipe<?, ?> recipe) {
        recipe.setId(addId(name));
        CraftTweakerAPI.apply(new WriteRecipe(this, recipe, Operation.ADD));
    }

    protected final List<String> findIds(Predicate<Recipe<?>> test) {
        return this.getRecipes().values().stream().filter(test).map(recipe -> recipe.getId().toString()).sorted().toList();
    }

    private static boolean matchesOutput(Recipe<?> recipe, IIngredient output) {
        return itemOutputs(recipe).filter(stack -> !stack.isEmpty()).anyMatch(stack -> output.matches(IItemStack.of(stack.copy())));
    }

    private static Stream<ItemStack> itemOutputs(Recipe<?> recipe) {
        if (recipe instanceof AltarRecipe altar) {
            return altar.getOutputs().stream();
        }
        if (recipe instanceof FocalCombineRecipe combine) {
            return combine.getOutputs().stream();
        }
        if (recipe instanceof InfusionRecipe infusion) {
            return Stream.of(infusion.getOutput());
        }
        if (recipe instanceof LiquidInteractionRecipe interaction && interaction.getResult() instanceof LiquidInteractionResultDropItem drop) {
            return Stream.of(drop.getOutput());
        }
        if (recipe instanceof LiquidStarlightRecipe liquid) {
            return liquid.getOutputModifiers().stream()
                    .filter(LiquidStarlightOutputDropItem.class::isInstance)
                    .map(modifier -> ((LiquidStarlightOutputDropItem) modifier).getResult());
        }
        return Stream.empty();
    }

    private void writeJson(ResourceLocation id, String json, Operation operation) {
        try {
            JsonObject data = JsonParser.parseString(json).getAsJsonObject();
            ResourceLocation type = this.getBracketResourceLocation();
            if (data.has("type") && !type.toString().equals(data.get("type").getAsString())) {
                throw new IllegalArgumentException("Expected serializer " + type + ", got " + data.get("type"));
            }
            data.addProperty("type", type.toString());
            if (BuiltInRegistries.RECIPE_SERIALIZER.get(type) instanceof RecipeTypesAS.CodecSerializer<?> serializer) {
                serializer.validateFields(data);
            }
            Recipe<?> recipe = RecipeManager.fromJson(id, data);
            if (recipe.getType() != this.getRecipeType()) {
                throw new IllegalArgumentException("Serializer returned recipe type " + recipe.getType());
            }
            CraftTweakerAPI.apply(new WriteRecipe(this, recipe, operation));
        } catch (RuntimeException error) {
            throw new IllegalArgumentException("Invalid " + this.getBracketResourceLocation() + " recipe " + id + ": " + error.getMessage(), error);
        }
    }

    static <T extends CustomRecipe<T, ?>> JsonObject encode(CustomRecipe<T, ?> recipe) {
        @SuppressWarnings("unchecked")
        RecipeTypesAS.CodecSerializer<T> serializer = (RecipeTypesAS.CodecSerializer<T>) recipe.getRecipeSerializer().get();
        @SuppressWarnings("unchecked")
        T self = (T) recipe;
        JsonObject data = serializer.toJson(self);
        data.addProperty("type", recipe.getRecipeType().holder().getId().toString());
        return data;
    }

    static ResourceLocation exactId(String id) {
        ResourceLocation parsed = ResourceLocation.tryParse(id);
        if (parsed == null || id.indexOf(':') <= 0 || parsed.getPath().isEmpty()) {
            throw new IllegalArgumentException("Expected a namespaced ID, got '" + id + "'");
        }
        return parsed;
    }

    private static ResourceLocation addId(String name) {
        if (name.contains(":")) {
            throw new IllegalArgumentException("Use a recipe path for addRecipe, or setJsonRecipe for a namespaced ID");
        }
        return exactId("crafttweaker:" + name);
    }

    private enum Operation {
        ADD, REPLACE, SET
    }

    private record WriteRecipe(AstralRecipeManager manager, Recipe<?> recipe, Operation operation) implements IRuntimeAction {

        @Override
        public boolean validate(Logger logger) {
            Recipe<?> existing = this.manager.getRecipeList().getByName().get(this.recipe.getId());
            if (existing != null && (this.operation == Operation.ADD || existing.getType() != this.recipe.getType())) {
                logger.error("Cannot write Astral recipe {} because its ID belongs to a {} recipe", this.recipe.getId(), existing.getType());
                return false;
            }
            if (existing == null && this.operation == Operation.REPLACE) {
                logger.error("Cannot replace missing {} recipe {}", this.manager.getBracketResourceLocation(), this.recipe.getId());
                return false;
            }
            return true;
        }

        @Override
        public void apply() {
            var recipes = this.manager.getRecipeList();
            recipes.remove(this.recipe.getId());
            recipes.add(this.recipe.getId(), this.recipe);
        }

        @Override
        public String describe() {
            return this.operation + " " + this.manager.getBracketResourceLocation() + " recipe " + this.recipe.getId();
        }

        @Override
        public String systemName() {
            return AstralSorcery.MODID;
        }
    }
}
