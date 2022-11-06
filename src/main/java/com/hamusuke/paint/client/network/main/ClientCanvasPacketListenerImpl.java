package com.hamusuke.paint.client.network.main;

import com.hamusuke.paint.client.PaintClient;
import com.hamusuke.paint.client.canvas.ClientCanvas;
import com.hamusuke.paint.client.gui.window.CanvasWindow;
import com.hamusuke.paint.network.channel.Connection;
import com.hamusuke.paint.network.protocol.packet.s2c.main.canvas.CanvasDataS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.canvas.LineS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.lobby.CanvasInfoResponseS2CPacket;

public class ClientCanvasPacketListenerImpl extends ClientCommonPacketListenerImpl {
    private final CanvasWindow canvasWindow;
    private final ClientCanvas canvas;

    public ClientCanvasPacketListenerImpl(PaintClient client, Connection connection, CanvasWindow canvasWindow, ClientCanvas canvas) {
        super(client, connection);
        this.canvasWindow = canvasWindow;
        this.canvas = canvas;
        this.clientPainter = client.clientPainter;
    }

    @Override
    public void handleLinePacket(LineS2CPacket packet) {
        this.canvas.acceptLine(packet.getLine());
    }

    @Override
    public void handleCanvasDataPacket(CanvasDataS2CPacket packet) {
        this.canvas.setData(packet.getBufferedImage());
    }

    @Override
    public void onDisconnected() {
        this.canvasWindow.dispose();
        super.onDisconnected();
    }

    @Override
    public void handleCanvasInfoResponse(CanvasInfoResponseS2CPacket packet) {
        throw new IllegalStateException();
    }
}
