/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/** A deferred block or item that can be passed to recipes as an item. */
public record RegisteredItemLike<T extends ItemLike>(RegistryObject<T> entry) implements Supplier<T>, ItemLike {

    @Override
    public T get() {
        return this.entry.get();
    }

    public ResourceLocation getId() {
        return this.entry.getId();
    }

    public ResourceKey<T> getKey() {
        return this.entry.getKey();
    }

    @Override
    public Item asItem() {
        return this.get().asItem();
    }

    public ItemStack toStack() {
        return this.asItem().getDefaultInstance();
    }

    public ItemStack toStack(int count) {
        return this.toStack().copyWithCount(count);
    }
}
