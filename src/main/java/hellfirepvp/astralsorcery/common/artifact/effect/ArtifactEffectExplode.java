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
import hellfirepvp.astralsorcery.common.entity.item.ItemEntityArtifact;
import hellfirepvp.astralsorcery.common.lib.types.ArtifactEffectTypesAS;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.Locale;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactEffectExplode
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactEffectExplode extends ArtifactEffect {

    private static final Codec<Level.ExplosionInteraction> INTERACTION_CODEC = CodecUtil.stringifyCodec(
            name -> Level.ExplosionInteraction.valueOf(name.toUpperCase(Locale.ROOT)),
            interaction -> interaction.name().toLowerCase(Locale.ROOT));

    public static final MapCodec<ArtifactEffectExplode> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ArtifactEffect::getId),
            Codec.INT.fieldOf("range").forGetter(ArtifactEffectExplode::getRange),
            INTERACTION_CODEC.fieldOf("interaction").forGetter(ArtifactEffectExplode::getInteraction)
    ).apply(inst, ArtifactEffectExplode::new));
    public static final StreamCodec<FriendlyByteBuf, ArtifactEffectExplode> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.RESOURCE_LOCATION,
            ArtifactEffect::getId,
            ByteBufCodecs.INT,
            ArtifactEffectExplode::getRange,
            CodecUtil.enumStreamCodec(Level.ExplosionInteraction.class),
            ArtifactEffectExplode::getInteraction,
            ArtifactEffectExplode::new
    );

    private final int range;
    private final Level.ExplosionInteraction interaction;

    protected ArtifactEffectExplode(ResourceLocation id, int range, Level.ExplosionInteraction interaction) {
        super(id);
        this.range = range;
        this.interaction = interaction;
    }

    public int getRange() {
        return this.range;
    }

    public Level.ExplosionInteraction getInteraction() {
        return this.interaction;
    }

    public static Provider of(int range, Level.ExplosionInteraction interaction) {
        return id -> new ArtifactEffectExplode(id, range, interaction);
    }

    @Override
    public DeferredType<?> getType() {
        return ArtifactEffectTypesAS.EXPLODE;
    }

    @Override
    public void applyEffect(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifact) {
        sLevel.explode(artifact, artifact.getX(), artifact.getY(), artifact.getZ(), this.getRange(), this.getInteraction());
    }
}
