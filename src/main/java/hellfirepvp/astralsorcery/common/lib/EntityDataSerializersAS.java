/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.recipe.altar.ActiveAltarRecipe;
import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import hellfirepvp.astralsorcery.common.util.data.ResolvedDeferredHolder;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.UnaryOperator;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EntityDataSerializersAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EntityDataSerializersAS {

    public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS_REGISTER =
            DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, AstralSorcery.MODID);

    public static final ResolvedDeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Vector3>> VECTOR =
            register("vector", serializer(Vector3.STREAM_CODEC, value -> value));
    public static final ResolvedDeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<FluidStack>> FLUID_STACK =
            register("fluid_stack", serializer(ByteBufCodecs.FLUID_STACK, FluidStack::copy));

    public static final ResolvedDeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<ActiveAltarRecipe.AdditionalInput>> ALTAR_INPUT_REFERENCE =
            register("altar_input_reference", serializer(ActiveAltarRecipe.AdditionalInput.STREAM_CODEC, value -> value));

    private static <T> ResolvedDeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<T>> register(String name, EntityDataSerializer<T> value) {
        RegistryObject<EntityDataSerializer<T>> holder = DATA_SERIALIZERS_REGISTER.register(name, () -> value);
        return ResolvedDeferredHolder.of(holder, value);
    }
    private static <T> EntityDataSerializer<T> serializer(StreamCodec<? super FriendlyByteBuf, T> codec,
                                                           UnaryOperator<T> copy) {
        return new EntityDataSerializer<>() {
            @Override
            public void write(FriendlyByteBuf buffer, T value) {
                codec.encode(buffer, value);
            }

            @Override
            public T read(FriendlyByteBuf buffer) {
                return codec.decode(buffer);
            }

            @Override
            public T copy(T value) {
                return copy.apply(value);
            }
        };
    }
}
