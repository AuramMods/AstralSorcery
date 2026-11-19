/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container;

import hellfirepvp.astralsorcery.common.structure.PatternAltarT2Expanded;
import hellfirepvp.astralsorcery.common.structure.observer.CompoundChangeObserverStructure;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.IntPoint;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ContainerAltarResonance
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ContainerAltarResonance extends ContainerAltar {

    private final boolean isExpanded;

    public ContainerAltarResonance(@Nullable MenuType<?> menuType, TileAltar tile, Inventory playerInv, int containerId) {
        super(menuType, tile, playerInv, containerId);
        this.isExpanded = matchesExpandedStructure(tile);
        this.initSlots(playerInv, tile);
    }

    public ContainerAltarResonance(@Nullable MenuType<?> menuType, TileAltar tile, Inventory playerInv, int containerId, FriendlyByteBuf byteBuf) {
        super(menuType, tile, playerInv, containerId);
        this.isExpanded = byteBuf.readBoolean();
        this.initSlots(playerInv, tile);
    }

    private void initSlots(Inventory playerInv, TileAltar tile) {
        this.addDefaultPlayerSlots(playerInv, this.getPlayerInventoryOffset().x(), this.getPlayerInventoryOffset().y());
        this.addAltarGridSlots();
        this.addInnerAltarRelaySlots(tile.getLevel(), tile.getBlockPos());
        if (this.isExpanded()) {
            this.addOuterAltarRelaySlots(tile.getLevel(), tile.getBlockPos());
        }
    }

    public boolean isExpanded() {
        return this.isExpanded;
    }

    @Override
    protected IntPoint getAltarGridOffset() {
        return this.isExpanded() ? new IntPoint(56, 57) : new IntPoint(36, 37);
    }

    @Override
    protected IntPoint getPlayerInventoryOffset() {
        return this.isExpanded() ? new IntPoint(48, 173) : new IntPoint(28, 133);
    }

    public static boolean matchesExpandedStructure(TileAltar tile) {
        return tile.getStructureObserver().map(subscriber ->
                subscriber.getObserver() instanceof CompoundChangeObserverStructure observer
                        && observer.getLastMatchedStructure().filter(PatternAltarT2Expanded.class::isInstance).isPresent()
        ).orElse(false);
    }
}
