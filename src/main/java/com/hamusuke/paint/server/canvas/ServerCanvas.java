package com.hamusuke.paint.server.canvas;

import com.hamusuke.paint.canvas.Canvas;
import com.hamusuke.paint.canvas.CanvasInfo;
import com.hamusuke.paint.network.LineData;
import com.hamusuke.paint.util.ConcurrentFixedDeque;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.Deque;
import java.util.UUID;

public class ServerCanvas extends Canvas {
    private static final Logger LOGGER = LogManager.getLogger();
    private final File saveDir;
    private final Deque<BufferedImage> historic = new ConcurrentFixedDeque<>(10);

    public ServerCanvas(File saveDir, UUID uuid, String title, UUID author, int width, int height) {
        super(uuid, title, author, width, height);
        this.saveDir = saveDir;
        if (!this.saveDir.exists()) {
            this.saveDir.mkdir();
        }
    }

    @Nullable
    public static ServerCanvas load(File saveDir) throws Throwable {
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
                ServerCanvas serverCanvas = new ServerCanvas(saveDir, info.getCanvasUUID(), info.getTitle(), info.getAuthor(), info.getWidth(), info.getHeight());
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
            this.saveDir.mkdir();
        }

        try (ObjectOutputStream stream = new ObjectOutputStream(Files.newOutputStream(new File(this.saveDir, "canvas.dat").toPath()))) {
            stream.writeObject(this.info);
            stream.flush();
            ImageIO.write(this.data, "png", new File(this.saveDir, "canvas.data"));
        } catch (Exception e) {
            LOGGER.warn("Error occurred while saving the canvas", e);
        }
    }
}
