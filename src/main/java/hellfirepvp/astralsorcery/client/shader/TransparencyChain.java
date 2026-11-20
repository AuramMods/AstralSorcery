/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.shader;

import com.mojang.blaze3d.pipeline.RenderTarget;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.mixin.client.AccessorPostChain;
import hellfirepvp.astralsorcery.mixin.client.AccessorPostPass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TransparencyChain
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TransparencyChain extends WrappedPostChain {

    private RenderTarget transparencyTarget;
    private WrappedPostPass blitPass;

    private boolean redirected;

    public TransparencyChain(ResourceLocation chainKey) {
        super(chainKey);
    }

    @Override
    public void load() throws Exception {
        super.load();
        this.transparencyTarget = this.getTarget("transparency").orElse(null);

        AccessorPostChain chainAccess = this.getWrapped().map(chain -> (AccessorPostChain) chain).orElseThrow();
        var passes = chainAccess.getPasses();
        PostPass inputPass = passes.get(0);
        AccessorPostPass inputAccess = (AccessorPostPass) inputPass;
        int depthSampler = inputAccess.getAuxNames().indexOf("DiffuseDepthSampler");
        if (depthSampler >= 0) {
            inputAccess.getAuxAssets().set(depthSampler, () -> inputPass.inTarget.getDepthTextureId());
        }

        int lastPass = passes.size() - 1;
        this.blitPass = WrappedPostPass.wrap(passes.get(lastPass), Minecraft.getInstance().getResourceManager());
        this.blitPass.setDepthDrawFunction(GL11.GL_LEQUAL);
        passes.set(lastPass, this.blitPass);
    }

    public Optional<RenderTarget> getTransparencyTarget() {
        return Optional.ofNullable(this.transparencyTarget);
    }

    public Supplier<RenderTarget> getTransparencyTargetSupplier() {
        return () -> this.getTransparencyTarget().orElseThrow();
    }

    public Optional<WrappedPostPass> getBlitPass() {
        return Optional.ofNullable(this.blitPass);
    }

    public void setColor(ColorWrapper color) {
        this.getWrapped().map(chain -> (AccessorPostChain) chain)
                .flatMap(chain -> chain.getPasses().stream().findFirst().map(PostPass::getEffect))
                .ifPresent(effect -> {
                    effect.safeGetUniform("ColorOverlay").set(color.getRgba());
                });
    }

    public void setIgnoreDepth(boolean ignoreDepth) {
        this.getWrapped().map(chain -> (AccessorPostChain) chain)
                .flatMap(chain -> chain.getPasses().stream().findFirst().map(PostPass::getEffect))
                .ifPresent(effect -> {
                    effect.safeGetUniform("IgnoreDepth").set(ignoreDepth ? 1 : 0);
                });
    }

    public void redirect(RenderTarget newTarget, Consumer<PostChain> redirectedFn) {
        this.getWrapped().ifPresent(chain -> {
            if (this.redirected) {
                throw new IllegalStateException("Chain already redirected!");
            }

            var passes = ((AccessorPostChain) chain).getPasses();
            PostPass inputPass = passes.get(0);
            PostPass outputPass = passes.get(passes.size() - 1);
            RenderTarget previousInput = inputPass.inTarget;
            RenderTarget previousOutput = outputPass.outTarget;
            AccessorPostPass inputAccess = (AccessorPostPass) inputPass;
            AccessorPostPass outputAccess = (AccessorPostPass) outputPass;

            this.redirected = true;
            try {
                inputAccess.setInTarget(newTarget);
                outputAccess.setOutTarget(newTarget);
                redirectedFn.accept(chain);
            } finally {
                inputAccess.setInTarget(previousInput);
                outputAccess.setOutTarget(previousOutput);
                this.redirected = false;
            }
        });
    }
}
