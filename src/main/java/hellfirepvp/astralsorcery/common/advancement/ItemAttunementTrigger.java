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
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ItemAttunementTrigger
 * Created by HellFirePvP
 * Date: 08.10.2026 / 09:44
 */
public class ItemAttunementTrigger extends AstralCriterionTrigger<ItemAttunementTrigger.Instance> {

    public ItemAttunementTrigger() {
        super("item_attunement", Instance.CODEC);
    }

    public void trigger(ServerPlayer sPlayer, BaseConstellation attunedTo, ItemStack attunedItem) {
        this.trigger(sPlayer, inst -> inst.value().matches(attunedTo, attunedItem));
    }

    public record Instance(Optional<BaseConstellation> constellation,
                           Optional<ItemPredicate> item) {

        public static final MapCodec<Instance> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                RegistriesAS.codec(RegistriesAS.KEY_CONSTELLATIONS).optionalFieldOf("constellation").forGetter(Instance::constellation),
                ITEM_PREDICATE_CODEC.optionalFieldOf("item").forGetter(Instance::item)
        ).apply(inst, Instance::new));

        public static CriterionTriggerInstance attuneAnyItem() {
            return AdvancementsAS.ITEM_ATTUNEMENT.criterion(new Instance(Optional.empty(), Optional.empty()));
        }

        public static CriterionTriggerInstance attuneItem(ItemPredicate item) {
            return AdvancementsAS.ITEM_ATTUNEMENT.criterion(new Instance(Optional.empty(), Optional.of(item)));
        }

        public static CriterionTriggerInstance attuneItemTo(BaseConstellation constellation) {
            return AdvancementsAS.ITEM_ATTUNEMENT.criterion(new Instance(Optional.of(constellation), Optional.empty()));
        }

        public static CriterionTriggerInstance attuneItemTo(BaseConstellation constellation, ItemPredicate item) {
            return AdvancementsAS.ITEM_ATTUNEMENT.criterion(new Instance(Optional.of(constellation), Optional.of(item)));
        }

        public boolean matches(BaseConstellation attunedTo, ItemStack attunedItem) {
            if (this.constellation.isPresent() && !this.constellation.get().equals(attunedTo)) {
                return false;
            }
            return this.item.isEmpty() || this.item.get().matches(attunedItem);
        }
    }
}
