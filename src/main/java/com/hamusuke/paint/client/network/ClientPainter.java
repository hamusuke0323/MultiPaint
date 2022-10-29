package com.hamusuke.paint.client.network;

import com.hamusuke.paint.client.canvas.ClientCanvas;
import com.hamusuke.paint.network.Painter;

import javax.annotation.Nullable;
import java.util.UUID;

public class ClientPainter extends Painter {
    public ClientPainter(UUID uuid) {
        super(uuid);
    }

    @Nullable
    @Override
    public ClientCanvas getCurrentCanvas() {
        return (ClientCanvas) this.currentCanvas;
    }
}
