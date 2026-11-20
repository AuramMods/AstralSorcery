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
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.function.Function;

public final class LootSerializers {

    private LootSerializers() {}

    static <T extends LootItemConditionalFunction> LootItemConditionalFunction.Serializer<T> conditional(
            Function<LootItemCondition[], T> constructor) {
        return new LootItemConditionalFunction.Serializer<>() {
            @Override
            public T deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions) {
                return constructor.apply(conditions);
            }
        };
    }

    public static <T> Serializer<T> fields(MapCodec<T> fields) {
        return new Serializer<>() {
            @Override
            public void serialize(JsonObject json, T value, JsonSerializationContext context) {
                var encoded = fields.codec().encodeStart(JsonOps.INSTANCE, value).getOrThrow(false, message -> {})
                        .getAsJsonObject();
                encoded.entrySet().forEach(entry -> json.add(entry.getKey(), entry.getValue()));
            }

            @Override
            public T deserialize(JsonObject json, JsonDeserializationContext context) {
                return fields.codec().parse(JsonOps.INSTANCE, json).getOrThrow(false, message -> {
                    throw new JsonSyntaxException(message);
                });
            }
        };
    }
}
