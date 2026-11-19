/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import hellfirepvp.astralsorcery.common.lumen.ILumenHandler;
import hellfirepvp.astralsorcery.common.lumen.capability.LumenHandlerView;
import hellfirepvp.astralsorcery.common.util.inventory.InventoryView;
import hellfirepvp.astralsorcery.common.util.tank.FluidTankView;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class TileCapabilities {

    private final Map<Access, LazyOptional<?>> handles = new HashMap<>();
    private boolean valid = true;

    <T> LazyOptional<T> get(Object data, Capability<T> capability, @Nullable Direction side) {
        if (!this.valid) {
            return LazyOptional.empty();
        }
        LazyOptional<?> handle = this.handles.computeIfAbsent(new Access(capability, side), access -> {
            Object value = resolve(data, access);
            return value == null ? null : LazyOptional.of(() -> value);
        });
        return handle == null ? LazyOptional.empty() : handle.cast();
    }

    @Nullable
    private static Object resolve(Object data, Access access) {
        Capability<?> capability = access.capability();
        Direction side = access.side();
        if (capability == ForgeCapabilities.ITEM_HANDLER && data instanceof ItemStorage storage) {
            return storage.getInventory().getInventoryAccess(side);
        }
        if (capability == ForgeCapabilities.FLUID_HANDLER && data instanceof FluidStorage storage) {
            return storage.getFluidTank().getFluidAccess(side);
        }
        if (capability == ILumenHandler.CAPABILITY && data instanceof LumenStorage storage) {
            return storage.getLumenHandler().getLumenAccess(side);
        }
        return null;
    }

    void clear() {
        var previous = this.handles.values().toArray(LazyOptional<?>[]::new);
        this.handles.clear();
        for (LazyOptional<?> handle : previous) {
            handle.invalidate();
        }
    }

    void invalidate() {
        this.valid = false;
        this.clear();
    }

    void revive() {
        this.valid = true;
    }

    private record Access(Capability<?> capability, @Nullable Direction side) {}

    public interface ItemStorage {
        InventoryView getInventory();
    }

    public interface FluidStorage {
        FluidTankView getFluidTank();
    }

    public interface LumenStorage {
        LumenHandlerView getLumenHandler();
    }
}
