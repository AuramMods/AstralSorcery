/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei.base;

import hellfirepvp.astralsorcery.common.component.StackDataType;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ComponentSubtypeInterpreter
 * Created by HellFirePvP
 * Date: 25.09.2026 / 19:16
 */
public class ComponentSubtypeInterpreter<T> implements IIngredientSubtypeInterpreter<ItemStack> {

    private final StackDataType<T> componentType;
    private final Function<T, ResourceLocation> subtypeId;

    public ComponentSubtypeInterpreter(StackDataType<T> componentType, Function<T, ResourceLocation> subtypeId) {
        this.componentType = componentType;
        this.subtypeId = subtypeId;
    }

    @Override
    public String apply(ItemStack ingredient, UidContext context) {
        T component = this.componentType.get(ingredient);
        if (component == null) return NONE;
        ResourceLocation id = this.subtypeId.apply(component);
        return id == null ? NONE : id.toString();
    }
}
