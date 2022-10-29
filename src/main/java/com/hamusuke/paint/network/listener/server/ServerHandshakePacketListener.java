package com.hamusuke.paint.network.listener.server;

import com.hamusuke.paint.network.protocol.packet.c2s.handshaking.HandshakeC2SPacket;

public interface ServerHandshakePacketListener extends ServerPacketListener {
    void onHandshake(HandshakeC2SPacket packet);
}
