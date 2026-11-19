/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.focal.observer;

import hellfirepvp.astralsorcery.common.focal.FocusCrystalPlacementHelper;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.observerlib.api.ChangeObserver;
import hellfirepvp.observerlib.api.ObservableArea;
import hellfirepvp.observerlib.api.ObservableAreaBoundingBox;
import hellfirepvp.observerlib.api.block.BlockChangeSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocusCrystalFilamentObserver
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocusCrystalFilamentObserver extends ChangeObserver {

    public static final int OBSERVED_AREA_RADIUS = 3;
    private final AABB observerBox;
    private final ObservableArea observedArea;
    private final Set<BlockPos> positions = new HashSet<>();

    public FocusCrystalFilamentObserver(ResourceLocation providerId) {
        super(providerId);
        this.observerBox = AABB.ofSize(Vec3.ZERO, OBSERVED_AREA_RADIUS * 2, OBSERVED_AREA_RADIUS * 2, OBSERVED_AREA_RADIUS * 2);
        this.observedArea = new ObservableAreaBoundingBox(this.observerBox);
    }

    public Set<BlockPos> getPositions() {
        return Collections.unmodifiableSet(this.positions);
    }

    @Override
    public void initialize(LevelAccessor world, BlockPos center) {
        this.positions.addAll(FocusCrystalPlacementHelper.collectFocusCrystalFilaments(world, center, this.observerBox.move(center)));
    }

    @Nonnull
    @Override
    public ObservableArea getObservableArea() {
        return this.observedArea;
    }

    @Override
    public boolean notifyChange(Level world, BlockPos center, BlockChangeSet changeSet) {
        for (BlockChangeSet.StateChange change : changeSet.getChanges()) {
            if (change.getNewState().is(BlocksAS.STELLAR_FILAMENT.get())) {
                this.positions.add(change.getAbsolutePosition());
            } else {
                this.positions.remove(change.getAbsolutePosition());
            }
        }
        return true;
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        this.positions.clear();
        for (long packed : tag.getLongArray("positions")) {
            this.positions.add(BlockPos.of(packed));
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.putLongArray("positions", this.positions.stream().mapToLong(BlockPos::asLong).toArray());
    }
}
