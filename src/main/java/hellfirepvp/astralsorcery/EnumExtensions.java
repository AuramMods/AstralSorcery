/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import net.minecraft.world.item.Rarity;

import java.util.Locale;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EnumExtensions
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EnumExtensions {

    public static final Rarity RARITY_RELIC = Rarity.create("ASTRALSORCERY_RELIC",
            style -> style.withColor(ColorsAS.RARITY_RELIC.getColor()));
    public static final Rarity RARITY_ARTIFACT = Rarity.create("ASTRALSORCERY_ARTIFACT",
            style -> style.withColor(ColorsAS.RARITY_ARTIFACT.getColor()));

    public static final Codec<Rarity> CODEC = Codec.STRING.comapFlatMap(name -> {
        return switch (name) {
            case "astralsorcery:relic" -> DataResult.success(RARITY_RELIC);
            case "astralsorcery:artifact" -> DataResult.success(RARITY_ARTIFACT);
            default -> {
                try {
                    yield DataResult.success(Rarity.valueOf(name.toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException e) {
                    yield DataResult.error(() -> "Unknown item rarity " + name);
                }
            }
        };
    }, rarity -> {
        if (rarity == RARITY_RELIC) {
            return "astralsorcery:relic";
        }
        if (rarity == RARITY_ARTIFACT) {
            return "astralsorcery:artifact";
        }
        return rarity.name().toLowerCase(Locale.ROOT);
    });
}
