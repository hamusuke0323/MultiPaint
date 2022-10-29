package com.hamusuke.paint.network.protocol.packet.c2s.handshaking;

import com.hamusuke.paint.Constants;
import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.server.ServerHandshakePacketListener;
import com.hamusuke.paint.network.protocol.Protocol;
import com.hamusuke.paint.network.protocol.packet.Packet;

public class HandshakeC2SPacket implements Packet<ServerHandshakePacketListener> {
    private static final int MAX_ADDRESS_LENGTH = 255;
    private final int protocolVersion;
    private final String address;
    private final int port;
    private final Protocol intendedProtocol;

    public HandshakeC2SPacket(String address, int port, Protocol intendedProtocol) {
        this.protocolVersion = Constants.PROTOCOL_VERSION;
        this.address = address;
        this.port = port;
        this.intendedProtocol = intendedProtocol;
    }

    public HandshakeC2SPacket(IntelligentByteBuf buf) {
        this.protocolVersion = buf.readVarInt();
        this.address = buf.readString(MAX_ADDRESS_LENGTH);
        this.port = buf.readUnsignedShort();
        this.intendedProtocol = Protocol.byId(buf.readVarInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.protocolVersion);
        buf.writeString(this.address);
        buf.writeShort(this.port);
        buf.writeVarInt(this.intendedProtocol.getStateId());
    }

    @Override
    public void handle(ServerHandshakePacketListener listener) {
        listener.onHandshake(this);
    }

    public Protocol getIntendedProtocol() {
        return this.intendedProtocol;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    public String getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }
}
