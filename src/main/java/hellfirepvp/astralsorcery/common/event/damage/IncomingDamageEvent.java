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
import net.minecraftforge.eventbus.api.Cancelable;

/** A hit that has passed the victim's invulnerability checks and has yet to reach the shield. */
@Cancelable
public class IncomingDamageEvent extends LivingEvent {

    private final DamageSource source;
    private float amount;
    private boolean bypassArmorReduction;

    public IncomingDamageEvent(LivingEntity entity, DamageSource source, float amount) {
        super(entity);
        this.source = source;
        this.amount = amount;
    }

    public DamageSource getSource() {
        return this.source;
    }

    public float getAmount() {
        return this.amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void bypassArmorReduction() {
        this.bypassArmorReduction = true;
    }

    public boolean bypassesArmorReduction() {
        return this.bypassArmorReduction;
    }
}
