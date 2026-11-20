/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.loot;

import hellfirepvp.astralsorcery.common.item.tool.CrystalToolItem;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.LootAS;
import hellfirepvp.astralsorcery.common.tile.base.TileDataCrystalAttributeContainer;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CopyCrystalPropertiesFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CopyCrystalPropertiesFunction extends LootItemConditionalFunction {

    public static final LootItemConditionalFunction.Serializer<CopyCrystalPropertiesFunction> SERIALIZER =
            LootSerializers.conditional(CopyCrystalPropertiesFunction::new);

    protected CopyCrystalPropertiesFunction(LootItemCondition[] predicates) {
        super(predicates);
    }

    public static LootItemConditionalFunction.Builder<?> copyProperties() {
        return simpleBuilder(CopyCrystalPropertiesFunction::new);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (DataComponentsAS.CRYSTAL_ATTRIBUTES.has(stack) && context.hasParam(LootContextParams.BLOCK_ENTITY)) {
            if (context.getParamOrNull(LootContextParams.BLOCK_ENTITY) instanceof TileEntitySynchronized<?> tile) {
                if (tile.getTileData() instanceof TileDataCrystalAttributeContainer attributeContainer) {
                    DataComponentsAS.CRYSTAL_ATTRIBUTES.set(stack, attributeContainer.getCrystalAttributes());
                }
            }
        }
        CrystalToolItem.updateModifiers(stack);
        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootAS.COPY_CRYSTAL_PROPERTIES_FUNCTION.get();
    }
}
