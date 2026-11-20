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
 * Class: PlayerAttunementTrigger
 * Created by HellFirePvP
 * Date: 08.10.2026 / 09:45
 */
public class PlayerAttunementTrigger extends AstralCriterionTrigger<PlayerAttunementTrigger.Instance> {

    public PlayerAttunementTrigger() {
        super("player_attunement", Instance.CODEC);
    }

    public void trigger(ServerPlayer sPlayer, BaseConstellation attunedTo) {
        this.trigger(sPlayer, inst -> inst.value().matches(attunedTo));
    }

    public record Instance(Optional<BaseConstellation> constellation) {

        public static final MapCodec<Instance> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                RegistriesAS.codec(RegistriesAS.KEY_CONSTELLATIONS).optionalFieldOf("constellation").forGetter(Instance::constellation)
        ).apply(inst, Instance::new));

        public static CriterionTriggerInstance attuneAny() {
            return AdvancementsAS.PLAYER_ATTUNEMENT.criterion(new Instance(Optional.empty()));
        }

        public static CriterionTriggerInstance attuneTo(BaseConstellation constellation) {
            return AdvancementsAS.PLAYER_ATTUNEMENT.criterion(new Instance(Optional.of(constellation)));
        }

        public boolean matches(BaseConstellation attunedTo) {
            return this.constellation.isEmpty() || this.constellation.get().equals(attunedTo);
        }
    }
}
