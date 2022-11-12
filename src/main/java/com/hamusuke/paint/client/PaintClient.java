package com.hamusuke.paint.client;

import com.google.common.collect.Lists;
import com.hamusuke.paint.client.canvas.ClientCanvas;
import com.hamusuke.paint.client.gui.component.Chat;
import com.hamusuke.paint.client.gui.component.list.PainterList;
import com.hamusuke.paint.client.gui.window.MenuWindow;
import com.hamusuke.paint.client.gui.window.Window;
import com.hamusuke.paint.client.network.ClientLoginPacketListenerImpl;
import com.hamusuke.paint.client.network.ClientPainter;
import com.hamusuke.paint.client.network.main.ClientCommonPacketListenerImpl;
import com.hamusuke.paint.network.channel.Connection;
import com.hamusuke.paint.network.protocol.Protocol;
import com.hamusuke.paint.network.protocol.packet.c2s.handshaking.HandshakeC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.login.LoginHelloC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.DisconnectC2SPacket;
import com.hamusuke.paint.network.protocol.packet.c2s.main.canvas.LeaveCanvasC2SPacket;
import com.hamusuke.paint.server.PaintServer;
import com.hamusuke.paint.server.integrated.IntegratedServer;
import com.hamusuke.paint.util.Util;
import com.hamusuke.paint.util.thread.ReentrantThreadExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class PaintClient extends ReentrantThreadExecutor<Runnable> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static PaintClient INSTANCE;
    private final AtomicBoolean running = new AtomicBoolean();
    private final TickCounter tickCounter = new TickCounter(20.0F, 0L);
    private final UUIDLoader uuidLoader;
    @Nullable
    private IntegratedServer server;
    @Nullable
    private Connection connection;
    @Nullable
    public ClientCommonPacketListenerImpl listener;
    @Nullable
    public ClientPainter clientPainter;
    @Nullable
    private Window currentWindow;
    private Thread thread;
    private int tickCount;
    public final List<ClientPainter> clientPainters = Lists.newArrayList();
    public PainterList painterList;
    public Chat chat;

    public PaintClient() {
        super("Client");
        INSTANCE = this;
        this.uuidLoader = new UUIDLoader();
        this.running.set(true);
        this.thread = Thread.currentThread();
        this.setCurrentWindow(new MenuWindow());
    }

    public static PaintClient getInstance() {
        return INSTANCE;
    }

    public void run() {
        this.thread = Thread.currentThread();
        if (Runtime.getRuntime().availableProcessors() > 4) {
            this.thread.setPriority(10);
        }

        try {
            boolean bl = false;

            while (this.running.get()) {
                try {
                    this.loop(!bl);
                } catch (OutOfMemoryError e) {
                    if (bl) {
                        throw e;
                    }

                    System.gc();
                    LOGGER.fatal("Out of memory", e);
                    bl = true;
                }
            }
        } catch (Exception e) {
            LOGGER.fatal("Error thrown!", e);
        }
    }

    @Nullable
    public ClientCanvas getCurrentCanvas() {
        return this.clientPainter == null ? null : this.clientPainter.getCurrentCanvas();
    }

    public void joinCanvas(@Nullable ClientCanvas canvas) {
        if (this.clientPainter != null) {
            this.clientPainter.joinCanvas(canvas);
            return;
        }

        throw new IllegalStateException("Y the client-side painter is null???");
    }

    public String getAddresses() {
        return this.connection == null ? "" : String.format("Client Address: %s, Server Address: %s", this.connection.getChannel().localAddress(), this.connection.getChannel().remoteAddress());
    }

    @Nullable
    public Window getCurrentWindow() {
        return this.currentWindow;
    }

    public void setCurrentWindow(@Nullable Window currentWindow) {
        this.currentWindow = currentWindow;

        if (this.currentWindow == null) {
            this.currentWindow = new MenuWindow();
        }

        this.currentWindow.setVisible(true);
    }

    public void stopLooping() {
        this.running.set(false);
    }

    public void stop() {
        try {
            LOGGER.info("Stopping");
            this.close();
        } catch (Exception e) {
            LOGGER.warn("Error occurred while stopping", e);
        }
    }

    private void loop(boolean tick) {
        if (tick) {
            int i = this.tickCounter.beginLoopTick(Util.getMeasuringTimeMs());
            this.runTasks();
            for (int j = 0; j < Math.min(10, i); j++) {
                this.tick();
            }
        }
    }

    public void tick() {
        this.tickCount++;

        if (this.currentWindow != null) {
            this.currentWindow.tick();
        }

        if (this.connection != null) {
            this.connection.tick();
            if (this.connection.isDisconnected()) {
                this.connection = null;
            }
        }
    }

    @Nullable
    public ClientPainter getById(int id) {
        synchronized (this.clientPainters) {
            for (ClientPainter painter : this.clientPainters) {
                if (painter.getId() == id) {
                    return painter;
                }
            }
        }

        return null;
    }

    public void startIntegratedServer(Consumer<String> consumer) {
        this.server = PaintServer.startServer(thread -> new IntegratedServer(thread, this));
        while (!this.server.isLoading()) {
            try {
                Thread.sleep(16L);
            } catch (InterruptedException ignored) {
            }
        }

        SocketAddress socketAddress = this.server.getNetworkIo().bindLocal();
        Connection connection = Connection.connectLocal(socketAddress);
        connection.setListener(new ClientLoginPacketListenerImpl(connection, this, consumer));
        connection.sendPacket(new HandshakeC2SPacket(socketAddress.toString(), 0, Protocol.LOGIN));
        connection.sendPacket(new LoginHelloC2SPacket(this.uuidLoader.getUuid()));
        this.connection = connection;
    }

    public void connectToServer(String host, int port, Consumer<String> consumer) {
        InetSocketAddress address = new InetSocketAddress(host, port);
        this.connection = Connection.connect(address);
        this.connection.setListener(new ClientLoginPacketListenerImpl(this.connection, this, consumer));
        this.connection.sendPacket(new HandshakeC2SPacket(host, port, Protocol.LOGIN));
        this.connection.sendPacket(new LoginHelloC2SPacket(this.uuidLoader.getUuid()));
    }

    @Override
    public void close() {
        Util.shutdownExecutors();
        System.exit(0);
    }

    @Override
    protected Runnable createTask(Runnable runnable) {
        return runnable;
    }

    @Override
    protected boolean canExecute(Runnable task) {
        return true;
    }

    @Override
    protected Thread getThread() {
        return this.thread;
    }

    @Nullable
    public Connection getConnection() {
        return this.connection;
    }

    public void disconnect() {
        this.connection.sendPacket(new DisconnectC2SPacket(), future -> this.connection.disconnect());
    }

    public void leaveCanvas() {
        this.connection.sendPacket(new LeaveCanvasC2SPacket());
        this.painterList.clear();
    }

    public void stopServer() {
        if (this.server != null && this.server.isLocal()) {
            Connection connection = this.getConnection();
            if (connection != null) {
                this.cancelTasks();
                connection.disconnect();
            }

            IntegratedServer server = this.server;
            this.server = null;

            while (!server.isStopping()) {
            }
        }
    }
}
