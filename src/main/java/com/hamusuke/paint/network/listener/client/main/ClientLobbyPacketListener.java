package com.hamusuke.paint.network.listener.client.main;

import com.hamusuke.paint.network.protocol.packet.s2c.main.lobby.CanvasInfoResponseS2CPacket;

public interface ClientLobbyPacketListener {
    void handleCanvasInfoResponse(CanvasInfoResponseS2CPacket packet);
}
