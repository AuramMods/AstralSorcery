/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.IntFunction;

public final class ByteBufCodecs {

    private static final int MAX_COLLECTION_SIZE = 65_536;

    public static final StreamCodec<ByteBuf, Boolean> BOOL = StreamCodec.of(ByteBuf::writeBoolean, ByteBuf::readBoolean);
    public static final StreamCodec<ByteBuf, Integer> INT = StreamCodec.of(ByteBuf::writeInt, ByteBuf::readInt);
    public static final StreamCodec<ByteBuf, Float> FLOAT = StreamCodec.of(ByteBuf::writeFloat, ByteBuf::readFloat);
    public static final StreamCodec<ByteBuf, Double> DOUBLE = StreamCodec.of(ByteBuf::writeDouble, ByteBuf::readDouble);
    public static final StreamCodec<ByteBuf, Integer> VAR_INT =
            StreamCodec.of((buffer, value) -> friendly(buffer).writeVarInt(value), buffer -> friendly(buffer).readVarInt());
    public static final StreamCodec<ByteBuf, Long> VAR_LONG =
            StreamCodec.of((buffer, value) -> friendly(buffer).writeVarLong(value), buffer -> friendly(buffer).readVarLong());
    public static final StreamCodec<ByteBuf, String> STRING_UTF8 =
            StreamCodec.of((buffer, value) -> friendly(buffer).writeUtf(value), buffer -> friendly(buffer).readUtf());
    public static final StreamCodec<ByteBuf, ResourceLocation> RESOURCE_LOCATION =
            StreamCodec.of((buffer, id) -> friendly(buffer).writeResourceLocation(id), buffer -> friendly(buffer).readResourceLocation());
    public static final StreamCodec<ByteBuf, BlockPos> BLOCK_POS =
            StreamCodec.of((buffer, pos) -> buffer.writeLong(pos.asLong()), buffer -> BlockPos.of(buffer.readLong()));
    public static final StreamCodec<ByteBuf, UUID> UUID_CODEC =
            StreamCodec.of((buffer, uuid) -> friendly(buffer).writeUUID(uuid), buffer -> friendly(buffer).readUUID());
    public static final StreamCodec<FriendlyByteBuf, Component> COMPONENT =
            StreamCodec.of(FriendlyByteBuf::writeComponent, FriendlyByteBuf::readComponent);
    public static final StreamCodec<FriendlyByteBuf, ItemStack> ITEM_STACK =
            StreamCodec.of((buffer, stack) -> buffer.writeItemStack(stack, false), FriendlyByteBuf::readItem);
    public static final StreamCodec<FriendlyByteBuf, FluidStack> FLUID_STACK =
            StreamCodec.of((buffer, stack) -> stack.writeToPacket(buffer), FluidStack::readFromPacket);
    public static final StreamCodec<FriendlyByteBuf, Ingredient> INGREDIENT =
            StreamCodec.of((buffer, ingredient) -> ingredient.toNetwork(buffer), Ingredient::fromNetwork);
    public static final StreamCodec<FriendlyByteBuf, MobEffectInstance> MOB_EFFECT_INSTANCE =
            StreamCodec.of((buffer, effect) -> buffer.writeNbt(effect.save(new CompoundTag())), buffer -> {
                CompoundTag tag = buffer.readNbt();
                MobEffectInstance effect = tag == null ? null : MobEffectInstance.load(tag);
                if (effect == null) {
                    throw new DecoderException("Missing or unknown mob effect");
                }
                return effect;
            });

    private ByteBufCodecs() {}

    public static <B extends ByteBuf, T> StreamCodec<B, Optional<T>> optional(StreamCodec<? super B, T> element) {
        return StreamCodec.of((buffer, value) -> {
            buffer.writeBoolean(value.isPresent());
            value.ifPresent(entry -> element.encode(buffer, entry));
        }, buffer -> buffer.readBoolean() ? Optional.of(element.decode(buffer)) : Optional.empty());
    }

    public static <B extends ByteBuf, T> StreamCodec.CodecOperation<B, T, List<T>> list() {
        return element -> collection(ArrayList::new, element);
    }

    public static <B extends ByteBuf, T, C extends Collection<T>> StreamCodec<B, C> collection(
            IntFunction<C> factory, StreamCodec<? super B, T> element) {
        return StreamCodec.of((buffer, values) -> {
            writeSize(buffer, values.size());
            for (T value : values) {
                element.encode(buffer, value);
            }
        }, buffer -> {
            int size = readSize(buffer);
            C values = factory.apply(size);
            for (int index = 0; index < size; index++) {
                values.add(element.decode(buffer));
            }
            return values;
        });
    }

