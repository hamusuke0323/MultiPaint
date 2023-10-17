package com.hamusuke.paint.client.gui.window;

import com.hamusuke.paint.client.gui.dialog.DedicatedConnectingDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ConnectingWindow extends Window {
    public ConnectingWindow() {
        super("Connect to server");
    }

    @Override
    public void init() {
        JTextField host = new JTextField();
        host.setName("host");
        host.setText("localhost");
        host.setToolTipText("host ip/name");

        JTextField port = new JTextField();
        port.setName("port");
        port.setText("8080");
        port.setToolTipText("port");
        port.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if ((c < '0' || c > '9') && (c != KeyEvent.VK_BACK_SPACE)) {
                    e.consume();
                }
            }
        });

        JButton button = new JButton("Connect");
        button.addActionListener(e -> {
            new DedicatedConnectingDialog(this, client, host.getText(), Integer.parseInt(port.getText()));
        });

        GridBagLayout layout = new GridBagLayout();
        JPanel panel = new JPanel(layout);
        addButton(panel, host, layout, 0, 0, 1, 1, 1.0D);
        addButton(panel, port, layout, 0, 1, 1, 1, 1.0D);
        addButton(panel, button, layout, 0, 2, 1, 1, 1.0D);

        this.add(panel, BorderLayout.CENTER);
        this.setSize(350, 120);
        this.setLocationRelativeTo(null);
    }

    @Override
    protected void onDisposed() {
        if (client.getConnection() == null) {
            client.setCurrentWindow(new MenuWindow());
        }
    }
}
