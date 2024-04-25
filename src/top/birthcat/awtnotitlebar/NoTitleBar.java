package top.birthcat.awtnotitlebar;

import sun.awt.windows.WToolkit;
import sun.awt.windows.WWindowPeer;
import top.birthcat.awtnotitlebar.internal.HitTestHelper;

import java.awt.*;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static top.birthcat.awtnotitlebar.internal.CUtils.*;
import static top.birthcat.awtnotitlebar.internal.WindowsCall.*;
import static top.birthcat.awtnotitlebar.internal.WindowsConstant.*;

@SuppressWarnings("preview")
public class NoTitleBar {

    /*
    This method will make window invisible and minimized.
     */
    public static void removeInWindows(Frame frame) {
        if (!System.getProperty("os.name").contains("Windows")) return;

        final var visible = frame.isVisible();
        // prevent hWnd is null.
        frame.setVisible(true);

        final var state = frame.getState();
        // prevent user see the window change.
        // use minimized icon will show in taskbar.
        frame.setState(Frame.ICONIFIED);

        try {
            // Get hWnd.
            final var peer = (WWindowPeer) WToolkit.targetToPeer(frame);
            final var hWnd = peer.getHWnd();

            replaceWndProc(hWnd);
            extendNonClientArea(hWnd);

            // Update window.
            SetWindowPos.invoke(hWnd, 0, 0, 0, 0, 0, FRESH_FLAGS);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            // reset the state
            frame.setVisible(visible);
            frame.setState(state);
        }
    }

    private static void extendNonClientArea(long hWnd) throws Throwable {
        final var margin = ARENA.allocate(MARGIN);
        margin.setAtIndex(INT, 0, 1);
        DwmExtendFrameIntoClientArea.invoke(hWnd, margin);
    }

    private static MemorySegment originWndProcAddress;

    private static void replaceWndProc(long hWnd) throws Throwable {
        originWndProcAddress = (MemorySegment) GetWindowLongA.invoke(hWnd, GWL_WNDPROC);
        final var myWndProcHandle = MethodHandles.lookup().findStatic(NoTitleBar.class, "myWndProc", MethodType.methodType(MemorySegment.class, long.class, int.class, MemorySegment.class, MemorySegment.class));
        final var myWndStub = LINKER.upcallStub(myWndProcHandle, WndProc, ARENA);
        SetWindowLongA.invoke(hWnd, GWL_WNDPROC, myWndStub);
    }

    private static MemorySegment myWndProc(long hWnd, int uMsg, MemorySegment wParam, MemorySegment lParam) {
        try {
            return switch (uMsg) {
                /* Ignore WM_NCCALCSIZE */
                case WM_NCCALCSIZE -> LRESULT;
                case WM_NCHITTEST -> HitTestHelper.hitTest(hWnd, lParam);
                default -> (MemorySegment) CallWindowProcA.invoke(originWndProcAddress, hWnd, uMsg, wParam, lParam);
            };
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
