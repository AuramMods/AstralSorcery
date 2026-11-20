/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.RandomSource;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderLightFanUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderLightFanUtil {

    private static final RenderType LIGHT_RAYS = RenderType.create(AstralSorcery.key("light_rays").toString(),
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, 1536, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeLightningShader))
                    .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, false))
                    .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
                    .createCompositeState(false));

    private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);

    public static void renderLightFan(PoseStack poseStack, long seed, int tick, float pTicks, MultiBufferSource src, int color, float minScale, float scale, int count) {
        RandomSource rand = RandomSource.create(seed);
        float effectTick = (tick + pTicks) / 200F;

        poseStack.pushPose();
        renderFan(poseStack, rand, effectTick, src.getBuffer(LIGHT_RAYS), color, minScale, scale, count);
        poseStack.popPose();

        RenderUtil.finishDrawing(src);
    }

    private static void renderFan(PoseStack poseStack, RandomSource rand, float effectTick, VertexConsumer buf, int color, float minScale, float scale, int count) {
        poseStack.pushPose();
        float scaleMultiplier = 30.0F / (Math.min(minScale, 10 * scale) / 10.0F);
        int brightColor = ColorWrapper.transparent(color).brighter().getColor();
        brightColor &= 0x00FFFFFF;

        Vector3f origin = new Vector3f();
        Vector3f v1 = new Vector3f();
        Vector3f v2 = new Vector3f();
        Vector3f v3 = new Vector3f();
        Quaternionf quat = new Quaternionf();
        for (int i = 0; i < count; i++) {
            quat.rotationXYZ(
                            rand.nextFloat() * (float) (Math.PI * 2),
                            rand.nextFloat() * (float) (Math.PI * 2),
                            rand.nextFloat() * (float) (Math.PI * 2)
                    )
                    .rotateXYZ(
                            rand.nextFloat() * (float) (Math.PI * 2),
                            rand.nextFloat() * (float) (Math.PI * 2),
                            rand.nextFloat() * (float) (Math.PI * 2) + effectTick * (float) (Math.PI / 2)
                    );
            poseStack.mulPose(quat);

            float f1 = (rand.nextFloat() * 20 + 5) / scaleMultiplier;
            float f2 = (rand.nextFloat() * 2 + 1) / scaleMultiplier;
            v1.set(-HALF_SQRT_3 * f2, f1, -0.5F * f2);
            v2.set(HALF_SQRT_3 * f2, f1, -0.5F * f2);
            v3.set(0.0F, f1, f2);

            PoseStack.Pose pose = poseStack.last();
            buf.vertex(pose.pose(), origin.x(), origin.y(), origin.z()).color(color).endVertex();
            buf.vertex(pose.pose(), v1.x(), v1.y(), v1.z()).color(brightColor).endVertex();
            buf.vertex(pose.pose(), v2.x(), v2.y(), v2.z()).color(brightColor).endVertex();
            buf.vertex(pose.pose(), origin.x(), origin.y(), origin.z()).color(color).endVertex();
            buf.vertex(pose.pose(), v2.x(), v2.y(), v2.z()).color(brightColor).endVertex();
            buf.vertex(pose.pose(), v3.x(), v3.y(), v3.z()).color(brightColor).endVertex();
            buf.vertex(pose.pose(), origin.x(), origin.y(), origin.z()).color(color).endVertex();
            buf.vertex(pose.pose(), v3.x(), v3.y(), v3.z()).color(brightColor).endVertex();
            buf.vertex(pose.pose(), v1.x(), v1.y(), v1.z()).color(brightColor).endVertex();
        }
        poseStack.popPose();
    }
}
