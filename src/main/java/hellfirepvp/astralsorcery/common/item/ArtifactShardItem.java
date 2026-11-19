/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.artifact.ArtifactType;
import hellfirepvp.astralsorcery.common.component.ArtifactComponent;
import hellfirepvp.astralsorcery.common.component.ArtifactTypeComponent;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactShardItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactShardItem extends ItemCustom {

    public ArtifactShardItem() {
        super(new Properties()
                .stacksTo(16));
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return CreativeTabsAS.CREATIVE_TAB_AS_ARTIFACTS.get();
    }

    @Override
    public void fillCreativeTab(Consumer<ItemStack> tabItems) {
        RegistriesAS.REGISTRY_ARTIFACT_TYPES.get().forEach(type -> {
            tabItems.accept(createShard(type));
        });
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level instanceof ServerLevel) {
            if (!DataComponentsAS.ARTIFACT_TYPE.has(stack)) {
                stack.shrink(stack.getCount());
            }
        }
    }

    public static ItemStack createShard(ArtifactType type) {
        ItemStack stack = ItemsAS.ARTIFACT_SHARD.toStack();
        DataComponentsAS.ARTIFACT_TYPE.set(stack, new ArtifactTypeComponent(type));
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (DataComponentsAS.ARTIFACT_TYPE.has(stack)) {
            ArtifactType type = DataComponentsAS.ARTIFACT_TYPE.get(stack).type();
            return Component.translatable("item.astralsorcery.artifact_shard.format", type.getName());
        }
        return super.getName(stack);
    }
}
