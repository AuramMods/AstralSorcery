/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type.base;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.perk.PerkManager;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VanillaAttributeType
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class VanillaAttributeType extends PerkAttributeType implements VanillaPerkAttributeType {

    private final Map<ModifierType, ModifierIdentity> modifierIds = new ConcurrentHashMap<>();

    protected VanillaAttributeType() {
        this(false);
    }

    protected VanillaAttributeType(boolean isOnlyMultiplicative) {
        super(isOnlyMultiplicative);
    }

    private ModifierIdentity createModifierIdentity(ModifierType type) {
        String path = BuiltInRegistries.ATTRIBUTE.getKey(this.getAttribute()).getPath();
        String name = AstralSorcery.key("dynamic_vanilla_modifier_" + path + "_" + type.name().toLowerCase(Locale.ROOT)).toString();
        return new ModifierIdentity(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)), name);
    }

    @Override
    public void onApply(Player player, LogicalSide side, ModifierSource source) {
        super.onApply(player, side, source);
        this.refreshAttribute(player, side);
    }

    @Override
    public void onRemove(Player player, LogicalSide side, boolean removedCompletely, ModifierSource source) {
        super.onRemove(player, side, removedCompletely, source);
        this.refreshAttribute(player, side);
    }

    @Override
    public void refreshAttribute(Player player, LogicalSide side) {
        AttributeInstance attr = player.getAttributes().getInstance(this.getAttribute());
        if (attr == null) return;

        PlayerProgress progress = ResearchManager.getProgress(player, side);
        for (ModifierType type : ModifierType.values()) {
            ModifierIdentity modifier = this.modifierIds.computeIfAbsent(type, this::createModifierIdentity);
            attr.removeModifier(modifier.id());

            float amount = PerkManager.getOrCreateAttributes(player).getModifier(player, progress, this, type) - 1;
            if (!type.isNeutralVanillaValue(amount)) {
                attr.addTransientModifier(new AttributeModifier(modifier.id(), modifier.name(), amount, type.getVanillaAttributeOperation()));
            }
        }
    }

    private record ModifierIdentity(UUID id, String name) {}
}
