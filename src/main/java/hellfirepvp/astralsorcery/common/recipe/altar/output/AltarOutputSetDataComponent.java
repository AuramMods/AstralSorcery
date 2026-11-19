/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar.output;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.HolderLookup;
import hellfirepvp.astralsorcery.common.component.StackDataType;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarOutputSetDataComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarOutputSetDataComponent<T> extends AltarRecipeOutputModifier {

    public static final MapCodec<AltarOutputSetDataComponent<?>> CODEC =
            DataComponentsAS.TYPE_CODEC.dispatchMap("component_type", AltarOutputSetDataComponent::getComponentType, type -> makeCodec(type).codec());
    public static final StreamCodec<FriendlyByteBuf, AltarOutputSetDataComponent<?>> STREAM_CODEC =
            DataComponentsAS.TYPE_STREAM_CODEC.dispatch(AltarOutputSetDataComponent::getComponentType, AltarOutputSetDataComponent::makeStreamCodec);
    public static final Type<AltarOutputSetDataComponent<?>> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private static MapCodec<AltarOutputSetDataComponent<?>> makeCodec(StackDataType<?> type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                type.codec().fieldOf("value").forGetter(cmp -> MiscUtil.cast(cmp.getValue()))
        ).apply(instance, value -> new AltarOutputSetDataComponent<>(type, MiscUtil.cast(value))));
    }

    private static StreamCodec<FriendlyByteBuf, AltarOutputSetDataComponent<?>> makeStreamCodec(StackDataType<?> type) {
        return StreamCodec.composite(
                ByteBufCodecs.fromCodecWithRegistries(type.codec()),
                cmp -> MiscUtil.cast(cmp.getValue()),
                val -> new AltarOutputSetDataComponent<>(type, MiscUtil.cast(val)));
    }

    private final StackDataType<T> componentType;
    private final T value;

    private AltarOutputSetDataComponent(StackDataType<T> componentType, T value) {
        this.componentType = componentType;
        this.value = value;
    }

    public static <T> AltarOutputSetDataComponent<T> of(StackDataType<T> componentType, T value) {
        return new AltarOutputSetDataComponent<>(componentType, value);
    }

    public StackDataType<T> getComponentType() {
        return this.componentType;
    }

    public T getValue() {
        return this.value;
    }

    @Override
    public ItemStack modifyOutput(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        this.componentType.set(output, this.value);
        return output;
    }

    @Override
    public Type<?> getType() {
        return TYPE;
    }
}
