package com.hamusuke.paint.client.gui.component;

import com.hamusuke.paint.client.PaintClient;
import com.hamusuke.paint.network.protocol.packet.c2s.main.ChatC2SPacket;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Chat {
    private final JTextArea textArea;
    private final JScrollPane scrollTextArea;
    private final JTextField field;
    private final JScrollPane scrollField;

    public Chat(PaintClient client) {
        this.textArea = new JTextArea();
        this.textArea.setLineWrap(true);
        this.textArea.setEditable(false);
        this.scrollTextArea = new JScrollPane(this.textArea);
        this.scrollTextArea.setAutoscrolls(true);

        this.field = new JTextField();
        this.field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    client.getConnection().sendPacket(new ChatC2SPacket(String.format("[%s]: %s", client.getConnection().getChannel().localAddress(), Chat.this.field.getText())));
                    Chat.this.field.setText("");
                    e.consume();
                }
            }
        });
        this.scrollField = new JScrollPane(this.field);
        this.scrollField.setAutoscrolls(true);
    }

    public void addMessage(String msg) {
        this.textArea.setText(this.textArea.getText() + msg + "\n");
    }

    public JScrollPane getTextArea() {
        return this.scrollTextArea;
    }

    public JScrollPane getField() {
        return this.scrollField;
    }
}
