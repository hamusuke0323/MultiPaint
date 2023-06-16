package com.hamusuke.paint.server.network.main;

import com.hamusuke.paint.network.channel.Connection;
import com.hamusuke.paint.network.protocol.packet.c2s.main.canvas.*;
import com.hamusuke.paint.network.protocol.packet.c2s.main.lobby.CreateCanvasC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.lobby.JoinCanvasC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.lobby.RequestCanvasInfoC2SPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.DisconnectS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.JoinCanvasS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.lobby.CanvasInfoResponseS2CPacket;
import com.hamusuke.paint.server.PaintServer;
import com.hamusuke.paint.server.canvas.ServerCanvas;
import com.hamusuke.paint.server.network.ServerPainter;

import java.util.stream.Collectors;

public class ServerLobbyPacketListenerImpl extends ServerCommonPacketListenerImpl {
    public ServerLobbyPacketListenerImpl(PaintServer server, Connection connection, ServerPainter painter) {
        super(server, connection, painter);
    }

    @Override
    public void handleRequestCanvasInfo(RequestCanvasInfoC2SPacket packet) {
        this.connection.sendPacket(new CanvasInfoResponseS2CPacket(this.server.getServerCanvases().stream().map(ServerCanvas::getInfo).collect(Collectors.toList())));
    }

    @Override
    public void handleJoinCanvas(JoinCanvasC2SPacket packet) {
        ServerCanvas canvas = this.server.getCanvas(packet.getCanvasId());
        if (canvas != null) {
            this.painter.joinCanvas(canvas);
            new ServerCanvasPacketListenerImpl(this.server, this.connection, this.painter, canvas);
            this.server.sendPacketToAll(new JoinCanvasS2CPacket(this.painter, canvas.getInfo()));
        } else {
            this.connection.sendPacket(new DisconnectS2CPacket(), future -> this.connection.disconnect());
        }
    }

    @Override
    public void handleLinePacket(LineC2SPacket packet) {
        throw new IllegalStateException();
    }

    @Override
    public void handleSyncLinesPacket(SyncLinesC2SPacket packet) {
        throw new IllegalStateException();
    }

    @Override
    public void handleCreateCanvasPacket(CreateCanvasC2SPacket packet) {
        this.server.createCanvas(packet.getTitle(), packet.getAuthor(), packet.getWidth(), packet.getHeight());
    }

    @Override
    public void handleChangeColorPacket(ChangeColorC2SPacket packet) {
        throw new IllegalStateException();
    }

    @Override
    public void handleChangeWidthPacket(ChangeWidthC2SPacket packet) {
        throw new IllegalStateException();
    }

    @Override
    public void handleLeaveCanvasPacket(LeaveCanvasC2SPacket packet) {
        throw new IllegalStateException();
    }
}
