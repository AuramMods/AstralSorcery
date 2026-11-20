/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.codec;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;

import java.util.function.Function;

public final class CriterionPredicateCodecs {

    public static final Codec<BlockPredicate> BLOCK = codec(BlockPredicate::fromJson, BlockPredicate::serializeToJson);
    public static final Codec<EntityPredicate> ENTITY = codec(EntityPredicate::fromJson, EntityPredicate::serializeToJson);

    private CriterionPredicateCodecs() {}

    private static <T> Codec<T> codec(Function<JsonElement, T> read, Function<T, JsonElement> write) {
        return Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
            JsonElement json = dynamic.convert(JsonOps.INSTANCE).getValue();
            if (!json.isJsonObject() && !json.isJsonNull()) {
                return DataResult.error(() -> "Expected a criterion predicate object");
            }
            try {
                if (json instanceof JsonObject object && object.size() == 0) {
                    json = JsonNull.INSTANCE;
                }
                return DataResult.success(read.apply(json));
            } catch (RuntimeException e) {
                return DataResult.error(() -> "Invalid criterion predicate: " + e.getMessage());
            }
        }, predicate -> {
            JsonElement json = write.apply(predicate);
            if (json.isJsonNull()) {
                json = new JsonObject();
            }
            omitMissingFields(json);
            return new Dynamic<>(JsonOps.INSTANCE, json);
        });
    }

    private static void omitMissingFields(JsonElement json) {
        if (json instanceof JsonObject object) {
            object.entrySet().removeIf(entry -> entry.getValue().isJsonNull());
            object.entrySet().forEach(entry -> omitMissingFields(entry.getValue()));
        } else if (json instanceof JsonArray array) {
            array.forEach(CriterionPredicateCodecs::omitMissingFields);
        }
    }
}
