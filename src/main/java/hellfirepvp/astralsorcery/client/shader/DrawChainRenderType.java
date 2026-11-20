/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.shader;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.opengl.GL30;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DrawChainRenderType
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DrawChainRenderType extends RenderType {

    protected final RenderType decorated;
    private final Supplier<RenderTarget> newTarget;
    private final Runnable preRender, postRender;

    private int previousFramebuffer;
    private RenderTarget activeTarget;

    protected DrawChainRenderType(String id, RenderType decorated, Supplier<RenderTarget> newTarget, Runnable preRender, Runnable postRender) {
        super(id, decorated.format(), decorated.mode(), decorated.bufferSize(), decorated.affectsCrumbling(),
                decorated.sortOnUpload, decorated::setupRenderState, decorated::clearRenderState);
        this.decorated = decorated;
        this.newTarget = newTarget;
        this.preRender = preRender;
        this.postRender = postRender;
    }

    public static DrawChainRenderType wrap(String id, RenderType type, RenderTarget newTarget) {
        return wrap(id, type, newTarget, () -> {}, () -> {});
    }

    public static DrawChainRenderType wrap(String id, RenderType type, RenderTarget newTarget, Runnable preRender, Runnable postRender) {
        return wrap(id, type, () -> newTarget, preRender, postRender);
    }

    public static DrawChainRenderType wrap(String id, RenderType type, Supplier<RenderTarget> newTarget, Runnable preRender, Runnable postRender) {
        return new DrawChainRenderType(id, type, newTarget, preRender, postRender);
    }

    @Override
    public void setupRenderState() {
        RenderTarget target = this.newTarget.get();
        this.decorated.setupRenderState();

        this.preRender.run();
        this.previousFramebuffer = GlStateManager.getBoundFramebuffer();
        target.bindWrite(false);
        this.activeTarget = target;
    }

    @Override
    public void clearRenderState() {
        this.activeTarget.unbindWrite();
        this.activeTarget = null;
        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.previousFramebuffer);
        try {
            this.postRender.run();
        } finally {
            this.decorated.clearRenderState();
        }
    }

    @Override
    public void end(BufferBuilder builder, VertexSorting sorting) {
        if (!builder.building()) {
            return;
        }
        if (this.sortOnUpload) {
            builder.setQuadSorting(sorting);
        }
        BufferBuilder.RenderedBuffer batch = builder.endOrDiscardIfEmpty();
        if (batch == null) {
            return;
        }

        try {
            this.setupRenderState();
        } catch (RuntimeException | Error failure) {
            batch.release();
            throw failure;
        }
        try {
            BufferUploader.drawWithShader(batch);
        } finally {
            this.clearRenderState();
        }
    }

    @Override
    public String toString() {
        return this.decorated.toString();
    }

    @Override
    public Optional<RenderType> outline() {
        return this.decorated.outline();
    }

    @Override
    public boolean isOutline() {
        return this.decorated.isOutline();
    }

    @Override
    public boolean canConsolidateConsecutiveGeometry() {
        return this.decorated.canConsolidateConsecutiveGeometry();
    }
}
