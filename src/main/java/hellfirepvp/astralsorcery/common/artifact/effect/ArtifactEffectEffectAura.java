/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.artifact.ArtifactEffect;
import hellfirepvp.astralsorcery.common.artifact.ContinuousArtifactEffect;
import hellfirepvp.astralsorcery.common.entity.item.ItemEntityArtifact;
import hellfirepvp.astralsorcery.common.lib.types.ArtifactEffectTypesAS;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.common.util.codec.MobEffectDataCodec;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactEffectEffectAura
 * Created by HellFirePvP
 * Date: 24.10.2026 / 19:24
 */
public class ArtifactEffectEffectAura extends ArtifactEffect implements ContinuousArtifactEffect {

    public static final MapCodec<ArtifactEffectEffectAura> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ArtifactEffect::getId),
            Codec.INT.fieldOf("range").forGetter(ArtifactEffectEffectAura::getRange),
            MobEffectDataCodec.CODEC.listOf().fieldOf("effects").forGetter(ArtifactEffectEffectAura::getEffects)
    ).apply(inst, ArtifactEffectEffectAura::new));
    public static final StreamCodec<FriendlyByteBuf, ArtifactEffectEffectAura> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.RESOURCE_LOCATION, ArtifactEffect::getId,
            ByteBufCodecs.INT, ArtifactEffectEffectAura::getRange,
            ByteBufCodecs.MOB_EFFECT_INSTANCE.apply(ByteBufCodecs.list()), ArtifactEffectEffectAura::getEffects,
            ArtifactEffectEffectAura::new);

    private final int range;
    private final List<MobEffectInstance> effects;

    protected ArtifactEffectEffectAura(ResourceLocation id, int range, List<MobEffectInstance> effects) {
        super(id);
        this.range = range;
        this.effects = effects;
    }

    public int getRange() {
        return this.range;
    }

    public List<MobEffectInstance> getEffects() {
        return Collections.unmodifiableList(this.effects);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public DeferredType<?> getType() {
        return ArtifactEffectTypesAS.EFFECT_AURA;
    }

    @Override
    public void tickEffect(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifact) {
        if (artifact.tickCount % 10 != 0) return;

        this.applyToEntities(sLevel, artifact);
        int color = PotionUtils.getColor(this.getEffects());
        double red = ((color >> 16) & 0xFF) / 255D;
        double green = ((color >> 8) & 0xFF) / 255D;
        double blue = (color & 0xFF) / 255D;
        double horizontalSpread = this.getRange() / 2D;
        for (int index = 0; index < this.getRange(); index++) {
            double x = artifact.getX() + rand.nextGaussian() * horizontalSpread;
            double y = artifact.getY() + 0.2 + rand.nextGaussian() * 0.5;
            double z = artifact.getZ() + rand.nextGaussian() * horizontalSpread;
            // The 1.20.1 entity-effect particle reads its RGB from the velocity fields of single-particle packets.
            sLevel.sendParticles(ParticleTypes.ENTITY_EFFECT, x, y, z, 0, red, green, blue, 1);
        }
    }

    @Override
    public void applyEffect(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifact) {
        this.applyToEntities(sLevel, artifact);
        sLevel.levelEvent(2002, artifact.blockPosition(), PotionUtils.getColor(this.getEffects()));
    }

    private void applyToEntities(ServerLevel sLevel, ItemEntityArtifact artifact) {
        AABB bounds = artifact.getBoundingBox().inflate(this.getRange());
        sLevel.getEntities(EntityTypeTest.forClass(LivingEntity.class), bounds, EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(Entity::isAlive)).forEach(entity -> {
            this.getEffects().forEach(effect -> entity.addEffect(new MobEffectInstance(effect)));
        });
    }

    public static class Builder {

        private int range = 6;
        private final List<MobEffectInstance> effects = new ArrayList<>();

        public Builder setRange(int range) {
            this.range = range;
            return this;
        }

        public Builder addEffect(MobEffect effect) {
            return this.addEffect(new MobEffectInstance(effect, 2 * 20, 0, true, true, false));
        }

        public Builder addEffect(MobEffect effect, int duration) {
            return this.addEffect(new MobEffectInstance(effect, duration, 0, true, true, false));
        }

        public Builder addEffect(MobEffect effect, int duration, int amplifier) {
            return this.addEffect(new MobEffectInstance(effect, duration, amplifier, true, true, false));
        }

        public Builder addEffect(MobEffectInstance effect) {
            this.effects.add(effect);
            return this;
        }

        public Provider build() {
            return id -> new ArtifactEffectEffectAura(id, this.range, List.copyOf(this.effects));
        }
    }
}
