package hellfirepvp.astralsorcery.common.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.IngredientsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.stream.Stream;

public record CrystalPropertyTagIngredient(Ingredient base, TagKey<CrystalProperty> tag, int minimumTier) implements AstralIngredient {

    public static final MapCodec<CrystalPropertyTagIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            IngredientJson.CODEC.fieldOf("base").forGetter(CrystalPropertyTagIngredient::base),
            TagKey.codec(RegistriesAS.KEY_CRYSTAL_PROPERTIES).fieldOf("tag").forGetter(CrystalPropertyTagIngredient::tag),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("minimum_tier", 1).forGetter(CrystalPropertyTagIngredient::minimumTier)
    ).apply(instance, CrystalPropertyTagIngredient::new));

    public CrystalPropertyTagIngredient {
        if (minimumTier < 1) {
            throw new IllegalArgumentException("Crystal property tier must be positive");
        }
    }

    @Override
    public boolean test(ItemStack stack) {
        if (!this.base.test(stack)) {
            return false;
        }
        var properties = DataComponentsAS.CRYSTAL_ATTRIBUTES.getOrDefault(stack, CrystalAttributesComponent.defaultEmpty());
        var registry = RegistriesAS.REGISTRY_CRYSTAL_PROPERTIES.get();
        return properties.getAttributes().stream().anyMatch(attribute -> attribute.getTier() >= this.minimumTier
                && registry.getResourceKey(attribute.getProperty()).flatMap(registry::getHolder)
                .filter(holder -> holder.is(this.tag)).isPresent());
    }

    @Override
    public Stream<ItemStack> getItems() {
        return RegistriesAS.REGISTRY_CRYSTAL_PROPERTIES.get().getTag(this.tag).stream().flatMap(holders -> holders.stream())
                .flatMap(property -> Arrays.stream(this.base.getItems()).map(input -> {
                    ItemStack stack = input.copy();
                    var attributes = DataComponentsAS.CRYSTAL_ATTRIBUTES.getOrDefault(stack,
                            CrystalAttributesComponent.empty(1, this.minimumTier));
                    DataComponentsAS.CRYSTAL_ATTRIBUTES.set(stack, attributes.setAttributeTier(property.value(), this.minimumTier));
                    return stack;
                }));
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public AstralIngredientSerializer<?> getType() {
        return IngredientsAS.CRYSTAL_PROPERTY_TAG;
    }
}
