package com.hamusuke.paint.client.canvas;

import com.hamusuke.paint.canvas.Canvas;
import com.hamusuke.paint.client.gui.window.CanvasWindow;
import com.hamusuke.paint.network.LineData;

import java.awt.image.BufferedImage;
import java.util.UUID;

public class ClientCanvas extends Canvas {
    public CanvasWindow canvasWindow;

    public ClientCanvas(String title, UUID author, int width, int height) {
        super(title, author, width, height);
    }

    @Override
    public void acceptLine(LineData lineData) {
        super.acceptLine(lineData);
        this.canvasWindow.getCanvas().repaint();
    }

    @Override
    public void setData(BufferedImage data) {
        super.setData(data);
        this.canvasWindow.getCanvas().repaint();
    }
}
