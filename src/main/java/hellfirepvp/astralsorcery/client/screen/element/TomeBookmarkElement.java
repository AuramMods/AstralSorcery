/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.screen.tome.BookmarkProvider;
import hellfirepvp.astralsorcery.client.screen.tome.TomeScreen;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderQuadUtil;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.sounds.SoundManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomeBookmarkElement
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TomeBookmarkElement extends AbstractWidget {

    private static final int TAIL_WIDTH = 30;
    private static final int TEXT_PADDING = 2;
    private static final String ELLIPSIS = "…";

    private final boolean selected;
    private final BookmarkProvider provider;

    public TomeBookmarkElement(int x, int y, int availableWidth, boolean selected, BookmarkProvider provider) {
        super(x, y, Math.min(availableWidth, preferredWidth(provider)), 14, provider.getName());
        this.selected = selected;
        this.provider = provider;
        if (Minecraft.getInstance().font.width(provider.getName()) > this.getWidth() - 2 * TEXT_PADDING) {
            this.setTooltip(Tooltip.create(provider.getName()));
        }
    }

    public static int preferredWidth(BookmarkProvider provider) {
        return bookmarkBodyWidth(provider) + TAIL_WIDTH;
    }

    private static int bookmarkBodyWidth(BookmarkProvider provider) {
        Font font = Minecraft.getInstance().font;
        return Math.max(font.width(provider.getName()) + 2 * TEXT_PADDING, 40);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        PoseStack pose = guiGraphics.pose();
        Font font = Minecraft.getInstance().font;
        float v = this.selected || this.isHovered() ? 0.5F : 0F;

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        this.provider.getBookmarkTexture().bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexShader, buf -> {
            int bodyWidth = Math.min(bookmarkBodyWidth(this.provider), this.getWidth());
            int leadingWidth = Math.min(20, bodyWidth);
            int offsetX = this.getX();

            RenderQuadUtil.rect(buf, pose, offsetX, this.getY(), leadingWidth, 14)
                    .tex(0F, v, leadingWidth / 57F, 0.5F)
                    .draw();

            offsetX += leadingWidth;
            int remainingBody = bodyWidth - leadingWidth;
            while (remainingBody > 0) {
                int segmentWidth = Math.min(5, remainingBody);
                RenderQuadUtil.rect(buf, pose, offsetX, this.getY(), segmentWidth, 14)
                        .tex(21F / 57F, v, segmentWidth / 57F, 0.5F)
                        .draw();
                offsetX += segmentWidth;
                remainingBody -= segmentWidth;
            }

            int tailWidth = this.getWidth() - bodyWidth;
            if (tailWidth > 0) {
                RenderQuadUtil.rect(buf, pose, offsetX, this.getY(), tailWidth, 14)
                        .tex(27F / 57F, v, TAIL_WIDTH / 57F, 0.5F)
                        .draw();
            }
        });
        RenderSystem.disableBlend();

        int textX = this.getX() + TEXT_PADDING;
        int textY = this.getY() + TEXT_PADDING;
        int textWidth = this.getWidth() - 2 * TEXT_PADDING;
        if (font.width(this.provider.getName()) > textWidth) {
            String label = font.plainSubstrByWidth(this.provider.getName().getString(), textWidth - font.width(ELLIPSIS));
            guiGraphics.drawString(font, label + ELLIPSIS, textX, textY, 0xDDDDDDDD);
        } else {
            guiGraphics.drawString(font, this.provider.getName(), textX, textY, 0xDDDDDDDD);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (Minecraft.getInstance().screen instanceof TomeScreen tomeScreen) {
            if (tomeScreen.getBookmarkIndex() != this.provider.getBookmarkIndex()) {
                tomeScreen.doBookmarkClick(this.provider);
            }
        }
    }

    @Override
    public void playDownSound(SoundManager handler) {
        // No sound ?
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}
