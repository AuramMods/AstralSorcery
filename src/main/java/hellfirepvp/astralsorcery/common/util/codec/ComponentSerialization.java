/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.chat.Component;

public final class ComponentSerialization {

    public static final Codec<Component> CODEC = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
        try {
            JsonElement json = dynamic.convert(JsonOps.INSTANCE).getValue();
            Component component = Component.Serializer.fromJson(json);
            return component == null ? DataResult.error(() -> "Missing text component") : DataResult.success(component);
        } catch (JsonParseException e) {
            return DataResult.error(e::getMessage);
        }
    }, component -> new Dynamic<>(JsonOps.INSTANCE,
            Component.Serializer.toJsonTree(component)));

    private ComponentSerialization() {}
}
