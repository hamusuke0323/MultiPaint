package com.hamusuke.paint.network.listener.server.main;

import com.hamusuke.paint.network.protocol.packet.c2s.main.lobby.JoinCanvasC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.lobby.RequestCanvasInfoC2SPacket;

public interface ServerLobbyPacketListener {
    void handleRequestCanvasInfo(RequestCanvasInfoC2SPacket packet);

    void handleJoinCanvas(JoinCanvasC2SPacket packet);
}
