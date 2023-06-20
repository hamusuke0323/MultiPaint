package com.hamusuke.paint.server.canvas;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hamusuke.paint.canvas.Canvas;
import com.hamusuke.paint.canvas.CanvasInfo;
import com.hamusuke.paint.network.LineData;
import com.hamusuke.paint.server.PaintServer;
import com.hamusuke.paint.server.network.ServerPainter;
import com.hamusuke.paint.server.network.ServerPainterData;
import com.hamusuke.paint.util.ConcurrentFixedDeque;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.Deque;
import java.util.UUID;

public class ServerCanvas extends Canvas {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final PaintServer server;
    private final File saveDir;
    private final File painterSaveDir;
    private final Deque<BufferedImage> historic = new ConcurrentFixedDeque<>(10);

    public ServerCanvas(PaintServer server, File saveDir, UUID uuid, String title, UUID author, int width, int height) {
        super(uuid, title, author, width, height);
        this.server = server;
        this.saveDir = saveDir;
        if (!this.saveDir.isDirectory() || !this.saveDir.exists()) {
            this.saveDir.mkdir();
        }

        this.painterSaveDir = this.saveDir.toPath().resolve("painters").toFile();
        if (!this.painterSaveDir.isDirectory() || !this.painterSaveDir.exists()) {
            this.painterSaveDir.mkdir();
        }
    }

    @Nullable
    public static ServerCanvas load(PaintServer server, File saveDir) throws Throwable {
        File[] files = saveDir.listFiles(File::isFile);
        if (files != null) {
            CanvasInfo info = null;
            BufferedImage data = null;
            for (File file : files) {
                String name = file.getName();
                switch (name) {
                    case "canvas.dat":
                        info = readCanvasInfo(file);
                    case "canvas.data":
                        data = ImageIO.read(file);
                }
            }

            if (info != null && data != null) {
                ServerCanvas serverCanvas = new ServerCanvas(server, saveDir, info.getCanvasUUID(), info.getTitle(), info.getAuthor(), info.getWidth(), info.getHeight());
                serverCanvas.setData(data);
                return serverCanvas;
            }
        }

        return null;
    }

    private static CanvasInfo readCanvasInfo(File dat) throws Exception {
        try (ObjectInputStream stream = new ObjectInputStream(Files.newInputStream(dat.toPath()))) {
            return (CanvasInfo) stream.readObject();
        }
    }

    @Override
    public void acceptLine(LineData lineData) {
        this.historic.addLast(this.data);
        super.acceptLine(lineData);
    }

    public synchronized void save() {
        if (!this.saveDir.exists()) {
            this.saveDir.mkdirs();
        }

        try (ObjectOutputStream stream = new ObjectOutputStream(Files.newOutputStream(new File(this.saveDir, "canvas.dat").toPath()))) {
            stream.writeObject(this.info);
            stream.flush();
            ImageIO.write(this.data, "png", new File(this.saveDir, "canvas.data"));
        } catch (Exception e) {
            LOGGER.warn("Error occurred while saving the canvas", e);
        }

        this.savePainters();
    }

    private synchronized void savePainters() {
        if (!this.painterSaveDir.exists()) {
            this.painterSaveDir.mkdir();
        }

        this.server.getPainterManager().getPainters().stream().filter(painter -> painter.isInCanvas(this)).forEach(this::savePainter);
    }

    public void savePainter(ServerPainter painter) {
        File data = this.createPainterDataFile(painter);
        try (FileWriter writer = new FileWriter(data)) {
            GSON.toJson(painter.serialize(), writer);
        } catch (Exception e) {
            LOGGER.warn("Error occurred while saving painters", e);
        }
    }

    public void loadPainter(ServerPainter painter) {
        File data = this.createPainterDataFile(painter);

        if (!data.exists()) {
            return;
        }

        try (FileReader fileReader = new FileReader(data)) {
            ServerPainterData painterData = GSON.fromJson(fileReader, ServerPainterData.class);
            if (painterData != null) {
                painter.deserialize(painterData);
            }
        } catch (Exception e) {
            LOGGER.warn("Error occurred while loading painter data", e);
        }
    }

    private File createPainterDataFile(ServerPainter painter) {
        return this.painterSaveDir.toPath().resolve(painter.getUuid().toString() + ".json").toFile();
    }
}
