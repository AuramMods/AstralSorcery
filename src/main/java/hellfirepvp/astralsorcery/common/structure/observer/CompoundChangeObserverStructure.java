/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure.observer;

import com.google.common.collect.Lists;
import hellfirepvp.observerlib.api.ChangeObserver;
import hellfirepvp.observerlib.api.ObservableArea;
import hellfirepvp.observerlib.api.ObservableAreaBoundingBox;
import hellfirepvp.observerlib.api.block.BlockChangeSet;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CompoundChangeObserverStructure
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CompoundChangeObserverStructure extends ChangeObserver {

    private final List<MatchableStructure> structures;
    private final ObservableArea combinedArea;

    private final Map<MatchableStructure, Set<BlockPos>> structuresMismatches = new HashMap<>();

    public CompoundChangeObserverStructure(ResourceLocation providerId, List<MatchableStructure> structures) {
        super(providerId);
        this.structures = structures;
        this.combinedArea = this.buildCombinedArea(structures);
    }

    private ObservableArea buildCombinedArea(List<MatchableStructure> structures) {
        int minX = structures.stream().mapToInt(s -> s.getMinimumOffset().getX()).min().orElse(0);
        int minY = structures.stream().mapToInt(s -> s.getMinimumOffset().getY()).min().orElse(0);
        int minZ = structures.stream().mapToInt(s -> s.getMinimumOffset().getZ()).min().orElse(0);
        int maxX = structures.stream().mapToInt(s -> s.getMaximumOffset().getX()).max().orElse(0);
        int maxY = structures.stream().mapToInt(s -> s.getMaximumOffset().getY()).max().orElse(0);
        int maxZ = structures.stream().mapToInt(s -> s.getMaximumOffset().getZ()).max().orElse(0);
        return new ObservableAreaBoundingBox(new Vec3i(minX, minY, minZ), new Vec3i(maxX, maxY, maxZ));
    }

    @Override
    public void initialize(LevelAccessor world, BlockPos center) {
        for (MatchableStructure structure : this.structures) {
            Set<BlockPos> mismatches = new HashSet<>();
            for (BlockPos offset : structure.getContents().keySet()) {
                if (!structure.matchesSingleBlock(world, center, offset)) {
                    mismatches.add(offset);
                }
            }
            this.structuresMismatches.put(structure, mismatches);
        }
    }

    @Nonnull
    @Override
    public ObservableArea getObservableArea() {
        return this.combinedArea;
    }

    public Optional<MatchableStructure> getLastMatchedStructure() {
        List<MatchableStructure> reversed = Lists.reverse(this.structures);
        for (MatchableStructure structure : reversed) {
            Set<BlockPos> mismatches = this.structuresMismatches.getOrDefault(structure, Collections.emptySet());
            if (mismatches.isEmpty()) {
                return Optional.of(structure);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean notifyChange(Level world, BlockPos center, BlockChangeSet changeSet) {
        for (MatchableStructure structure : this.structures) {
            Set<BlockPos> mismatches = this.structuresMismatches.computeIfAbsent(structure, s -> new HashSet<>());
            for (BlockChangeSet.StateChange change : changeSet.getChanges()) {
                BlockPos relativePos = change.getRelativePosition();
                if (structure.hasBlockAt(relativePos) &&
                        !structure.matchesSingleBlock(world, center, relativePos, change.getNewState(), world.getBlockEntity(change.getAbsolutePosition()))) {
                    mismatches.add(relativePos);
                } else {
                    mismatches.remove(relativePos);
                }
            }

            mismatches.removeIf(pos -> !structure.hasBlockAt(pos));
        }
        return this.getLastMatchedStructure().isPresent();
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        this.structuresMismatches.clear();
        ListTag mismatches = tag.getList("mismatches", Tag.TAG_LONG_ARRAY);
        for (int i = 0; i < this.structures.size(); i++) {
            Set<BlockPos> positions = new HashSet<>();
            for (long packed : mismatches.getLongArray(i)) {
                positions.add(BlockPos.of(packed));
            }
            this.structuresMismatches.put(this.structures.get(i), positions);
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        ListTag mismatches = new ListTag();
        for (MatchableStructure structure : this.structures) {
            long[] positions = this.structuresMismatches.getOrDefault(structure, Set.of()).stream()
                    .mapToLong(BlockPos::asLong).toArray();
            mismatches.add(new LongArrayTag(positions));
        }
        tag.put("mismatches", mismatches);
    }
}
