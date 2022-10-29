package com.hamusuke.paint.server;

import com.hamusuke.paint.server.dedicated.DedicatedPaintServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainServer {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        final DedicatedPaintServer server = PaintServer.startServer(thread -> new DedicatedPaintServer(thread, "localhost", 8080));
        Thread thread = new Thread(() -> server.stop(true), "Server Shutdown Thread");
        thread.setUncaughtExceptionHandler((t, e) -> LOGGER.error("Caught exception", e));
        Runtime.getRuntime().addShutdownHook(thread);
    }
}
