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
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.server.level.ServerPlayer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkLevelTrigger
 * Created by HellFirePvP
 * Date: 07.10.2026 / 23:29
 */
public class PerkLevelTrigger extends AstralCriterionTrigger<PerkLevelTrigger.Instance> {

    public PerkLevelTrigger() {
        super("perk_level", Instance.CODEC);
    }

    public void trigger(ServerPlayer sPlayer, int levelReached) {
        this.trigger(sPlayer, inst -> inst.value().levelNeeded() <= levelReached);
    }

    public record Instance(int levelNeeded) {

        public static final MapCodec<Instance> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.INT.fieldOf("levelNeeded").forGetter(Instance::levelNeeded)
        ).apply(inst, Instance::new));

        public static CriterionTriggerInstance reachLevel(int levelNeeded) {
            return AdvancementsAS.PERK_LEVEL.criterion(new Instance(levelNeeded));
        }
    }
}
