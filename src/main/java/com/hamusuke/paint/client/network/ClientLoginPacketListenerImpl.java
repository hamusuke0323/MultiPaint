package com.hamusuke.paint.client.network;

import com.hamusuke.paint.client.PaintClient;
import com.hamusuke.paint.client.gui.window.ConnectingWindow;
import com.hamusuke.paint.client.gui.window.DisconnectWindow;
import com.hamusuke.paint.client.gui.window.LoginWindow;
import com.hamusuke.paint.client.network.main.ClientLobbyPacketListenerImpl;
import com.hamusuke.paint.network.channel.Connection;
import com.hamusuke.paint.network.encryption.NetworkEncryptionUtil;
import com.hamusuke.paint.network.listener.client.ClientLoginPacketListener;
import com.hamusuke.paint.network.protocol.Protocol;
import com.hamusuke.paint.network.protocol.packet.c2s.login.AliveC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.login.LoginKeyC2SPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.login.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.util.function.Consumer;

public class ClientLoginPacketListenerImpl implements ClientLoginPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final PaintClient client;
    private final Consumer<String> statusConsumer;
    private final Runnable onJoinLobby;
    private final Connection connection;
    private boolean waitingAuthComplete;
    private int ticks;
    private String disconnectionMsg = "";

    public ClientLoginPacketListenerImpl(Connection connection, PaintClient client, Consumer<String> statusConsumer, Runnable onJoinLobby) {
        this.client = client;
        this.connection = connection;
        this.statusConsumer = statusConsumer;
        this.onJoinLobby = onJoinLobby;
    }

    @Override
    public void tick() {
        if (this.waitingAuthComplete && this.ticks % 20 == 0) {
            this.connection.sendPacket(new AliveC2SPacket());
        }

        this.ticks++;
    }

    @Override
    public void onHello(LoginHelloS2CPacket packet) {
        Cipher cipher;
        Cipher cipher2;
        LoginKeyC2SPacket loginKeyC2SPacket;
        try {
            SecretKey secretKey = NetworkEncryptionUtil.generateKey();
            PublicKey publicKey = packet.getPublicKey();
            cipher = NetworkEncryptionUtil.cipherFromKey(2, secretKey);
            cipher2 = NetworkEncryptionUtil.cipherFromKey(1, secretKey);
            loginKeyC2SPacket = new LoginKeyC2SPacket(secretKey, publicKey, packet.getNonce());
        } catch (Exception e) {
            LOGGER.error("Protocol error", e);
            throw new IllegalStateException("Protocol error", e);
        }

        this.statusConsumer.accept("Encrypting...");
        this.connection.sendPacket(loginKeyC2SPacket, future -> this.connection.setupEncryption(cipher, cipher2));
    }

    @Override
    public void onSuccess(LoginSuccessS2CPacket packet) {
        this.waitingAuthComplete = false;
        this.statusConsumer.accept("Joining the lobby...");
        this.connection.setProtocol(Protocol.MAIN);
        this.connection.setListener(new ClientLobbyPacketListenerImpl(this.client, this.connection));
        this.onJoinLobby.run();
    }

    @Override
    public void onDisconnect(LoginDisconnectS2CPacket packet) {
        this.disconnectionMsg = packet.getMsg();
        this.connection.disconnect();
    }

    @Override
    public void onCompression(LoginCompressionS2CPacket packet) {
        if (!this.connection.isLocal()) {
            this.connection.setCompression(packet.getThreshold(), false);
        }
    }

    @Override
    public void onAuth(AuthRequestS2CPacket packet) {
        this.waitingAuthComplete = true;
        this.onJoinLobby.run();
        this.client.setCurrentWindow(new LoginWindow());
    }

    @Override
    public void onDisconnected() {
        this.client.setCurrentWindow(new DisconnectWindow(this.disconnectionMsg, ConnectingWindow::new));
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
