/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import hellfirepvp.astralsorcery.common.lib.AttributesAS;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class MixinPlayerScale {

    @Unique
    private float astralsorcery$appliedScale = 1;

    @ModifyReturnValue(method = "getDimensions", at = @At("RETURN"))
    private EntityDimensions astralsorcery$scaleDimensions(EntityDimensions dimensions) {
        return dimensions.scale(AttributesAS.getPlayerScale((Player) (Object) this));
    }

    @ModifyReturnValue(method = "getStandingEyeHeight", at = @At("RETURN"))
    private float astralsorcery$scaleEyeHeight(float height, Pose pose, EntityDimensions dimensions) {
        return dimensions.fixed ? height : height * AttributesAS.getPlayerScale((Player) (Object) this);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void astralsorcery$updateScaledDimensions(CallbackInfo callback) {
        Player player = (Player) (Object) this;
        float scale = AttributesAS.getPlayerScale(player);
        if (this.astralsorcery$appliedScale != scale) {
            this.astralsorcery$appliedScale = scale;
            player.refreshDimensions();
        }
    }
}
