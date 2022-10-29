package com.hamusuke.paint.client.gui.component.list;

import com.hamusuke.paint.client.PaintClient;

import javax.swing.*;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.DefaultTableModel;

public class PainterList extends JTable {
    private static final DefaultTableModel MODEL = new DefaultTableModel(new String[]{"painter", "ping"}, 0);
    protected final PaintClient client;

    public PainterList(PaintClient client) {
        super(MODEL);
        this.client = client;
        this.setDragEnabled(false);
        this.setColumnSelectionAllowed(false);
        this.setCellSelectionEnabled(false);
        this.getColumnModel().getColumn(0).setCellRenderer(new PainterListPainterInfoRenderer());
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void update() {
        MODEL.setRowCount(0);
        synchronized (this.client.clientPainters) {
            this.client.clientPainters.stream().filter(p -> p.isInLobby() && this.client.clientPainter.isInLobby()).forEach(painter -> {
                MODEL.addRow(new Object[]{painter, painter.getPing() + "ms"});
            });
        }
    }
}
