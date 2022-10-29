package com.hamusuke.paint.network.protocol.packet.s2c.main.canvas;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;
import com.hamusuke.paint.util.Util;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class CanvasDataS2CPacket implements Packet<ClientCommonPacketListener> {
    private final BufferedImage bufferedImage;

    public CanvasDataS2CPacket(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public CanvasDataS2CPacket(IntelligentByteBuf byteBuf) {
        BufferedImage bufferedImage;

        try {
            bufferedImage = Util.unpack(byteBuf);
        } catch (IOException e) {
            e.printStackTrace();
            bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        }

        this.bufferedImage = bufferedImage;
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
        Util.pack(this.bufferedImage, byteBuf);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleCanvasDataPacket(this);
    }

    public BufferedImage getBufferedImage() {
        return this.bufferedImage;
    }
}
