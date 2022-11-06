package com.hamusuke.paint.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hamusuke.paint.network.Painter;
import com.hamusuke.paint.network.channel.Connection;
import com.hamusuke.paint.network.protocol.packet.Packet;
import com.hamusuke.paint.network.protocol.packet.s2c.main.ChangeColorS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.ChatS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.JoinCanvasS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.JoinPainterS2CPacket;
import com.hamusuke.paint.server.network.ServerPainter;
import com.hamusuke.paint.server.network.main.ServerLobbyPacketListenerImpl;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import javax.annotation.Nullable;
import java.util.List;

public class PainterManager {
    private final PaintServer server;
    private final List<ServerPainter> painters = Lists.newArrayList();

    public PainterManager(PaintServer server) {
        this.server = server;
    }

    public boolean canJoin(ServerPainter serverPainter) {
        return serverPainter.isAuthorized();
    }

    public void addPainter(Connection connection, ServerPainter serverPainter) {
        this.sendPacketToAll(new JoinPainterS2CPacket(serverPainter, true));
        ServerLobbyPacketListenerImpl listener = new ServerLobbyPacketListenerImpl(this.server, connection, serverPainter);
        listener.connection.sendPacket(new JoinPainterS2CPacket(serverPainter, false));
        this.sendPacketToAll(new ChatS2CPacket(String.format("Painter[%s] joined the server", serverPainter.connection.getConnection().getAddress())));

        synchronized (this.painters) {
            this.painters.forEach(serverPainter1 -> listener.connection.sendPacket(new JoinPainterS2CPacket(serverPainter1, true)));
            this.painters.stream().filter(Painter::isInAnyCanvas).forEach(serverPainter1 -> listener.connection.sendPacket(new JoinCanvasS2CPacket(serverPainter1, serverPainter1.getCurrentCanvas().getInfo())));
            this.painters.forEach(serverPainter1 -> listener.connection.sendPacket(new ChangeColorS2CPacket(serverPainter1)));
            this.painters.add(serverPainter);
        }
    }

    public void sendPacketToAll(Packet<?> packet) {
        this.sendPacketToAll(packet, null);
    }

    public void sendPacketToAll(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
        synchronized (this.painters) {
            this.painters.forEach(serverPainter -> serverPainter.sendPacket(packet, callback));
        }
    }

    public void sendPacketToOthers(ServerPainter sender, Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
        synchronized (this.painters) {
            this.painters.stream().filter(p -> !p.equals(sender)).forEach(serverPainter -> serverPainter.sendPacket(packet, callback));
        }
    }

    public void sendPacketToAllInLobby(Packet<?> packet) {
        synchronized (this.painters) {
            this.painters.stream().filter(Painter::isInLobby).forEach(p -> p.sendPacket(packet));
        }
    }

    public void sendPacketToAllInCanvas(ServerPainter sender, Packet<?> packet) {
        if (sender.isInAnyCanvas()) {
            synchronized (this.painters) {
                this.painters.stream().filter(p -> p.isInCanvas(sender.getCurrentCanvas())).forEach(p -> p.sendPacket(packet));
            }
        }
    }

    public void removePainter(ServerPainter painter) {
        synchronized (this.painters) {
            this.painters.remove(painter);
        }

        this.sendPacketToAll(new ChatS2CPacket(String.format("Painter[%s] left the server", painter.connection.getConnection().getAddress())));
    }

    public ImmutableList<ServerPainter> getPainters() {
        return ImmutableList.copyOf(this.painters);
    }
}
