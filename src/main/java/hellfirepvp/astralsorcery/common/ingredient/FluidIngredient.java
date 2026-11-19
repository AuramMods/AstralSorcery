/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.ingredient;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public record FluidIngredient(Either<Fluid, TagKey<Fluid>> source, Optional<CompoundTag> nbt) {

    private static final Codec<FluidIngredient> FLUID_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid")
                    .forGetter(value -> value.source.left().orElseThrow()),
            CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(FluidIngredient::nbt)
    ).apply(instance, (fluid, nbt) -> new FluidIngredient(Either.left(fluid), nbt)));

    private static final Codec<FluidIngredient> TAG_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.FLUID).fieldOf("tag")
                    .forGetter(value -> value.source.right().orElseThrow()),
            CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(FluidIngredient::nbt)
    ).apply(instance, (tag, nbt) -> new FluidIngredient(Either.right(tag), nbt)));

    public static final Codec<FluidIngredient> CODEC = Codec.either(FLUID_CODEC, TAG_CODEC).xmap(
            value -> value.map(Function.identity(), Function.identity()),
            value -> value.source.left().isPresent() ? Either.left(value) : Either.right(value)
    );

    public static FluidIngredient of(Fluid fluid) {
        return new FluidIngredient(Either.left(fluid), Optional.empty());
    }

    public static FluidIngredient of(FluidStack stack) {
        return new FluidIngredient(Either.left(stack.getFluid()), Optional.ofNullable(stack.getTag()).map(CompoundTag::copy));
    }

    public boolean test(FluidStack stack) {
        return !stack.isEmpty()
                && this.source.map(fluid -> stack.getFluid() == fluid, tag -> stack.getFluid().is(tag))
                && this.nbt.map(tag -> Objects.equals(tag, stack.getTag())).orElse(true);
    }

    public boolean isSimple() {
        return this.nbt.isEmpty();
    }

    public boolean isEmpty() {
        return this.source.map(fluid -> fluid == Fluids.EMPTY, tag -> false);
    }

    public boolean hasNoFluids() {
        return this.fluids().findAny().isEmpty();
    }

    public Stream<Fluid> fluids() {
        return this.source.map(fluid -> Stream.of(fluid), tag -> ForgeRegistries.FLUIDS.tags().getTag(tag).stream());
    }

    public FluidStack[] getFluids(int amount) {
        return this.fluids().map(fluid -> new FluidStack(fluid, amount, this.nbt.map(CompoundTag::copy).orElse(null)))
                .toArray(FluidStack[]::new);
    }
}
