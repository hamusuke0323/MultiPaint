package com.hamusuke.paint.network.protocol;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.PacketListener;
import com.hamusuke.paint.network.listener.client.ClientLoginPacketListener;
import com.hamusuke.paint.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.paint.network.listener.server.ServerHandshakePacketListener;
import com.hamusuke.paint.network.listener.server.ServerLoginPacketListener;
import com.hamusuke.paint.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;
import com.hamusuke.paint.network.protocol.packet.c2s.handshaking.HandshakeC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.login.AliveC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.login.AuthResponseC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.login.LoginHelloC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.login.LoginKeyC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.ChatC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.DisconnectC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.PingC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.RTTC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.canvas.*;
import com.hamusuke.paint.network.protocol.packet.c2s.main.lobby.CreateCanvasC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.lobby.JoinCanvasC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.lobby.RequestCanvasInfoC2SPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.login.*;
import com.hamusuke.paint.network.protocol.packet.s2c.main.*;
import com.hamusuke.paint.network.protocol.packet.s2c.main.canvas.CanvasDataS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.canvas.LineS2CPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.lobby.CanvasInfoResponseS2CPacket;
import com.hamusuke.paint.util.Util;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public enum Protocol {
    HANDSHAKING(-1, protocol()
            .addDirection(PacketDirection.SERVERBOUND, new PacketSet<ServerHandshakePacketListener>()
                    .add(HandshakeC2SPacket.class, HandshakeC2SPacket::new)
            )
    ),
    MAIN(0, protocol()
            .addDirection(PacketDirection.CLIENTBOUND, new PacketSet<ClientCommonPacketListener>()
                    .add(JoinPainterS2CPacket.class, JoinPainterS2CPacket::new)
                    .add(LeavePainterS2CPacket.class, LeavePainterS2CPacket::new)
                    .add(DisconnectS2CPacket.class, DisconnectS2CPacket::new)
                    .add(ChatS2CPacket.class, ChatS2CPacket::new)
                    .add(PongS2CPacket.class, PongS2CPacket::new)
                    .add(RTTS2CPacket.class, RTTS2CPacket::new)
                    .add(CanvasDataS2CPacket.class, CanvasDataS2CPacket::new)
                    .add(LineS2CPacket.class, LineS2CPacket::new)
                    .add(CanvasInfoResponseS2CPacket.class, CanvasInfoResponseS2CPacket::new)
                    .add(JoinCanvasS2CPacket.class, JoinCanvasS2CPacket::new)
                    .add(ChangeColorS2CPacket.class, ChangeColorS2CPacket::new)
                    .add(ChangeWidthS2CPacket.class, ChangeWidthS2CPacket::new)
                    .add(LeaveCanvasS2CPacket.class, LeaveCanvasS2CPacket::new)
            )
            .addDirection(PacketDirection.SERVERBOUND, new PacketSet<ServerCommonPacketListener>()
                    .add(DisconnectC2SPacket.class, DisconnectC2SPacket::new)
                    .add(PingC2SPacket.class, PingC2SPacket::new)
                    .add(RTTC2SPacket.class, RTTC2SPacket::new)
                    .add(ChatC2SPacket.class, ChatC2SPacket::new)
                    .add(LineC2SPacket.class, LineC2SPacket::new)
                    .add(SyncLinesC2SPacket.class, SyncLinesC2SPacket::new)
                    .add(JoinCanvasC2SPacket.class, JoinCanvasC2SPacket::new)
                    .add(RequestCanvasInfoC2SPacket.class, RequestCanvasInfoC2SPacket::new)
                    .add(CreateCanvasC2SPacket.class, CreateCanvasC2SPacket::new)
                    .add(ChangeColorC2SPacket.class, ChangeColorC2SPacket::new)
                    .add(ChangeWidthC2SPacket.class, ChangeWidthC2SPacket::new)
                    .add(LeaveCanvasC2SPacket.class, LeaveCanvasC2SPacket::new)
            )
    ),
    LOGIN(1, protocol()
            .addDirection(PacketDirection.CLIENTBOUND, new PacketSet<ClientLoginPacketListener>()
                    .add(LoginDisconnectS2CPacket.class, LoginDisconnectS2CPacket::new)
                    .add(LoginHelloS2CPacket.class, LoginHelloS2CPacket::new)
                    .add(LoginSuccessS2CPacket.class, LoginSuccessS2CPacket::new)
                    .add(LoginCompressionS2CPacket.class, LoginCompressionS2CPacket::new)
                    .add(AliveS2CPacket.class, AliveS2CPacket::new)
                    .add(AuthRequestS2CPacket.class, AuthRequestS2CPacket::new)
            )
            .addDirection(PacketDirection.SERVERBOUND, new PacketSet<ServerLoginPacketListener>()
                    .add(LoginHelloC2SPacket.class, LoginHelloC2SPacket::new)
                    .add(LoginKeyC2SPacket.class, LoginKeyC2SPacket::new)
                    .add(AliveC2SPacket.class, AliveC2SPacket::new)
                    .add(AuthResponseC2SPacket.class, AuthResponseC2SPacket::new)
            )
    );

    private static final int MIN = -1;
    private static final int MAX = 1;
    private static final Protocol[] PROTOCOLS = new Protocol[MAX - MIN + 1];
    private static final Map<Class<? extends Packet<?>>, Protocol> HANDLER_PROTOCOL_MAP = Maps.newHashMap();

    static {
        for (Protocol protocol : values()) {
            int id = protocol.getStateId();
            if (id < MIN || id > MAX) {
                throw new Error("Invalid protocol ID " + id);
            }

            PROTOCOLS[id - MIN] = protocol;
            protocol.packetHandlers.forEach((direction, packetSet) -> {
                packetSet.getPacketIds().forEach(packetClass -> {
                    if (HANDLER_PROTOCOL_MAP.containsKey(packetClass) && HANDLER_PROTOCOL_MAP.get(packetClass) != protocol) {
                        throw new IllegalStateException("Packet " + packetClass + " is already assigned to protocol " + HANDLER_PROTOCOL_MAP.get(packetClass) + " - can't reassign to " + protocol);
                    } else {
                        HANDLER_PROTOCOL_MAP.put(packetClass, protocol);
                    }
                });
            });
        }
    }

    private final int stateId;
    private final Map<PacketDirection, ? extends Protocol.PacketSet<?>> packetHandlers;

    private static Protocol.Builder protocol() {
        return new Protocol.Builder();
    }

    Protocol(int stateId, Protocol.Builder builder) {
        this.stateId = stateId;
        this.packetHandlers = builder.packetHandlers;
    }

    @Nullable
    public static Protocol byId(int id) {
        return id >= MIN && id <= MAX ? PROTOCOLS[id - MIN] : null;
    }

    public static Protocol getPacketHandlerState(Packet<?> packet) {
        return HANDLER_PROTOCOL_MAP.get(packet.getClass());
    }

    public Integer getPacketId(PacketDirection direction, Packet<?> packet) {
        return this.packetHandlers.get(direction).getId(packet.getClass());
    }

    public Packet<?> createPacket(PacketDirection direction, int id, IntelligentByteBuf byteBuf) {
        return this.packetHandlers.get(direction).create(id, byteBuf);
    }

    public int getStateId() {
        return this.stateId;
    }

    static class PacketSet<T extends PacketListener> {
        final Object2IntMap<Class<? extends Packet<T>>> packetIds = Util.makeAndAccess(new Object2IntOpenHashMap<>(), map -> map.defaultReturnValue(-1));
        private final List<Function<IntelligentByteBuf, ? extends Packet<T>>> idToInitializer = Lists.newArrayList();

        public <P extends Packet<T>> Protocol.PacketSet<T> add(Class<P> clazz, Function<IntelligentByteBuf, ? extends P> function) {
            int i = this.idToInitializer.size();
            int j = this.packetIds.put(clazz, i);

            if (j != -1) {
                throw new IllegalArgumentException("Packet " + clazz + " is already registered to ID " + j);
            } else {
                this.idToInitializer.add(function);
                return this;
            }
        }

        @Nullable
        public Integer getId(Class<?> clazz) {
            int i = this.packetIds.getInt(clazz);
            return i == -1 ? null : i;
        }

        @Nullable
        public Packet<?> create(int id, IntelligentByteBuf byteBuf) {
            if (0 > id || this.idToInitializer.size() <= id) {
                return null;
            }

            Function<IntelligentByteBuf, ? extends Packet<T>> function = this.idToInitializer.get(id);
            return function != null ? function.apply(byteBuf) : null;
        }

        public Iterable<Class<? extends Packet<?>>> getPacketIds() {
            return Iterables.unmodifiableIterable(this.packetIds.keySet());
        }
    }

    static class Builder {
        final Map<PacketDirection, Protocol.PacketSet<?>> packetHandlers = new EnumMap<>(PacketDirection.class);

        public <T extends PacketListener> Protocol.Builder addDirection(PacketDirection direction, Protocol.PacketSet<T> packetSet) {
            this.packetHandlers.put(direction, packetSet);
            return this;
        }
    }
}
