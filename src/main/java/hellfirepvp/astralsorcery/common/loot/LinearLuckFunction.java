/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import hellfirepvp.astralsorcery.common.lib.LootAS;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LinearLuckFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LinearLuckFunction extends LootItemConditionalFunction {

    private static final Codec<List<Enchantment>> ENCHANTMENTS = BuiltInRegistries.ENCHANTMENT.byNameCodec().listOf();

    public static final LootItemConditionalFunction.Serializer<LinearLuckFunction> SERIALIZER =
            new LootItemConditionalFunction.Serializer<>() {
                @Override
                public void serialize(JsonObject json, LinearLuckFunction function, JsonSerializationContext context) {
                    super.serialize(json, function, context);
                    json.add("enchantments", ENCHANTMENTS.encodeStart(JsonOps.INSTANCE, function.enchantments)
                            .getOrThrow(false, message -> {}));
                }

                @Override
                public LinearLuckFunction deserialize(JsonObject json, JsonDeserializationContext context,
                                                       LootItemCondition[] conditions) {
                    var enchantments = ENCHANTMENTS.parse(JsonOps.INSTANCE, GsonHelper.getAsJsonArray(json, "enchantments"))
                            .getOrThrow(false, message -> { throw new JsonSyntaxException(message); });
                    return new LinearLuckFunction(conditions, enchantments);
                }
            };

    private final List<Enchantment> enchantments;

    protected LinearLuckFunction(LootItemCondition[] predicates, List<Enchantment> enchantments) {
        super(predicates);
        this.enchantments = List.copyOf(enchantments);
    }

    public static LootItemConditionalFunction.Builder<?> onlyLuck() {
        return simpleBuilder(conditions -> new LinearLuckFunction(conditions, List.of()));
    }

    public static LootItemConditionalFunction.Builder<?> luckAndEnchantments(List<Enchantment> enchantments) {
        return simpleBuilder(conditions -> new LinearLuckFunction(conditions, enchantments));
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        if (tool != null) {
            int luck = 0;
            Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
            if (entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(MobEffects.LUCK)) {
                luck = livingEntity.getEffect(MobEffects.LUCK).getAmplifier() + 1;
            }
            for (Enchantment enchantment : this.enchantments) {
                luck += tool.getEnchantmentLevel(enchantment);
            }

            RandomSource rand = context.getRandom();
            int count = stack.getCount();
            for (int i = 0; i < luck; i++) {
                count += rand.nextInt(3) + 1;
            }
            stack.setCount(count);
        }
        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootAS.LINEAR_LUCK_FUNCTION.get();
    }
}
