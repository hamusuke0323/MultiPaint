package com.hamusuke.paint.client.gui.window;

import com.hamusuke.paint.canvas.CanvasInfo;
import com.hamusuke.paint.network.protocol.packet.c2s.main.lobby.JoinCanvasC2SPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LobbyWindow extends Window {
    private static final Logger LOGGER = LogManager.getLogger();
    private JList<CanvasInfo> list;
    private DefaultListModel<CanvasInfo> listModel;

    public LobbyWindow() {
        super("Lobby");
    }

    @Override
    protected void init() {
        this.listModel = new DefaultListModel<>();
        this.list = new JList<>(this.listModel);
        JButton create = new JButton("Create New Canvas");
        create.addActionListener(this);
        create.setActionCommand("create");
        JButton button = new JButton("Join Selected Canvas");
        button.addActionListener(this);
        button.setActionCommand("join");
        this.add(this.createMenuBar(), BorderLayout.NORTH);
        this.add(new JScrollPane(this.list), BorderLayout.CENTER);
        this.add(new JScrollPane(client.painterList), BorderLayout.EAST);
        JPanel buttons = new JPanel();
        buttons.add(create, BorderLayout.NORTH);
        buttons.add(button, BorderLayout.SOUTH);
        this.add(buttons, BorderLayout.SOUTH);
        this.setSize(720, 360);
        this.setLocationRelativeTo(null);
    }

    private JMenuBar createMenuBar() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem disconnect = new JMenuItem("Disconnect");
        disconnect.setActionCommand("disconnect");
        disconnect.addActionListener(this);
        file.add(disconnect);
        jMenuBar.add(file);
        return jMenuBar;
    }

    public void addCanvasInfo(CanvasInfo canvasInfo) {
        this.listModel.addElement(canvasInfo);
    }

    private void joinCanvas(CanvasInfo canvasInfo) {
        LOGGER.info("Joining the selected canvas: {}", canvasInfo);
        client.getConnection().sendPacket(new JoinCanvasC2SPacket(canvasInfo.getCanvasId()));
    }

    @Override
    protected void onClose() {
        this.dispose(client::disconnect);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "create":
                break;
            case "join":
                CanvasInfo canvasInfo = this.list.getSelectedValue();
                if (canvasInfo != null) {
                    this.dispose();
                    this.joinCanvas(canvasInfo);
                }
                break;
            case "disconnect":
                this.onClose();
                break;
        }
    }
}
