package com.hamusuke.paint.server;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.hamusuke.paint.network.channel.*;
import com.hamusuke.paint.network.protocol.PacketDirection;
import com.hamusuke.paint.network.protocol.packet.s2c.main.DisconnectS2CPacket;
import com.hamusuke.paint.server.network.handshake.LocalServerHandshakePacketListenerImpl;
import com.hamusuke.paint.server.network.handshake.ServerHandshakePacketListenerImpl;
import com.hamusuke.paint.util.Lazy;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerNetworkIo {
    public static final Lazy<NioEventLoopGroup> DEFAULT_CHANNEL = new Lazy<>(() -> {
        return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).build());
    });
    public static final Lazy<EpollEventLoopGroup> EPOLL_CHANNEL = new Lazy<>(() -> {
        return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
    });
    private static final Logger LOGGER = LogManager.getLogger();
    public final AtomicBoolean active = new AtomicBoolean();
    final PaintServer server;
    final List<Connection> connections = Collections.synchronizedList(Lists.newArrayList());
    private final List<ChannelFuture> channels = Collections.synchronizedList(Lists.newArrayList());

    public ServerNetworkIo(PaintServer server) {
        this.server = server;
        this.active.set(true);
    }

    public void bind(@Nullable InetAddress address, int port) {
        synchronized (this.channels) {
            Class<? extends ServerChannel> clazz;
            Lazy<? extends EventLoopGroup> lazy;
            if (Epoll.isAvailable()) {
                clazz = EpollServerSocketChannel.class;
                lazy = EPOLL_CHANNEL;
                LOGGER.info("Using epoll channel type");
            } else {
                clazz = NioServerSocketChannel.class;
                lazy = DEFAULT_CHANNEL;
                LOGGER.info("Using default channel type");
            }

            this.channels.add(new ServerBootstrap().channel(clazz).childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) {
                    try {
                        channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                    } catch (ChannelException ignored) {
                    }

                    channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("splitter", new PacketSplitter()).addLast("decoder", new PacketDecoder(PacketDirection.SERVERBOUND)).addLast("prepender", new PacketPrepender()).addLast("encoder", new PacketEncoder(PacketDirection.CLIENTBOUND));
                    Connection connection = new Connection(PacketDirection.SERVERBOUND);
                    ServerNetworkIo.this.connections.add(connection);
                    channel.pipeline().addLast("packet_handler", connection);
                    connection.setListener(new ServerHandshakePacketListenerImpl(ServerNetworkIo.this.server, connection));
                }
            }).group(lazy.get()).localAddress(address, port).bind().syncUninterruptibly());
        }
    }

    public SocketAddress bindLocal() {
        ChannelFuture channelFuture;
        synchronized (this.channels) {
            channelFuture = new ServerBootstrap().channel(LocalServerChannel.class).childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) {
                    Connection connection = new Connection(PacketDirection.SERVERBOUND);
                    connection.setListener(new LocalServerHandshakePacketListenerImpl(ServerNetworkIo.this.server, connection));
                    ServerNetworkIo.this.connections.add(connection);
                    channel.pipeline().addLast("packet_handler", connection);
                }
            }).group(DEFAULT_CHANNEL.get()).localAddress(LocalAddress.ANY).bind().syncUninterruptibly();
            this.channels.add(channelFuture);
        }

        return channelFuture.channel().localAddress();
    }

    public void stop() {
        this.active.set(false);

        for (ChannelFuture channelFuture : this.channels) {
            try {
                channelFuture.channel().close().sync();
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while closing channel");
            }
        }
    }

    public void tick() {
        synchronized (this.connections) {
            for (Connection connection : this.connections) {
                if (connection.isConnected()) {
                    try {
                        connection.tick();
                    } catch (Exception e) {
                        if (connection.isLocal()) {
                            throw new Error("Ticking memory connection", e);
                        }

                        LOGGER.warn("Failed to handle packet for {}", connection.getAddress(), e);
                        connection.sendPacket(new DisconnectS2CPacket(), future -> connection.disconnect());
                        connection.disableAutoRead();
                    }
                }
            }

            this.connections.removeIf(connection -> {
                if (!connection.isConnecting() && !connection.isConnected()) {
                    connection.handleDisconnection();
                    return true;
                }

                return false;
            });
        }
    }

    public PaintServer getServer() {
        return this.server;
    }

    public List<Connection> getConnections() {
        return this.connections;
    }
}
