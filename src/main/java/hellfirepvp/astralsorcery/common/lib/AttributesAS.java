/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Syncable 1.21.1 perk attributes that have no equivalent Forge 1.20.1 attribute. */
public final class AttributesAS {

    private static final DeferredRegister<Attribute> REGISTER = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, AstralSorcery.MODID);

    public static final RegistryObject<Attribute> BLOCK_BREAK_SPEED = register("block_break_speed", "player.block_break_speed", 1, 0, 1024);
    public static final RegistryObject<Attribute> FALL_DAMAGE_MULTIPLIER = register("fall_damage_multiplier", "generic.fall_damage_multiplier", 1, 0, 100);
    public static final RegistryObject<Attribute> SAFE_FALL_DISTANCE = register("safe_fall_distance", "generic.safe_fall_distance", 3, -1024, 1024);
    public static final RegistryObject<Attribute> SCALE = register("scale", "generic.scale", 1, 0.0625, 16);
    public static final RegistryObject<Attribute> STEP_HEIGHT = register("step_height", "generic.step_height", 0.6, 0, 10);

    private AttributesAS() {}

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
        bus.addListener(AttributesAS::addPlayerAttributes);
        bus.addListener(AttributesAS::registerJumpFallModifier);
    }

    private static void addPlayerAttributes(EntityAttributeModificationEvent event) {
        REGISTER.getEntries().forEach(attribute -> event.add(EntityType.PLAYER, attribute.get()));
    }

    private static void registerJumpFallModifier(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> MobEffects.JUMP.addAttributeModifier(SAFE_FALL_DISTANCE.get(),
                "303d9fe2-98e6-3008-ac90-94639f4cc759", 1, AttributeModifier.Operation.ADDITION));
    }

    private static RegistryObject<Attribute> register(String id, String description, double base, double min, double max) {
        return REGISTER.register(id, () -> new RangedAttribute("attribute.name." + description, base, min, max).setSyncable(true));
    }
}
