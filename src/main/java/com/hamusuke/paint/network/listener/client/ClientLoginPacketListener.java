package com.hamusuke.paint.network.listener.client;

import com.hamusuke.paint.network.listener.PacketListener;
import com.hamusuke.paint.network.protocol.packet.s2c.login.*;

public interface ClientLoginPacketListener extends PacketListener {
    void onHello(LoginHelloS2CPacket packet);

    void onSuccess(LoginSuccessS2CPacket packet);

    void onDisconnect(LoginDisconnectS2CPacket packet);

    void onCompression(LoginCompressionS2CPacket packet);

    void onAuth(AuthRequestS2CPacket packet);
}
