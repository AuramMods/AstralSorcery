/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.AdvancementsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenDiscoveryTrigger
 * Created by HellFirePvP
 * Date: 08.10.2026 / 09:40
 */
public class LumenDiscoveryTrigger extends AstralCriterionTrigger<LumenDiscoveryTrigger.Instance> {

    public LumenDiscoveryTrigger() {
        super("lumen_discovery", Instance.CODEC);
    }

    public void trigger(ServerPlayer sPlayer, Lumen discovered) {
        this.trigger(sPlayer, inst -> inst.value().matches(discovered));
    }

    public record Instance(Optional<Lumen> lumen,
                           Optional<Boolean> elementary) {

        public static final MapCodec<Instance> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                RegistriesAS.codec(RegistriesAS.KEY_LUMEN).optionalFieldOf("lumen").forGetter(Instance::lumen),
                Codec.BOOL.optionalFieldOf("elementary").forGetter(Instance::elementary)
        ).apply(inst, Instance::new));

        public static CriterionTriggerInstance discoverAny() {
            return AdvancementsAS.LUMEN_DISCOVERY.criterion(new Instance(Optional.empty(), Optional.empty()));
        }

        public static CriterionTriggerInstance discover(Lumen lumen) {
            return AdvancementsAS.LUMEN_DISCOVERY.criterion(new Instance(Optional.of(lumen), Optional.empty()));
        }

        public static CriterionTriggerInstance discoverElementary() {
            return AdvancementsAS.LUMEN_DISCOVERY.criterion(new Instance(Optional.empty(), Optional.of(true)));
        }

        public static CriterionTriggerInstance discoverNonElementary() {
            return AdvancementsAS.LUMEN_DISCOVERY.criterion(new Instance(Optional.empty(), Optional.of(false)));
        }

        public boolean matches(Lumen discovered) {
            if (this.lumen.isPresent() && !this.lumen.get().equals(discovered)) {
                return false;
            }
            return this.elementary.isEmpty() || this.elementary.get() == discovered.isElementary();
        }
    }
}
