/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar.output;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.component.EnchantmentModifierComponent;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentAmuletGenerator;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentModifier;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarOutputAddEnchantmentModifier
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarOutputAddEnchantmentModifier extends AltarRecipeOutputModifier {

    public static final AltarOutputAddEnchantmentModifier INSTANCE = new AltarOutputAddEnchantmentModifier();
    public static final MapCodec<AltarOutputAddEnchantmentModifier> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<FriendlyByteBuf, AltarOutputAddEnchantmentModifier> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final Type<AltarOutputAddEnchantmentModifier> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private AltarOutputAddEnchantmentModifier() {}

    @Override
    public ItemStack modifyOutput(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        EnchantmentModifierComponent comp = DataComponentsAS.ENCHANTMENT_MODIFIERS.get(output);
        if (comp != null) {
            List<EnchantmentModifier> modifiers = new ArrayList<>(comp.modifiers());
            EnchantmentModifier newMod = EnchantmentAmuletGenerator.generateAnyModifier(registries);
            if (newMod != null) {
                modifiers.add(newMod);
                DataComponentsAS.ENCHANTMENT_MODIFIERS.set(output, new EnchantmentModifierComponent(modifiers));
            }
        }
        return output;
    }

    @Override
    public ItemStack modifyOutputForDisplay(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries, long tick) {
        ItemUtil.appendLore(output, Component.translatable("recipe.astralsorcery.result.add_enchantment_modifier").withStyle(ChatFormatting.GRAY));
        return output;
    }

    @Override
    public Type<?> getType() {
        return TYPE;
    }
}
