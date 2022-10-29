package com.hamusuke.paint.network.listener.server;

import com.hamusuke.paint.network.protocol.packet.c2s.login.AliveC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.login.AuthResponseC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.login.LoginHelloC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.login.LoginKeyC2SPacket;

public interface ServerLoginPacketListener extends ServerPacketListener {
    void onHello(LoginHelloC2SPacket packet);

    void onKey(LoginKeyC2SPacket packet);

    void onPing(AliveC2SPacket packet);

    void onAuth(AuthResponseC2SPacket packet);
}
