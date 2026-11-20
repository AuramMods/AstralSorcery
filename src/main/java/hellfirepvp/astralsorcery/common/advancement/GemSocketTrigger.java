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
import hellfirepvp.astralsorcery.common.lib.AdvancementsAS;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GemSocketTrigger
 * Created by HellFirePvP
 * Date: 08.10.2026 / 09:33
 */
public class GemSocketTrigger extends AstralCriterionTrigger<GemSocketTrigger.Instance> {

    public GemSocketTrigger() {
        super("gem_socket", Instance.CODEC);
    }

    public void trigger(ServerPlayer sPlayer, ItemStack socketedItem) {
        this.trigger(sPlayer, inst -> inst.value().matches(socketedItem));
    }

    public record Instance(Optional<ItemPredicate> item) {

        public static final MapCodec<Instance> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                ITEM_PREDICATE_CODEC.optionalFieldOf("item").forGetter(Instance::item)
        ).apply(inst, Instance::new));

        public static CriterionTriggerInstance socketAny() {
            return AdvancementsAS.GEM_SOCKET.criterion(new Instance(Optional.empty()));
        }

        public static CriterionTriggerInstance socket(ItemPredicate item) {
            return AdvancementsAS.GEM_SOCKET.criterion(new Instance(Optional.of(item)));
        }

        public boolean matches(ItemStack socketedItem) {
            return this.item.isEmpty() || this.item.get().matches(socketedItem);
        }
    }
}
