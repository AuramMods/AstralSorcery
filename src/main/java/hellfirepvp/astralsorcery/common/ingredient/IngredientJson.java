/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.ingredient;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.crafting.Ingredient;

/** Routes ingredient JSON through Forge so vanilla tags and registered serializers work together. */
public final class IngredientJson {

    public static final Codec<Ingredient> CODEC = create(true);
    public static final Codec<Ingredient> NONEMPTY_CODEC = create(false);

    private IngredientJson() {}

    private static Codec<Ingredient> create(boolean allowEmpty) {
        return Codec.PASSTHROUGH.comapFlatMap(value -> {
            try {
                return DataResult.success(Ingredient.fromJson(value.convert(JsonOps.INSTANCE).getValue(), allowEmpty));
            } catch (JsonParseException | IllegalArgumentException e) {
                return DataResult.error(e::getMessage);
            }
        }, ingredient -> new Dynamic<>(JsonOps.INSTANCE, ingredient.toJson()));
    }
}
