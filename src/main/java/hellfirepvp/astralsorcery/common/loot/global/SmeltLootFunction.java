/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.loot.global;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.util.LootUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.RecipeFinder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SmeltLootFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SmeltLootFunction extends LootModifier {

    public static final Codec<SmeltLootFunction> CODEC =
            RecordCodecBuilder.create(instance -> codecStart(instance).apply(instance, SmeltLootFunction::new));

    protected SmeltLootFunction(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    public static SmeltLootFunction of(LootItemCondition... conditions) {
        return new SmeltLootFunction(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!LootUtil.doesContextFulfill(context, LootContextParamSets.BLOCK)) return generatedLoot;
        ServerLevel level = context.getLevel();

        return generatedLoot.stream()
                .filter(stack -> !stack.isEmpty())
                .map(lootStack -> {
                    return RecipeFinder.of(level).findSmeltingRecipe(lootStack).map(recipeHolder -> {
                        SimpleContainer input = new SimpleContainer(lootStack);
                        AbstractCookingRecipe recipe = recipeHolder.value();
                        ItemStack result = recipe.assemble(input, level.registryAccess());
                        float exp = recipe.getExperience();

                        if (context.hasParam(LootContextParams.THIS_ENTITY)) {
                            Entity e = context.getParam(LootContextParams.THIS_ENTITY);
                            if (e instanceof Player player) {
                                ForgeEventFactory.firePlayerSmeltedEvent(player, result);
                            }
                        }

                        ItemStack tool = context.getParam(LootContextParams.TOOL);
                        if (!tool.isEmpty() && !(result.getItem() instanceof BlockItem)) {
                            int silkTouchLevel = tool.getEnchantmentLevel(Enchantments.SILK_TOUCH);
                            if (silkTouchLevel <= 0) {

                                int extraCount = 0;
                                int fortuneLevel = tool.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
                                if (fortuneLevel > 0) {
                                    extraCount = Math.max(context.getRandom().nextInt(fortuneLevel + 2) - 1, 0);
                                    result.setCount(result.getCount() * (extraCount + 1));
                                }

                                exp *= (extraCount + 1);
                                if (exp > 0) {
                                    int expCount = MiscUtil.roundChanced(exp, context.getRandom());
                                    if (expCount > 0) {
                                        Vec3 dropPos = context.getParamOrNull(LootContextParams.ORIGIN);
                                        if (dropPos != null) {
                                            level.addFreshEntity(new ExperienceOrb(level, dropPos.x(), dropPos.y(), dropPos.z(), expCount));
                                        }
                                    }
                                }
                            }
                        }

                        return result;
                    }).orElse(lootStack);
                })
                .collect(Collectors.toCollection(ObjectArrayList::of));
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
