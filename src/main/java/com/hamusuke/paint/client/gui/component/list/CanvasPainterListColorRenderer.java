package com.hamusuke.paint.client.gui.component.list;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

public class CanvasPainterListColorRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        AtomicReference<JLabel> label = new AtomicReference<>();
        label.set((JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column));

        PainterListPainterInfoRenderer.searchByClass(table, row, Color.class, (c, integer) -> {
            label.set((JLabel) super.getTableCellRendererComponent(table, c, isSelected, hasFocus, row, integer));
            label.get().setText("");
            label.get().setBackground(c);
            label.get().setToolTipText("R: " + c.getRed() + ", G: " + c.getGreen() + ", B: " + c.getBlue());
        });

        return label.get();
    }
}
