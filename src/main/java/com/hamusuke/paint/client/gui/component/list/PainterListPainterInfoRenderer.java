package com.hamusuke.paint.client.gui.component.list;

import com.hamusuke.paint.network.Painter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public class PainterListPainterInfoRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        AtomicReference<JLabel> label = new AtomicReference<>();
        label.set((JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column));

        searchByClass(table, row, Painter.class, (painter, integer) -> {
            label.set((JLabel) super.getTableCellRendererComponent(table, painter, isSelected, hasFocus, row, integer));
            label.get().setText(painter.getName());
            label.get().setToolTipText("<html>" + painter.getName() + "<br>" + "UUID: " + painter.getUuid() + "</html>");
        });

        return label.get();
    }

    public static <T> void searchByClass(JTable table, int row, Class<T> target, BiConsumer<T, Integer> biConsumer) {
        TableModel model = table.getModel();
        for (int i = 0; i < model.getColumnCount(); i++) {
            Object o = model.getValueAt(row, i);
            if (target.isInstance(o)) {
                biConsumer.accept(target.cast(o), i);
                break;
            }
        }
    }
}
