/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.reader;

import hellfirepvp.astralsorcery.common.perk.PerkAttributeMap;
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.base.VanillaAttributeType;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ReaderVanillaAttribute
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ReaderVanillaAttribute extends PerkAttributeTypeReader {

    private final Supplier<? extends VanillaAttributeType> vanillaType;
    protected boolean formatAsDecimal = false;

    public ReaderVanillaAttribute(Supplier<? extends VanillaAttributeType> type) {
        super(type);
        this.vanillaType = type;
    }

    public <T extends ReaderVanillaAttribute> T formatAsDecimal() {
        this.formatAsDecimal = true;
        return (T) this;
    }

    @Override
    public double getDefaultValue(PerkAttributeMap statMap, Player player, LogicalSide side) {
        Attribute attribute = this.vanillaType.get().getAttribute();
        AttributeInstance ai = player.getAttribute(attribute);
        return ai == null ? attribute.getDefaultValue() : ai.getBaseValue();
    }

    @Override
    public double getModifierValueForMode(PerkAttributeMap statMap, Player player, LogicalSide side, ModifierType mode) {
        return statMap.getModifier(player, ResearchManager.getProgress(player, side), this.getType(), mode);
    }

    protected String formatForDisplay(double value) {
        String valueStr;
        if (this.formatAsDecimal) {
            valueStr = formatDecimal(value);
        } else {
            valueStr = String.valueOf(Mth.floor(value));
        }
        return valueStr;
    }
}
