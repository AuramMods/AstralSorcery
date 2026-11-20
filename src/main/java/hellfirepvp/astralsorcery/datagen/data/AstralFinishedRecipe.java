/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.recipe.CustomRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public record AstralFinishedRecipe<R extends CustomRecipe<R, ?>>(
        ResourceLocation id, R recipe, MapCodec<R> codec,
        ResourceLocation advancementId, Advancement.Builder advancement) implements FinishedRecipe {

    public AstralFinishedRecipe {
        recipe.setId(id);
    }

    public static Advancement.Builder advancement(ResourceLocation recipeId) {
        return Advancement.Builder.recipeAdvancement()
                .parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT)
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
        var ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
        var encoded = this.codec.codec().encodeStart(ops, this.recipe)
                .getOrThrow(false, message -> { throw new JsonSyntaxException("Cannot generate recipe " + this.id + ": " + message); })
                .getAsJsonObject();
        encoded.entrySet().forEach(field -> json.add(field.getKey(), field.getValue()));
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return this.recipe.getSerializer();
    }

    @Override
    public JsonObject serializeAdvancement() {
        return this.advancement.serializeToJson();
    }

    @Override
    public ResourceLocation getAdvancementId() {
        return this.advancementId;
    }
}
