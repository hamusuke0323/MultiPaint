package com.hamusuke.paint.network.listener.server.main;

import com.hamusuke.paint.network.protocol.packet.c2s.main.canvas.*;

public interface ServerCanvasPacketListener {
    void handleLinePacket(LineC2SPacket packet);

    void handleSyncLinesPacket(SyncLinesC2SPacket packet);

    void handleChangeColorPacket(ChangeColorC2SPacket packet);

    void handleChangeWidthPacket(ChangeWidthC2SPacket packet);

    void handleLeaveCanvasPacket(LeaveCanvasC2SPacket packet);
}
