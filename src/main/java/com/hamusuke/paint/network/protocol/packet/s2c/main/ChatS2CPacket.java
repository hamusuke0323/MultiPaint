package com.hamusuke.paint.network.protocol.packet.s2c.main;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

public class ChatS2CPacket implements Packet<ClientCommonPacketListener> {
    private final String msg;

    public ChatS2CPacket(String msg) {
        this.msg = msg;
    }

    public ChatS2CPacket(IntelligentByteBuf byteBuf) {
        this.msg = byteBuf.readString();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeString(this.msg);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleChatPacket(this);
    }

    public String getMsg() {
        return this.msg;
    }
}
