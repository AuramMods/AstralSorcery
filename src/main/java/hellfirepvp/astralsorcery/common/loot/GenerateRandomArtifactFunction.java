/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.loot;

import hellfirepvp.astralsorcery.common.component.ArtifactComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.LootAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GenerateRandomArtifactFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class GenerateRandomArtifactFunction extends LootItemConditionalFunction {

    public static final LootItemConditionalFunction.Serializer<GenerateRandomArtifactFunction> SERIALIZER =
            LootSerializers.conditional(GenerateRandomArtifactFunction::new);

    protected GenerateRandomArtifactFunction(LootItemCondition[] predicates) {
        super(predicates);
    }

    public static LootItemConditionalFunction.Builder<?> randomArtifact() {
        return simpleBuilder(GenerateRandomArtifactFunction::new);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        RegistriesAS.REGISTRY_ARTIFACT_TYPES.get().getRandom(context.getRandom()).ifPresent(type -> {
            DataComponentsAS.ARTIFACT.set(stack, ArtifactComponent.initialize(type.value()));
        });
        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootAS.GENERATE_RANDOM_ARTIFACT_FUNCTION.get();
    }
}
