package com.hamusuke.paint.server;

import com.hamusuke.paint.server.dedicated.DedicatedPaintServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainServer {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) throws UnknownHostException {
        String s = InetAddress.getLocalHost().getHostAddress();
        int i = 8080;
        for (String arg : args) {
            if (arg.contains(":")) {
                String[] KV = arg.split(":");
                switch (KV[0]) {
                    case "address":
                        s = KV[1];
                        break;
                    case "port":
                        i = Integer.parseInt(KV[1]);
                }
            }
        }
        String host = s;
        int port = i;
        final DedicatedPaintServer server = PaintServer.startServer(thread -> new DedicatedPaintServer(thread, host, port));
        Thread thread = new Thread(() -> server.stop(true), "Server Shutdown Thread");
        thread.setUncaughtExceptionHandler((t, e) -> LOGGER.error("Caught exception", e));
        Runtime.getRuntime().addShutdownHook(thread);
    }
}
