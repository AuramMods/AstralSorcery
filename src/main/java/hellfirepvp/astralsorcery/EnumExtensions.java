/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery;

import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import net.minecraft.world.item.Rarity;

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
}
