/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.level;

import hellfirepvp.observerlib.common.data.WorldCacheDomain;
import hellfirepvp.observerlib.common.data.base.SectionWorldData;
import hellfirepvp.observerlib.common.data.base.WorldSection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

/** ObserverLib stores these caches entirely in section files. */
abstract class AstralSectionData<S extends WorldSection> extends SectionWorldData<S> {

    protected AstralSectionData(WorldCacheDomain.SaveKey<?> key, int sectionPrecision) {
        super(key, sectionPrecision);
    }

    @Override
    public final void writeToNBT(CompoundTag metadata) {}

    @Override
    public final void readFromNBT(CompoundTag metadata) {}

    @Override
    public void updateTick(Level level) {}
}
