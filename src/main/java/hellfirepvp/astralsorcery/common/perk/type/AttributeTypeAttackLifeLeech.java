/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.perk.PerkManager;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.SidedHelper;
import hellfirepvp.astralsorcery.common.event.damage.DamageResolutionEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AttributeTypeAttackLifeLeech
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AttributeTypeAttackLifeLeech extends PerkAttributeType {

    public AttributeTypeAttackLifeLeech() {
        super(false);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(this::onDamageDealt);
    }

    private void onDamageDealt(DamageResolutionEvent.Applied event) {
        if (!event.getSource().isIndirect() && event.getSource().getDirectEntity() instanceof Player player) {
            LogicalSide side = SidedHelper.getSide(player);
            if (!this.hasTypeApplied(player, side)) return;

            PlayerProgress progress = ResearchManager.getProgress(player, side);
            if (!progress.isValid()) return;

            float leechPerc = PerkManager.getOrCreateAttributes(player)
                    .modifyValue(player, progress, this, 0);
            leechPerc = AttributeEvent.postProcessModded(player, this, leechPerc);
            if (leechPerc > 0) {
                float toLeech = event.getAmount() * leechPerc;
                if (toLeech > 0) {
                    player.heal(toLeech);
                }
            }
        }
    }
}
