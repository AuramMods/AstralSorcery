/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.advancement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.AdvancementsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalPointDiscoveryTrigger
 * Created by HellFirePvP
 * Date: 08.10.2026 / 09:38
 */
public class FocalPointDiscoveryTrigger extends AstralCriterionTrigger<FocalPointDiscoveryTrigger.Instance> {

    public FocalPointDiscoveryTrigger() {
        super("focal_point_discovery", Instance.CODEC);
    }

    public void trigger(ServerPlayer sPlayer, BaseConstellation discovered) {
        this.trigger(sPlayer, inst -> inst.value().matches(discovered));
    }

    public record Instance(Optional<BaseConstellation> constellation) {

        public static final MapCodec<Instance> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                RegistriesAS.codec(RegistriesAS.KEY_CONSTELLATIONS).optionalFieldOf("constellation").forGetter(Instance::constellation)
        ).apply(inst, Instance::new));

        public static CriterionTriggerInstance discoverAny() {
            return AdvancementsAS.FOCAL_POINT_DISCOVERY.criterion(new Instance(Optional.empty()));
        }

        public static CriterionTriggerInstance discover(BaseConstellation constellation) {
            return AdvancementsAS.FOCAL_POINT_DISCOVERY.criterion(new Instance(Optional.of(constellation)));
        }

        public boolean matches(BaseConstellation discovered) {
            return this.constellation.isEmpty() || this.constellation.get().equals(discovered);
        }
    }
}
