package com.hamusuke.paint.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hamusuke.paint.network.encryption.NetworkEncryptionUtil;
import com.hamusuke.paint.network.protocol.packet.Packet;
import com.hamusuke.paint.server.canvas.ServerCanvas;
import com.hamusuke.paint.server.network.ServerPainter;
import com.hamusuke.paint.util.Util;
import com.hamusuke.paint.util.thread.ReentrantThreadExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public abstract class PaintServer extends ReentrantThreadExecutor<ServerTask> implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ServerNetworkIo networkIo;
    private final Random random;
    private final AtomicBoolean running = new AtomicBoolean();
    private final Thread serverThread;
    private final Executor worker;
    private String serverIp;
    private int serverPort;
    private boolean stopped;
    private int ticks;
    private PainterManager painterManager;
    @Nullable
    private KeyPair keyPair;
    private boolean waitingForNextTick;
    private long nextTickTimestamp;
    private long timeReference;
    private long lastTimeReference;
    private final AtomicBoolean loading = new AtomicBoolean();
    private final List<ServerCanvas> serverCanvases = Collections.synchronizedList(Lists.newArrayList());

    public PaintServer(Thread serverThread) {
        super("Server");
        this.random = new Random();
        this.serverPort = -1;
        this.running.set(true);
        this.networkIo = new ServerNetworkIo(this);
        this.serverThread = serverThread;
        this.worker = Util.getMainWorkerExecutor();
        this.setPainterManager(new PainterManager(this));
        this.serverCanvases.add(new ServerCanvas("テストだよ", new UUID(0L, 0L), 1920, 1080));
        this.serverCanvases.add(new ServerCanvas("テストだよ2", new UUID(0L, 0L), 1920, 1080));
    }

    public static <S extends PaintServer> S startServer(Function<Thread, S> factory) {
        AtomicReference<S> atomicReference = new AtomicReference<>();
        Thread thread = new Thread(() -> atomicReference.get().runServer(), "Server Thread");
        thread.setUncaughtExceptionHandler((t, e) -> LOGGER.error("Error occurred in server thread", e));
        if (Runtime.getRuntime().availableProcessors() > 4) {
            thread.setPriority(8);
        }

        S server = factory.apply(thread);
        atomicReference.set(server);
        thread.start();
        return server;
    }

    protected abstract boolean setupServer() throws IOException;

    protected void runServer() {
        try {
            if (this.setupServer()) {
                this.timeReference = Util.getMeasuringTimeMs();

                while (this.running.get()) {
                    long l = Util.getMeasuringTimeMs() - this.timeReference;
                    if (l > 2000L && this.timeReference - this.lastTimeReference >= 15000L) {
                        long m = l / 50L;
                        LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", l, m);
                        this.timeReference += m * 50L;
                        this.lastTimeReference = this.timeReference;
                    }

                    this.timeReference += 50L;
                    this.tick();
                    this.waitingForNextTick = true;
                    this.nextTickTimestamp = Math.max(Util.getMeasuringTimeMs() + 50L, this.timeReference);
                    this.runTasksTillTickEnd();
                    if (!this.loading.get()) {
                        this.loading.set(true);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Encountered an unexpected exception", e);
        } finally {
            try {
                this.stopped = true;
                this.shutdown();
            } catch (Throwable e) {
                LOGGER.error("Error occurred while stopping the server", e);
            } finally {
                this.exit();
            }
        }
    }

    public void sendPacketToAll(Packet<?> packet) {
        this.sendPacketToAll(packet, null);
    }

    public void sendPacketToAll(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
        this.getPainterManager().sendPacketToAll(packet, callback);
    }

    public ImmutableList<ServerCanvas> getServerCanvases() {
        return ImmutableList.copyOf(this.serverCanvases);
    }

    @Nullable
    public ServerCanvas getCanvas(int id) {
        synchronized (this.serverCanvases) {
            for (ServerCanvas canvas : this.serverCanvases) {
                if (canvas.getCanvasId() == id) {
                    return canvas;
                }
            }
        }

        return null;
    }

    public void tick() {
        this.ticks++;
        this.getNetworkIo().tick();
    }

    private boolean shouldKeepTicking() {
        return this.hasRunningTasks() || Util.getMeasuringTimeMs() < (this.waitingForNextTick ? this.nextTickTimestamp : this.timeReference);
    }

    protected void runTasksTillTickEnd() {
        this.runTasks();
        this.runTasks(() -> !this.shouldKeepTicking());
    }

    @Override
    protected ServerTask createTask(Runnable runnable) {
        return new ServerTask(this.ticks, runnable);
    }

    @Override
    protected boolean canExecute(ServerTask task) {
        return task.getCreationTicks() + 3 < this.ticks || this.shouldKeepTicking();
    }

    @Override
    public boolean runTask() {
        boolean bl = this.runOneTask();
        this.waitingForNextTick = bl;
        return bl;
    }

    private boolean runOneTask() {
        return super.runTask();
    }

    @Override
    public void close() {
        this.shutdown();
    }

    public void shutdown() {
        LOGGER.info("Stopping server");
        if (this.getNetworkIo() != null) {
            this.getNetworkIo().stop();
        }
    }

    public void stop(boolean bl) {
        this.running.set(false);
        if (bl) {
            try {
                this.serverThread.join();
            } catch (InterruptedException e) {
                LOGGER.error("Error occurred while shutting down", e);
            }
        }
    }

    public boolean isLocal() {
        return false;
    }

    public boolean isHost(@Nullable ServerPainter serverPainter) {
        return false;
    }

    public int getCompressionThreshold() {
        return 256;
    }

    public void exit() {
    }

    protected void generateKeyPair() {
        LOGGER.info("Generating keypair");

        try {
            this.keyPair = NetworkEncryptionUtil.generateServerKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate key pair", e);
        }
    }

    public boolean isLoading() {
        return this.loading.get();
    }

    public boolean acceptsStatusQuery() {
        return true;
    }

    public boolean isStopping() {
        return !this.serverThread.isAlive();
    }

    public PainterManager getPainterManager() {
        return this.painterManager;
    }

    public void setPainterManager(PainterManager painterManager) {
        this.painterManager = painterManager;
    }

    @Nullable
    public KeyPair getKeyPair() {
        return this.keyPair;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public boolean isStopped() {
        return this.stopped;
    }

    public String getServerIp() {
        return this.serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public boolean isRunning() {
        return this.running.get();
    }

    public ServerNetworkIo getNetworkIo() {
        return this.networkIo;
    }

    public int getTicks() {
        return this.ticks;
    }

    @Override
    protected boolean shouldExecuteAsync() {
        return super.shouldExecuteAsync() && !this.isStopped();
    }

    @Override
    public void executeSync(Runnable runnable) {
        if (this.isStopped()) {
            throw new RejectedExecutionException("Server already shutting down");
        } else {
            super.executeSync(runnable);
        }
    }

    @Override
    protected Thread getThread() {
        return this.serverThread;
    }
}
