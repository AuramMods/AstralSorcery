/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.ingredient;

import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.IngredientsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.VoidFluidHandler;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: IngredientBridge
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public final class IngredientBridge implements AstralIngredient {

    public static final IngredientBridge EMPTY = of(Ingredient.EMPTY);
    public static final MapCodec<IngredientBridge> CODEC = StringRepresentable.fromEnum(Type::values)
            .dispatchMap(ingredient -> ingredient.type, type -> type.getIngredientCodec().codec());
    public static final StreamCodec<FriendlyByteBuf, IngredientBridge> STREAM_CODEC =
            Type.STREAM_CODEC.dispatch(ing -> ing.type, Type::getIngredientStreamCodec);

    private final Type type;
    private final Ingredient ingredient;
    private final SizedFluidIngredient fluidIngredient;

    private IngredientBridge(Type type, Ingredient ingredient, SizedFluidIngredient fluidIngredient) {
        this.type = type;
        this.ingredient = ingredient;
        this.fluidIngredient = fluidIngredient;
    }

    public static IngredientBridge of(Ingredient ingredient) {
        return new IngredientBridge(Type.ITEM, ingredient, null);
    }

    public static IngredientBridge of(SizedFluidIngredient fluidIngredient) {
        return new IngredientBridge(Type.FLUID, null, fluidIngredient);
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public SizedFluidIngredient getFluidIngredient() {
        return this.fluidIngredient;
    }

    public Type getIngredientType() {
        return this.type;
    }

    @Override
    public boolean test(ItemStack stack) {
        return switch (this.type) {
            case ITEM -> this.ingredient.test(stack);
            case FLUID -> FluidUtil.getFluidContained(stack).map(this.fluidIngredient::test).orElse(false);
        };
    }

    @Override
    public Stream<ItemStack> getItems() {
        return switch (this.type) {
            case ITEM -> Arrays.stream(this.ingredient.getItems());
            case FLUID -> Arrays.stream(this.fluidIngredient.getFluids()).map(FluidUtil::getFilledBucket);
        };
    }

    @Override
    public boolean isSimple() {
        return switch (this.type) {
            case ITEM -> this.ingredient.isSimple();
            case FLUID -> false;
        };
    }

    public boolean isEmpty() {
        return switch (this.type) {
            case ITEM -> this.ingredient.isEmpty();
            case FLUID -> this.fluidIngredient.amount() <= 0 ||
                    this.fluidIngredient.ingredient().isEmpty() ||
                    this.fluidIngredient.ingredient().hasNoFluids();
        };
    }

    public boolean consume(Supplier<ItemStack> getter, Function<ItemStack, ItemStack> setter, Consumer<ItemStack> onRemainder, boolean simulate) {
        ItemStack extracted = getter.get();
        if (extracted.isEmpty()) return this.isEmpty();

        return switch (this.type) {
            case ITEM -> {
                ItemStack remainder = extracted.getCraftingRemainingItem().copy();
                if (!remainder.isEmpty()) {
                    if (!setter.apply(remainder).isEmpty() && !simulate) {
                        onRemainder.accept(remainder);
                    }
                }
                yield true;
            }
            case FLUID -> {
                FluidActionResult result = FluidUtil.tryEmptyContainer(extracted, VoidFluidHandler.INSTANCE,
                        this.fluidIngredient.amount(), null, !simulate);
                if (!result.isSuccess()) yield false;

                ItemStack remainder = result.getResult().copy();
                if (!remainder.isEmpty()) {
                    if (!setter.apply(remainder).isEmpty() && !simulate) {
                        onRemainder.accept(remainder);
                    }
                }
                yield true;
            }
        };
    }

    @Override
    public AstralIngredientSerializer<?> getType() {
        return IngredientsAS.INGREDIENT_BRIDGE;
    }

    public enum Type implements StringRepresentable {

        ITEM(RecordCodecBuilder.mapCodec(inst -> inst.group(
                IngredientJson.NONEMPTY_CODEC.fieldOf("ingredient").forGetter(IngredientBridge::getIngredient)
        ).apply(inst, IngredientBridge::of)), StreamCodec.composite(
                ByteBufCodecs.INGREDIENT,
                IngredientBridge::getIngredient,
                IngredientBridge::of
        )),
        FLUID(RecordCodecBuilder.mapCodec(inst -> inst.group(
                SizedFluidIngredient.NESTED_CODEC.fieldOf("ingredient").forGetter(IngredientBridge::getFluidIngredient)
        ).apply(inst, IngredientBridge::of)), StreamCodec.composite(
                SizedFluidIngredient.STREAM_CODEC,
                IngredientBridge::getFluidIngredient,
                IngredientBridge::of
        ));

        private static final StreamCodec<FriendlyByteBuf, Type> STREAM_CODEC = MiscUtil.cast(CodecUtil.enumStreamCodec(Type.class));

        private final MapCodec<IngredientBridge> ingredientCodec;
        private final StreamCodec<FriendlyByteBuf, IngredientBridge> ingredientStreamCodec;

        Type(MapCodec<IngredientBridge> ingredientCodec, StreamCodec<FriendlyByteBuf, IngredientBridge> ingredientStreamCodec) {
            this.ingredientCodec = ingredientCodec;
            this.ingredientStreamCodec = ingredientStreamCodec;
        }

        public MapCodec<? extends IngredientBridge> getIngredientCodec() {
            return this.ingredientCodec;
        }

        public StreamCodec<FriendlyByteBuf, ? extends IngredientBridge> getIngredientStreamCodec() {
            return this.ingredientStreamCodec;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
