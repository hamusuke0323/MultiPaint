package com.hamusuke.paint.command;

import com.hamusuke.paint.server.PaintServer;
import com.hamusuke.paint.server.network.ServerPainter;

import javax.annotation.Nullable;

public interface CommandSource {
    PaintServer getServer();

    @Nullable
    ServerPainter getSender();
}
