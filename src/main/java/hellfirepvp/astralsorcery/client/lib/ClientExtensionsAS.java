/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.lib;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ClientExtensionsAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ClientExtensionsAS {

    public static final IClientBlockExtensions NO_BLOCK_PARTICLES = new IClientBlockExtensions() {
        @Override
        public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
            return true;
        }

        @Override
        public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
            return true;
        }
    };
}
