package com.hamusuke.paint.client.network.main;

import com.hamusuke.paint.client.PaintClient;
import com.hamusuke.paint.client.canvas.ClientCanvas;
import com.hamusuke.paint.client.gui.component.list.CanvasPainterList;
import com.hamusuke.paint.client.gui.window.CanvasWindow;
import com.hamusuke.paint.client.gui.window.LobbyWindow;
import com.hamusuke.paint.network.channel.Connection;
import com.hamusuke.paint.network.protocol.packet.c2s.main.canvas.SyncLinesC2SPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.canvas.CanvasDataS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.canvas.LineS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.lobby.CanvasInfoResponseS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.lobby.JoinCanvasS2CPacket;

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
    public void handleJoinCanvas(JoinCanvasS2CPacket packet) {
        ClientCanvas canvas = new ClientCanvas(packet.getInfo().getTitle(), packet.getInfo().getAuthor(), packet.getWidth(), packet.getHeight());
        canvas.setCanvasId(packet.getInfo().getCanvasId());
        this.client.joinCanvas(canvas);
        this.client.painterList = new CanvasPainterList(this.client);
        CanvasWindow canvasWindow = new CanvasWindow();
        canvas.canvasWindow = canvasWindow;
        ClientCanvasPacketListenerImpl listener = new ClientCanvasPacketListenerImpl(this.client, this.connection, canvasWindow, canvas);
        this.client.listener = listener;
        this.connection.setListener(listener);
        this.client.setCurrentWindow(canvasWindow);
        this.connection.sendPacket(new SyncLinesC2SPacket());
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
