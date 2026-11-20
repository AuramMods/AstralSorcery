/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import hellfirepvp.astralsorcery.common.lib.AttributesAS;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcherScale {

    @ModifyExpressionValue(method = "render", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, ordinal = 1,
            target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;shadowRadius:F"))
    private float astralsorcery$scalePlayerShadow(float radius, @Local(argsOnly = true) Entity entity) {
        return entity instanceof Player player ? radius * AttributesAS.getPlayerScale(player) : radius;
    }
}
