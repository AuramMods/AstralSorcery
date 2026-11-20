/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.item;

import hellfirepvp.astralsorcery.common.item.ChiselItem;
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.entity.ItemEntityChiselAttackable;
import hellfirepvp.astralsorcery.common.event.CrystalPropertyEvent;
import hellfirepvp.astralsorcery.common.item.crystal.RockCrystalItem;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ItemEntityCrystal
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ItemEntityCrystal extends ItemEntityChiselAttackable {

    public ItemEntityCrystal(EntityType<? extends ItemEntityCrystal> entityType, Level level) {
        super(entityType, level);
    }

    public ItemEntityCrystal(EntityType<? extends ItemEntityCrystal> entityType, Level level, double posX, double posY, double posZ, ItemStack itemStack) {
        super(entityType, level, posX, posY, posZ, itemStack);
    }

    public ItemEntityCrystal(EntityType<? extends ItemEntityCrystal> entityType, Level level, double posX, double posY, double posZ, ItemStack itemStack, double deltaX, double deltaY, double deltaZ) {
        super(entityType, level, posX, posY, posZ, itemStack, deltaX, deltaY, deltaZ);
    }

    public static EntityType.EntityFactory<ItemEntityCrystal> factory() {
        return ItemEntityCrystal::new;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return !source.is(DamageTypeTags.IS_EXPLOSION) && super.hurt(source, amount);
    }

    @Override
    public void onAttack(ServerPlayer sPlayer, ItemStack chisel) {
        ItemStack crystalStack = this.getItem();
        if (!(crystalStack.getItem() instanceof RockCrystalItem thisItem)) {
            return;
        }
        CrystalAttributesComponent thisAttributes = DataComponentsAS.CRYSTAL_ATTRIBUTES.getOrDefault(crystalStack, CrystalAttributesComponent.defaultEmpty());
        if (thisAttributes.isEmpty()) {
            return;
        }

        boolean didSplit = false;
        if (random.nextFloat() < 0.5F) {
            int fortuneLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE, sPlayer);
            didSplit = this.splitCrystal(thisItem, crystalStack, thisAttributes, fortuneLevel);
        }
        if (didSplit || random.nextFloat() < 0.5F) {
            ChiselItem.damageAfterUse(chisel, sPlayer, 1);
        }
    }

    protected boolean splitCrystal(RockCrystalItem thisItem, ItemStack crystalStack, CrystalAttributesComponent thisAttributes, int fortuneLevel) {
        RockCrystalItem splitCrystalItem = thisItem.getCrystalSplitItem();
        ItemStack splitCrystal = new ItemStack(splitCrystalItem);
        if (splitCrystal.isEmpty()) {
            return false;
        }
        int maxSplit = Mth.ceil(thisAttributes.getTotalTierCount() / 2F);
        if (maxSplit >= thisAttributes.getTotalTierCount()) {
            return false;
        }
        int lostModifiers = 0;
        if (maxSplit > 1 && this.random.nextFloat() < (0.9F / (fortuneLevel + 1))) {
            lostModifiers++;
            if (maxSplit > 2 && this.random.nextFloat() < (0.6F / (fortuneLevel + 1))) {
                lostModifiers++;
            }
        }

        CrystalAttributesComponent originalComponent = thisAttributes;
        CrystalAttributesComponent.GenerationProperties props = thisAttributes.getProperties();
        CrystalAttributesComponent newAttributes = CrystalAttributesComponent.empty(props.generateCount(), props.maxTierCount());

        for (int i = 0; i < maxSplit; i++) {
            CrystalAttributesComponent.TieredAttribute attr = MiscUtil.getRandomEntry(thisAttributes.getAttributes(), this.random).orElse(null);
            if (attr == null) {
                break;
            }

            thisAttributes = thisAttributes.setAttributeTier(attr, attr.getTier() - 1);
            if (lostModifiers > 0) {
                lostModifiers--;
            } else {
                newAttributes = newAttributes.setAttributeTier(attr, newAttributes.getAttributeTier(attr) + 1);
            }
        }

        CrystalPropertyEvent.Split splitEvent = new CrystalPropertyEvent.Split(crystalStack,
                CrystalPropertyEvent.Split.Type.CHISEL_SPLIT_CRYSTAL, originalComponent, List.of(thisAttributes, newAttributes));
        MinecraftForge.EVENT_BUS.post(splitEvent);
        List<CrystalAttributesComponent> results = splitEvent.getResultComponents();

        if (results.isEmpty()) {
            this.discard();
            return true;
        }

        thisAttributes = results.get(0);

        ItemStack thisStack = this.getItem();
        DataComponentsAS.CRYSTAL_ATTRIBUTES.set(thisStack, thisAttributes);

        List<CrystalAttributesComponent> otherComponents = results.subList(1, results.size());
        for (CrystalAttributesComponent other : otherComponents) {
            ItemStack otherStack = splitCrystal.copy();
            DataComponentsAS.CRYSTAL_ATTRIBUTES.set(otherStack, other);
            ItemUtil.dropItemNaturally(this.level(), this.getX(), this.getY() + 0.25F, this.getZ(), otherStack);
        }
        return true;
    }
}
