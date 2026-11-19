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

public abstract class DamageResolutionEvent extends LivingEvent {

    private final DamageSource source;

    protected DamageResolutionEvent(LivingEntity entity, DamageSource source) {
        super(entity);
        this.source = source;
    }

    public final DamageSource getSource() {
        return this.source;
    }

    /** The armor and magic reductions have been applied. Absorption has yet to be consumed. */
    public static final class BeforeAbsorption extends DamageResolutionEvent {
        private float amount;

        public BeforeAbsorption(LivingEntity entity, DamageSource source, float amount) {
            super(entity, source);
            this.amount = amount;
        }

        public float getAmount() {
            return this.amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }
    }

    /** An accepted hit has finished processing, including any resulting health change. */
    public static final class Applied extends DamageResolutionEvent {
        private final float amount;

        public Applied(LivingEntity entity, DamageSource source, float amount) {
            super(entity, source);
            this.amount = amount;
        }

        public float getAmount() {
            return this.amount;
        }
    }
}