    public static <B extends ByteBuf, K, V, M extends Map<K, V>> StreamCodec<B, M> map(
            IntFunction<M> factory, StreamCodec<? super B, K> key, StreamCodec<? super B, V> value) {
        return StreamCodec.of((buffer, values) -> {
            writeSize(buffer, values.size());
            for (Map.Entry<K, V> entry : values.entrySet()) {
                key.encode(buffer, entry.getKey());
                value.encode(buffer, entry.getValue());
            }
        }, buffer -> {
            int size = readSize(buffer);
            M values = factory.apply(size);
            for (int index = 0; index < size; index++) {
                values.put(key.decode(buffer), value.decode(buffer));
            }
            return values;
        });
    }

    public static <T> StreamCodec<ByteBuf, ResourceKey<T>> resourceKey(ResourceKey<? extends Registry<T>> registry) {
        return RESOURCE_LOCATION.map(location -> ResourceKey.create(registry, location), ResourceKey::location);
    }

    public static <T> StreamCodec<FriendlyByteBuf, T> registry(ResourceKey<? extends Registry<T>> key) {
        return StreamCodec.of((buffer, value) -> {
            ResourceLocation id = registryAccess().registryOrThrow(key).getKey(value);
            if (id == null) {
                throw new EncoderException("Unregistered value in " + key.location());
            }
            buffer.writeResourceLocation(id);
        }, buffer -> {
            ResourceLocation id = buffer.readResourceLocation();
            return registryAccess().registryOrThrow(key).getOptional(id)
                    .orElseThrow(() -> new DecoderException("Unknown " + key.location() + " entry " + id));
        });
    }

    public static <T> StreamCodec<FriendlyByteBuf, Holder<T>> holderRegistry(ResourceKey<? extends Registry<T>> key) {
        return StreamCodec.of((buffer, holder) -> {
            ResourceLocation id = holder.unwrapKey().map(ResourceKey::location)
                    .orElseGet(() -> registryAccess().registryOrThrow(key).getKey(holder.value()));
            if (id == null) {
                throw new EncoderException("Unregistered holder in " + key.location());
            }
            buffer.writeResourceLocation(id);
        }, buffer -> {
            ResourceLocation id = buffer.readResourceLocation();
            return registryAccess().registryOrThrow(key).getHolder(ResourceKey.create(key, id))
                    .orElseThrow(() -> new DecoderException("Unknown " + key.location() + " holder " + id));
        });
    }

    public static <T> StreamCodec<FriendlyByteBuf, T> fromCodec(Codec<T> codec) {
        return nbt(codec, NbtOps.INSTANCE);
    }

    public static <T> StreamCodec<FriendlyByteBuf, T> fromCodecWithRegistries(Codec<T> codec) {
        return nbt(codec, RegistryOps.create(NbtOps.INSTANCE, registryAccess()));
    }

    private static <T> StreamCodec<FriendlyByteBuf, T> nbt(Codec<T> codec, DynamicOps<Tag> ops) {
        return StreamCodec.of((buffer, value) -> {
            Tag encoded = codec.encodeStart(ops, value).getOrThrow(false, message -> {});
            CompoundTag root = new CompoundTag();
            root.put("value", encoded);
            buffer.writeNbt(root);
        }, buffer -> {
            CompoundTag root = buffer.readNbt();
            if (root == null || !root.contains("value")) {
                throw new DecoderException("Missing encoded packet value");
            }
            return codec.parse(ops, root.get("value")).getOrThrow(false, message -> {});
        });
    }

    private static RegistryAccess registryAccess() {
        return RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
    }

    private static FriendlyByteBuf friendly(ByteBuf buffer) {
        return buffer instanceof FriendlyByteBuf friendly ? friendly : new FriendlyByteBuf(buffer);
    }

    private static int readSize(ByteBuf buffer) {
        int size = friendly(buffer).readVarInt();
        if (size < 0 || size > MAX_COLLECTION_SIZE) {
            throw new DecoderException("Invalid collection size " + size);
        }
        return size;
    }

    private static void writeSize(ByteBuf buffer, int size) {
        if (size > MAX_COLLECTION_SIZE) {
            throw new EncoderException("Collection is too large to synchronize: " + size);
        }
        friendly(buffer).writeVarInt(size);
    }
}
