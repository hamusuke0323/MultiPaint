package com.hamusuke.paint.server.network;

import com.hamusuke.paint.network.channel.Connection;
import com.hamusuke.paint.network.encryption.NetworkEncryptionUtil;
import com.hamusuke.paint.network.listener.server.ServerLoginPacketListener;
import com.hamusuke.paint.network.protocol.packet.c2s.login.AliveC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.login.AuthResponseC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.login.LoginHelloC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.login.LoginKeyC2SPacket;
import com.hamusuke.paint.network.protocol.packet.s2c.login.*;
import com.hamusuke.paint.server.PaintServer;
import com.hamusuke.paint.server.integrated.IntegratedServer;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;

public class ServerLoginPacketListenerImpl implements ServerLoginPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int TIMEOUT_TICKS = 600;
    private static final Random RANDOM = new Random();
    public final Connection connection;
    final PaintServer server;
    private final byte[] nonce = new byte[4];
    private final String serverId;
    State state;
    private int ticks;
    private ServerPainter serverPainter;

    public ServerLoginPacketListenerImpl(PaintServer server, Connection connection) {
        this.state = State.HELLO;
        this.serverId = "";
        this.server = server;
        this.connection = connection;
        RANDOM.nextBytes(this.nonce);
    }

    public static boolean isValidName(String name) {
        return !name.chars().filter(c -> c <= 32 || c >= 127).findAny().isPresent();
    }

    @Override
    public void tick() {
        if (this.state == State.READY) {
            this.acceptPainter();
        }

        this.ticks++;
        if ((this.state == State.HELLO || this.state == State.KEY) && this.ticks == TIMEOUT_TICKS) {
            LOGGER.info("Login is too slow");
            this.disconnect();
        }
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    public void disconnect() {
        try {
            LOGGER.info("Disconnecting {}", this.getConnectionInfo());
            this.connection.sendPacket(new LoginDisconnectS2CPacket());
            this.connection.disconnect();
        } catch (Exception e) {
            LOGGER.error("Error while disconnecting painter", e);
        }
    }

    public void acceptPainter() {
        this.state = State.ACCEPTED;
        if (this.server.getCompressionThreshold() >= 0 && !this.connection.isLocal()) {
            this.connection.sendPacket(new LoginCompressionS2CPacket(this.server.getCompressionThreshold()), future -> {
                this.connection.setCompression(this.server.getCompressionThreshold(), true);
            });
        }

        if (this.server.getPainterManager().canJoin(this.serverPainter)) {
            if (this.server instanceof IntegratedServer && ((IntegratedServer) this.server).isHostAbsent()) {
                ((IntegratedServer) this.server).setHost(this.serverPainter);
            }

            this.connection.sendPacket(new LoginSuccessS2CPacket());
            this.server.getPainterManager().addPainter(this.connection, this.serverPainter);
        } else {
            this.disconnect();
        }
    }

    @Override
    public void onDisconnected() {
        LOGGER.info("{} lost connection", this.getConnectionInfo());
    }

    public String getConnectionInfo() {
        return String.valueOf(this.connection.getAddress());
    }

    @Override
    public void onHello(LoginHelloC2SPacket packet) {
        Validate.validState(this.state == State.HELLO, "Unexpected hello packet");
        this.serverPainter = new ServerPainter(packet.getClient(), this.server, null);
        if (!this.connection.isLocal()) {
            this.state = State.KEY;
            this.connection.sendPacket(new LoginHelloS2CPacket("", this.server.getKeyPair().getPublic().getEncoded(), this.nonce));
        } else {
            this.state = State.READY;
            this.serverPainter.setAuthorized(true);
        }
    }

    @Override
    public void onKey(LoginKeyC2SPacket packet) {
        Validate.validState(this.state == State.KEY, "Unexpected key packet");
        PrivateKey privateKey = this.server.getKeyPair().getPrivate();

        try {
            if (!Arrays.equals(this.nonce, packet.decryptNonce(privateKey))) {
                throw new IllegalStateException("Protocol error");
            }

            SecretKey secretKey = packet.decryptSecretKey(privateKey);
            Cipher cipher = NetworkEncryptionUtil.cipherFromKey(2, secretKey);
            Cipher cipher2 = NetworkEncryptionUtil.cipherFromKey(1, secretKey);
            this.connection.setupEncryption(cipher, cipher2);
            this.state = State.AUTH;
            this.connection.sendPacket(new AuthRequestS2CPacket());
        } catch (Exception e) {
            throw new IllegalStateException("Protocol error", e);
        }
    }

    @Override
    public void onPing(AliveC2SPacket packet) {
        this.connection.sendPacket(new AliveS2CPacket());
    }

    @Override
    public void onAuth(AuthResponseC2SPacket packet) {
        if (this.tryLogin(packet.getPwd())) {
            this.serverPainter.setAuthorized(true);
            this.state = State.READY;
        } else {
            this.connection.sendPacket(new AuthRequestS2CPacket());
        }
    }

    private boolean tryLogin(String s) {
        //TODO
        return true;
    }

    private enum State {
        HELLO,
        KEY,
        AUTH,
        READY,
        ACCEPTED
    }
}
