package com.hamusuke.paint.server.network.handshake;

import com.hamusuke.paint.Constants;
import com.hamusuke.paint.network.channel.Connection;
import com.hamusuke.paint.network.listener.server.ServerHandshakePacketListener;
import com.hamusuke.paint.network.protocol.Protocol;
import com.hamusuke.paint.network.protocol.packet.c2s.handshaking.HandshakeC2SPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.login.LoginDisconnectS2CPacket;
import com.hamusuke.paint.server.PaintServer;
import com.hamusuke.paint.server.network.ServerLoginPacketListenerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerHandshakePacketListenerImpl implements ServerHandshakePacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final PaintServer server;
    private final Connection connection;

    public ServerHandshakePacketListenerImpl(PaintServer server, Connection connection) {
        this.server = server;
        this.connection = connection;
    }

    @Override
    public void onHandshake(HandshakeC2SPacket packet) {
        switch (packet.getIntendedProtocol()) {
            case LOGIN:
                this.connection.setProtocol(Protocol.LOGIN);
                if (packet.getProtocolVersion() != Constants.PROTOCOL_VERSION) {
                    this.connection.sendPacket(new LoginDisconnectS2CPacket("Protocol version is different!"));
                    this.connection.disconnect();
                } else {
                    LOGGER.info("Hello Packet came from {} and the connection established!", this.connection.getAddress());
                    this.connection.setListener(new ServerLoginPacketListenerImpl(this.server, this.connection));
                }
                break;
            default:
                throw new UnsupportedOperationException("Invalid intention " + packet.getIntendedProtocol());
        }
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
