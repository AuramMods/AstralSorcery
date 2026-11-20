/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact.effect;

import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.artifact.ArtifactEffect;
import hellfirepvp.astralsorcery.common.entity.item.ItemEntityArtifact;
import hellfirepvp.astralsorcery.common.lib.types.ArtifactEffectTypesAS;
import hellfirepvp.astralsorcery.common.util.EntityUtil;
import hellfirepvp.astralsorcery.common.util.data.FloatRange;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactEffectRandomTeleport
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactEffectRandomTeleport extends ArtifactEffect {

    public static final MapCodec<ArtifactEffectRandomTeleport> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ArtifactEffect::getId),
            FloatRange.CODEC.fieldOf("distance").forGetter(ArtifactEffectRandomTeleport::getDistance)
    ).apply(inst, ArtifactEffectRandomTeleport::new));
    public static final StreamCodec<FriendlyByteBuf, ArtifactEffectRandomTeleport> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.RESOURCE_LOCATION,
            ArtifactEffect::getId,
            FloatRange.STREAM_CODEC,
            ArtifactEffectRandomTeleport::getDistance,
            ArtifactEffectRandomTeleport::new);

    private final FloatRange distance;

    protected ArtifactEffectRandomTeleport(ResourceLocation id, FloatRange distance) {
        super(id);
        this.distance = distance;
    }

    public FloatRange getDistance() {
        return this.distance;
    }

    public static Provider of(FloatRange distance) {
        return id -> new ArtifactEffectRandomTeleport(id, distance);
    }

    @Override
    public DeferredType<?> getType() {
        return ArtifactEffectTypesAS.RANDOM_TELEPORT;
    }

    @Override
    public void applyEffect(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifact) {
        int attempts = 50;
        BlockPos target = null;
        do {
            float dist = this.getDistance().getRandom(rand);
            Vector3 dir = Vector3.random(rand).normalize().multiply(dist);
            BlockPos candidate = artifact.blockPosition().offset(dir.toBlockPos());
            AABB atCandidate = artifact.getBoundingBox().deflate(1.0E-7).move(Vec3.atBottomCenterOf(dir.toBlockPos()).add(0, 0.05F, 0));
            if (sLevel.isInWorldBounds(candidate) && !sLevel.getBlockCollisions(artifact, atCandidate).iterator().hasNext()) {
                target = candidate;
            }
        } while (target == null && attempts-- > 0);
        if (target == null) return;

        artifact.playSound(SoundEvents.ENDERMAN_TELEPORT, 1F, 1F);
        EntityUtil.transferEntity(artifact, Vector3.atCenter(target));
    }
}
