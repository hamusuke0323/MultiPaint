package com.hamusuke.paint.client.gui.window;

import com.hamusuke.paint.client.gui.component.CanvasComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CanvasWindow extends Window {
    private CanvasComponent canvas;

    public CanvasWindow() {
        super(String.format("Client Address: %s, Server Address: %s", client.getConnection().getChannel().localAddress(), client.getConnection().getChannel().remoteAddress()));
    }

    private JMenuBar createMenuBar() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem disconnect = new JMenuItem("Disconnect");
        disconnect.setActionCommand("disconnect");
        disconnect.addActionListener(this);
        file.add(disconnect);
        jMenuBar.add(file);
        return jMenuBar;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "disconnect":
                this.onClose();
                break;
        }
    }
}
