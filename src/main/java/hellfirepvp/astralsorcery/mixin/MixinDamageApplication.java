/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import hellfirepvp.astralsorcery.common.event.damage.DamageResolutionEvent;
import hellfirepvp.astralsorcery.common.event.damage.ForgeDamageHook;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntity.class, Player.class})
public class MixinDamageApplication {

    @ModifyExpressionValue(method = "actuallyHurt", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"),
            @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F")
    }, allow = 1)
    private float astralsorcery$beforeAbsorption(float amount, @Local(argsOnly = true) DamageSource source) {
        var event = new DamageResolutionEvent.BeforeAbsorption((LivingEntity) (Object) this, source, amount);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getAmount();
    }

    @WrapOperation(method = "actuallyHurt", at = @At(value = "INVOKE", remap = false,
            target = "Lnet/minecraftforge/common/ForgeHooks;onLivingDamage(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private float astralsorcery$trackDamageApplication(LivingEntity entity, DamageSource source, float amount, Operation<Float> original,
                                                      @Share("appliedDamage") LocalRef<DamageResolutionEvent.Applied> appliedDamage) {
        ForgeDamageHook.Result result = ForgeDamageHook.observe(entity, source, () -> original.call(entity, source, amount));
        if (!result.canceled()) {
            appliedDamage.set(new DamageResolutionEvent.Applied(entity, source, result.amount()));
        }
        return result.amount();
    }

    @Inject(method = "actuallyHurt", at = @At("RETURN"))
    private void astralsorcery$afterDamageApplication(DamageSource source, float amount, CallbackInfo callback,
                                                     @Share("appliedDamage") LocalRef<DamageResolutionEvent.Applied> appliedDamage) {
        DamageResolutionEvent.Applied event = appliedDamage.get();
        if (event != null) {
            MinecraftForge.EVENT_BUS.post(event);
        }
    }
}
