/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.worldgen.structure.IgnoreWaterloggingPoolPiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(StructureTemplate.class)
public abstract class MixinStructureTemplate {

    @ModifyVariable(method = "placeInWorld", at = @At("HEAD"), argsOnly = true)
    private StructurePlaceSettings astralsorcery$applyLiquidSettings(StructurePlaceSettings settings) {
        if (IgnoreWaterloggingPoolPiece.ignoresWaterlogging()) {
            return settings.copy().setKeepLiquids(false);
        }
        return settings;
    }
}
