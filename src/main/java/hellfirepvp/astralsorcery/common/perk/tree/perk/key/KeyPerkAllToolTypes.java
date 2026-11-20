/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree.perk.key;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.types.PerkDataTypesAS;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tree.PerkCategory;
import hellfirepvp.astralsorcery.common.perk.tree.PerkType;
import hellfirepvp.astralsorcery.common.perk.tree.perk.KeyPerk;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.SidedHelper;
import hellfirepvp.astralsorcery.common.util.event.SidedEventBus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.LogicalSide;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: KeyPerkAllToolTypes
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class KeyPerkAllToolTypes extends KeyPerk {

    public static final MapCodec<KeyPerkAllToolTypes> CODEC = RecordCodecBuilder.mapCodec(inst -> perkModifierFields(inst).apply(inst, KeyPerkAllToolTypes::new));
    public static final PerkType<KeyPerkAllToolTypes> TYPE =
            PerkType.of(KeyPerkAllToolTypes.CODEC, PerkDataTypesAS.DEFAULT_DATA, KeyPerkAllToolTypes::new);

    private KeyPerkAllToolTypes(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.MAJOR, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    protected KeyPerkAllToolTypes(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
    }

    @Override
    protected void attachEventListeners(SidedEventBus sidedEventBus) {
        super.attachEventListeners(sidedEventBus);
        sidedEventBus.addListener(PlayerEvent.HarvestCheck.class, SidedEventBus.entityEvent(), this::onHarvestCheck);
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }

    private void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        if (event.canHarvest()) return;

        LogicalSide side = this.getSide(event.getEntity());
        PlayerProgress progress = ResearchManager.getProgress(event.getEntity(), side);
        if (progress.getPerkData().hasPerkEffect(this)) {
            ItemStack tool = event.getEntity().getInventory().getSelected();
            if (canHarvestWithPerk(tool, event.getTargetBlock())) {
                event.setCanHarvest(true);
            }
        }
    }

    public static void adjustHarvestSpeed(Inventory playerInv, BlockState state, CallbackInfoReturnable<Float> returnOvr) {
        Player thisPlayer = playerInv.player;
        LogicalSide side = SidedHelper.getSide(thisPlayer);
        PlayerProgress progress = ResearchManager.getProgress(thisPlayer, side);
        if (progress.getPerkData().hasPerkEffect(perk -> perk instanceof KeyPerkAllToolTypes)) {
            float existingSpeed = returnOvr.getReturnValueF();
            float newSpeed = getMiningSpeedWithPerk(playerInv.getSelected(), state, existingSpeed);
            if (newSpeed > existingSpeed) {
                returnOvr.setReturnValue(newSpeed);
            }
        }
    }

    private static boolean canHarvestWithPerk(ItemStack tool, BlockState state) {
        return supportsExtendedMining(tool, state) &&
                (tool.isCorrectToolForDrops(state) ||
                        tool.getItem() instanceof TieredItem tiered && TierSortingRegistry.isCorrectTierForDrops(tiered.getTier(), state));
    }

    private static float getMiningSpeedWithPerk(ItemStack tool, BlockState state, float existingSpeed) {
        if (existingSpeed > 1F || !supportsExtendedMining(tool, state)) {
            return existingSpeed;
        }

        float pickaxeSpeed = tool.getDestroySpeed(Blocks.STONE.defaultBlockState());
        if (tool.getItem() instanceof TieredItem tiered) {
            pickaxeSpeed = Math.max(pickaxeSpeed, tiered.getTier().getSpeed());
        }
        return Math.max(existingSpeed, pickaxeSpeed);
    }

    private static boolean supportsExtendedMining(ItemStack tool, BlockState state) {
        return !tool.isEmpty() && tool.canPerformAction(ToolActions.PICKAXE_DIG) &&
                (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_AXE) ||
                        state.is(BlockTags.MINEABLE_WITH_SHOVEL) || state.is(BlockTags.MINEABLE_WITH_HOE));
    }
}
