package com.hamusuke.paint.client.gui.window;

import com.hamusuke.paint.client.canvas.ClientCanvas;
import com.hamusuke.paint.client.gui.component.CanvasComponent;
import com.hamusuke.paint.network.protocol.packet.c2s.main.canvas.ChangeColorC2SPacket;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

public class CanvasWindow extends Window {
    private CanvasComponent canvas;

    public CanvasWindow(ClientCanvas canvas) {
        super(canvas.getInfo().toString());
    }

    private JMenuBar createMenuBar() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem disconnect = new JMenuItem("Disconnect");
        disconnect.addActionListener(e -> this.onClose());
        JMenuItem exit = new JMenuItem("Leave Canvas");
        exit.addActionListener(e -> this.dispose(client::leaveCanvas));
        menu.add(disconnect);
        menu.add(exit);
        JMenu canvas = new JMenu("Canvas");
        JMenuItem color = new JMenuItem("Change Color");
        color.addActionListener(e -> this.changeColor(JColorChooser.showDialog(this, "Choose a color to paint", Color.BLACK)));
        canvas.add(color);
        jMenuBar.add(menu);
        jMenuBar.add(canvas);
        return jMenuBar;
    }

    private void changeColor(@Nullable Color color) {
        if (color != null) {
            client.getConnection().sendPacket(new ChangeColorC2SPacket(color));
        }
    }

    @Override
    protected void init() {
        JPanel panel = new JPanel(this.springLayout);
        this.canvas = new CanvasComponent(client, client.getCurrentCanvas());

        this.addScalable(panel, this.createMenuBar(), 0.0F, 0.0F, 1.0F, 0.05F);
        this.addScalable(panel, new JScrollPane(this.canvas), 0.0F, 0.05F, 0.85F, 0.8F);
        this.addScalable(panel, client.chat.getTextArea(), 0.0F, 0.85F, 0.85F, 0.1F);
        this.addScalable(panel, client.chat.getField(), 0.0F, 0.95F, 0.85F, 0.05F);
        this.addScalable(panel, new JScrollPane(client.painterList), 0.85F, 0.05F, 0.15F, 0.95F);

        this.add(panel);
        this.setSize(720, 720);
        this.setLocationRelativeTo(null);
    }

    public CanvasComponent getCanvas() {
        return this.canvas;
    }

    @Override
    protected void onClose() {
        this.dispose(client::disconnect);
    }
}
