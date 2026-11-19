/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.client.screen.ScreenStructureMarker;
import hellfirepvp.astralsorcery.common.block.tile.base.BaseTileBlock;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.tile.TileStructureMarker;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StructureMarkerBlock
 * Created by HellFirePvP
 * Date: 06.10.2026 / 21:35
 */
public class StructureMarkerBlock extends BaseTileBlock<TileStructureMarker> {

    public StructureMarkerBlock(Properties properties) {
        super(properties, TileEntitiesAS.STRUCTURE_MARKER);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND || !player.canUseGameMasterBlocks()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            openMarkerScreen(level, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @OnlyIn(Dist.CLIENT)
    private static void openMarkerScreen(Level level, BlockPos pos) {
        MiscUtil.getTileAt(level, pos, TileStructureMarker.class, true).ifPresent(ScreenStructureMarker::open);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
