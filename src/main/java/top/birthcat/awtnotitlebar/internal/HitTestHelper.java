package top.birthcat.awtnotitlebar.internal;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;

import static top.birthcat.awtnotitlebar.internal.CUtils.*;

@SuppressWarnings("preview")
public final class HitTestHelper {

    private HitTestHelper() {
    }

    private static final MemorySegment HTTOP = toLongPtr(12);
    private static final MemorySegment HTTOPLEFT = toLongPtr(13);
    private static final MemorySegment HTTOPRIGHT = toLongPtr(14);

    private static final MemorySegment HTLEFT = toLongPtr(10);
    private static final MemorySegment HTNOWHERE = toLongPtr(0);
    private static final MemorySegment HTRIGHT = toLongPtr(11);


    private static final MemorySegment HTBOTTOMLEFT = toLongPtr(16);
    private static final MemorySegment HTBOTTOM = toLongPtr(15);
    private static final MemorySegment HTBOTTOMRIGHT = toLongPtr(17);


    private static final MemorySegment[][] hitTests = new MemorySegment[][]{
            new MemorySegment[]{HTTOPLEFT, HTTOP, HTTOPRIGHT},
            new MemorySegment[]{HTLEFT, HTNOWHERE, HTRIGHT},
            new MemorySegment[]{HTBOTTOMLEFT, HTBOTTOM, HTBOTTOMRIGHT},
    };

    private static final int TopHeight = 6;
    private static final int BottomHeight = 6;
    private static final int LeftWidth = 6;
    private static final int RightWidth = 6;

    public static final MemoryLayout RECT = MemoryLayout.structLayout(
            INT, INT, INT, INT
    );
    public static final MemorySegment RECTPTR = ARENA.allocate(RECT);

    public record Rect(int left, int top, int right, int bottom) {
    }

    public record Point(int x, int y) {
    }

    public static MemorySegment _hitTest(Rect rcWindow, Point ptMouse) {
        // Determine if the hit test is for resizing. Default middle (1,1).
        int uRow = 1;
        int uCol = 1;

        // Determine if the point is at the top or bottom of the window.
        if (ptMouse.y >= rcWindow.top && ptMouse.y < rcWindow.top + TopHeight) {
            uRow = 0;
        } else if (ptMouse.y < rcWindow.bottom && ptMouse.y >= rcWindow.bottom - BottomHeight) {
            uRow = 2;
        }

        // Determine if the point is at the left or right of the window.
        if (ptMouse.x >= rcWindow.left && ptMouse.x < rcWindow.left + LeftWidth) {
            uCol = 0; // left side
        } else if (ptMouse.x < rcWindow.right && ptMouse.x >= rcWindow.right - RightWidth) {
            uCol = 2; // right side
        }

        return hitTests[uRow][uCol];
    }
}
