/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.lib.AttributesAS;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public class MixinPlayerMiningSpeed {

    @ModifyVariable(method = "getDigSpeed", ordinal = 0, at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
            target = "Lnet/minecraft/tags/FluidTags;WATER:Lnet/minecraft/tags/TagKey;"))
    private float astralsorcery$applyMiningSpeed(float speed) {
        return speed * (float) ((Player) (Object) this).getAttributeValue(AttributesAS.BLOCK_BREAK_SPEED.get());
    }
}
