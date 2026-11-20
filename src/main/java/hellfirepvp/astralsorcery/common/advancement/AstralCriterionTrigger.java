/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.advancement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;

public abstract class AstralCriterionTrigger<P> extends SimpleCriterionTrigger<AstralCriterionTrigger.Condition<P>> {

    protected static final Codec<ItemPredicate> ITEM_PREDICATE_CODEC = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
        try {
            return DataResult.success(ItemPredicate.fromJson(dynamic.convert(JsonOps.INSTANCE).getValue()));
        } catch (JsonParseException exception) {
            return DataResult.error(exception::getMessage);
        }
    }, predicate -> new Dynamic<>(JsonOps.INSTANCE, predicate.serializeToJson()));

    private final ResourceLocation id;
    private final MapCodec<P> fields;

    protected AstralCriterionTrigger(String name, MapCodec<P> fields) {
        this.id = AstralSorcery.key(name);
        this.fields = fields;
    }

    @Override
    public final ResourceLocation getId() {
        return this.id;
    }

    @Override
    protected Condition<P> createInstance(JsonObject json, ContextAwarePredicate player, DeserializationContext context) {
        P value = this.fields.codec().parse(JsonOps.INSTANCE, json).getOrThrow(false, message -> {
            throw new JsonSyntaxException("Invalid conditions for " + this.id + ": " + message);
        });
        return new Condition<>(this.id, player, this.fields, value);
    }

    protected final CriterionTriggerInstance criterion(P value) {
        return new Condition<>(this.id, ContextAwarePredicate.ANY, this.fields, value);
    }

    public static final class Condition<P> extends AbstractCriterionTriggerInstance {

        private final MapCodec<P> fields;
        private final P value;

        private Condition(ResourceLocation id, ContextAwarePredicate player, MapCodec<P> fields, P value) {
            super(id, player);
            this.fields = fields;
            this.value = value;
        }

        public P value() {
            return this.value;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            var json = super.serializeToJson(context);
            var encoded = this.fields.codec().encodeStart(JsonOps.INSTANCE, this.value).getOrThrow(false, message -> {
                throw new JsonSyntaxException("Cannot serialize conditions for " + this.getCriterion() + ": " + message);
            }).getAsJsonObject();
            encoded.entrySet().forEach(field -> json.add(field.getKey(), field.getValue()));
            return json;
        }
    }
}
