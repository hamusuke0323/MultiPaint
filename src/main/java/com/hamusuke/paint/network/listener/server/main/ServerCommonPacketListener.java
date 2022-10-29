package com.hamusuke.paint.network.listener.server.main;

import com.hamusuke.paint.network.listener.server.ServerPacketListener;
import com.hamusuke.paint.network.protocol.packet.c2s.main.ChatC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.DisconnectC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.PingC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.RTTC2SPacket;

public interface ServerCommonPacketListener extends ServerPacketListener, ServerCanvasPacketListener, ServerLobbyPacketListener {
    void handleDisconnect(DisconnectC2SPacket packet);

    void handleChatPacket(ChatC2SPacket packet);

    void handlePingPacket(PingC2SPacket packet);

    void handleRTTPacket(RTTC2SPacket packet);
}
