package demo;

import top.birthcat.notitlebar.NoTitleBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandles;

import static top.birthcat.notitlebar.NoTitleBar.JAVA_WND_PROC_TYPE;
import static top.birthcat.notitlebar.WindowsConstant.WM_NCCALCSIZE;

public class FeatureDemo {

    public static void main(String[] args) {
        final var frame = new JFrame();

        frame.setSize(300,300);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new FlowLayout());

        final var jButton1 = new JButton("Maximize");
        jButton1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
        final var jButton2= new JButton("Minimize");
        jButton2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.setExtendedState(JFrame.ICONIFIED);
            }
        });
        final var pane = frame.getContentPane();
        pane.add(jButton1);
        pane.add(jButton2);

        frame.setVisible(true);

        try {
            NoTitleBar.setWndProcMethod(MethodHandles.lookup()
                    .findStatic(FeatureDemo.class, "javaWndProc", JAVA_WND_PROC_TYPE));
            NoTitleBar.removeIn(frame);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static MemorySegment javaWndProc(long hWnd, int uMsg, MemorySegment wParam, MemorySegment lParam) {
        switch (uMsg) {
            case WM_NCCALCSIZE -> System.out.println("CalcSize.");
        }
        return NoTitleBar.javaWndProc(hWnd, uMsg, wParam, lParam);
    }
}
