package com.hamusuke.paint.network.protocol.packet.s2c.login;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.ClientLoginPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

public class LoginCompressionS2CPacket implements Packet<ClientLoginPacketListener> {
    private final int threshold;

    public LoginCompressionS2CPacket(int threshold) {
        this.threshold = threshold;
    }

    public LoginCompressionS2CPacket(IntelligentByteBuf byteBuf) {
        this.threshold = byteBuf.readVarInt();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVarInt(this.threshold);
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.onCompression(this);
    }

    public int getThreshold() {
        return this.threshold;
    }
}
