package com.hamusuke.paint.network.channel;

import com.hamusuke.paint.network.protocol.PacketDirection;
import com.hamusuke.paint.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    private final PacketDirection direction;

    public PacketDecoder(PacketDirection direction) {
        this.direction = direction;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int i = in.readableBytes();
        if (i != 0) {
            IntelligentByteBuf buf = new IntelligentByteBuf(in);
            int j = buf.readVariableInt();
            Packet<?> packet = ctx.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get().createPacket(this.direction, j, buf);
            if (packet == null) {
                throw new IOException("Bad packet id: " + j);
            } else {
                if (buf.readableBytes() > 0) {
                    throw new IOException("Packet " + packet.getClass().getSimpleName() + " was larger than expected, found " + buf.readableBytes());
                } else {
                    out.add(packet);
                }
            }
        }
    }
}
