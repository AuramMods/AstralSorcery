/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin.client;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.resource.AtlasTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(ModelManager.class)
public class MixinModelManager {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/model/AtlasSet;<init>(Ljava/util/Map;Lnet/minecraft/client/renderer/texture/TextureManager;)V"), index = 0)
    private Map<ResourceLocation, ResourceLocation> astralSorcery$addLumenAtlas(Map<ResourceLocation, ResourceLocation> atlases) {
        Map<ResourceLocation, ResourceLocation> withLumen = new LinkedHashMap<>(atlases);
        withLumen.put(AtlasTexture.getLumenAtlas().getKey(), AstralSorcery.key("lumen"));
        return withLumen;
    }
}
