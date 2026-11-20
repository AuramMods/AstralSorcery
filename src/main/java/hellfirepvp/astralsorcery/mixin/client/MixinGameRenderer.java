/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import hellfirepvp.astralsorcery.client.helper.FocalPointEffectHelper;
import hellfirepvp.astralsorcery.client.lib.ShaderProgramsAS;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MixinGameRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "resize", at = @At("TAIL"))
    public void resize(int width, int height, CallbackInfo ci) {
        ShaderProgramsAS.resize(width, height);
    }

    @WrapOperation(method = "pick(F)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;pick(DFZ)Lnet/minecraft/world/phys/HitResult;"))
    private HitResult modifyEntityPick(Entity entity, double reach, float partialTick, boolean includeFluids,
                                       Operation<HitResult> original) {
        return FocalPointEffectHelper.overrideStellarFilamentInteract(original.call(entity, reach, partialTick, includeFluids));
    }
}
