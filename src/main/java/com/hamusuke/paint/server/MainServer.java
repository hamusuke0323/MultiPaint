package com.hamusuke.paint.server;

import com.hamusuke.paint.server.dedicated.DedicatedPaintServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainServer {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        int i = 8080;
        if (args.length > 0) {
            try {
                i = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
        }
        int port = i;
        final DedicatedPaintServer server = PaintServer.startServer(thread -> new DedicatedPaintServer(thread, "localhost", port));
        Thread thread = new Thread(() -> server.stop(true), "Server Shutdown Thread");
        thread.setUncaughtExceptionHandler((t, e) -> LOGGER.error("Caught exception", e));
        Runtime.getRuntime().addShutdownHook(thread);
    }
}
