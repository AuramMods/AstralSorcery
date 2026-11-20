/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data;

import hellfirepvp.astralsorcery.common.recipe.CustomRecipe;
import hellfirepvp.astralsorcery.common.research.tome.TomePageRecipe;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ResolvingRecipeTypeRegistryObject;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import java.util.*;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GeneratedRecipeBuffer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class GeneratedRecipeBuffer {

    private static final Map<RecipeType<?>, List<RecipeReference<?>>> TRACKED_RECIPES = new HashMap<>();

    public static <T extends Recipe<?>> void addRecipe(ResolvingRecipeTypeRegistryObject<T> type, ResourceLocation id, T recipe) {
        addRecipe(type.get(), id, recipe, type.outputMatchProvider().apply(recipe));
    }

    public static <T extends Recipe<?>> void addRecipe(RecipeType<?> recipeType, ResourceLocation id, T recipe, ItemStack matchOutput) {
        TRACKED_RECIPES.computeIfAbsent(recipeType, t -> new ArrayList<>())
                .add(new RecipeReference<>(id, recipe, matchOutput));
    }

    public static TomePageRecipe findRecipe(RecipeType<?> type, ItemLike output) {
        return findRecipe(type, new ItemStack(output));
    }

    public static TomePageRecipe findRecipe(RecipeType<?> type, ItemStack output) {
        return BuiltInRegistries.RECIPE_TYPE.getResourceKey(type)
                .flatMap(key -> find(type, output).map(ref -> TomePageRecipe.of(key, ref.id())))
                .orElseThrow();
    }

    public static TomePageRecipe findRecipe(ResolvingRecipeTypeRegistryObject<?> type, ItemLike output) {
        return findRecipe(type.get(), new ItemStack(output));
    }

    public static TomePageRecipe findRecipe(ResolvingRecipeTypeRegistryObject<?> type, ItemStack output) {
        return findRecipe(type.get(), output);
    }

    public static TomePageRecipe findRecipe(Holder<RecipeType<?>> type, ItemLike output) {
        return findRecipe(type, new ItemStack(output));
    }

    public static TomePageRecipe findRecipe(Holder<RecipeType<?>> type, ItemStack output) {
        return findRecipe(type.value(), output);
    }

    public static <T extends Recipe<?>> Optional<RecipeReference<T>> find(RecipeType<T> type, ItemLike output) {
        return find(type, new ItemStack(output));
    }

    public static <T extends Recipe<?>> Optional<RecipeReference<T>> find(RecipeType<T> type, ItemStack output) {
        return TRACKED_RECIPES.getOrDefault(type, Collections.emptyList()).stream()
                .filter(ref -> ItemStack.isSameItemSameTags(ref.generatedOutput(), output))
                .findFirst()
                .map(MiscUtil::<RecipeReference<T>, RecipeReference<?>>cast);
    }

    public static class BufferedRecipeOutput implements Consumer<FinishedRecipe> {

        private final Consumer<FinishedRecipe> output;

        public BufferedRecipeOutput(Consumer<FinishedRecipe> output) {
            this.output = output;
            TRACKED_RECIPES.clear();
        }

        @Override
        public void accept(FinishedRecipe finished) {
            this.output.accept(finished);
            if (finished instanceof AstralFinishedRecipe<?> astral) {
                this.trackCustomRecipe(astral);
                return;
            }

            var serializer = finished.getType();
            if (serializer == RecipeSerializer.SHAPED_RECIPE || serializer == RecipeSerializer.SHAPELESS_RECIPE
                    || serializer instanceof SimpleCookingSerializer<?>) {
                Recipe<?> recipe = RecipeManager.fromJson(finished.getId(), finished.serializeRecipe());
                addRecipe(recipe.getType(), finished.getId(), recipe, recipe.getResultItem(RegistryAccess.EMPTY).copy());
            }
        }

        private <R extends CustomRecipe<R, ?>> void trackCustomRecipe(AstralFinishedRecipe<R> finished) {
            R recipe = finished.recipe();
            addRecipe(recipe.getRecipeType(), finished.getId(), recipe);
        }
    }

    public record RecipeReference<T extends Recipe<?>>(ResourceLocation id, T recipe, ItemStack generatedOutput) {}
}
