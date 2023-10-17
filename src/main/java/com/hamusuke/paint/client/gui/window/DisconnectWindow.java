package com.hamusuke.paint.client.gui.window;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class DisconnectWindow extends Window {
    private final String msg;
    private final Supplier<Window> windowSupplier;

    public DisconnectWindow(String msg, Supplier<Window> windowSupplier) {
        super("Disconnected");
        this.msg = msg;
        this.windowSupplier = windowSupplier;
    }

    @Override
    public void init() {
        JTextField msg = new JTextField(this.getMessage());
        msg.setEditable(false);
        JButton back = new JButton("Back");
        back.addActionListener(e -> this.dispose(() -> client.setCurrentWindow(this.windowSupplier.get())));
        this.add(msg, BorderLayout.CENTER);
        this.add(back, BorderLayout.SOUTH);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private String getMessage() {
        if (this.msg.isEmpty()) {
            return "Connection is closed.";
        }

        return "Connection is closed:\n" + this.msg;
    }
}
