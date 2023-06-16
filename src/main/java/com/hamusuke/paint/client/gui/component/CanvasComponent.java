package com.hamusuke.paint.client.gui.component;

import com.hamusuke.paint.client.PaintClient;
import com.hamusuke.paint.client.canvas.ClientCanvas;
import com.hamusuke.paint.network.LineData;
import com.hamusuke.paint.network.protocol.packet.c2s.main.canvas.LineC2SPacket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class CanvasComponent extends JComponent implements MouseListener, MouseMotionListener {
    private int x = -1;
    private int y = -1;
    private int px = -1;
    private int py = -1;
    private Operation op = Operation.UNKNOWN;
    private final PaintClient client;
    private final ClientCanvas canvas;

    public CanvasComponent(PaintClient client, ClientCanvas canvas) {
        this.client = client;
        this.canvas = canvas;
        this.setOpaque(false);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public void sendLine(LineData line) {
        if (this.client.getConnection() == null) {
            return;
        }

        this.client.getConnection().sendPacket(new LineC2SPacket(line));
    }

    @Override
    public void paint(Graphics g) {
        if (this.isLineValid()) {
            if (this.op == Operation.DRAW) {
                this.sendLine(new LineData(this.px, this.py, this.x, this.y, this.client.clientPainter.getColor(), this.client.clientPainter.getWidth()));
            } else if (this.op == Operation.ERASE) {
                this.sendLine(new LineData(this.px, this.py, this.x, this.y, Color.WHITE, 50.0F));
            }
        }

        g.drawImage(this.canvas.getData(), 0, 0, null);
    }

    protected boolean isLineValid() {
        return this.x >= 0 && this.y >= 0 && this.px >= 0 && this.py >= 0;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point point = e.getPoint();
        this.x = point.x;
        this.y = point.y;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        this.op = Operation.byId(e.getModifiers());
        this.px = this.x;
        this.py = this.y;
        Point point = e.getPoint();
        this.x = point.x;
        this.y = point.y;
        this.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.onFinishDragging();
    }

    protected void onFinishDragging() {
        this.x = -1;
        this.y = -1;
        this.px = -1;
        this.py = -1;
        this.op = Operation.UNKNOWN;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(this.canvas.getData().getWidth(), this.canvas.getData().getHeight());
    }

    private enum Operation {
        UNKNOWN(-1),
        DRAW(MouseEvent.BUTTON1_MASK),
        ERASE(MouseEvent.BUTTON3_MASK);

        private final int id;

        Operation(int id) {
            this.id = id;
        }

        public static Operation byId(int id) {
            for (Operation op : values()) {
                if (op.id == id) {
                    return op;
                }
            }

            return UNKNOWN;
        }
    }
}
