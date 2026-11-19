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
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public record SizedFluidIngredient(FluidIngredient ingredient, int amount) {

    public static final Codec<SizedFluidIngredient> NESTED_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FluidIngredient.CODEC.fieldOf("ingredient").forGetter(SizedFluidIngredient::ingredient),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("amount").forGetter(SizedFluidIngredient::amount)
    ).apply(instance, SizedFluidIngredient::new));

    public static final StreamCodec<FriendlyByteBuf, SizedFluidIngredient> STREAM_CODEC =
            ByteBufCodecs.fromCodec(NESTED_CODEC);

    public SizedFluidIngredient {
        if (amount < 1) {
            throw new IllegalArgumentException("Ingredient fluid amount must be positive");
        }
    }

    public static SizedFluidIngredient of(FluidStack stack) {
        return new SizedFluidIngredient(FluidIngredient.of(stack), stack.getAmount());
    }

    public static SizedFluidIngredient of(Fluid fluid, int amount) {
        return new SizedFluidIngredient(FluidIngredient.of(fluid), amount);
    }

    public boolean test(FluidStack stack) {
        return stack.getAmount() >= this.amount && this.ingredient.test(stack);
    }

    public FluidStack[] getFluids() {
        return this.ingredient.getFluids(this.amount);
    }
}
