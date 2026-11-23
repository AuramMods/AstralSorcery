/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.ingredient.*;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.RegisterEvent;

import java.util.List;

public final class IngredientsAS {

    public static final AstralIngredientSerializer<IngredientBridge> INGREDIENT_BRIDGE =
            serializer("bridge", IngredientBridge.CODEC);
    public static final AstralIngredientSerializer<IsEnchantedIngredient> IS_ENCHANTED =
            serializer("is_enchanted", IsEnchantedIngredient.CODEC);
    public static final AstralIngredientSerializer<IsStableArtifactIngredient> IS_STABLE_ARTIFACT =
            serializer("is_stable_artifact", IsStableArtifactIngredient.CODEC);
    public static final AstralIngredientSerializer<IsFlagSetIngredient> IS_FLAG_SET =
            serializer("is_flag_set", IsFlagSetIngredient.CODEC);
    public static final AstralIngredientSerializer<IsLumenBindableIngredient> LUMEN_BINDABLE =
            serializer("lumen_bindable", IsLumenBindableIngredient.CODEC);
    public static final AstralIngredientSerializer<HasStoredLumenIngredient> STORED_LUMEN =
            serializer("has_stored_lumen", HasStoredLumenIngredient.CODEC);
    public static final AstralIngredientSerializer<StackDataIngredient<?>> STACK_DATA =
            serializer("stack_data", StackDataIngredient.CODEC);
    public static final AstralIngredientSerializer<LumenTagIngredient> LUMEN_TAG =
            serializer("lumen_tag", LumenTagIngredient.CODEC);
    public static final AstralIngredientSerializer<CrystalPropertyTagIngredient> CRYSTAL_PROPERTY_TAG =
            serializer("crystal_property_tag", CrystalPropertyTagIngredient.CODEC);

    private IngredientsAS() {}

    public static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.RECIPE_SERIALIZER)) {
            for (AstralIngredientSerializer<?> serializer : List.of(
                    INGREDIENT_BRIDGE, IS_ENCHANTED, IS_STABLE_ARTIFACT, IS_FLAG_SET, LUMEN_BINDABLE, STORED_LUMEN,
                    STACK_DATA, LUMEN_TAG, CRYSTAL_PROPERTY_TAG)) {
                CraftingHelper.register(serializer.id(), serializer);
            }
        }
    }

    private static <T extends AstralIngredient> AstralIngredientSerializer<T> serializer(String name, MapCodec<T> codec) {
        return new AstralIngredientSerializer<>(AstralSorcery.key(name), codec);
    }
}
