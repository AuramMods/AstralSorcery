/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.loot;

import hellfirepvp.astralsorcery.common.component.DynamicModifiersComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.LootAS;
import hellfirepvp.astralsorcery.common.perk.DynamismGemModifierHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GenerateDynamismGemRollsFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class GenerateDynamismGemRollsFunction extends LootItemConditionalFunction {

    public static final LootItemConditionalFunction.Serializer<GenerateDynamismGemRollsFunction> SERIALIZER =
            LootSerializers.conditional(GenerateDynamismGemRollsFunction::new);

    protected GenerateDynamismGemRollsFunction(LootItemCondition[] predicates) {
        super(predicates);
    }

    public static LootItemConditionalFunction.Builder<?> randomProperties() {
        return simpleBuilder(GenerateDynamismGemRollsFunction::new);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (DataComponentsAS.DYNAMIC_MODIFIERS.getOrDefault(stack, DynamicModifiersComponent.EMPTY).modifiers().isEmpty()) {
            DynamismGemModifierHelper.rollGem(stack);
        }
        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootAS.GENERATE_DYNAMISM_GEM_ROLLS_FUNCTION.get();
    }
}
