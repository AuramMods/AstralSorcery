/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.init;

import hellfirepvp.astralsorcery.common.lumen.ILumenHandler;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class InitCapabilities {

    public static void init(RegisterCapabilitiesEvent event) {
        event.register(ILumenHandler.class);
    }
}
