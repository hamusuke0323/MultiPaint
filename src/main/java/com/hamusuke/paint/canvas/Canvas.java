package com.hamusuke.paint.canvas;

import com.hamusuke.paint.network.LineData;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Canvas {
    private static final AtomicInteger NEXT_ID = new AtomicInteger();
    protected int canvasId = NEXT_ID.getAndIncrement();
    protected BufferedImage data;
    protected Graphics2D graphics2D;
    protected final CanvasInfo info;

    protected Canvas(UUID uuid, String title, UUID author, int width, int height) {
        this.info = new CanvasInfo(this.canvasId, uuid, title, author);
        this.data = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.graphics2D = this.getData().createGraphics();
        this.graphics2D.setColor(Color.WHITE);
        this.graphics2D.fillRect(0, 0, width, height);
    }

    public void acceptLine(LineData lineData) {
        this.graphics2D.setStroke(new BasicStroke(lineData.width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        this.graphics2D.setColor(lineData.color);
        this.graphics2D.drawLine(lineData.x1, lineData.y1, lineData.x2, lineData.y2);
    }

    public void resize(int width, int height) {
        Image prev = this.getData().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage newData = new BufferedImage(width, height, this.getData().getType());
        this.data = newData;
        this.graphics2D = newData.createGraphics();
        this.graphics2D.setColor(Color.WHITE);
        this.graphics2D.fillRect(0, 0, width, height);
        this.graphics2D.drawImage(prev, 0, 0, null);
    }

    public int getCanvasId() {
        return this.canvasId;
    }

    public void setCanvasId(int canvasId) {
        this.canvasId = canvasId;
        this.info.setCanvasId(this.canvasId);
    }

    public CanvasInfo getInfo() {
        return this.info;
    }

    public BufferedImage getData() {
        return this.data;
    }

    public int getWidth() {
        return this.getData().getWidth();
    }

    public int getHeight() {
        return this.getData().getHeight();
    }

    public void setData(BufferedImage data) {
        this.data = data;
        this.graphics2D = this.getData().createGraphics();
        this.graphics2D.drawImage(this.getData(), 0, 0, null);
    }
}
