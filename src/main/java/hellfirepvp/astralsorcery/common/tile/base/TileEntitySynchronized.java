/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.SyncedAuxiliaryLightManager;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileEntitySynchronized
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class TileEntitySynchronized<T extends TileEntitySynchronized.Data> extends BlockEntity {

    public static final String KEY_SAVE_DATA = "saveData";
    private static final String KEY_SYNC_DATA = "syncData";

    protected final RandomSource rand = RandomSource.create();

    private final TileCapabilities tileCapabilities = new TileCapabilities();
    private T data;

    protected TileEntitySynchronized(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        this(type.type(), pos, blockState);
    }

    protected TileEntitySynchronized(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.setTileData(this.newTileData());
    }

    protected T newTileData() {
        return this.dataCodec().parse(NbtOps.INSTANCE, new CompoundTag()).getOrThrow(false, str -> {
            throw new IllegalStateException(String.format("Failed to create new data for tile entity! (%s)", str));
        });
    }

    protected final void setTileData(T data) {
        this.data = data;
        this.data.tile = this;
        this.tileCapabilities.clear();
    }

    @Nonnull
    @Override
    public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> capability, @Nullable Direction side) {
        if (this.tileCapabilities != null) {
            LazyOptional<C> local = this.tileCapabilities.get(this.data, capability, side);
            if (local.isPresent()) {
                return local;
            }
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void invalidateCaps() {
        this.tileCapabilities.invalidate();
        super.invalidateCaps();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.tileCapabilities.revive();
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        this.reviveCaps();
    }

    public final T getTileData() {
        return this.data;
    }

    public abstract Codec<T> dataCodec();

    public Codec<T> netDataCodec() {
        return this.dataCodec();
    }

    public void markForUpdate() {
        if (this.getLevel() != null) {
            BlockState thisState = this.getBlockState();
            this.getLevel().sendBlockUpdated(getBlockPos(), thisState, thisState, Block.UPDATE_ALL);
        }
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(KEY_SAVE_DATA, this.dataCodec().encodeStart(this.ops(), this.getTileData())
                .getOrThrow(false, error -> {}));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(KEY_SAVE_DATA)) {
            this.dataCodec().parse(this.ops(), tag.get(KEY_SAVE_DATA)).result().ifPresent(this::setTileData);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.put(KEY_SYNC_DATA, this.netDataCodec().encodeStart(this.ops(), this.getTileData())
                .getOrThrow(false, error -> {}));
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        CompoundTag tag = packet.getTag();
        if (tag != null) {
            this.handleUpdateTag(tag);
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.load(tag);
        if (!tag.contains(KEY_SYNC_DATA)) {
            return;
        }

        this.netDataCodec().parse(this.ops(), tag.get(KEY_SYNC_DATA)).result().ifPresent(newData -> {
            T previous = this.getTileData();
            this.setTileData(newData);
            this.onClientDataUpdated(previous);
        });
    }

    private DynamicOps<Tag> ops() {
        if (this.getLevel() != null) {
            return RegistryOps.create(NbtOps.INSTANCE, this.getLevel().registryAccess());
        }
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return RegistryOps.create(NbtOps.INSTANCE, server.registryAccess());
        }
        return RegistryOps.create(NbtOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
    }

    //Called after receiving tile data from server, either chunk sync or explicit tile sync
    protected void onClientDataUpdated(T previousData) {}

    //Actual removal from the chunk, not including 'just' unloading
    public void onTileEntityRemove(Level level, BlockPos pos) {}

    public void setLight(ServerLevel sLevel, int light) {
        SyncedAuxiliaryLightManager.get(sLevel).setLightAt(this.getBlockPos(), light);
    }

    public static class Data {

        TileEntitySynchronized<?> tile;

        @Nullable
        protected <T extends TileEntitySynchronized<?>> T getTile() {
            this.ensureTileIsBound();
            return MiscUtil.cast(this.tile);
        }

        @Nullable
        protected <T extends TileEntitySynchronized<?>> T getTile(Class<T> clazzHint) {
            this.ensureTileIsBound();
            return clazzHint.cast(this.tile);
        }

        @Nonnull
        protected <T extends TileEntitySynchronized<?>> Optional<T> getOptionalTile(Class<T> clazzHint) {
            this.ensureTileIsBound();
            if (clazzHint.isInstance(this.tile)) {
                return Optional.of(clazzHint.cast(this.tile));
            }
            return Optional.empty();
        }

        protected Level getLevel() {
            return this.getTile().getLevel();
        }

        private void ensureTileIsBound() {
            if (this.tile == null) {
                throw new IllegalStateException("TileEntity data without bound tileentity!");
            }
        }

        public void markDirty() {
            if (this.getTile() != null) {
                this.getTile().setChanged();
            }
        }

        public void markForUpdate() {
            if (this.getTile() != null) {
                this.getTile().markForUpdate();
            }
        }
    }
}
