package hellfirepvp.astralsorcery.common.ingredient;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.item.LumenCrystalItem;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.IngredientsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;

import java.util.stream.Stream;

public record LumenTagIngredient(TagKey<Lumen> tag) implements AstralIngredient {

    public static final MapCodec<LumenTagIngredient> CODEC = TagKey.codec(RegistriesAS.KEY_LUMEN)
            .xmap(LumenTagIngredient::new, LumenTagIngredient::tag).fieldOf("tag");

    @Override
    public boolean test(ItemStack stack) {
        if (!(stack.getItem() instanceof LumenCrystalItem)) {
            return false;
        }
        var component = DataComponentsAS.LUMEN.get(stack);
        return component != null && RegistriesAS.REGISTRY_LUMEN.get().getHolder(component.key())
                .filter(holder -> holder.is(this.tag)).isPresent();
    }

    @Override
    public Stream<ItemStack> getItems() {
        return RegistriesAS.REGISTRY_LUMEN.get().getTag(this.tag).stream().flatMap(holders -> holders.stream())
                .map(LumenCrystalItem::getCrystal);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public AstralIngredientSerializer<?> getType() {
        return IngredientsAS.LUMEN_TAG;
    }
}
