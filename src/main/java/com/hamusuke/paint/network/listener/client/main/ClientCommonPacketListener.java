package com.hamusuke.paint.network.listener.client.main;

import com.hamusuke.paint.network.listener.PacketListener;
import com.hamusuke.paint.network.protocol.packet.s2c.main.*;

public interface ClientCommonPacketListener extends PacketListener, ClientCanvasPacketListener, ClientLobbyPacketListener {
    void handleChatPacket(ChatS2CPacket packet);

    void handlePongPacket(PongS2CPacket packet);

    void handleDisconnectPacket(DisconnectS2CPacket packet);

    void handleJoinPacket(JoinPainterS2CPacket packet);

    void handleRTTPacket(RTTS2CPacket packet);

    void handleLeavePacket(LeavePainterS2CPacket packet);

    void handleJoinCanvasPacket(JoinCanvasS2CPacket packet);

    void handleChangeColorPacket(ChangeColorS2CPacket packet);

    void handleChangeWidthPacket(ChangeWidthS2CPacket packet);

    void handleLeaveCanvasPacket(LeaveCanvasS2CPacket packet);
}
