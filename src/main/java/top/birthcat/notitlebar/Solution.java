package top.birthcat.notitlebar;

import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.WrongMethodTypeException;

import static top.birthcat.notitlebar.CUtils.*;
import static top.birthcat.notitlebar.CUtils.INT;
import static top.birthcat.notitlebar.HitTestHelper.*;
import static top.birthcat.notitlebar.NoTitleBar.JAVA_WND_PROC_TYPE;
import static top.birthcat.notitlebar.WindowsCall.*;
import static top.birthcat.notitlebar.WindowsCall.CallWindowProcA;
import static top.birthcat.notitlebar.WindowsConstant.*;

@SuppressWarnings("preview")
public final class Solution {
    private Solution() {
    }

    private static MethodHandle WndProcMethod;

    /*
        You muse call NoTitleBar::javaWndProc to handle the other message.
     */
    @SuppressWarnings("unused")
    public static void setWndProcMethod(MethodHandle wndProcMethod) {
        if (!wndProcMethod.type().equals(JAVA_WND_PROC_TYPE)) {
            throw new WrongMethodTypeException(STR."Your type: \{wndProcMethod.type()}, Except type: \{JAVA_WND_PROC_TYPE}");
        }
        WndProcMethod = wndProcMethod;
    }

    public static void setStyleWithCaption(long hWnd) throws Throwable {
        final var style = (MemorySegment) GetWindowLongA.invoke(hWnd, GWL_STYLE);
        SetWindowLongA.invoke(hWnd, GWL_STYLE, toLongPtr(style.address() | WS_CAPTION));
    }

    public static void extendNonClientArea(long hWnd) throws Throwable {
        final var margin = ARENA.allocate(MARGIN);
        margin.set(INT, 0, 1);
        DwmExtendFrameIntoClientArea.invoke(hWnd, margin);
    }

    public static MemorySegment originWndProcAddress;

    public static void replaceWndProc(long hWnd) throws Throwable {

        originWndProcAddress = (MemorySegment) GetWindowLongA.invoke(hWnd, GWL_WNDPROC);

        final var javaWndProc = WndProcMethod != null ? WndProcMethod
                : MethodHandles.lookup().findStatic(Solution.class, "javaWndProc", JAVA_WND_PROC_TYPE);
        final var wndProcPtr = LINKER.upcallStub(javaWndProc, WndProc, ARENA);
        SetWindowLongA.invoke(hWnd, GWL_WNDPROC, wndProcPtr);
    }

    /*
        fast path for compose
     */
    public static MemorySegment composeJavaWndProc(long hWnd, int uMsg, MemorySegment wParam, MemorySegment lParam) {
        try {
            return (MemorySegment) switch (uMsg) {
                /* Ignore WM_NCCALCSIZE */
                case WM_NCCALCSIZE -> LRESULT;
                case WM_NCHITTEST -> DefWindowProcA.invoke(hWnd, uMsg, wParam, lParam);
                default -> CallWindowProcA.invoke(originWndProcAddress, hWnd, uMsg, wParam, lParam);
            };
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static MemorySegment javaWndProc(long hWnd, int uMsg, MemorySegment wParam, MemorySegment lParam) {
        try {
            return (MemorySegment) switch (uMsg) {
                /* Ignore WM_NCCALCSIZE */
                case WM_NCCALCSIZE -> LRESULT;
                /* AWT need custom HITTEST */
                case WM_NCHITTEST -> {
                    var point = new HitTestHelper.Point((int) (lParam.address() & 0xffff), (int) (lParam.address() >> 16 & 0xffff));
                    GetWindowRect.invoke(hWnd, RECTPTR);
                    var hitRst = _hitTest(
                            new HitTestHelper.Rect(RECTPTR.getAtIndex(INT, 0),
                                    RECTPTR.getAtIndex(INT, 1),
                                    RECTPTR.getAtIndex(INT, 2),
                                    RECTPTR.getAtIndex(INT, 3)),
                            point
                    );
                    if (hitRst == HTCLIENT) {
                        yield CallWindowProcA.invoke(originWndProcAddress, hWnd, uMsg, wParam, lParam);
                    } else yield hitRst;
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
