/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.ingredient;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.component.StackDataType;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.IngredientsAS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public record StackDataIngredient<T>(Ingredient base, StackDataType<T> field, T expected) implements AstralIngredient {

    public static final MapCodec<StackDataIngredient<?>> CODEC = DataComponentsAS.TYPE_CODEC.dispatchMap(
            "key", StackDataIngredient::field, field -> codecFor(field).codec());

    private static <T> MapCodec<StackDataIngredient<T>> codecFor(StackDataType<T> field) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                IngredientJson.NONEMPTY_CODEC.fieldOf("ingredient").forGetter(StackDataIngredient<T>::base),
                field.codec().fieldOf("expected").forGetter(StackDataIngredient<T>::expected)
        ).apply(instance, (base, expected) -> new StackDataIngredient<>(base, field, expected)));
    }

    public static <T> Ingredient of(StackDataType<T> field, T expected, ItemLike... items) {
        return new StackDataIngredient<>(Ingredient.of(items), field, expected).toVanilla();
    }

    @Override
    public boolean test(ItemStack stack) {
        return this.base.test(stack) && Objects.equals(this.expected, this.field.get(stack));
    }

    @Override
    public Stream<ItemStack> getItems() {
        return Arrays.stream(this.base.getItems()).map(stack -> {
            ItemStack example = stack.copy();
            this.field.set(example, this.expected);
            return example;
        });
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public AstralIngredientSerializer<?> getType() {
        return IngredientsAS.STACK_DATA;
    }
}
