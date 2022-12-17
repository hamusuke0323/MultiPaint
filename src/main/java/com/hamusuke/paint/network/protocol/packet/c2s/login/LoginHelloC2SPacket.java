package com.hamusuke.paint.network.protocol.packet.c2s.login;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.server.ServerLoginPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

import java.util.UUID;

public class LoginHelloC2SPacket implements Packet<ServerLoginPacketListener> {
    private final UUID client;
    private final boolean cantEncrypt;

    public LoginHelloC2SPacket(UUID client) {
        this(client, false);
    }

    public LoginHelloC2SPacket(UUID client, boolean cantEncrypt) {
        this.client = client;
        this.cantEncrypt = cantEncrypt;
    }

    public LoginHelloC2SPacket(IntelligentByteBuf byteBuf) {
        this.client = byteBuf.readUUID();
        this.cantEncrypt = byteBuf.readBoolean();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeUUID(this.client);
        byteBuf.writeBoolean(this.cantEncrypt);
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.onHello(this);
    }

    public UUID getClient() {
        return this.client;
    }

    public boolean canEncrypt() {
        return !this.cantEncrypt;
    }
}
