/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.recipe.CustomRecipe;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import hellfirepvp.astralsorcery.common.recipe.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LazyRecipeHolder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LazyRecipeHolder<T extends Recipe<?>> {

    private final ResourceLocation recipeId;
    private final Supplier<? extends RecipeType<?>> expectedType;
    private T initialRecipe;
    private String definition;

    private LazyRecipeHolder(ResourceLocation recipeId, Supplier<? extends RecipeType<?>> expectedType, T initialRecipe, String definition) {
        this.recipeId = recipeId;
        this.expectedType = expectedType;
        this.initialRecipe = initialRecipe;
        this.definition = definition;
    }

    public static <R extends Recipe<?>> Codec<LazyRecipeHolder<R>> typedCodec(Supplier<RecipeType<R>> typeHint) {
        return RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("recipe_id").forGetter(LazyRecipeHolder::getRecipeId),
                Codec.STRING.optionalFieldOf("definition").forGetter(holder -> Optional.ofNullable(holder.definition))
        ).apply(instance, (id, definition) -> new LazyRecipeHolder<>(id, typeHint, null, definition.orElse(null))));
    }

    public static <T extends Recipe<?>> LazyRecipeHolder<T> of(RecipeHolder<T> holder) {
        T recipe = holder.value();
        String definition = recipe instanceof CustomRecipe<?, ?> custom ? custom.encodeDefinition().toString() : null;
        return new LazyRecipeHolder<>(holder.id(), recipe::getType, recipe, definition);
    }

    public ResourceLocation getRecipeId() {
        return this.recipeId;
    }

    public T resolveRecipe(Level level) {
        if (level == null) return null;
        Recipe<?> current = level.getRecipeManager().byKey(this.recipeId).orElse(null);
        if (current == null || current.getType() != this.expectedType.get()) return null;
        if (level.isClientSide()) return MiscUtil.cast(current);
        if (this.initialRecipe != current) {
            if (current instanceof CustomRecipe<?, ?> custom) {
                var encoded = custom.encodeDefinition();
                if (this.definition != null && !JsonParser.parseString(this.definition).equals(encoded)) return null;
                this.definition = encoded.toString();
            } else if (this.initialRecipe != null) {
                return null;
            }
            this.initialRecipe = MiscUtil.cast(current);
        }
        return this.initialRecipe;
    }
}
