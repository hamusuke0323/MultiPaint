package com.hamusuke.paint.server.network.handshake;

import com.hamusuke.paint.network.channel.Connection;
import com.hamusuke.paint.network.listener.server.ServerHandshakePacketListener;
import com.hamusuke.paint.network.protocol.packet.c2s.handshaking.HandshakeC2SPacket;
import com.hamusuke.paint.server.PaintServer;
import com.hamusuke.paint.server.network.ServerLoginPacketListenerImpl;

public class LocalServerHandshakePacketListenerImpl implements ServerHandshakePacketListener {
    private final PaintServer server;
    private final Connection connection;

    public LocalServerHandshakePacketListenerImpl(PaintServer server, Connection connection) {
        this.server = server;
        this.connection = connection;
    }

    @Override
    public void onHandshake(HandshakeC2SPacket packet) {
        this.connection.setProtocol(packet.getIntendedProtocol());
        this.connection.setListener(new ServerLoginPacketListenerImpl(this.server, this.connection));
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
