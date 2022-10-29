package com.hamusuke.paint.client.gui.window;

import com.hamusuke.paint.client.PaintClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public abstract class Window extends JFrame implements ActionListener, WindowListener {
    protected static final PaintClient client = PaintClient.getInstance();
    protected final SpringLayout springLayout = new SpringLayout();
    private Runnable onDisposed = () -> {
    };

    protected Window(String title) {
        this.setTitle(title);
        this.init();
        this.addWindowListener(this);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    protected void addScalable(Container container, JComponent child, float scaleX, float scaleY, float scaleWidth, float scaleHeight) {
        this.modifyScale(container, child, scaleX, scaleY, scaleWidth, scaleHeight);
        container.add(child);
    }

    protected void modifyScale(Container container, JComponent child, float scaleX, float scaleY, float scaleWidth, float scaleHeight) {
        Spring width = this.springLayout.getConstraint(SpringLayout.WIDTH, container);
        Spring height = this.springLayout.getConstraint(SpringLayout.HEIGHT, container);

        SpringLayout.Constraints constraints = this.springLayout.getConstraints(child);
        constraints.setX(Spring.scale(width, scaleX));
        constraints.setY(Spring.scale(height, scaleY));
        constraints.setWidth(Spring.scale(width, scaleWidth));
        constraints.setHeight(Spring.scale(height, scaleHeight));
    }

    protected abstract void init();

    public void tick() {
    }

    protected void onOpen() {
    }

    protected void onClose() {
        this.dispose();
    }

    protected void onHide() {
    }

    protected void onDisposed() {
        this.onDisposed.run();
    }

    @Override
    public void dispose() {
        this.dispose(() -> {
        });
    }

    public void dispose(Runnable after) {
        this.onDisposed = after;
        super.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.onClose();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        this.onDisposed();
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
        this.onOpen();
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        this.onHide();
    }
}
