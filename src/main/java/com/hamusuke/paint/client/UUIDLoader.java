package com.hamusuke.paint.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.UUID;

public class UUIDLoader {
    private static final String NAME = "clientid";
    private static final Logger LOGGER = LogManager.getLogger();
    private final UUID uuid;

    public UUIDLoader() {
        File file = new File("./" + NAME);
        this.uuid = this.loadOrCreate(file);
    }

    private static UUID create(File file) {
        UUID out = UUID.randomUUID();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(file.toPath()))) {
            objectOutputStream.writeObject(out);
            return out;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private UUID loadOrCreate(File file) {
        if (file.isFile()) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(Files.newInputStream(file.toPath()))) {
                return (UUID) objectInputStream.readObject();
            } catch (Exception e) {
                LOGGER.warn("Error occurred while loading id", e);
                return create(file);
            }
        } else {
            return create(file);
        }
    }

    public UUID getUuid() {
        return this.uuid;
    }
}
