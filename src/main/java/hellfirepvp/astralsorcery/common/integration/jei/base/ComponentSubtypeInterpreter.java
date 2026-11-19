/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei.base;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import hellfirepvp.astralsorcery.common.component.StackDataType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ComponentSubtypeInterpreter
 * Created by HellFirePvP
 * Date: 25.09.2026 / 19:16
 */
public class ComponentSubtypeInterpreter<T> implements ISubtypeInterpreter<ItemStack> {

    private final StackDataType<T> componentType;
    private final Function<T, ?> valueDataExtractor;

    public ComponentSubtypeInterpreter(StackDataType<T> componentType, Function<T, ?> valueDataExtractor) {
        this.componentType = componentType;
        this.valueDataExtractor = valueDataExtractor;
    }

    @Nullable
    @Override
    public Object getSubtypeData(ItemStack ingredient, UidContext context) {
        T component = this.componentType.get(ingredient);
        if (component == null) return null;
        return this.valueDataExtractor.apply(component);
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        return "";
    }
}
