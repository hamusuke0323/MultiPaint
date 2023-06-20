package com.hamusuke.paint.server.network.main;

import com.hamusuke.paint.network.LineData;
import com.hamusuke.paint.network.channel.Connection;
import com.hamusuke.paint.network.protocol.packet.c2s.main.canvas.*;
import com.hamusuke.paint.network.protocol.packet.c2s.main.lobby.CreateCanvasC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.lobby.JoinCanvasC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.lobby.RequestCanvasInfoC2SPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.ChangeColorS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.ChangeWidthS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.LeaveCanvasS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.canvas.CanvasDataS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.canvas.LineS2CPacket;
import com.hamusuke.paint.server.PaintServer;
import com.hamusuke.paint.server.canvas.ServerCanvas;
import com.hamusuke.paint.server.network.ServerPainter;

public class ServerCanvasPacketListenerImpl extends ServerCommonPacketListenerImpl {
    private final ServerCanvas canvas;

    public ServerCanvasPacketListenerImpl(PaintServer server, Connection connection, ServerPainter painter, ServerCanvas canvas) {
        super(server, connection, painter);
        this.canvas = canvas;
    }

    @Override
    public void handleLinePacket(LineC2SPacket packet) {
        LineData data = packet.getLineData();
        this.canvas.acceptLine(data);
        this.painter.sendPacketToAllInCanvas(new LineS2CPacket(data));
    }

    @Override
    public void handleSyncLinesPacket(SyncLinesC2SPacket packet) {
        this.painter.sendPacket(new CanvasDataS2CPacket(this.canvas.getData()));
    }

    @Override
    public void handleRequestCanvasInfo(RequestCanvasInfoC2SPacket packet) {
        throw new IllegalStateException();
    }

    @Override
    public void handleJoinCanvas(JoinCanvasC2SPacket packet) {
        throw new IllegalStateException();
    }

    @Override
    public void handleCreateCanvasPacket(CreateCanvasC2SPacket packet) {
        throw new IllegalStateException();
    }

    @Override
    public void handleChangeColorPacket(ChangeColorC2SPacket packet) {
        this.painter.setColor(packet.getColor());
        this.server.sendPacketToAll(new ChangeColorS2CPacket(this.painter));
    }

    @Override
    public void handleChangeWidthPacket(ChangeWidthC2SPacket packet) {
        this.painter.setWidth(packet.getWidth());
        this.server.sendPacketToAll(new ChangeWidthS2CPacket(this.painter));
    }

    @Override
    public void handleLeaveCanvasPacket(LeaveCanvasC2SPacket packet) {
        ServerCanvas curCanvas = this.painter.getCurrentCanvas();

        this.painter.joinCanvas(null);
        new ServerLobbyPacketListenerImpl(this.server, this.connection, this.painter);

        if (curCanvas != null) {
            curCanvas.savePainter(this.painter);
        }

        this.server.sendPacketToAll(new LeaveCanvasS2CPacket(this.painter));
    }
}
