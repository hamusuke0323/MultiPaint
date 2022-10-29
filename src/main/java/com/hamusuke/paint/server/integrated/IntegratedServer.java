package com.hamusuke.paint.server.integrated;

import com.hamusuke.paint.Constants;
import com.hamusuke.paint.client.PaintClient;
import com.hamusuke.paint.server.PaintServer;
import com.hamusuke.paint.server.network.ServerPainter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Objects;

public class IntegratedServer extends PaintServer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final PaintClient client;
    private int lanPort = -1;
    @Nullable
    private ServerPainter host;

    public IntegratedServer(Thread serverThread, PaintClient client) {
        super(serverThread);
        this.client = client;
    }

    public void setHost(ServerPainter host) {
        this.host = host;
    }

    public boolean isHostAbsent() {
        return this.host == null;
    }

    @Override
    public boolean isHost(ServerPainter serverPainter) {
        return Objects.equals(serverPainter, this.host);
    }

    @Override
    protected boolean setupServer() {
        LOGGER.info("Starting integrated paint server version {}", Constants.VERSION);
        this.generateKeyPair();
        return true;
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    @Override
    public int getServerPort() {
        return this.lanPort;
    }
}
