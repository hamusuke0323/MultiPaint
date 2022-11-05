package com.hamusuke.paint.server.dedicated;

import com.google.common.collect.Lists;
import com.hamusuke.paint.Constants;
import com.hamusuke.paint.server.PaintServer;
import com.hamusuke.paint.util.Util;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class DedicatedPaintServer extends PaintServer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<String> commandQueue = Collections.synchronizedList(Lists.newArrayList());

    public DedicatedPaintServer(Thread serverThread, String host, int port) {
        super(serverThread);
        this.setServerIp(host);
        this.setServerPort(port);
    }

    @Override
    protected boolean setupServer() throws IOException {
        Thread thread = new Thread("Server console handler") {
            public void run() {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

                String string;
                try {
                    while (!DedicatedPaintServer.this.isStopped() && DedicatedPaintServer.this.isRunning() && (string = bufferedReader.readLine()) != null) {
                        DedicatedPaintServer.this.enqueueCommand(string);
                    }
                } catch (IOException var4) {
                    DedicatedPaintServer.LOGGER.error("Exception handling console input", var4);
                }
            }
        };
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler((t, e) -> LOGGER.error("Caught exception", e));
        thread.start();
        LOGGER.info("Starting paint server version {}", Constants.VERSION);
        InetAddress inetAddress = null;
        if (!this.getServerIp().isEmpty()) {
            inetAddress = InetAddress.getByName(this.getServerIp());
        }
        this.generateKeyPair();
        LOGGER.info("Starting paint server on {}:{}", this.getServerIp().isEmpty() ? "*" : this.getServerIp(), this.getServerPort());
        this.getNetworkIo().bind(inetAddress, this.getServerPort());
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        Util.shutdownExecutors();
    }

    @Override
    public void tick() {
        super.tick();
        this.runQueuedCommands();
    }

    public void enqueueCommand(String command) {
        this.commandQueue.add(command);
    }

    public void runQueuedCommands() {
        while (!this.commandQueue.isEmpty()) {
            String command = this.commandQueue.remove(0);
            this.runCommand(command);
        }
    }

    private void runCommand(String command) {
        try {
            this.dispatcher.execute(command, this);
        } catch (CommandSyntaxException e) {
            LOGGER.info("Command Syntax Error!", e);
        }
    }
}
