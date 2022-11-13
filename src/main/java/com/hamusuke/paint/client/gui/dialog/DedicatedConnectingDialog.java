package com.hamusuke.paint.client.gui.dialog;

import com.hamusuke.paint.client.PaintClient;
import com.hamusuke.paint.client.gui.window.ConnectingWindow;

import javax.swing.*;

public class DedicatedConnectingDialog extends JDialog {
    public DedicatedConnectingDialog(ConnectingWindow owner, PaintClient client, String host, int port) {
        super(owner, String.format("Connecting to %s:%d...", host, port), true);
        JLabel label = new JLabel(String.format("Connecting to %s:%d...", host, port));
        this.add(label);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        new SwingWorker() {
            @Override
            protected Object doInBackground() {
                try {
                    client.connectToServer(host, port, label::setText, DedicatedConnectingDialog.this::dispose);
                    DedicatedConnectingDialog.this.getOwner().dispose();
                } catch (Exception e) {
                    DedicatedConnectingDialog.this.dispose();
                }
                return null;
            }
        }.execute();
        this.setVisible(true);
    }
}
