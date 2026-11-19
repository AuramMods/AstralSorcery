/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.config.json.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.config.json.JsonDataRegistry;
import hellfirepvp.astralsorcery.common.lib.PerksAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkGemModifierRegistry
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkGemModifierRegistry extends JsonDataRegistry<PerkGemModifierRegistry.Entry> {

    private static final PerkGemModifierRegistry INSTANCE = new PerkGemModifierRegistry();

    private PerkGemModifierRegistry() {}

    public static PerkGemModifierRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public Codec<Entry> elementCodec() {
        return Entry.CODEC;
    }

    @Override
    public List<Entry> getDefaultValues() {
        return List.of(
                entry(PerksAS.AttributeTypes.MAX_HEALTH, 2),
                entry(PerksAS.AttributeTypes.MOVEMENT_SPEED, 8),
                entry(PerksAS.AttributeTypes.ARMOR, 8),
                entry(PerksAS.AttributeTypes.BLOCK_REACH, 4),
                entry(PerksAS.AttributeTypes.ATTACK_SPEED, 2),
                entry(PerksAS.AttributeTypes.ATTACK_DAMAGE, 8),
                entry(PerksAS.AttributeTypes.SCALE, 2),

                entry(PerksAS.AttributeTypes.PROJECTILE_DAMAGE, 8),
                entry(PerksAS.AttributeTypes.BLOCK_BREAK_SPEED, 2),
                entry(PerksAS.AttributeTypes.CRITICAL_HIT_CHANCE, 4),
                entry(PerksAS.AttributeTypes.CRITICAL_HIT_DAMAGE, 4),
                entry(PerksAS.AttributeTypes.ELEMENTAL_RESISTANCE, 2),
                entry(PerksAS.AttributeTypes.BLOCK_CHANCE, 2),
                entry(PerksAS.AttributeTypes.LIFE_RECOVERY, 2),
                entry(PerksAS.AttributeTypes.PERK_EXPERIENCE, 1)
        );
    }

    private static Entry entry(RegistryObject<? extends PerkAttributeType> attribute, int weight) {
        ResourceKey<PerkAttributeType> key = ResourceKey.create(RegistriesAS.KEY_PERK_ATTRIBUTE_TYPES, attribute.getId());
        return new Entry(RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPES.get().getHolderOrThrow(key), weight);
    }

    public record Entry(Holder<PerkAttributeType> type, int weight) {

        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                RegistryFixedCodec.create(RegistriesAS.KEY_PERK_ATTRIBUTE_TYPES).fieldOf("attribute").forGetter(Entry::type),
                Codec.INT.fieldOf("weight").forGetter(Entry::weight)
        ).apply(inst, Entry::new));
    }
}
