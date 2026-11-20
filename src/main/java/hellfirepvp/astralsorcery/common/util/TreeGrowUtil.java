/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;

/**
 * Captures native tree feature placement for nearby tree beacons.
 */
public class TreeGrowUtil {

    private static final Map<ServerLevel, Map<BlockPos, Entry>> handlers = new HashMap<>();
    private static final Table<ServerLevel, ChunkPos, Set<Entry>> chunkHandlers = HashBasedTable.create();

    public static void addHandler(ServerLevel level, BlockPos pos, double range, BiPredicate<BlockPos, BlockState> handler) {
        removeHandler(level, pos);
        Entry entry = new Entry(pos.immutable(), range, handler);
        handlers.computeIfAbsent(level, ignored -> new HashMap<>()).put(entry.pos(), entry);
        Map<ChunkPos, Set<Entry>> chunks = chunkHandlers.row(level);
        for (ChunkPos chunk : affectedChunks(entry)) {
            chunks.computeIfAbsent(chunk, ignored -> new HashSet<>()).add(entry);
        }
    }

    public static void removeHandler(ServerLevel level, BlockPos pos) {
        Map<BlockPos, Entry> entries = handlers.get(level);
        if (entries == null) return;
        Entry entry = entries.remove(pos);
        if (entry == null) return;
        if (entries.isEmpty()) handlers.remove(level);

        Map<ChunkPos, Set<Entry>> chunks = chunkHandlers.row(level);
        for (ChunkPos chunk : affectedChunks(entry)) {
            chunks.computeIfPresent(chunk, (ignored, bucket) -> {
                bucket.remove(entry);
                return bucket.isEmpty() ? null : bucket;
            });
        }
    }

    private static List<ChunkPos> affectedChunks(Entry entry) {
        int radius = (int) Math.ceil(entry.range() / 16);
        ChunkPos center = new ChunkPos(entry.pos());
        return ChunkPos.rangeClosed(center, radius).toList();
    }

    public static boolean placeTree(ServerLevel level, BlockPos origin, BooleanSupplier growth) {
        Set<Entry> entries = chunkHandlers.get(level, new ChunkPos(origin));
        if (entries == null) return growth.getAsBoolean();

        List<Entry> nearby = entries.stream()
                .filter(entry -> entry.pos().distSqr(origin) <= entry.range() * entry.range())
                .sorted(Comparator.comparingDouble((Entry entry) -> entry.pos().distSqr(origin)).thenComparing(Entry::pos))
                .toList();
        if (nearby.isEmpty()) return growth.getAsBoolean();

        // Preserve Forge's chosen feature and notify clients only after the beacons process its changes.
        return BlockUtil.captureLevelChanges(level, () -> {
            boolean placed = growth.getAsBoolean();
            if (placed) {
                List<BlockPos> changedPositions = level.capturedBlockSnapshots.stream().map(BlockSnapshot::getPos).distinct().toList();
                for (BlockPos pos : changedPositions) {
                    BlockState grown = level.getBlockState(pos);
                    for (Entry entry : nearby) {
                        if (entry.handler().test(pos, grown)) break;
                    }
                }
            }
            return BlockUtil.ChangeResult.apply(placed);
        }).value();
    }

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(TreeGrowUtil::onUnload);
    }

    private static void onUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            handlers.remove(level);
            chunkHandlers.row(level).clear();
        }
    }

    private record Entry(BlockPos pos, double range, BiPredicate<BlockPos, BlockState> handler) {}
}
