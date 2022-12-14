package com.hamusuke.paint.network.channel;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.hamusuke.paint.network.encryption.PacketDecryptor;
import com.hamusuke.paint.network.encryption.PacketEncryptor;
import com.hamusuke.paint.network.listener.PacketListener;
import com.hamusuke.paint.network.protocol.PacketDirection;
import com.hamusuke.paint.network.protocol.Protocol;
import com.hamusuke.paint.network.protocol.packet.Packet;
import com.hamusuke.paint.network.protocol.packet.s2c.login.LoginDisconnectS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.DisconnectS2CPacket;
import com.hamusuke.paint.util.Lazy;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.crypto.Cipher;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Connection extends SimpleChannelInboundHandler<Packet<?>> {
    public static final AttributeKey<Protocol> ATTRIBUTE_PROTOCOL = AttributeKey.valueOf("protocol");
    public static final Lazy<NioEventLoopGroup> NIO_EVENT_LOOP_GROUP = new Lazy<>(() -> {
        return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
    });
    public static final Lazy<EpollEventLoopGroup> EPOLL_EVENT_LOOP_GROUP = new Lazy<>(() -> {
        return new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
    });
    public static final Lazy<DefaultEventLoopGroup> LOCAL_EVENT_LOOP_GROUP = new Lazy<>(() -> {
        return new DefaultEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
    });
    private static final Logger LOGGER = LogManager.getLogger();
    private PacketListener packetListener;
    private Channel channel;
    private SocketAddress address;
    private final PacketDirection receiving;
    private final Queue<QueuedPacket> packetQueue = new ConcurrentLinkedQueue<>();
    private boolean encrypted;
    private boolean disconnected;

    public Connection(PacketDirection receiving) {
        this.receiving = receiving;
    }

    public static Connection connect(InetSocketAddress address) {
        final Connection connection = new Connection(PacketDirection.CLIENTBOUND);
        Class<? extends SocketChannel> clazz;
        Lazy<? extends EventLoopGroup> lazy;
        if (Epoll.isAvailable()) {
            clazz = EpollSocketChannel.class;
            lazy = EPOLL_EVENT_LOOP_GROUP;
        } else {
            clazz = NioSocketChannel.class;
            lazy = NIO_EVENT_LOOP_GROUP;
        }

        new Bootstrap().group(lazy.get()).handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException ignored) {
                }

                channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("splitter", new PacketSplitter()).addLast("decoder", new PacketDecoder(PacketDirection.CLIENTBOUND)).addLast("prepender", new PacketPrepender()).addLast("encoder", new PacketEncoder(PacketDirection.SERVERBOUND)).addLast("packet_handler", connection);
            }
        }).channel(clazz).connect(address.getAddress(), address.getPort()).syncUninterruptibly();
        return connection;
    }

    public static Connection connectLocal(SocketAddress address) {
        final Connection connection = new Connection(PacketDirection.CLIENTBOUND);
        new Bootstrap().group(LOCAL_EVENT_LOOP_GROUP.get()).handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) {
                channel.pipeline().addLast("packet_handler", connection);
            }
        }).channel(LocalChannel.class).connect(address).syncUninterruptibly();
        return connection;
    }

    @SuppressWarnings("unchecked")
    private static <T extends PacketListener> void handle(Packet<T> packet, PacketListener packetListener) {
        try {
            packet.handle((T) packetListener);
        } catch (Exception e) {
            if (packetListener.shouldCrashOnException()) {
                throw new RuntimeException(e);
            }

            LOGGER.warn("Error occurred while handling packet", e);
        }
    }

    public void setupEncryption(Cipher decryptionCipher, Cipher encryptionCipher) {
        this.encrypted = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", new PacketDecryptor(decryptionCipher));
        this.channel.pipeline().addBefore("prepender", "encrypt", new PacketEncryptor(encryptionCipher));
        LOGGER.debug("The connection has been encrypted");
    }

    public boolean isEncrypted() {
        return this.encrypted;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.disconnect();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channel = ctx.channel();
        this.address = this.channel.remoteAddress();

        this.setProtocol(Protocol.HANDSHAKING);
    }

    public void disableAutoRead() {
        this.channel.config().setAutoRead(false);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (this.channel.isOpen()) {
            LOGGER.error(String.format("Caught exception in %s side", this.receiving == PacketDirection.SERVERBOUND ? "server" : "client"), cause);
            this.sendPacket(this.getProtocol() == Protocol.LOGIN ? new LoginDisconnectS2CPacket() : new DisconnectS2CPacket(), future -> {
                this.disconnect();
            });
            this.disableAutoRead();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet<?> msg) {
        if (this.isConnected()) {
            handle(msg, this.packetListener);
        }
    }

    public void tick() {
        this.sendQueuedPackets();

        if (this.packetListener != null) {
            this.packetListener.tick();
        }

        if (!this.isConnected() && !this.disconnected) {
            this.handleDisconnection();
        }

        if (this.channel != null) {
            this.channel.flush();
        }
    }

    public void setListener(PacketListener listener) {
        Validate.notNull(listener, "packetListener");
        this.packetListener = listener;
    }

    public void setCompression(int threshold, boolean validate) {
        ChannelHandler decompress = this.channel.pipeline().get("decompress");
        ChannelHandler compress = this.channel.pipeline().get("compress");

        if (threshold >= 0) {
            if (decompress instanceof PacketInflater) {
                ((PacketInflater) decompress).setThreshold(threshold, validate);
            } else {
                this.channel.pipeline().addBefore("decoder", "decompress", new PacketInflater(threshold, validate));
            }

            if (compress instanceof PacketDeflater) {
                ((PacketDeflater) compress).setThreshold(threshold);
            } else {
                this.channel.pipeline().addBefore("encoder", "compress", new PacketDeflater(threshold));
            }
        } else {
            if (decompress instanceof PacketInflater) {
                this.channel.pipeline().remove("decompress");
            }

            if (compress instanceof PacketDeflater) {
                this.channel.pipeline().remove("compress");
            }
        }
    }

    public void sendPacket(Packet<?> packet) {
        this.sendPacket(packet, null);
    }

    public void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
        if (this.isConnected()) {
            this.sendQueuedPackets();
            this.sendImmediately(packet, callback);
        } else {
            this.packetQueue.add(new QueuedPacket(packet, callback));
        }
    }

    private void sendImmediately(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
        Protocol protocol = Protocol.getPacketHandlerState(packet);
        Protocol protocol1 = this.getProtocol();

        if (protocol1 != protocol) {
            LOGGER.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }

        if (this.channel.eventLoop().inEventLoop()) {
            this.sendInternal(packet, callback, protocol, protocol1);
        } else {
            this.channel.eventLoop().execute(() -> this.sendInternal(packet, callback, protocol, protocol1));
        }
    }

    private void sendInternal(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback, Protocol packetProto, Protocol currentProto) {
        if (packetProto != currentProto) {
            this.setProtocol(packetProto);
        }

        ChannelFuture channelFuture = this.channel.writeAndFlush(packet);
        if (callback != null) {
            channelFuture.addListener(callback);
        }

        channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    private Protocol getProtocol() {
        return this.channel.attr(ATTRIBUTE_PROTOCOL).get();
    }

    public void setProtocol(Protocol state) {
        this.channel.attr(ATTRIBUTE_PROTOCOL).set(state);
        this.channel.config().setAutoRead(true);
        LOGGER.debug("Enabled auto read");
    }

    private void sendQueuedPackets() {
        if (this.channel != null && this.channel.isOpen()) {
            synchronized (this.packetQueue) {
                QueuedPacket queuedPacket;
                while ((queuedPacket = this.packetQueue.poll()) != null) {
                    this.sendImmediately(queuedPacket.packet, queuedPacket.callback);
                }
            }
        }
    }

    public void disconnect() {
        if (this.channel.isOpen()) {
            this.channel.close().awaitUninterruptibly();
        }
    }

    public void handleDisconnection() {
        if (this.channel != null && !this.channel.isOpen()) {
            if (this.disconnected) {
                LOGGER.warn("handleDisconnection() called twice");
            } else {
                this.disconnected = true;
                this.getPacketListener().onDisconnected();
            }
        }
    }

    public PacketListener getPacketListener() {
        return this.packetListener;
    }

    public boolean isDisconnected() {
        return this.disconnected;
    }

    public boolean isLocal() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public SocketAddress getAddress() {
        return this.address;
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean isConnecting() {
        return this.channel == null;
    }

    static class QueuedPacket {
        final Packet<?> packet;
        @Nullable
        final GenericFutureListener<? extends Future<? super Void>> callback;

        public QueuedPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
            this.packet = packet;
            this.callback = callback;
        }
    }
}
