/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import hellfirepvp.astralsorcery.common.event.helper.EnchantmentModifierHelper;
import hellfirepvp.astralsorcery.common.event.helper.VeiledItemStackHelper;
import hellfirepvp.astralsorcery.common.item.base.ItemStackEquipmentReference;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.extensions.IForgeItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MixinItemStack
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(ItemStack.class)
public abstract class MixinItemStack implements ItemStackEquipmentReference, IForgeItemStack {

    @Unique
    @Nullable
    private UUID astralSorcery_1_21$playerReference;

    @Unique
    @Nullable
    private EquipmentSlot astralSorcery_1_21$equippedSlot;

    @Nullable
    @Override
    public UUID getPlayerReference() {
        return this.astralSorcery_1_21$playerReference;
    }

    @Override
    public void setPlayerReference(@Nullable UUID playerReference) {
        if ((Object) this == ItemStack.EMPTY) return;
        this.astralSorcery_1_21$playerReference = playerReference;
    }

    @Nullable
    @Override
    public EquipmentSlot getEquippedSlot() {
        return this.astralSorcery_1_21$equippedSlot;
    }

    @Override
    public void setEquippedSlot(@Nullable EquipmentSlot slot) {
        if ((Object) this == ItemStack.EMPTY) return;
        this.astralSorcery_1_21$equippedSlot = slot;
    }

    @Inject(method = "copy", at = @At("RETURN"))
    public void copyPlayerReference(CallbackInfoReturnable<ItemStack> cir) {
        ItemStackEquipmentReference.setEquipmentReference(cir.getReturnValue(),
                this.astralSorcery_1_21$playerReference,
                this.astralSorcery_1_21$equippedSlot);
    }

    @Inject(method = "getRarity", at = @At("HEAD"), cancellable = true)
    private void getAstralRarity(CallbackInfoReturnable<Rarity> cir) {
        Rarity rarity = DataComponentsAS.RARITY.get(MiscUtil.cast(this));
        if (rarity != null) {
            cir.setReturnValue(rarity);
        }
    }

    @Inject(method = "getHoverName", at = @At("HEAD"), cancellable = true)
    public void getVeiledHoverName(CallbackInfoReturnable<Component> cir) {
        ItemStack thisStack = MiscUtil.cast(this);
        ItemStack displayed = VeiledItemStackHelper.getDisplayedStack(thisStack);
        if (displayed != thisStack) {
            cir.setReturnValue(VeiledItemStackHelper.markUnknownVeil(thisStack, displayed.getHoverName()));
        }
    }

    @Override
    public int getEnchantmentLevel(Enchantment enchantment) {
        ItemStack stack = MiscUtil.cast(this);
        int level = stack.getItem().getEnchantmentLevel(stack, enchantment);
        return EnchantmentModifierHelper.applyToLevel(stack, enchantment, level);
    }

    @Override
    public Map<Enchantment, Integer> getAllEnchantments() {
        ItemStack stack = MiscUtil.cast(this);
        return EnchantmentModifierHelper.applyToLevels(stack, stack.getItem().getAllEnchantments(stack));
    }

    @WrapOperation(method = "getTooltipLines", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;appendEnchantmentNames(Ljava/util/List;Lnet/minecraft/nbt/ListTag;)V"))
    private void showEffectiveEnchantments(List<Component> lines, ListTag tags, Operation<Void> original) {
        ItemStack stack = MiscUtil.cast(this);
        original.call(lines, this.astralSorcery$tooltipEnchantmentTags(stack, tags));
    }

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;hasTag()Z"))
    private void showEffectiveEnchantmentsWithoutTags(@Nullable Player player, TooltipFlag flag,
                                                       CallbackInfoReturnable<List<Component>> cir,
                                                       @Local(ordinal = 0) List<Component> lines) {
        ItemStack stack = MiscUtil.cast(this);
        if (!stack.hasTag()) {
            ListTag tags = this.astralSorcery$tooltipEnchantmentTags(stack, new ListTag());
            ItemStack.appendEnchantmentNames(lines, tags);
        }
    }

    @Unique
    private ListTag astralSorcery$tooltipEnchantmentTags(ItemStack stack, ListTag originalTags) {
        var originalLevels = EnchantmentHelper.deserializeEnchantments(originalTags);
        var effectiveLevels = EnchantmentModifierHelper.applyToLevels(stack, originalLevels);
        if (effectiveLevels.equals(originalLevels)) {
            return originalTags;
        }
        ListTag tags = new ListTag();
        effectiveLevels.forEach((enchantment, level) ->
                tags.add(EnchantmentHelper.storeEnchantment(EnchantmentHelper.getEnchantmentId(enchantment), level)));
        return tags;
    }
}
