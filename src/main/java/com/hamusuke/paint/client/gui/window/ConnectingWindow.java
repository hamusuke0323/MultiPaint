package com.hamusuke.paint.client.gui.window;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ConnectingWindow extends Window {
    public ConnectingWindow() {
        super("Connect to server");
    }

    @Override
    protected void init() {
        JPanel panel = new JPanel(this.springLayout);

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
            this.setVisible(false);
            client.connectToServer(host.getText(), Integer.parseInt(port.getText()), s -> {
            });
        });

        float oneThird = 1.0F / 3.0F;
        this.addScalable(panel, host, 0.0F, 0.0F, 1.0F, oneThird);
        this.addScalable(panel, port, 0.0F, oneThird, 1.0F, oneThird);
        this.addScalable(panel, button, 0.0F, oneThird * 2.0F, 1.0F, oneThird);

        this.add(panel);
        this.setSize(350, 120);
        this.setLocationRelativeTo(null);
    }

    @Override
    protected void onDisposed() {
        client.setCurrentWindow(new MenuWindow());
    }
}
