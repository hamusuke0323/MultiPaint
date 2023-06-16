package com.hamusuke.paint.client.gui.dialog;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ChangeWidthDialog extends JDialog {
    public ChangeWidthDialog(Frame owner, int curWidth, Consumer<Integer> consumer) {
        super(owner, "Change Width", true);

        JTextField width = new JTextField(String.valueOf(curWidth));

        JSlider slider = new JSlider(1, 100, curWidth);
        slider.addChangeListener(e -> {
            width.setText(String.valueOf(slider.getValue()));
        });

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> this.dispose());

        JButton button = new JButton("OK");
        button.addActionListener(e -> {
            consumer.accept(slider.getValue());
            this.dispose();
        });

        this.add(width, BorderLayout.NORTH);
        this.add(slider, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.add(cancel, BorderLayout.WEST);
        buttons.add(button, BorderLayout.EAST);
        this.add(buttons, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
