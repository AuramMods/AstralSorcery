package hellfirepvp.astralsorcery.common.integration.gamestages;

import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public final class GameStagesBridge {

    private GameStagesBridge() {}

    public static boolean validStage(String stage) {
        return GameStageHelper.isValidStageName(stage);
    }

    public static boolean hasStages(ServerPlayer player, List<String> stages, boolean any) {
        return any ? GameStageHelper.hasAnyOf(player, stages) : GameStageHelper.hasAllOf(player, stages);
    }
}
