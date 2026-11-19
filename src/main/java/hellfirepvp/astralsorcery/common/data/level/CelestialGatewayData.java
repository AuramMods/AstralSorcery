/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataManager;
import hellfirepvp.astralsorcery.common.lib.types.SyncDataTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import hellfirepvp.observerlib.common.data.WorldCacheDomain;
import hellfirepvp.observerlib.common.data.base.GlobalWorldData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import hellfirepvp.astralsorcery.common.util.codec.ComponentSerialization;
import hellfirepvp.astralsorcery.common.util.codec.ByteBufCodecs;
import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CelestialGatewayData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CelestialGatewayData extends GlobalWorldData {

    private final Map<BlockPos, GatewayEntry> gatewayEntries = new HashMap<>();

    private static final Codec<Set<GatewayEntry>> ENTRY_CODEC = SetCodec.of(GatewayEntry.CODEC);

    public CelestialGatewayData(WorldCacheDomain.SaveKey<CelestialGatewayData> key) {
        super(key);
    }

    public Set<GatewayEntry> getGatewayEntries() {
        return this.read(() -> Set.copyOf(this.gatewayEntries.values()));
    }

    public Optional<GatewayEntry> getGateway(BlockPos pos) {
        return this.read(() -> Optional.ofNullable(this.gatewayEntries.get(pos)));
    }

    public void addGateway(Level level, BlockPos pos, Component customName, DyeColor color) {
        this.removeGateway(level, pos);

        GatewayEntry entry = new GatewayEntry(pos, Optional.ofNullable(customName), color);
        this.write(() -> {
            this.gatewayEntries.put(pos, entry);
            this.markDirty();
        });
        SyncDataManager.getInstance().getData(SyncDataTypesAS.CELESTIAL_GATEWAY).addEntry(level, entry);
    }

    public void removeGateway(Level level, BlockPos pos) {
        GatewayEntry entry = this.write(() -> {
            GatewayEntry removed = this.gatewayEntries.remove(pos);
            if (removed != null) {
                this.markDirty();
            }
            return removed;
        });
        if (entry != null) {
            SyncDataManager.getInstance().getData(SyncDataTypesAS.CELESTIAL_GATEWAY).removeEntry(level, entry);
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        WorldCacheNbt.write(tag, ENTRY_CODEC.fieldOf("entries").codec(), this.getGatewayEntries());
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        Set<GatewayEntry> entries = WorldCacheNbt.read(tag, ENTRY_CODEC.fieldOf("entries").codec());
        this.gatewayEntries.clear();
        entries.forEach(entry -> this.gatewayEntries.put(entry.getPos(), entry));
    }

    @Override
    public void updateTick(Level level) {}

    @Override
    public void onLoad(Level world) {
        super.onLoad(world);

        List.copyOf(this.gatewayEntries.keySet()).forEach(pos -> {
            if (!MiscUtil.getTileExists(world, pos, TileCelestialGateway.class, true)) {
                this.gatewayEntries.remove(pos);
                this.markDirty();
            }
        });
    }

    public static class GatewayEntry {

        public static final Codec<GatewayEntry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                BlockPos.CODEC.fieldOf("pos").forGetter(GatewayEntry::getPos),
                CodecUtil.optional(ComponentSerialization.CODEC, "customName", GatewayEntry::getCustomName),
                DyeColor.CODEC.fieldOf("color").forGetter(GatewayEntry::getColor)
        ).apply(inst, GatewayEntry::new));

        public static final StreamCodec<FriendlyByteBuf, GatewayEntry> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BLOCK_POS,
                GatewayEntry::getPos,
                ByteBufCodecs.optional(ByteBufCodecs.COMPONENT),
                entry -> Optional.ofNullable(entry.getCustomName()),
                CodecUtil.enumStreamCodec(DyeColor.class),
                GatewayEntry::getColor,
                GatewayEntry::new
        );

        private final BlockPos pos;
        private final Component customName;
        private final DyeColor color;

        public GatewayEntry(BlockPos pos, Optional<Component> customName, DyeColor color) {
            this.pos = pos;
            this.customName = customName.orElse(null);
            this.color = color;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        @Nullable
        public Component getCustomName() {
            return this.customName;
        }

        public DyeColor getColor() {
            return ObjectUtils.firstNonNull(this.color, DyeColor.YELLOW);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            GatewayEntry that = (GatewayEntry) o;
            return Objects.equals(pos, that.pos);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(pos);
        }
    }
}
