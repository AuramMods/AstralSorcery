/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/** An item data field with a stable ID, an NBT codec, and optional item defaults. */
public final class StackDataType<T> {

    private static final String DATA_TAG = AstralSorcery.MODID;
    private static final String VALUE_TAG = "value";

    private final ResourceLocation id;
    private final Codec<T> codec;
    private final Map<Item, T> defaults = new ConcurrentHashMap<>();

    public StackDataType(ResourceLocation id, Codec<T> codec) {
        this.id = id;
        this.codec = codec;
    }

    public ResourceLocation id() {
        return this.id;
    }

    public Codec<T> codec() {
        return this.codec;
    }

    public void setDefault(Item item, T value) {
        this.defaults.put(item, value);
    }

    @Nullable
    public T get(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        CompoundTag data = stack.getTagElement(DATA_TAG);
        String key = this.id.toString();
        if (data == null || !data.contains(key, Tag.TAG_COMPOUND)) {
            return this.defaults.get(stack.getItem());
        }
        Tag value = data.getCompound(key).get(VALUE_TAG);
        return value == null ? null : this.codec.parse(ops(), value).result().orElse(null);
    }

    public T getOrDefault(ItemStack stack, T fallback) {
        T value = this.get(stack);
        return value == null ? fallback : value;
    }

    public boolean has(ItemStack stack) {
        return this.get(stack) != null;
    }

    public void set(ItemStack stack, T value) {
        if (stack.isEmpty()) {
            return;
        }
        if (Objects.equals(value, this.defaults.get(stack.getItem()))) {
            this.clearOverride(stack);
            return;
        }
        Tag encoded = this.codec.encodeStart(ops(), value).getOrThrow(false, message -> {});
        CompoundTag field = new CompoundTag();
        field.put(VALUE_TAG, encoded);
        stack.getOrCreateTagElement(DATA_TAG).put(this.id.toString(), field);
    }

    public void remove(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (this.defaults.containsKey(stack.getItem())) {
            stack.getOrCreateTagElement(DATA_TAG).put(this.id.toString(), new CompoundTag());
        } else {
            this.clearOverride(stack);
        }
    }

    public void copy(ItemStack source, ItemStack target) {
        T value = this.get(source);
        if (value != null) {
            this.set(target, value);
        }
    }

    public static void copyOtherTags(ItemStack source, ItemStack target) {
        CompoundTag sourceTag = source.getTag();
        if (sourceTag != null) {
            CompoundTag other = sourceTag.copy();
            other.remove(DATA_TAG);
            if (!other.isEmpty()) {
                target.getOrCreateTag().merge(other);
            }
        }
    }

    private void clearOverride(ItemStack stack) {
        CompoundTag data = stack.getTagElement(DATA_TAG);
        if (data != null) {
            data.remove(this.id.toString());
            if (data.isEmpty()) {
                stack.removeTagKey(DATA_TAG);
            }
        }
    }

    private static DynamicOps<Tag> ops() {
        return RegistryOps.create(NbtOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
    }
}
