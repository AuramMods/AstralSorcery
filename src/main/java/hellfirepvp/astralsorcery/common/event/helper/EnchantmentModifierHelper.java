/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.helper;

import com.mojang.datafixers.util.Pair;
import hellfirepvp.astralsorcery.common.Mods;
import hellfirepvp.astralsorcery.common.component.EnchantmentModifierComponent;
import hellfirepvp.astralsorcery.common.enchantment.CombinedEnchantmentModifiers;
import hellfirepvp.astralsorcery.common.event.DynamicEnchantmentEvent;
import hellfirepvp.astralsorcery.common.integration.IntegrationCurios;
import hellfirepvp.astralsorcery.common.item.base.ItemStackEquipmentReference;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EnchantmentModifierHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EnchantmentModifierHelper {

    private static final ResourceLocation AIR_ID = new ResourceLocation("minecraft", "air");

    public static void attachListeners(IEventBus bus) {
        bus.addListener(EnchantmentModifierHelper::onPlayerTick);
    }

    private static void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        UUID playerId = event.player.getUUID();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = event.player.getItemBySlot(slot);
            if (!stack.isEmpty() && canHaveModifiedEnchantments(stack)) {
                ItemStackEquipmentReference.setEquipmentReference(stack, playerId, slot);
            }
        }
    }

    public static int applyToLevel(ItemStack stack, Enchantment enchantment, int level) {
        CombinedEnchantmentModifiers modifiers = getModifiersToApply(stack);
        Holder<Enchantment> holder = BuiltInRegistries.ENCHANTMENT.wrapAsHolder(enchantment);
        return applyToLevel(stack, holder, level, modifiers);
    }

    public static Map<Enchantment, Integer> applyToLevels(ItemStack stack, Map<Enchantment, Integer> levels) {
        CombinedEnchantmentModifiers modifiers = getModifiersToApply(stack);
        if (modifiers.getAddedToSpecific().isEmpty() && modifiers.getAddedToExistingSpecific().isEmpty()
                && modifiers.getAddedToAll() == 0) {
            return levels;
        }

        Map<Enchantment, Integer> result = new LinkedHashMap<>(levels);
        modifiers.getAddedToSpecific().keySet().forEach(holder -> result.putIfAbsent(holder.value(), 0));
        result.replaceAll((enchantment, level) ->
                applyToLevel(stack, BuiltInRegistries.ENCHANTMENT.wrapAsHolder(enchantment), level, modifiers));
        result.values().removeIf(level -> level <= 0);
        return result;
    }

    private static int applyToLevel(ItemStack stack, Holder<Enchantment> enchantment, int level,
                                    CombinedEnchantmentModifiers modifiers) {
        int added = modifiers.getAddedToSpecific().getOrDefault(enchantment, 0);
        if (added != 0 && enchantment.value().canEnchant(stack)) {
            level = adjustedLevel(level, added);
        }

        int addedToExisting = modifiers.getAddedToExistingSpecific().getOrDefault(enchantment, 0);
        if (level > 0 && addedToExisting != 0) {
            level = adjustedLevel(level, addedToExisting);
        }
        if (level > 0 && modifiers.getAddedToAll() != 0) {
            level = adjustedLevel(level, modifiers.getAddedToAll());
        }
        return level;
    }

    private static int adjustedLevel(int level, int modifier) {
        return (int) Math.max(0, Math.min(255, (long) level + modifier));
    }

    private static CombinedEnchantmentModifiers getModifiersToApply(ItemStack tool) {
        return getPlayer(tool).map(player -> {
            DynamicEnchantmentEvent.Add createEvent = new DynamicEnchantmentEvent.Add(player);
            MinecraftForge.EVENT_BUS.post(createEvent);
            CombinedEnchantmentModifiers modifiers = createEvent.getDynamicEnchantments().build();

            for (ItemStack modifierStack : findEnchantmentModifiers(player)) {
                EnchantmentModifierComponent mod = DataComponentsAS.ENCHANTMENT_MODIFIERS.getOrDefault(modifierStack, EnchantmentModifierComponent.EMPTY);
                modifiers = modifiers.combine(mod.combined());
            }

            DynamicEnchantmentEvent.Modify modifyEvent = new DynamicEnchantmentEvent.Modify(player, modifiers);
            MinecraftForge.EVENT_BUS.post(modifyEvent);
            return modifyEvent.getDynamicEnchantments().build();
        }).orElse(CombinedEnchantmentModifiers.of());
    }

    private static Optional<Player> getPlayer(ItemStack heldTool) {
        return getPlayerFromToolReference(heldTool).map(reference -> {
            Player player = reference.getFirst();
            ItemStack equipped = player.getItemBySlot(reference.getSecond());
            if (ItemStack.isSameItemSameTags(equipped, heldTool)) {
                return player;
            }
            return null;
        });
    }

    private static Optional<Pair<Player, EquipmentSlot>> getPlayerFromToolReference(ItemStack tool) {
        Pair<UUID, EquipmentSlot> reference = ItemStackEquipmentReference.getEquipmentReference(tool);
        if (reference == null) return Optional.empty();

        UUID playerId = reference.getFirst();

        LogicalSide side = EffectiveSide.get();
        if (side.isServer()) {
            MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
            if (srv != null && srv.isSameThread()) {
                return Optional.ofNullable(srv.getPlayerList().getPlayer(playerId))
                        .map(player -> new Pair<>(player, reference.getSecond()));
            }
        } else {
            return LogicalSidedProvider.CLIENTWORLD.get(LogicalSide.CLIENT)
                    .flatMap(level -> resolveClientReference(level, playerId))
                    .map(player -> new Pair<>(player, reference.getSecond()));
        }

        return Optional.empty();
    }

    @OnlyIn(Dist.CLIENT)
    private static Optional<Player> resolveClientReference(Level clientLevel, UUID playerId) {
        if (Minecraft.getInstance().isSameThread()) {
            return Optional.ofNullable(clientLevel.getPlayerByUUID(playerId));
        }
        return Optional.empty();
    }

    private static List<ItemStack> findEnchantmentModifiers(Player player) {
        List<ItemStack> foundEnchantmentModifierItems = new ArrayList<>();
        Predicate<ItemStack> filter = stack -> {
            //TODO specific slot filters
            return !DataComponentsAS.ENCHANTMENT_MODIFIERS.getOrDefault(stack, EnchantmentModifierComponent.EMPTY).isEmpty();
        };

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.HAND) continue;

            ItemStack stack = player.getItemBySlot(slot);
            if (filter.test(stack)) {
                foundEnchantmentModifierItems.add(stack);
            }
        }

        foundEnchantmentModifierItems.addAll(IntegrationCurios.getCurio(player, filter));
        return foundEnchantmentModifierItems;
    }

    private static boolean canHaveModifiedEnchantments(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id.equals(AIR_ID)) return false;
        try {
            if (!stack.getItem().isEnchantable(stack) || stack.getItem() instanceof BookItem) {
                return false;
            }
        } catch (Exception exc) {
            return false;
        }
        if (id.getNamespace().equals(Mods.DRACONIC_EVOLUTION.getModId())) {
            return false;
        }

        return true;
    }
}
