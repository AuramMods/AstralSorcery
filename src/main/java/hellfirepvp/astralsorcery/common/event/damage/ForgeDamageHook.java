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
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.function.Supplier;

public final class ForgeDamageHook {

    // Forge returns zero for both canceled events and accepted hits with no health damage.
    private static final ThreadLocal<Probe> ACTIVE = new ThreadLocal<>();

    private ForgeDamageHook() {}

    public static Result observe(LivingEntity entity, DamageSource source, Supplier<Float> post) {
        Probe previous = ACTIVE.get();
        Probe current = new Probe(entity, source);
        ACTIVE.set(current);
        try {
            return new Result(post.get(), current.canceled);
        } finally {
            if (previous == null) {
                ACTIVE.remove();
            } else {
                ACTIVE.set(previous);
            }
        }
    }

    public static void recordCancellation(LivingDamageEvent event, boolean canceled) {
        Probe probe = ACTIVE.get();
        if (probe != null && probe.entity == event.getEntity() && probe.source == event.getSource()) {
            probe.canceled = canceled;
        }
    }

    public record Result(float amount, boolean canceled) {}

    private static final class Probe {
        private final LivingEntity entity;
        private final DamageSource source;
        private boolean canceled;

        private Probe(LivingEntity entity, DamageSource source) {
            this.entity = entity;
            this.source = source;
        }
    }
}
