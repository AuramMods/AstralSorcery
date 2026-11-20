/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.helper.GatewayInterfaceRenderHelper;
import hellfirepvp.astralsorcery.client.helper.TranslucentBlockRenderHelper;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MixinParticleEngine
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(ParticleEngine.class)
public abstract class MixinParticleEngine {

    @Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V")
    )
    public void renderParticles(PoseStack pose, MultiBufferSource.BufferSource buffers, LightTexture lightTexture, Camera camera,
                                float partialTick, Frustum frustum, CallbackInfo ci) {
        EffectHandler.getInstance().render(camera, frustum, false, partialTick);
        EffectHandler.getInstance().render(camera, frustum, true, partialTick);
        TranslucentBlockRenderHelper.renderFrame(partialTick);

        if (Minecraft.getInstance().level != null) {
            PoseStack identity = new PoseStack();
            MultiBufferSource.BufferSource effectBuffers = RenderUtil.effectBufferSource();
            GatewayInterfaceRenderHelper.getInstance().renderCurrentUI(Minecraft.getInstance().level, identity, camera, effectBuffers, partialTick);
        }

        Blending.DEFAULT.apply();
        RenderSystem.enableDepthTest();
    }
}
