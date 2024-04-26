package top.birthcat.awtnotitlebar;

import sun.awt.windows.WToolkit;
import sun.awt.windows.WWindowPeer;
import top.birthcat.awtnotitlebar.internal.HitTestHelper;

import javax.naming.OperationNotSupportedException;
import java.awt.*;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static top.birthcat.awtnotitlebar.internal.CUtils.*;
import static top.birthcat.awtnotitlebar.internal.HitTestHelper.RECTPTR;
import static top.birthcat.awtnotitlebar.internal.HitTestHelper._hitTest;
import static top.birthcat.awtnotitlebar.internal.WindowsCall.*;
import static top.birthcat.awtnotitlebar.internal.WindowsConstant.*;

@SuppressWarnings("preview")
public class NoTitleBar {

    private static final MethodType JAVA_WND_PROC_TYPE = MethodType.methodType(MemorySegment.class, long.class, int.class, MemorySegment.class, MemorySegment.class);
    /* Prepare for wrapper JavaWndPorc */
    public static Class<?> wndProcClass = NoTitleBar.class;

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
    public static void removeIn(Frame frame) throws Throwable {
        if (!System.getProperty("os.name").contains("Windows")) {
            throw new OperationNotSupportedException("This can't call in non windows system.");
        }

        try {
            final var visible = frame.isVisible();
            // prevent hWnd is null.
            frame.setVisible(true);

            final var state = frame.getState();
            // prevent user see the window change.
            // use minimized icon will show in taskbar.
            frame.setState(Frame.ICONIFIED);

            // Get hWnd.
            final var peer = (WWindowPeer) WToolkit.targetToPeer(frame);
            final var hWnd = peer.getHWnd();

            replaceWndProc(hWnd);
            extendNonClientArea(hWnd);

            // Update window.
            SetWindowPos.invoke(hWnd, 0, 0, 0, 0, 0, FRESH_FLAGS);

            // reset the state
            frame.setVisible(visible);
            frame.setState(state);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static void extendNonClientArea(long hWnd) throws Throwable {
        final var margin = ARENA.allocate(MARGIN);
        margin.set(INT,0,1);
        DwmExtendFrameIntoClientArea.invoke(hWnd, margin);
    }

    private static MemorySegment originWndProcAddress;

    private static void replaceWndProc(long hWnd) throws Throwable {

        originWndProcAddress = (MemorySegment) GetWindowLongA.invoke(hWnd, GWL_WNDPROC);

        final var javaWndProc = MethodHandles.lookup().findStatic(wndProcClass, "javaWndProc", JAVA_WND_PROC_TYPE);
        final var wndProcPtr = LINKER.upcallStub(javaWndProc, WndProc, ARENA);
        SetWindowLongA.invoke(hWnd, GWL_WNDPROC, wndProcPtr);
    }

    public static MemorySegment javaWndProc(long hWnd, int uMsg, MemorySegment wParam, MemorySegment lParam) {
        try {
            return (MemorySegment) switch (uMsg) {
                /* Ignore WM_NCCALCSIZE */
                case WM_NCCALCSIZE -> LRESULT;
                case WM_NCHITTEST -> {
                    var point = new HitTestHelper.Point((int) (lParam.address() & 0xffff), (int) (lParam.address() >> 16 & 0xffff));
                    GetWindowRect.invoke(hWnd, RECTPTR);
                    yield _hitTest(
                            new HitTestHelper.Rect(RECTPTR.getAtIndex(INT, 0),
                                    RECTPTR.getAtIndex(INT, 1),
                                    RECTPTR.getAtIndex(INT, 2),
                                    RECTPTR.getAtIndex(INT, 3)),
                            point
                    );
                }
                default -> CallWindowProcA.invoke(originWndProcAddress, hWnd, uMsg, wParam, lParam);
            };
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
