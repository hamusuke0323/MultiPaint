package com.hamusuke.paint.client.gui.dialog;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class CreatingCanvasDialog extends JDialog {
    public CreatingCanvasDialog(Frame owner, Consumer<CanvasInformation> consumer) {
        super(owner, "Create Canvas", true);
        JTextField field = new JTextField("New Canvas");
        JSpinner width = new JSpinner(new SpinnerNumberModel(1, 1, 7680, 1));
        ((JSpinner.NumberEditor) width.getEditor()).getFormat().setGroupingUsed(false);
        width.setValue(1920);
        JSpinner height = new JSpinner(new SpinnerNumberModel(1, 1, 4320, 1));
        ((JSpinner.NumberEditor) height.getEditor()).getFormat().setGroupingUsed(false);
        height.setValue(1080);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> this.dispose());

        JButton button = new JButton("Create");
        button.addActionListener(e -> {
            CanvasInformation information = new CanvasInformation(field.getText(), (int) width.getValue(), (int) height.getValue());
            if (isInfoValid(information)) {
                consumer.accept(information);
                this.dispose();
            }
        });

        this.add(field, BorderLayout.NORTH);

        JPanel wh = new JPanel();
        wh.add(width, BorderLayout.WEST);
        wh.add(height, BorderLayout.EAST);
        this.add(wh, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.add(cancel, BorderLayout.WEST);
        buttons.add(button, BorderLayout.EAST);
        this.add(buttons, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private static boolean isInfoValid(CanvasInformation information) {
        return !StringUtils.isBlank(information.title) && information.width > 0 && information.width <= 7680 && information.height > 0 && information.height <= 4320;
    }

    public static final class CanvasInformation {
        private final String title;
        private final int width;
        private final int height;

        private CanvasInformation(String title, int width, int height) {
            this.title = title;
            this.width = width;
            this.height = height;
        }

        public String getTitle() {
            return this.title;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }
    }
}
