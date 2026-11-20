/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.shader;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.util.OwnedBufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: WrappedBufferSource
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class WrappedBufferSource extends MultiBufferSource.BufferSource {

    private static final int MAX_POOLED_BUFFERS = 4;
    private static final Deque<OwnedBufferBuilder> BUFFER_POOL = new ArrayDeque<>();

    private final MultiBufferSource baseSource;
    private final Function<RenderType, RenderType> decorate;
    private final Map<RenderType, RenderType> wrappedTypes = new HashMap<>();
    private final Map<RenderType, OwnedBufferBuilder> batches = new LinkedHashMap<>();

    public WrappedBufferSource(MultiBufferSource baseSource, Function<RenderType, RenderType> decorate) {
        super(null, Map.of());
        this.baseSource = baseSource;
        this.decorate = decorate;
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        RenderType wrapped = this.wrappedTypes.computeIfAbsent(renderType, this.decorate);
        OwnedBufferBuilder builder = this.batches.get(renderType);
        if (builder != null && !wrapped.canConsolidateConsecutiveGeometry()) {
            this.drawWrapped(renderType);
            builder = null;
        }

        if (builder == null) {
            if (this.baseSource instanceof BufferSource source) {
                source.endLastBatch();
            }
            builder = BUFFER_POOL.pollLast();
            if (builder == null) {
                builder = new OwnedBufferBuilder(wrapped.bufferSize());
            }
            builder.begin(wrapped.mode(), wrapped.format());
            this.batches.put(renderType, builder);
        }
        return builder;
    }

    public void end() {
        try {
            Lists.reverse(new ArrayList<>(this.batches.keySet())).forEach(this::drawWrapped);
        } finally {
            this.batches.values().forEach(OwnedBufferBuilder::close);
            this.batches.clear();
        }
    }

    @Override
    public void endLastBatch() {
        this.end();
    }

    @Override
    public void endBatch() {
        this.end();
    }

    @Override
    public void endBatch(RenderType renderType) {
        this.drawWrapped(renderType);
    }

    protected void drawWrapped(RenderType raw) {
        OwnedBufferBuilder builder = this.batches.remove(raw);
        if (builder == null) {
            return;
        }

        try {
            this.wrappedTypes.get(raw).end(builder, RenderSystem.getVertexSorting());
        } finally {
            if (builder.building() || BUFFER_POOL.size() >= MAX_POOLED_BUFFERS) {
                builder.close();
            } else {
                builder.discard();
                builder.unsetDefaultColor();
                BUFFER_POOL.addLast(builder);
            }
        }
    }
}
