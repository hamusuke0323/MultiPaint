package com.hamusuke.paint.client.network.main;

import com.hamusuke.paint.client.PaintClient;
import com.hamusuke.paint.client.canvas.ClientCanvas;
import com.hamusuke.paint.client.gui.component.Chat;
import com.hamusuke.paint.client.gui.component.list.CanvasPainterList;
import com.hamusuke.paint.client.gui.component.list.PainterList;
import com.hamusuke.paint.client.gui.window.*;
import com.hamusuke.paint.client.network.ClientPainter;
import com.hamusuke.paint.network.channel.Connection;
import com.hamusuke.paint.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.c2s.main.PingC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.RTTC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.canvas.SyncLinesC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.lobby.RequestCanvasInfoC2SPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.main.*;
import com.hamusuke.paint.util.Util;

public abstract class ClientCommonPacketListenerImpl implements ClientCommonPacketListener {
    protected final Connection connection;
    protected final PaintClient client;
    protected ClientPainter clientPainter;
    protected int tickCount;
    protected String disconnectionMsg = "";

    protected ClientCommonPacketListenerImpl(PaintClient client, Connection connection) {
        this.client = client;
        this.client.listener = this;
        this.connection = connection;
    }

    @Override
    public void tick() {
        this.tickCount++;
        if (this.tickCount % 20 == 0) {
            this.connection.sendPacket(new PingC2SPacket(Util.getMeasuringTimeMs()));
        }
    }

    @Override
    public void handleChatPacket(ChatS2CPacket packet) {
        this.client.chat.addMessage(packet.getMsg());
    }

    @Override
    public void handlePongPacket(PongS2CPacket packet) {
        this.connection.sendPacket(new RTTC2SPacket((int) (Util.getMeasuringTimeMs() - packet.getClientTime())));
    }

    @Override
    public void handleRTTPacket(RTTS2CPacket packet) {
        synchronized (this.client.clientPainters) {
            this.client.clientPainters.stream().filter(p -> p.getId() == packet.getPainterId()).forEach(painter -> {
                painter.setPing(packet.getRtt());
            });
        }

        this.client.painterList.update();
    }

    @Override
    public void handleDisconnectPacket(DisconnectS2CPacket packet) {
        this.disconnectionMsg = packet.getMsg();
        this.connection.disconnect();
    }

    @Override
    public void handleJoinPacket(JoinPainterS2CPacket packet) {
        ClientPainter painter = new ClientPainter(packet.getUuid());
        painter.setId(packet.getId());
        if (this.clientPainter == null && !packet.isOthers()) {
            this.clientPainter = painter;
            this.client.clientPainter = painter;
            this.client.painterList = new PainterList(this.client);
            this.client.chat = new Chat(this.client);
            if (this instanceof ClientLobbyPacketListenerImpl) {
                LobbyWindow lobbyWindow = new LobbyWindow();
                ((ClientLobbyPacketListenerImpl) this).lobbyWindow = lobbyWindow;
                this.client.setCurrentWindow(lobbyWindow);
                this.connection.sendPacket(new RequestCanvasInfoC2SPacket());
            }
        }

        synchronized (this.client.clientPainters) {
            this.client.clientPainters.add(painter);
        }
    }

    @Override
    public void handleLeavePacket(LeavePainterS2CPacket packet) {
        synchronized (this.client.clientPainters) {
            this.client.clientPainters.removeIf(p -> p.getId() == packet.getId());
        }
    }

    @Override
    public void handleJoinCanvasPacket(JoinCanvasS2CPacket packet) {
        ClientPainter painter = this.client.getById(packet.getId());
        if (painter != null) {
            ClientCanvas canvas = new ClientCanvas(packet.getInfo().getCanvasUUID(), packet.getInfo().getTitle(), packet.getInfo().getAuthor(), packet.getInfo().getWidth(), packet.getInfo().getHeight());
            canvas.setCanvasId(packet.getInfo().getCanvasId());
            painter.joinCanvas(canvas);

            if (painter.equals(this.clientPainter)) {
                this.client.painterList.clear();
                this.client.painterList = new CanvasPainterList(this.client);
                CanvasWindow canvasWindow = new CanvasWindow(canvas);
                ClientCanvasPacketListenerImpl listener = new ClientCanvasPacketListenerImpl(this.client, this.connection, canvasWindow, canvas);
                this.client.listener = listener;
                this.connection.setListener(listener);
                this.client.setCurrentWindow(canvasWindow);
                this.connection.sendPacket(new SyncLinesC2SPacket());
            }
        }
    }

    @Override
    public void handleChangeColorPacket(ChangeColorS2CPacket packet) {
        ClientPainter painter = this.client.getById(packet.getId());
        if (painter != null) {
            painter.setColor(packet.getColor());
        }
    }

    @Override
    public void handleChangeWidthPacket(ChangeWidthS2CPacket packet) {
        ClientPainter painter = this.client.getById(packet.getId());
        if (painter != null) {
            painter.setWidth(packet.getWidth());
        }
    }

    @Override
    public void handleLeaveCanvasPacket(LeaveCanvasS2CPacket packet) {
        ClientPainter painter = this.client.getById(packet.getId());
        if (painter != null) {
            painter.joinCanvas(null);
            if (painter.equals(this.clientPainter)) {
                ClientLobbyPacketListenerImpl listener = new ClientLobbyPacketListenerImpl(this.client, this.connection);
                this.connection.setListener(listener);
                this.client.painterList = new PainterList(this.client);
                LobbyWindow lobbyWindow = new LobbyWindow();
                listener.lobbyWindow = lobbyWindow;
                this.client.setCurrentWindow(lobbyWindow);
                this.connection.sendPacket(new RequestCanvasInfoC2SPacket());
            }
        }
    }

    @Override
    public void onDisconnected() {
        this.client.clientPainters.clear();
        this.client.disconnect();
        this.client.stopServer();
        Window window = this.client.getCurrentWindow();
        if (window != null) {
            window.dispose();
        }
        this.client.setCurrentWindow(new DisconnectWindow(this.disconnectionMsg, MenuWindow::new));
        this.client.clientPainter = null;
        this.client.painterList = null;
        this.client.chat = null;
    }

    public PaintClient getClient() {
        return this.client;
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
