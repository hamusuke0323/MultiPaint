package com.hamusuke.paint.network.channel;

import com.hamusuke.paint.network.protocol.PacketDirection;
import com.hamusuke.paint.network.protocol.Protocol;
import com.hamusuke.paint.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {
    private final PacketDirection direction;

    public PacketEncoder(PacketDirection direction) {
        this.direction = direction;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet<?> msg, ByteBuf out) throws Exception {
        Protocol protocol = ctx.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get();
        if (protocol == null) {
            throw new RuntimeException("Protocol unknown: " + msg);
        } else {
            Integer integer = protocol.getPacketId(this.direction, msg);

            if (integer == null) {
                throw new IOException("Can't serialize unregistered packet: " + msg.getClass().getName());
            } else {
                IntelligentByteBuf buf = new IntelligentByteBuf(out);
                buf.writeVariableInt(integer);
                int i = buf.writerIndex();
                msg.write(buf);
                int j = buf.writerIndex() - i;
                if (j > PacketInflater.MAXIMUM_UNCOMPRESSED_LENGTH) {
                    throw new IllegalArgumentException("Packet too big (is " + j + ", should be less than " + PacketInflater.MAXIMUM_UNCOMPRESSED_LENGTH + "): " + msg);
                }
            }
        }
    }
}
