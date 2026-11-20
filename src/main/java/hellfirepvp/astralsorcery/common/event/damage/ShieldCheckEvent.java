/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

/** The shield decision for an eligible hit, before Forge applies the block. */
public final class ShieldCheckEvent extends LivingEvent {

    private final DamageSource source;
    private boolean blocked;

    public ShieldCheckEvent(LivingEntity entity, DamageSource source, boolean blocked) {
        super(entity);
        this.source = source;
        this.blocked = blocked;
    }

    public DamageSource getSource() {
        return this.source;
    }

    public boolean isBlocked() {
        return this.blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
