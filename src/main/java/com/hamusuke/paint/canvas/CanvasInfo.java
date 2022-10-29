package com.hamusuke.paint.canvas;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;

import java.util.UUID;

public class CanvasInfo {
    private int canvasId;
    private final String title;
    private final UUID author;

    public CanvasInfo(int canvasId, String title, UUID author) {
        this.canvasId = canvasId;
        this.title = title;
        this.author = author;
    }

    public CanvasInfo(IntelligentByteBuf byteBuf) {
        this.canvasId = byteBuf.readVarInt();
        this.title = byteBuf.readString();
        this.author = byteBuf.readUUID();
    }

    public void pack(IntelligentByteBuf byteBuf) {
        byteBuf.writeVarInt(this.canvasId);
        byteBuf.writeString(this.title);
        byteBuf.writeUUID(this.author);
    }

    public int getCanvasId() {
        return this.canvasId;
    }

    public void setCanvasId(int canvasId) {
        this.canvasId = canvasId;
    }

    public String getTitle() {
        return this.title;
    }

    public UUID getAuthor() {
        return this.author;
    }

    @Override
    public String toString() {
        return String.format("Canvas Id: %d, Title: %s, Author: %s", this.canvasId, this.title, this.author);
    }
}
