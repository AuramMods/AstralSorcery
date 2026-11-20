/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.MinecraftForge;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BasicMobEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class BasicMobEffect extends MobEffect {

    public BasicMobEffect(MobEffectCategory category, int color) {
        super(category, color);
        this.attachEventListeners(MinecraftForge.EVENT_BUS);
    }

    public void attachEventListeners(IEventBus bus) {}
}
