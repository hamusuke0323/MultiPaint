package com.hamusuke.paint.client.gui.window;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MenuWindow extends Window {
    public MenuWindow() {
        super("Main Menu");
    }

    @Override
    protected void init() {
        JPanel panel = new JPanel(this.springLayout);

        JButton single = new JButton("Single Paint");
        single.setActionCommand("single");
        single.addActionListener(this);
        JButton multi = new JButton("Multi Paint");
        multi.setActionCommand("multi");
        multi.addActionListener(this);

        this.addScalable(panel, single, 0.0F, 0.0F, 1.0F, 0.5F);
        this.addScalable(panel, multi, 0.0F, 0.5F, 1.0F, 0.5F);

        this.add(panel);
        this.setSize(360, 120);
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
