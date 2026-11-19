/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact.effect.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.reload.RegistryJsonDataLoader;
import hellfirepvp.astralsorcery.common.artifact.ArtifactEffect;
import hellfirepvp.astralsorcery.common.artifact.SelectableArtifactEffect;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.MapStream;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactEffectLoader
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactEffectLoader extends RegistryJsonDataLoader {

    private static final ArtifactEffectLoader POSITIVE_INSTANCE = new ArtifactEffectLoader("artifact_effects");
    private static final ArtifactEffectLoader NEGATIVE_INSTANCE = new ArtifactEffectLoader("artifact_penalties");

    private final Set<SelectableArtifactEffect> effects = new HashSet<>();

    private ArtifactEffectLoader(String path) {
        super(path);
    }

    public static ArtifactEffectLoader getPositiveInstance() {
        return POSITIVE_INSTANCE;
    }

    public static ArtifactEffectLoader getNegativeInstance() {
        return NEGATIVE_INSTANCE;
    }

    public Set<SelectableArtifactEffect> getLoadedEffects() {
        return Collections.unmodifiableSet(this.effects);
    }

    public Optional<ArtifactEffect> pickNewEffect(RandomSource rand, List<ArtifactEffect> existingEffects) {
        List<ResourceLocation> existingIds = existingEffects.stream().map(ArtifactEffect::getId).toList();
        List<SelectableArtifactEffect> availableEffects = this.getLoadedEffects().stream()
                .filter(select -> !select.selector().conflictsWith(existingIds))
                .toList();
        return MiscUtil.getWeightedRandomEntry(availableEffects, rand, select -> select.selector().getWeight())
                .map(SelectableArtifactEffect::effect);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> dataMap, RegistryAccess registries) {
        Map<ResourceLocation, JsonObject> effectObjects = MapStream.of(dataMap)
                .filterValue(JsonElement::isJsonObject)
                .mapValue(JsonElement::getAsJsonObject)
                .toMap();
        AstralSorcery.LOG.info("Loading artifact effects with {} effects.", effectObjects.size());

        this.effects.clear();
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registries);
        effectObjects.forEach((key, obj) -> {
            var result = SelectableArtifactEffect.CODEC.parse(ops, obj);
            result.error().ifPresent(error -> AstralSorcery.LOG.warn("Failed to load artifact effect {}: {}", key, error.message()));
            result.result().ifPresent(this.effects::add);
        });
    }
}
