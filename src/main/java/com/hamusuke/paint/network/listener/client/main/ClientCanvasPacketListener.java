package com.hamusuke.paint.network.listener.client.main;

import com.hamusuke.paint.network.protocol.packet.s2c.main.canvas.CanvasDataS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.canvas.LineS2CPacket;

public interface ClientCanvasPacketListener {
    void handleLinePacket(LineS2CPacket packet);

    void handleCanvasDataPacket(CanvasDataS2CPacket packet);
}
