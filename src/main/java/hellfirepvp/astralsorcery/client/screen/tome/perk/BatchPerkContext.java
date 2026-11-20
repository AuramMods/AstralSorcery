/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.perk;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.screen.tome.perk.render.PerkRenderType;
import hellfirepvp.astralsorcery.client.util.OwnedBufferBuilder;
import net.minecraft.client.renderer.RenderType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BatchPerkContext
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class BatchPerkContext {

    public static final int PRIORITY_BACKGROUND = 100;
    public static final int PRIORITY_FOREGROUND = 200;
    public static final int PRIORITY_OVERLAY    = 300;

    private final Map<PerkRenderType, OwnedBufferBuilder> buffers = new TreeMap<>();
    private boolean closed;

    private BatchPerkContext(Collection<PerkRenderType> renderTypes) {
        renderTypes.forEach(type -> this.buffers.put(type, null));
    }

    public VertexConsumer getBuffer(PerkRenderType type) {
        if (this.closed) {
            throw new IllegalStateException("Tried to get buffer from already closed BatchPerkContext!");
        }
        OwnedBufferBuilder buffer = this.buffers.computeIfAbsent(type, ignored -> new OwnedBufferBuilder(0x7_0000));
        if (!buffer.building()) {
            RenderType renderType = type.renderType();
            buffer.begin(renderType.mode(), renderType.format());
        }
        return buffer;
    }

    public void draw() {
        this.buffers.forEach((type, buffer) -> {
            if (buffer != null && buffer.building()) {
                if (buffer.isCurrentBatchEmpty()) {
                    buffer.endOrDiscardIfEmpty();
                } else {
                    type.renderType().end(buffer, RenderSystem.getVertexSorting());
                }
                buffer.unsetDefaultColor();
            }
        });
    }

    public void free() {
        this.buffers.values().forEach(buffer -> {
            if (buffer != null) {
                buffer.close();
            }
        });
        this.buffers.clear();
        this.closed = true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<PerkRenderType> renderTypes = new ArrayList<>();

        public Builder addRenderType(PerkRenderType renderType) {
            if (this.renderTypes.stream().noneMatch(rt -> rt.equals(renderType))) {
                this.renderTypes.add(renderType);
            }
            return this;
        }

        public BatchPerkContext build() {
            return new BatchPerkContext(this.renderTypes);
        }
    }
}
