package com.hamusuke.paint.server.network.main;

import com.hamusuke.paint.network.channel.Connection;
import com.hamusuke.paint.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.c2s.main.ChatC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.DisconnectC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.PingC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.RTTC2SPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.ChatS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.LeavePainterS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.PongS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.RTTS2CPacket;
import com.hamusuke.paint.server.PaintServer;
import com.hamusuke.paint.server.network.ServerPainter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ServerCommonPacketListenerImpl implements ServerCommonPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    public final Connection connection;
    protected final PaintServer server;
    public ServerPainter painter;

    protected ServerCommonPacketListenerImpl(PaintServer server, Connection connection, ServerPainter painter) {
        this.server = server;
        this.connection = connection;
        connection.setListener(this);
        this.painter = painter;
        painter.connection = this;
    }

    @Override
    public void handleDisconnect(DisconnectC2SPacket packet) {
        this.connection.disconnect();
    }

    @Override
    public void handleChatPacket(ChatC2SPacket packet) {
        this.server.sendPacketToAll(new ChatS2CPacket(packet.getMsg()));
    }

    @Override
    public void handlePingPacket(PingC2SPacket packet) {
        this.connection.sendPacket(new PongS2CPacket(packet.getClientTime()));
    }

    @Override
    public void handleRTTPacket(RTTC2SPacket packet) {
        this.painter.setPing(packet.getRtt());
        this.server.sendPacketToAll(new RTTS2CPacket(this.painter.getId(), packet.getRtt()));
    }

    @Override
    public void onDisconnected() {
        LOGGER.info("{} lost connection", this.connection.getAddress());
        this.painter.sendPacketToOthers(new LeavePainterS2CPacket(this.painter.getId()));
        this.server.getPainterManager().removePainter(this.painter);
        if (this.server.isLocal()) {
            this.server.stop(false);
        }
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
