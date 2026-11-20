/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.loot;

import hellfirepvp.astralsorcery.common.component.ColorComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.LootAS;
import hellfirepvp.astralsorcery.common.tile.base.TileDataDyeColor;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import hellfirepvp.astralsorcery.common.util.ColorReference;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Set;

public final class CopyDyeColorFunction extends LootItemConditionalFunction {

    public static final LootItemConditionalFunction.Serializer<CopyDyeColorFunction> SERIALIZER =
            LootSerializers.conditional(CopyDyeColorFunction::new);

    private CopyDyeColorFunction(LootItemCondition[] conditions) {
        super(conditions);
    }

    public static LootItemConditionalFunction.Builder<?> copyColor() {
        return simpleBuilder(CopyDyeColorFunction::new);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (context.getParamOrNull(LootContextParams.BLOCK_ENTITY) instanceof TileEntitySynchronized<?> tile
                && tile.getTileData() instanceof TileDataDyeColor coloredData) {
            DyeColor color = coloredData.getColor();
            if (color != null) {
                DataComponentsAS.COLOR.set(stack, new ColorComponent(ColorReference.Dye.of(color)));
            }
        }
        return stack;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.BLOCK_ENTITY);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootAS.COPY_DYE_COLOR_FUNCTION.get();
    }
}
