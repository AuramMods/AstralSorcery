package hellfirepvp.astralsorcery.common.starlight.api;

import hellfirepvp.astralsorcery.common.starlight.transmission.StarlightTransmissionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

/** Capability exposed by block entities that receive linked starlight on the server. */
@AutoRegisterCapability
@FunctionalInterface
public interface BlockStarlightReceiver {
    Capability<BlockStarlightReceiver> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    /** Delivers one source's packet after native splitting and transmission losses. */
    void receive(ServerLevel level, StarlightTransmissionPacket packet);
}
