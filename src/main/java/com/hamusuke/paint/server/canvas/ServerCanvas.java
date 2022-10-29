package com.hamusuke.paint.server.canvas;

import com.hamusuke.paint.canvas.Canvas;
import com.hamusuke.paint.network.LineData;
import com.hamusuke.paint.util.ConcurrentFixedDeque;

import java.awt.image.BufferedImage;
import java.util.Deque;
import java.util.UUID;

public class ServerCanvas extends Canvas {
    private final Deque<BufferedImage> historic = new ConcurrentFixedDeque<>(10);

    public ServerCanvas(String title, UUID author, int width, int height) {
        super(title, author, width, height);
    }

    @Override
    public void acceptLine(LineData lineData) {
        this.historic.addLast(this.data);
        super.acceptLine(lineData);
    }
}
