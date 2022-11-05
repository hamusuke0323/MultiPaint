package com.hamusuke.paint.network.listener.client.main;

import com.hamusuke.paint.network.listener.PacketListener;
import com.hamusuke.paint.network.protocol.packet.s2c.main.*;
import com.hamusuke.paint.network.protocol.packet.s2c.main.lobby.JoinCanvasS2CPacket;

public interface ClientCommonPacketListener extends PacketListener, ClientCanvasPacketListener, ClientLobbyPacketListener {
    void handleChatPacket(ChatS2CPacket packet);

    void handlePongPacket(PongS2CPacket packet);

    void handleDisconnectPacket(DisconnectS2CPacket packet);

    void handleJoinPacket(JoinPainterS2CPacket packet);

    void handleRTTPacket(RTTS2CPacket packet);

    void handleLeavePacket(LeavePainterS2CPacket packet);

    void handleJoinCanvas(JoinCanvasS2CPacket packet);
}
