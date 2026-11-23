package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.recipe.StageActor;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntityProvenance {

    @Inject(method = "merge(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"))
    private static void astralsorcery$trackMixedThrowers(ItemEntity into, ItemStack intoStack, ItemEntity from, ItemStack fromStack, CallbackInfo ci) {
        if (!into.level().isClientSide() && (from.getPersistentData().getBoolean(StageActor.MIXED_THROWERS)
                || !Objects.equals(((AccessorItemEntityThrower) into).astralsorcery$getThrower(),
                ((AccessorItemEntityThrower) from).astralsorcery$getThrower()))) {
            into.getPersistentData().putBoolean(StageActor.MIXED_THROWERS, true);
        }
    }
}
