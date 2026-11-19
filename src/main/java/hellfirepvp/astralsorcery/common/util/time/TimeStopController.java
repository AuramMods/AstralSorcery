/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.time;

import hellfirepvp.astralsorcery.common.data.sync.SyncDataManager;
import hellfirepvp.astralsorcery.common.lib.types.SyncDataTypesAS;
import hellfirepvp.astralsorcery.common.util.EntityMatchPredicate;
import hellfirepvp.astralsorcery.common.util.data.MapStream;
import hellfirepvp.astralsorcery.common.util.tick.TimeoutList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TimeStopController
 * Created by HellFirePvP
 * Date: 08.10.2026 / 14:00
 */
public class TimeStopController {

    private static final Map<ResourceKey<Level>, Map<PosIdentifier, TimeStopZone>> activeStopZones = new HashMap<>();
    private static final TimeoutList<LivingEntity> freezeTimeout = new TimeoutList<>(entity -> {
        SyncDataManager.getInstance().getData(SyncDataTypesAS.TIME_STOP_ENTITIES).unfreeze(entity);
    });

    private TimeStopController() {}

    public static List<TimeStopZone> getZone(ServerLevel sLevel, BlockPos pos) {
        return MapStream.of(activeStopZones.getOrDefault(sLevel.dimension(), Collections.emptyMap()))
                .filterKey(id -> id.pos().equals(pos))
                .valueStream()
                .toList();
    }

    public static TimeStopZone create(ServerLevel sLevel, UUID id, BlockPos pos, float range, int maxAge, EntityMatchPredicate entityFilter) {
        TimeStopZone zone = new TimeStopZone(id, pos, range, entityFilter, maxAge);
        activeStopZones.computeIfAbsent(sLevel.dimension(), key -> new HashMap<>())
                .put(new PosIdentifier(pos, id), zone);

        SyncDataManager.getInstance().getData(SyncDataTypesAS.TIME_STOP_ZONES).update(sLevel, zone);
        return zone;
    }

    public static boolean mayBeFrozen(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel sLevel)) return false;
        return activeStopZones.getOrDefault(sLevel.dimension(), Collections.emptyMap()).values().stream()
                .anyMatch(zone -> zone.shouldInterceptEntityTick(sLevel, entity));
    }

    public static void freeze(LivingEntity entity) {
        if (entity.level().isClientSide()) return;

        if (freezeTimeout.setOrAddTimeout(20, entity)) {
            SyncDataManager.getInstance().getData(SyncDataTypesAS.TIME_STOP_ENTITIES).freeze(entity);
        }
    }

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(TimeStopController::onLevelTick);
        bus.addListener(freezeTimeout::onServerTick);

        bus.addListener(TimeStopController::onKnockback);
        bus.addListener(TimeStopController::onDestroyBlock);

        bus.addListener(TimeStopController::onClientEffectTick);
    }

    private static void onLevelTick(LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (!(event.level instanceof ServerLevel sLevel)) return;

        activeStopZones.getOrDefault(sLevel.dimension(), Collections.emptyMap()).values().removeIf(zone -> {
            if (!zone.isActive()) {
                zone.stopEffect(sLevel);

                SyncDataManager.getInstance().getData(SyncDataTypesAS.TIME_STOP_ZONES).remove(sLevel, zone);
                return true;
            }
            zone.levelTick(sLevel);
            if (zone.isDirty()) {
                SyncDataManager.getInstance().getData(SyncDataTypesAS.TIME_STOP_ZONES).update(sLevel, zone);
                zone.clearDirty();
            }
            return false;
        });
    }

    private static void onKnockback(LivingKnockBackEvent event) {
        if (TimeStopEntityHelper.skipAndRunLivingTick(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    private static void onDestroyBlock(LivingDestroyBlockEvent event) {
        if (TimeStopEntityHelper.skipAndRunLivingTick(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    private static void onClientEffectTick(ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        TimeStopEffectHelper.playClientEffectTick();
    }

    public record PosIdentifier(BlockPos pos, UUID id) {}
}
