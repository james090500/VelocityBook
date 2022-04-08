package com.james090500.VelocityBook;

import com.james090500.VelocityBook.packets.OpenBookPacket;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocol;
import dev.simplix.protocolize.api.module.ProtocolizeModule;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;

public class BookModule implements ProtocolizeModule {

    @Override
    public void registerMappings(MappingProvider mappingProvider) {

    }

    @Override
    public void registerPackets(ProtocolRegistrationProvider protocolRegistrationProvider) {
        protocolRegistrationProvider.registerPacket(OpenBookPacket.MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, OpenBookPacket.class);
    }
}
