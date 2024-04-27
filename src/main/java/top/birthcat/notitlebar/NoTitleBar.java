package top.birthcat.notitlebar;

import sun.awt.windows.WToolkit;
import sun.awt.windows.WWindowPeer;

import java.awt.*;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static top.birthcat.notitlebar.Solution.*;
import static top.birthcat.notitlebar.WindowsCall.*;
import static top.birthcat.notitlebar.WindowsConstant.*;

@SuppressWarnings("preview")
public final class NoTitleBar {

    public static final MethodType JAVA_WND_PROC_TYPE = MethodType.methodType(
            MemorySegment.class, long.class, int.class, MemorySegment.class, MemorySegment.class
    );

    private NoTitleBar() {
    }

    /*
    Using default NoTitleBar::composeJavaWndProc to handle the message.
 */
    @SuppressWarnings("unused")
    public static void workWithCompose() {
        try {
            Solution.setWndProcMethod(MethodHandles.lookup()
                    .findStatic(Solution.class, "composeJavaWndProc", JAVA_WND_PROC_TYPE));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /*
        This method will make window invisible and minimized.
     */
    @SuppressWarnings("unused")
    public static boolean tryRemoveIn(Frame frame) {
        if (!System.getProperty("os.name").contains("Windows")) return false;

        try {
            removeIn(frame);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    /*
       This method will make window invisible and minimized.
    */
    @SuppressWarnings("unused")
    public static void removeIn(Frame frame) {
        if (!System.getProperty("os.name").contains("Windows")) {
            throw new UnsupportedOperationException("This can't call in non windows system.");
        }

        try {
            // Get hWnd.
            final var peer = (WWindowPeer) WToolkit.targetToPeer(frame);
            final var hWnd = peer.getHWnd();

            replaceWndProc(hWnd);
            extendNonClientArea(hWnd);
            setStyleWithCaption(hWnd);

            // Update window.
            SetWindowPos.invoke(hWnd, 0, 0, 0, 0, 0, FRESH_FLAGS);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


}
