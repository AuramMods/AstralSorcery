/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.binding.effect;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.component.StoredItemsComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.types.LumenBindingEffectTypesAS;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingCollectDropsEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingCollectDropsEffect extends LumenBindingEffect {

    public static final LumenBindingCollectDropsEffect INSTANCE = new LumenBindingCollectDropsEffect();
    public static final MapCodec<LumenBindingCollectDropsEffect> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<FriendlyByteBuf, LumenBindingCollectDropsEffect> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public static void collectDrops(ServerPlayer player, List<ItemEntity> drops) {
        if (drops.isEmpty()) return;

        forEachEffect(player, LumenBindingCollectDropsEffect.class, (stack, effect) -> {
            int slot = ItemUtil.findItemsInInventory(player, item -> item.is(ItemsAS.AKASHIC_SINGULARITY.get())).keySet()
                    .stream().findFirst().orElse(-1);
            ItemStack singularity;
            if (slot == -1) {
                slot = player.getInventory().getFreeSlot();
                if (slot == -1) return;
                singularity = ItemsAS.AKASHIC_SINGULARITY.toStack();
            } else {
                singularity = player.getInventory().getItem(slot);
            }

            StoredItemsComponent contents = DataComponentsAS.STORED_ITEMS.getOrDefault(singularity, StoredItemsComponent.EMPTY);
            for (ItemEntity item : drops) {
                if (item.isRemoved()) continue;

                contents = contents.accept(item.getItem());
                item.discard();
            }
            DataComponentsAS.STORED_ITEMS.set(singularity, contents);
            player.getInventory().setItem(slot, singularity);
        });
    }

    @Override
    public List<Component> getDisplayText(LogicalSide side, ItemStack stack) {
        return List.of(Component.translatable("lumen.binding.astralsorcery.collect_drops"));
    }

    @Override
    public DeferredType<?> getType() {
        return LumenBindingEffectTypesAS.COLLECT_DROPS;
    }
}
