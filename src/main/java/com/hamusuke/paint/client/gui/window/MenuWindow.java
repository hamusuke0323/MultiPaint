package com.hamusuke.paint.client.gui.window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MenuWindow extends Window {
    public MenuWindow() {
        super("Main Menu");
    }

    @Override
    protected void init() {
        JButton single = new JButton("Single Paint");
        single.setActionCommand("single");
        single.addActionListener(this);
        JButton multi = new JButton("Multi Paint");
        multi.setActionCommand("multi");
        multi.addActionListener(this);
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        addButton(this, single, layout, 0, 0, 1, 1, 1.0D);
        addButton(this, multi, layout, 0, 1, 1, 1, 1.0D);
        this.pack();
        this.setSize(this.getWidth() + this.getWidth() / 2, this.getHeight());
        this.setLocationRelativeTo(null);
    }

    @Override
    protected void onClose() {
        client.stopLooping();
        super.onClose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.setVisible(false);

        switch (e.getActionCommand()) {
            case "single":
                client.startIntegratedServer(s -> {
                });
                break;
            case "multi":
                client.setCurrentWindow(new ConnectingWindow());
                break;
        }
    }
}
