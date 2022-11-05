package com.hamusuke.paint.client.gui.component.list;

import com.hamusuke.paint.client.PaintClient;

import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CanvasPainterList extends PainterList {
    private static final DefaultTableModel MODEL = new DefaultTableModel(new String[]{"color", "painter", "ping"}, 0);

    public CanvasPainterList(PaintClient client) {
        super(client);
        this.setModel(MODEL);
        this.getColumnModel().getColumn(0).setCellRenderer(new CanvasPainterListColorRenderer());
        this.getColumnModel().getColumn(1).setCellRenderer(new PainterListPainterInfoRenderer());
    }

    @Override
    public void update() {
        MODEL.setRowCount(0);
        synchronized (this.client.clientPainters) {
            this.client.clientPainters.stream().filter(p -> p.isInCanvas(this.client.getCurrentCanvas())).forEach(painter -> {
                MODEL.addRow(new Object[]{new Color(0, 0, 0), painter, painter.getPing() + "ms"});
            });
        }
    }
}
