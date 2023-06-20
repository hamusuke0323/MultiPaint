package com.hamusuke.paint.server.network;

import java.awt.*;

public class ServerPainterData {
    private final int color;
    private final float width;

    public ServerPainterData(Color color, float width) {
        this.color = color.getRGB();
        this.width = width;
    }

    public Color getColor() {
        return new Color(this.color, true);
    }

    public float getWidth() {
        return this.width <= 0.0F ? 5.0F : this.width;
    }
}
