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
import hellfirepvp.astralsorcery.common.event.damage.IncomingDamageEvent;
import hellfirepvp.astralsorcery.common.event.damage.ShieldCheckEvent;
import hellfirepvp.astralsorcery.common.lib.AttributesAS;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Unique
    private IncomingDamageEvent astralsorcery$activeArmorHit;

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"), cancellable = true)
    private void astralsorcery$postIncomingDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback,
                                                 @Share("incomingDamage") LocalRef<IncomingDamageEvent> incomingDamage) {
        IncomingDamageEvent event = new IncomingDamageEvent((LivingEntity) (Object) this, source, amount);
        incomingDamage.set(event);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            callback.setReturnValue(false);
        }
    }

    @ModifyVariable(method = "hurt", argsOnly = true, ordinal = 0,
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;noActionTime:I", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private float astralsorcery$takeIncomingAmount(float amount, @Share("incomingDamage") LocalRef<IncomingDamageEvent> incomingDamage) {
        return incomingDamage.get().getAmount();
    }

    @WrapOperation(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDamageSourceBlocked(Lnet/minecraft/world/damagesource/DamageSource;)Z"))
    private boolean astralsorcery$allowPerkBlock(LivingEntity entity, DamageSource source, Operation<Boolean> original) {
        var event = new ShieldCheckEvent(entity, source, original.call(entity, source));
        MinecraftForge.EVENT_BUS.post(event);
        return event.isBlocked();
    }

    @WrapOperation(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V"))
    private void astralsorcery$applyArmorPiercing(LivingEntity entity, DamageSource source, float amount, Operation<Void> original,
                                                @Share("incomingDamage") LocalRef<IncomingDamageEvent> incomingDamage) {
        IncomingDamageEvent previous = this.astralsorcery$activeArmorHit;
        this.astralsorcery$activeArmorHit = incomingDamage.get();
        try {
            original.call(entity, source, amount);
        } finally {
            this.astralsorcery$activeArmorHit = previous;
        }
    }

    @WrapOperation(method = "getDamageAfterArmorAbsorb", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatRules;getDamageAfterAbsorb(FFF)F"))
    private float astralsorcery$modifyArmorReduction(float damage, float armor, float toughness, Operation<Float> original,
                                                    @Local(argsOnly = true) DamageSource source) {
        float reducedDamage = original.call(damage, armor, toughness);
        IncomingDamageEvent event = this.astralsorcery$activeArmorHit;
        return event != null && event.getSource() == source && event.bypassesArmorReduction() ? damage : reducedDamage;
    }

    @ModifyExpressionValue(method = "calculateFallDamage", at = @At(value = "CONSTANT", args = "floatValue=3.0"))
    private float astralsorcery$safeFallDistance(float vanillaDistance) {
        return (Object) this instanceof Player player ? (float) player.getAttributeValue(AttributesAS.SAFE_FALL_DISTANCE.get()) : vanillaDistance;
    }

    @ModifyExpressionValue(method = "calculateFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getEffect(Lnet/minecraft/world/effect/MobEffect;)Lnet/minecraft/world/effect/MobEffectInstance;"))
    private MobEffectInstance astralsorcery$skipDuplicateJumpBonus(MobEffectInstance jumpBoost) {
        // Players receive the jump boost allowance through safe fall distance.
        return (Object) this instanceof Player ? null : jumpBoost;
    }

    @WrapOperation(method = "calculateFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;ceil(F)I"))
    private int astralsorcery$scaleFallDamage(float damage, Operation<Integer> original) {
        if ((Object) this instanceof Player player) {
            double multiplier = player.getAttributeValue(AttributesAS.FALL_DAMAGE_MULTIPLIER.get());
            if (multiplier != 1) {
                return Mth.ceil((double) damage * multiplier);
            }
        }
        return original.call(damage);
    }

    @ModifyExpressionValue(method = "maxUpStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;maxUpStep()F"))
    private float astralsorcery$applyStepHeight(float vanillaHeight) {
        return (Object) this instanceof Player player ? (float) player.getAttributeValue(AttributesAS.STEP_HEIGHT.get()) : vanillaHeight;
    }
}
