/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar.output;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarOutputIncreaseEnchantments
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarOutputIncreaseEnchantments extends AltarRecipeOutputModifier {

    public static final MapCodec<AltarOutputIncreaseEnchantments> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.FLOAT.fieldOf("additional_level_chance").forGetter(AltarOutputIncreaseEnchantments::getAdditionalLevelChance)
    ).apply(inst, AltarOutputIncreaseEnchantments::new));

    public static final StreamCodec<FriendlyByteBuf, AltarOutputIncreaseEnchantments> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            AltarOutputIncreaseEnchantments::getAdditionalLevelChance,
            AltarOutputIncreaseEnchantments::new);
    public static final Type<AltarOutputIncreaseEnchantments> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private static final int MAX_ENCHANTMENT_LEVEL = 255;

    private final float additionalLevelChance;

    public AltarOutputIncreaseEnchantments(float additionalLevelChance) {
        this.additionalLevelChance = additionalLevelChance;
    }

    public static AltarOutputIncreaseEnchantments of() {
        return of(0F);
    }

    public static AltarOutputIncreaseEnchantments of(float additionalLevelChance) {
        return new AltarOutputIncreaseEnchantments(additionalLevelChance);
    }

    public float getAdditionalLevelChance() {
        return this.additionalLevelChance;
    }

    @Override
    public ItemStack modifyOutput(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        if (output.isEnchanted()) {
            var enchantments = EnchantmentHelper.getEnchantments(output);
            RandomSource rand = RandomSource.create();
            enchantments.replaceAll((enchantment, level) -> {
                if (enchantment.isCurse() || enchantment.getMaxLevel() == 1 || level >= MAX_ENCHANTMENT_LEVEL) return level;

                int increased = level + 1;
                while (increased < MAX_ENCHANTMENT_LEVEL && rand.nextFloat() < this.additionalLevelChance) {
                    increased++;
                }
                return increased;
            });
            EnchantmentHelper.setEnchantments(enchantments, output);
        }
        return output;
    }

    @Override
    public ItemStack modifyOutputForDisplay(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries, long tick) {
        ItemUtil.appendLore(output, Component.translatable("recipe.astralsorcery.result.increase_enchantment").withStyle(ChatFormatting.GRAY));
        return output;
    }

    @Override
    public Type<?> getType() {
        return TYPE;
    }
}
