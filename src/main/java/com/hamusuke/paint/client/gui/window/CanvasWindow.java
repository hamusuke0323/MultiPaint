package com.hamusuke.paint.client.gui.window;

import com.hamusuke.paint.client.canvas.ClientCanvas;
import com.hamusuke.paint.client.gui.component.CanvasComponent;
import com.hamusuke.paint.client.gui.dialog.ChangeWidthDialog;
import com.hamusuke.paint.network.protocol.packet.c2s.main.canvas.ChangeColorC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.canvas.ChangeWidthC2SPacket;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

public class CanvasWindow extends Window {
    private CanvasComponent canvas;

    public CanvasWindow(ClientCanvas canvas) {
        super(canvas.getInfo().toString());
        canvas.canvasWindow = this;
    }

    @Nullable
    @Override
    protected JMenuBar createMenuBar() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem disconnect = new JMenuItem("Disconnect");
        disconnect.addActionListener(e -> client.disconnect());
        JMenuItem exit = new JMenuItem("Leave Canvas");
        exit.addActionListener(e -> this.dispose(client::leaveCanvas));
        menu.add(disconnect);
        menu.add(exit);
        JMenu canvas = new JMenu("Canvas");
        JMenuItem color = new JMenuItem("Change Color");
        color.addActionListener(e -> this.changeColor(JColorChooser.showDialog(this, "Choose a color to paint", client.clientPainter.getColor())));

        JMenuItem width = new JMenuItem("Change Width");
        width.addActionListener(e -> this.changeWidth());

        canvas.add(color);
        canvas.add(width);
        jMenuBar.add(menu);
        jMenuBar.add(canvas);
        return jMenuBar;
    }

    private void changeColor(@Nullable Color color) {
        if (color != null) {
            client.getConnection().sendPacket(new ChangeColorC2SPacket(color));
        }
    }

    private void changeWidth() {
        new ChangeWidthDialog(this, (int) client.clientPainter.getWidth(), integer -> {
            client.getConnection().sendPacket(new ChangeWidthC2SPacket(integer));
        });
    }

    @Override
    protected void init() {
        this.canvas = new CanvasComponent(client, client.getCurrentCanvas());
        GridBagLayout layout = new GridBagLayout();
        JPanel panel = new JPanel(layout);
        addButton(panel, new JScrollPane(this.canvas), layout, 0, 0, 1, 1, 1.0D);
        addButton(panel, client.chat.getTextArea(), layout, 0, 1, 1, 1, 0.25D);
        addButton(panel, client.chat.getField(), layout, 0, 2, 1, 1, 0.125D);
        addButton(panel, new JScrollPane(client.painterList), layout, 1, 0, 1, 3, 0.5D, 1.0D);
        this.add(panel, BorderLayout.CENTER);
        this.setSize(720, 720);
        this.setLocationRelativeTo(null);
    }

    public CanvasComponent getCanvas() {
        return this.canvas;
    }

    @Override
    protected void onClose() {
        this.dispose(client::leaveCanvas);
    }
}
