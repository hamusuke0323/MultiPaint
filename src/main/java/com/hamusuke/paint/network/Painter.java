package com.hamusuke.paint.network;

import com.hamusuke.paint.canvas.Canvas;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Painter {
    private static final AtomicInteger PAINTER_ID_INCREMENTER = new AtomicInteger();
    private final UUID uuid;
    @Nullable
    protected Canvas currentCanvas;
    private int id = PAINTER_ID_INCREMENTER.getAndIncrement();
    private int ping;
    private Color color;
    private float width = 5.0F;

    protected Painter(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return "" + this.id; //TODO
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPing() {
        return this.ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    public Color getColor() {
        if (this.color == null) {
            this.setColor(Color.BLACK);
        }

        return this.color;
    }

    public float getWidth() {
        return this.width;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void joinCanvas(Canvas canvas) {
        this.currentCanvas = canvas;
    }

    @Nullable
    public Canvas getCurrentCanvas() {
        return this.currentCanvas;
    }

    public boolean isInAnyCanvas() {
        return this.currentCanvas != null;
    }

    public boolean isInCanvas(Canvas canvas) {
        return canvas != null && this.isInAnyCanvas() && this.currentCanvas.getCanvasId() == canvas.getCanvasId();
    }

    public boolean isInLobby() {
        return this.currentCanvas == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Painter painter = (Painter) o;
        return this.id == painter.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
