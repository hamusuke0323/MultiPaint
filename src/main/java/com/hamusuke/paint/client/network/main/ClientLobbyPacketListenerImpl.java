package com.hamusuke.paint.client.network.main;

import com.hamusuke.paint.client.PaintClient;
import com.hamusuke.paint.client.gui.window.LobbyWindow;
import com.hamusuke.paint.network.channel.Connection;
import com.hamusuke.paint.network.protocol.packet.s2c.main.canvas.CanvasDataS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.canvas.LineS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.lobby.CanvasInfoResponseS2CPacket;

public class ClientLobbyPacketListenerImpl extends ClientCommonPacketListenerImpl {
    public LobbyWindow lobbyWindow;

    public ClientLobbyPacketListenerImpl(PaintClient client, Connection connection) {
        super(client, connection);
    }

    @Override
    public void handleCanvasInfoResponse(CanvasInfoResponseS2CPacket packet) {
        packet.getInfo().forEach(this.lobbyWindow::addCanvasInfo);
    }

    @Override
    public void handleLinePacket(LineS2CPacket packet) {
        throw new IllegalStateException();
    }

    @Override
    public void handleCanvasDataPacket(CanvasDataS2CPacket packet) {
        throw new IllegalStateException();
    }
}
