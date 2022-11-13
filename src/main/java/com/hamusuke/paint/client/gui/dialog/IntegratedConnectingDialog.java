package com.hamusuke.paint.client.gui.dialog;

import com.hamusuke.paint.client.PaintClient;

import javax.swing.*;
import java.awt.*;

public class IntegratedConnectingDialog extends JDialog {
    public IntegratedConnectingDialog(Frame owner, PaintClient client) {
        super(owner, "Connecting to local server...");
        this.setModalityType(ModalityType.TOOLKIT_MODAL);
        JLabel label = new JLabel("Please wait...");
        this.add(label);
        this.pack();
        this.setLocationRelativeTo(null);
        new SwingWorker() {
            @Override
            protected Object doInBackground() {
                client.startIntegratedServer(label::setText, () -> {
                    IntegratedConnectingDialog.this.getOwner().dispose();
                    IntegratedConnectingDialog.this.dispose();
                });
                return null;
            }
        }.execute();
        this.setVisible(true);
    }
}
