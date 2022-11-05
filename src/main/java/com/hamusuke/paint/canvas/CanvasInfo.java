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

    public CanvasInfo(int canvasId, UUID canvasUUID, String title, UUID author) {
        this.canvasId = canvasId;
        this.canvasUUID = canvasUUID;
        this.title = title;
        this.author = author;
    }

    public CanvasInfo(IntelligentByteBuf byteBuf) {
        this.canvasId = byteBuf.readVariableInt();
        this.canvasUUID = byteBuf.readUUID();
        this.title = byteBuf.readString();
        this.author = byteBuf.readUUID();
    }

    public void pack(IntelligentByteBuf byteBuf) {
        byteBuf.writeVariableInt(this.canvasId);
        byteBuf.writeUUID(this.canvasUUID);
        byteBuf.writeString(this.title);
        byteBuf.writeUUID(this.author);
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

    @Override
    public String toString() {
        return "CanvasInfo{" +
                "canvasId=" + canvasId +
                ", canvasUUID=" + canvasUUID +
                ", title='" + title + '\'' +
                ", author=" + author +
                '}';
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
        return Objects.hash(canvasId, canvasUUID, title, author);
    }
}
