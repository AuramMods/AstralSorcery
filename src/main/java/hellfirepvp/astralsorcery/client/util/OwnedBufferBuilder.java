/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.vertex.BufferBuilder;
import org.lwjgl.system.MemoryUtil;

public final class OwnedBufferBuilder extends BufferBuilder implements AutoCloseable {

    public OwnedBufferBuilder(int initialBytes) {
        super(Math.max(1, (initialBytes + 5) / 6));
    }

    @Override
    public void close() {
        if (this.buffer != null) {
            // Match the allocator used by Minecraft's MemoryTracker for BufferBuilder.
            MemoryUtil.getAllocator(false).free(MemoryUtil.memAddress0(this.buffer));
            this.buffer = null;
        }
    }
}
