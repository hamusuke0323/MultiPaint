package com.hamusuke.paint.network.protocol.packet.c2s.main.lobby;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

import java.io.IOException;
import java.util.UUID;

public class CreateCanvasC2SPacket implements Packet<ServerCommonPacketListener> {
    private final String title;
    private final UUID author;
    private final int width;
    private final int height;

    public CreateCanvasC2SPacket(String title, UUID author, int width, int height) {
        this.title = title;
        this.author = author;
        this.width = width;
        this.height = height;
    }

    public CreateCanvasC2SPacket(IntelligentByteBuf byteBuf) {
        this.title = byteBuf.readString();
        this.author = byteBuf.readUUID();
        this.width = byteBuf.readVariableInt();
        this.height = byteBuf.readVariableInt();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
        byteBuf.writeString(this.title);
        byteBuf.writeUUID(this.author);
        byteBuf.writeVariableInt(this.width);
        byteBuf.writeVariableInt(this.height);
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleCreateCanvasPacket(this);
    }

    public String getTitle() {
        return this.title;
    }

    public UUID getAuthor() {
        return this.author;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
