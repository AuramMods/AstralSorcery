/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.crystal.CrystalPropertyCalculator;
import hellfirepvp.astralsorcery.common.item.base.CreativeTabItem;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CrystalToolItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface CrystalToolItem extends CreativeTabItem {

    UUID CRYSTAL_ATTACK_DAMAGE = UUID.nameUUIDFromBytes(
            AstralSorcery.key("crystal_properties_added_damage").toString().getBytes(StandardCharsets.UTF_8));

    static void applyAttackModifiers(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (event.getSlotType() != EquipmentSlot.MAINHAND || !(stack.getItem() instanceof CrystalToolItem tool)) {
            return;
        }
        float damage = tool.getCrystalAttackDamage(stack);
        if (damage != 0F) {
            event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                    CRYSTAL_ATTACK_DAMAGE, "Astral crystal properties", damage, AttributeModifier.Operation.ADDITION));
        }
    }

    default float getCrystalAttackDamage(ItemStack stack) {
        return CrystalPropertyCalculator.getToolDamage(0, stack, 1F / this.getCrystalCount());
    }

    @Override
    default void fillCreativeTab(Consumer<ItemStack> tabItems) {
        ItemStack stack = new ItemStack(this);
        DataComponentsAS.CRYSTAL_ATTRIBUTES.set(stack, CrystalAttributesComponent.defaultEmpty()
                .setAttributeTier(CrystalPropertiesAS.SIZE.get(), CrystalPropertiesAS.SIZE.get().getMaxTier() * this.getCrystalCount())
                .setAttributeTier(CrystalPropertiesAS.CUT.get(), CrystalPropertiesAS.CUT.get().getMaxTier() * this.getCrystalCount())
                .setAttributeTier(CrystalPropertiesAS.TOOL_EFFICIENCY.get(), CrystalPropertiesAS.TOOL_EFFICIENCY.get().getMaxTier() * this.getCrystalCount())
                .setAttributeTier(CrystalPropertiesAS.TOOL_DURABILITY.get(), CrystalPropertiesAS.TOOL_DURABILITY.get().getMaxTier() * this.getCrystalCount()));
        tabItems.accept(stack);
    }

    int getCrystalCount();
}
