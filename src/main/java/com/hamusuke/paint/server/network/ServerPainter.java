package com.hamusuke.paint.server.network;

import com.hamusuke.paint.network.Painter;
import com.hamusuke.paint.network.protocol.packet.Packet;
import com.hamusuke.paint.network.protocol.packet.s2c.main.ChangeColorS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.ChangeWidthS2CPacket;
import com.hamusuke.paint.server.PaintServer;
import com.hamusuke.paint.server.canvas.ServerCanvas;
import com.hamusuke.paint.server.network.main.ServerCommonPacketListenerImpl;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.UUID;

public class ServerPainter extends Painter {
    public final PaintServer server;
    public ServerCommonPacketListenerImpl connection;
    private boolean isAuthorized;

    public ServerPainter(UUID uuid, PaintServer server, ServerCanvas currentCanvas) {
        super(uuid);
        this.server = server;
        this.currentCanvas = currentCanvas;
    }

    public boolean isAuthorized() {
        return this.isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        this.isAuthorized = authorized;
    }

    public ServerCanvas getCurrentCanvas() {
        return (ServerCanvas) this.currentCanvas;
    }

    public void sendPacket(Packet<?> packet) {
        this.sendPacket(packet, null);
    }

    public void sendPacket(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> callback) {
        this.connection.getConnection().sendPacket(packet, callback);
    }

    public void sendPacketToOthers(Packet<?> packet) {
        this.sendPacketToOthers(packet, null);
    }

    public void sendPacketToOthers(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> callback) {
        this.server.getPainterManager().sendPacketToOthers(this, packet, callback);
    }

    public void sendPacketToAllInCanvas(Packet<?> packet) {
        this.server.getPainterManager().sendPacketToAllInCanvas(this, packet);
    }

    public ServerPainterData serialize() {
        return new ServerPainterData(this.getColor(), this.getWidth());
    }

    public void deserialize(ServerPainterData data) {
        this.setColor(data.getColor());
        this.setWidth(data.getWidth());
        this.syncPainterData();
    }

    private void syncPainterData() {
        this.server.sendPacketToAll(new ChangeColorS2CPacket(this));
        this.server.sendPacketToAll(new ChangeWidthS2CPacket(this));
    }
}
