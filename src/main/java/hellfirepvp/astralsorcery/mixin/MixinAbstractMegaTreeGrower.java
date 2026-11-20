/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import hellfirepvp.astralsorcery.common.util.TreeGrowUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractMegaTreeGrower.class)
public abstract class MixinAbstractMegaTreeGrower {

    @WrapOperation(method = "placeMega", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;place(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Z"))
    private boolean astralsorcery$captureTree(ConfiguredFeature<?, ?> feature, WorldGenLevel level, ChunkGenerator generator,
                                               RandomSource random, BlockPos placement, Operation<Boolean> original,
                                               @Local(argsOnly = true) BlockPos sapling) {
        return TreeGrowUtil.placeTree((ServerLevel) level, sapling, () -> original.call(feature, level, generator, random, placement));
    }
}
