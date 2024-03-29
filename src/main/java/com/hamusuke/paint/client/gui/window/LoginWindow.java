package com.hamusuke.paint.client.gui.window;

import com.hamusuke.paint.network.protocol.packet.c2s.login.AuthResponseC2SPacket;
import com.hamusuke.paint.util.Util;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.security.NoSuchAlgorithmException;

public class LoginWindow extends Window {
    private JPasswordField passwordField;

    public LoginWindow() {
        super("Login");
    }

    @Override
    public void init() {
        this.passwordField = new JPasswordField();
        JButton login = new JButton("Login");
        login.setActionCommand("login");
        login.addActionListener(this);

        this.add(this.passwordField, BorderLayout.CENTER);
        this.add(login, BorderLayout.SOUTH);
        this.pack();
        this.setSize(this.getWidth() + 100, this.getHeight());
        this.setLocationRelativeTo(null);
    }

    @Nullable
    @Override
    protected JMenuBar createMenuBar() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem disconnect = new JMenuItem("Disconnect");
        disconnect.setActionCommand("disconnect");
        disconnect.addActionListener(this);
        menu.add(disconnect);
        jMenuBar.add(menu);
        return jMenuBar;
    }

    private void login(String pwd) {
        try {
            client.getConnection().sendPacket(new AuthResponseC2SPacket(Util.hash(pwd)));
        } catch (NoSuchAlgorithmException e) {
            client.disconnect();
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onClose() {
        this.dispose(client::disconnect);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "login":
                if (this.passwordField.getPassword().length > 0) {
                    this.dispose();
                    this.login(String.valueOf(this.passwordField.getPassword()));
                }
                break;
            case "disconnect":
                this.onClose();
                break;
        }
    }
}
