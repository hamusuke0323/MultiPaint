package com.hamusuke.paint.canvas;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class CanvasInfo implements Serializable {
    private static final long serialVersionUID = 8790258815904981565L;
    private int canvasId;
    private final UUID canvasUUID;
    private final String title;
    private final UUID author;
    private int width;
    private int height;

    public CanvasInfo(int canvasId, UUID canvasUUID, String title, UUID author, int width, int height) {
        this.canvasId = canvasId;
        this.canvasUUID = canvasUUID;
        this.title = title;
        this.author = author;
        this.width = width;
        this.height = height;
    }

    public CanvasInfo(IntelligentByteBuf byteBuf) {
        this.canvasId = byteBuf.readVariableInt();
        this.canvasUUID = byteBuf.readUUID();
        this.title = byteBuf.readString();
        this.author = byteBuf.readUUID();
        this.width = byteBuf.readVariableInt();
        this.height = byteBuf.readVariableInt();
    }

    public void pack(IntelligentByteBuf byteBuf) {
        byteBuf.writeVariableInt(this.canvasId);
        byteBuf.writeUUID(this.canvasUUID);
        byteBuf.writeString(this.title);
        byteBuf.writeUUID(this.author);
        byteBuf.writeVariableInt(this.width);
        byteBuf.writeVariableInt(this.height);
    }

    public int getCanvasId() {
        return this.canvasId;
    }

    public UUID getCanvasUUID() {
        return this.canvasUUID;
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

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return String.format("%s, W: %d, H: %d, UUID: %s", this.title, this.width, this.height, this.canvasUUID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CanvasInfo that = (CanvasInfo) o;
        return canvasId == that.canvasId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(canvasId);
    }
}
