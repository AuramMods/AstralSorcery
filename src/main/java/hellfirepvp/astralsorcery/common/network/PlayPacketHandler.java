/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network;

import hellfirepvp.astralsorcery.common.util.codec.StreamCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class PlayPacketHandler<T> {

    private final Class<T> packetType;
    private final NetworkDirection direction;

    PlayPacketHandler(Class<T> packetType, @Nullable NetworkDirection direction) {
        this.packetType = packetType;
        this.direction = direction;
    }

    public abstract StreamCodec<FriendlyByteBuf, T> codec();

    protected abstract void receive(T packet, PacketContext context);

    final void register(SimpleChannel channel, int discriminator) {
        channel.registerMessage(discriminator, this.packetType,
                (packet, buffer) -> this.codec().encode(buffer, packet),
                buffer -> this.codec().decode(buffer),
                (packet, contextSupplier) -> {
                    NetworkEvent.Context context = contextSupplier.get();
                    context.setPacketHandled(true);
                    this.receive(packet, new PacketContext(channel, context));
                }, Optional.ofNullable(this.direction));
    }

    public abstract static class ToClient<T> extends PlayPacketHandler<T> {

        protected ToClient(Class<T> packetType) {
            super(packetType, NetworkDirection.PLAY_TO_CLIENT);
        }

        @Override
        protected final void receive(T packet, PacketContext context) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> this.handle(packet, context));
        }

        public abstract void handle(T packet, PacketContext context);
    }

    public abstract static class ToServer<T> extends PlayPacketHandler<T> {

        protected ToServer(Class<T> packetType) {
            super(packetType, NetworkDirection.PLAY_TO_SERVER);
        }

        @Override
        protected final void receive(T packet, PacketContext context) {
            this.handle(packet, context);
        }

        public abstract void handle(T packet, PacketContext context);
    }

    public abstract static class BiDirectional<T> extends PlayPacketHandler<T> {

        protected BiDirectional(Class<T> packetType) {
            super(packetType, null);
        }

        @Override
        protected final void receive(T packet, PacketContext context) {
            switch (context.forge.getDirection()) {
                case PLAY_TO_SERVER -> this.handleServer(packet, context);
                case PLAY_TO_CLIENT -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> () -> this.handleClient(packet, context));
                default -> throw new IllegalStateException("Astral Sorcery packets require a play connection");
            }
        }

        public abstract void handleClient(T payload, PacketContext context);

        public abstract void handleServer(T payload, PacketContext context);
    }

    public static final class PacketContext {

        private final SimpleChannel channel;
        private final NetworkEvent.Context forge;

        private PacketContext(SimpleChannel channel, NetworkEvent.Context forge) {
            this.channel = channel;
            this.forge = forge;
        }

        public void enqueueWork(Runnable work) {
            this.forge.enqueueWork(() -> {
                if (this.forge.getNetworkManager().isConnected()) {
                    work.run();
                }
            });
        }

        @Nullable
        public Player player() {
            if (this.forge.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
                return this.forge.getSender();
            }
            return DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> this::clientPlayer);
        }

        @Nullable
        private Player clientPlayer() {
            Minecraft minecraft = Minecraft.getInstance();
            ClientPacketListener connection = minecraft.getConnection();
            return connection != null && connection.getConnection() == this.forge.getNetworkManager()
                    ? minecraft.player : null;
        }

        public void reply(Object packet) {
            this.channel.reply(packet, this.forge);
        }
    }
}
