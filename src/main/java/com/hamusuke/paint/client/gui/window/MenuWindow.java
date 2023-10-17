package com.hamusuke.paint.client.gui.window;

import com.hamusuke.paint.client.gui.dialog.IntegratedConnectingDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MenuWindow extends Window {
    public MenuWindow() {
        super("Main Menu");
    }

    @Override
    public void init() {
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
        switch (e.getActionCommand()) {
            case "single":
                new IntegratedConnectingDialog(this, client);
                break;
            case "multi":
                this.dispose();
                client.setCurrentWindow(new ConnectingWindow());
                break;
        }
    }
}
