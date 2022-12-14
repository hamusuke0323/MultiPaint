package com.hamusuke.paint.network.protocol.packet.c2s.main;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

public class ChatC2SPacket implements Packet<ServerCommonPacketListener> {
    private final String msg;

    public ChatC2SPacket(String msg) {
        this.msg = msg;
    }

    public ChatC2SPacket(IntelligentByteBuf byteBuf) {
        this.msg = byteBuf.readString();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeString(this.msg);
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleChatPacket(this);
    }

    public String getMsg() {
        return this.msg;
    }
}
