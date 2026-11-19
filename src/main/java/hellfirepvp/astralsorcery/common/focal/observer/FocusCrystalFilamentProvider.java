/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.focal.observer;

import hellfirepvp.observerlib.api.ObserverProvider;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocusCrystalFilamentProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocusCrystalFilamentProvider extends ObserverProvider {

    public FocusCrystalFilamentProvider(ResourceLocation id) {
        super(id);
    }

    @Nonnull
    @Override
    public FocusCrystalFilamentObserver provideObserver() {
        return new FocusCrystalFilamentObserver(this.getRegistryName());
    }
}
