package hellfirepvp.astralsorcery.mixin;

import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(ItemEntity.class)
public interface AccessorItemEntityThrower {

    @Nullable
    @Accessor("thrower")
    UUID astralsorcery$getThrower();
}
