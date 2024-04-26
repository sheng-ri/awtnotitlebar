package demo;

import top.birthcat.awtnotitlebar.NoTitleBar;

import javax.swing.*;

public class FeatureDemo {

    public static void main(String[] args) {
        final var frame = new JFrame();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        try {
            NoTitleBar.removeIn(frame);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
