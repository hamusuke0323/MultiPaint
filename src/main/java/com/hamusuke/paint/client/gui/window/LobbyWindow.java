package com.hamusuke.paint.client.gui.window;

import com.hamusuke.paint.canvas.CanvasInfo;
import com.hamusuke.paint.client.gui.dialog.CreatingCanvasDialog;
import com.hamusuke.paint.network.protocol.packet.c2s.main.lobby.CreateCanvasC2SPacket;
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
        super(getWindowTitle());
    }

    private static String getWindowTitle() {
        return client.getConnection().isLocal() ? "Lobby" : "Lobby - " + client.getAddresses();
    }

    private static void addButton(Container owner, Component component, GridBagLayout layout, int x, int y, int w, int h, double wy) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.insets = new Insets(1, 1, 1, 1);
        constraints.gridwidth = w;
        constraints.gridheight = h;
        constraints.weightx = 1.0D;
        constraints.weighty = wy;
        layout.setConstraints(component, constraints);
        owner.add(component);
    }

    @Override
    protected void init() {
        this.listModel = new DefaultListModel<>();
        this.list = new JList<>(this.listModel);
        this.add(this.createMenuBar(), BorderLayout.NORTH);

        JButton create = new JButton("Create New Canvas");
        create.addActionListener(this);
        create.setActionCommand("create");
        JButton join = new JButton("Join Selected Canvas");
        join.addActionListener(this);
        join.setActionCommand("join");

        GridBagLayout layout = new GridBagLayout();
        JPanel panel = new JPanel(layout);
        addButton(panel, new JScrollPane(this.list), layout, 0, 0, 2, 1, 1.0D);
        addButton(panel, create, layout, 0, 1, 1, 1, 0.125D);
        addButton(panel, join, layout, 1, 1, 1, 1, 0.125D);
        addButton(panel, client.chat.getTextArea(), layout, 0, 2, 2, 1, 1.0D);
        addButton(panel, client.chat.getField(), layout, 0, 3, 2, 1, 0.125D);
        this.add(panel, BorderLayout.CENTER);
        this.add(new JScrollPane(client.painterList), BorderLayout.EAST);
        this.setSize(1280, 720);
        this.setLocationRelativeTo(null);
    }

    private JMenuBar createMenuBar() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem disconnect = new JMenuItem("Disconnect");
        disconnect.setActionCommand("disconnect");
        disconnect.addActionListener(this);
        menu.add(disconnect);
        jMenuBar.add(menu);
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
                new CreatingCanvasDialog(this, info -> {
                    client.getConnection().sendPacket(new CreateCanvasC2SPacket(info.getTitle(), client.clientPainter.getUuid(), info.getWidth(), info.getHeight()));
                });
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
