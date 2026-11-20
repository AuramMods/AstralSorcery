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
import hellfirepvp.astralsorcery.common.event.damage.ForgeDamageHook;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ForgeHooks.class, remap = false)
public class MixinForgeDamageHook {

    @WrapOperation(method = {"onLivingHurt", "onLivingDamage"}, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z"))
    private static boolean astralsorcery$trackCancellation(IEventBus bus, Event event, Operation<Boolean> original,
                                                          @Local(argsOnly = true) LivingEntity entity, @Local(argsOnly = true) DamageSource source) {
        boolean canceled = original.call(bus, event);
        ForgeDamageHook.recordCancellation(entity, source, canceled);
        return canceled;
    }
}
