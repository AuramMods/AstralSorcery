package hellfirepvp.astralsorcery.common.recipe;

import hellfirepvp.astralsorcery.mixin.AccessorItemEntityThrower;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public record StageActor(@Nullable ServerPlayer player, boolean attributed) {

    public static final StageActor AUTOMATION = new StageActor(null, false);
    private static final StageActor UNAVAILABLE = new StageActor(null, true);
    public static final String MIXED_THROWERS = "astralsorcery_mixed_throwers";

    public static StageActor fromPlayer(@Nullable ServerPlayer player) {
        return player == null || player instanceof FakePlayer ? AUTOMATION : new StageActor(player, true);
    }

    public static StageActor fromOwner(ServerLevel level, @Nullable UUID id) {
        if (id == null) return AUTOMATION;
        ServerPlayer player = level.getServer().getPlayerList().getPlayer(id);
        return player == null ? UNAVAILABLE : fromPlayer(player);
    }

    public static StageActor fromOwners(ServerLevel level, @Nullable UUID first, @Nullable UUID second) {
        return Objects.equals(first, second) ? fromOwner(level, first) : UNAVAILABLE;
    }

    public static StageActor fromItems(ServerLevel level, Collection<ItemEntity> items) {
        UUID thrower = null;
        boolean anonymous = false;
        for (ItemEntity item : items) {
            if (item.getPersistentData().getBoolean(MIXED_THROWERS)) return UNAVAILABLE;
            UUID id = ((AccessorItemEntityThrower) item).astralsorcery$getThrower();
            if (id == null) {
                anonymous = true;
            } else if (thrower == null) {
                thrower = id;
            } else if (!thrower.equals(id)) {
                return UNAVAILABLE;
            }
        }
        if (anonymous && thrower != null) return UNAVAILABLE;
        return fromOwner(level, thrower);
    }
}
