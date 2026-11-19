/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;

public record SizedIngredient(Ingredient ingredient, int count) {

    public static final Codec<SizedIngredient> NESTED_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IngredientJson.NONEMPTY_CODEC.fieldOf("ingredient").forGetter(SizedIngredient::ingredient),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("count").forGetter(SizedIngredient::count)
    ).apply(instance, SizedIngredient::new));

    public static final StreamCodec<FriendlyByteBuf, SizedIngredient> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INGREDIENT, SizedIngredient::ingredient,
            ByteBufCodecs.VAR_INT, SizedIngredient::count,
            SizedIngredient::new
    );

    public SizedIngredient {
        if (count < 1) {
            throw new IllegalArgumentException("Ingredient count must be positive");
        }
    }

    public boolean test(ItemStack stack) {
        return stack.getCount() >= this.count && this.ingredient.test(stack);
    }

    public ItemStack[] getItems() {
        return Arrays.stream(this.ingredient.getItems())
                .map(stack -> stack.copyWithCount(this.count))
                .toArray(ItemStack[]::new);
    }
}
