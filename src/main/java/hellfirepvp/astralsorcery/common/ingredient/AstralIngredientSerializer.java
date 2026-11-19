/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public final class AstralIngredientSerializer<T extends AstralIngredient>
        implements IIngredientSerializer<AstralIngredient.Adapter> {

    private final ResourceLocation id;
    private final MapCodec<T> codec;
    private final StreamCodec<FriendlyByteBuf, T> wire;

    public AstralIngredientSerializer(ResourceLocation id, MapCodec<T> codec) {
        this.id = id;
        this.codec = codec;
        this.wire = ByteBufCodecs.fromCodec(codec.codec());
    }

    public ResourceLocation id() {
        return this.id;
    }

    @Override
    public AstralIngredient.Adapter parse(FriendlyByteBuf buffer) {
        return new AstralIngredient.Adapter(this.wire.decode(buffer));
    }

    @Override
    public AstralIngredient.Adapter parse(JsonObject json) {
        JsonElement data = json.has("ingredient_data") ? json.get("ingredient_data") : json;
        T ingredient = this.codec.codec().parse(JsonOps.INSTANCE, data).getOrThrow(false, message -> {
            throw new JsonSyntaxException("Invalid ingredient " + this.id + ": " + message);
        });
        return new AstralIngredient.Adapter(ingredient);
    }

    @Override
    public void write(FriendlyByteBuf buffer, AstralIngredient.Adapter ingredient) {
        this.wire.encode(buffer, this.value(ingredient));
    }

    public JsonObject toJson(AstralIngredient.Adapter ingredient) {
        JsonObject value = this.codec.codec().encodeStart(JsonOps.INSTANCE, this.value(ingredient))
                .getOrThrow(false, message -> {}).getAsJsonObject();
        JsonObject result = value;
        if (value.has("type")) {
            // Compound ingredient data can have its own dispatch type.
            result = new JsonObject();
            result.add("ingredient_data", value);
        }
        result.addProperty("type", this.id.toString());
        return result;
    }

    @SuppressWarnings("unchecked")
    private T value(AstralIngredient.Adapter ingredient) {
        return (T) ingredient.value();
    }
}
