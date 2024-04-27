package demo;

import top.birthcat.awtnotitlebar.NoTitleBar;

import javax.swing.*;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandles;

import static top.birthcat.awtnotitlebar.NoTitleBar.JAVA_WND_PROC_TYPE;
import static top.birthcat.awtnotitlebar.internal.WindowsConstant.WM_NCCALCSIZE;

public class FeatureDemo {

    public static void main(String[] args) {
        final var frame = new JFrame();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        try {
            NoTitleBar.setWndProcMethod(MethodHandles.lookup()
                    .findStatic(FeatureDemo.class, "javaWndProc", JAVA_WND_PROC_TYPE));
            NoTitleBar.removeIn(frame);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static MemorySegment javaWndProc(long hWnd,int uMsg,MemorySegment wParam,MemorySegment lParam) {
        switch (uMsg) {
            case WM_NCCALCSIZE -> System.out.println("CalcSize.");
        }
        return NoTitleBar.javaWndProc(hWnd,uMsg,wParam,lParam);
    }
}
