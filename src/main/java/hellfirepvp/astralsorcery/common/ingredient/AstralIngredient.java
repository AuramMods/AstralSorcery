/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.ingredient;

import com.google.gson.JsonElement;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;

import java.util.stream.Stream;

public interface AstralIngredient {

    boolean test(ItemStack stack);

    Stream<ItemStack> getItems();

    boolean isSimple();

    default boolean isEmpty() {
        return false;
    }

    AstralIngredientSerializer<?> getType();

    default Ingredient toVanilla() {
        return new Adapter(this);
    }

    final class Adapter extends AbstractIngredient {

        private final AstralIngredient value;

        Adapter(AstralIngredient value) {
            this.value = value;
        }

        AstralIngredient value() {
            return this.value;
        }

        @Override
        public boolean test(ItemStack stack) {
            return stack != null && this.value.test(stack);
        }

        @Override
        public ItemStack[] getItems() {
            return this.value.getItems().toArray(ItemStack[]::new);
        }

        @Override
        public boolean isEmpty() {
            return this.value.isEmpty();
        }

        @Override
        public boolean isSimple() {
            return this.value.isSimple();
        }

        @Override
        public AstralIngredientSerializer<?> getSerializer() {
            return this.value.getType();
        }

        @Override
        public JsonElement toJson() {
            return this.getSerializer().toJson(this);
        }
    }
}
