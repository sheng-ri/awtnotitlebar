package top.birthcat.awtnotitlebar.internal;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;

import static top.birthcat.awtnotitlebar.internal.CUtils.*;

@SuppressWarnings({"preview", "OptionalGetWithoutIsPresent"})
public class WindowsCall {


    public static final MethodHandle SetWindowLongA;
    public static final MethodHandle GetWindowLongA;
    public static final MethodHandle CallWindowProcA;
    public static final MethodHandle GetWindowRect;
    public static final MethodHandle DwmExtendFrameIntoClientArea;
    public static final MethodHandle SetWindowPos;

    public static final FunctionDescriptor WndProc = FunctionDescriptor.of(LONG_PTR, LONG, INT, INT_PTR, LONG_PTR);
    public static final MemoryLayout MARGIN = MemoryLayout.structLayout(
            INT, INT, INT, INT
    );
    public static final MemorySegment LRESULT = toLongPtr(0);

    static {
        var user32 = SymbolLookup.libraryLookup("user32.dll", ARENA);
        var dwmApi = SymbolLookup.libraryLookup("dwmapi.dll", ARENA);

        SetWindowLongA = LINKER.downcallHandle(
                user32.find("SetWindowLongPtrA").get(),
                FunctionDescriptor.of(
                        LONG_PTR, LONG, INT, LONG_PTR
                )
        );
        GetWindowLongA = LINKER.downcallHandle(
                user32.find("GetWindowLongPtrA").get(),
                FunctionDescriptor.of(
                        LONG_PTR, LONG, INT
                )
        );
        SetWindowPos = LINKER.downcallHandle(
                user32.find("SetWindowPos").get(),
                FunctionDescriptor.of(
                        INT, LONG, LONG, INT, INT, INT, INT, INT
                )
        );
        CallWindowProcA = LINKER.downcallHandle(
                user32.find("CallWindowProcA").get(),
                FunctionDescriptor.of(
                        LONG_PTR, LONG_PTR, LONG, INT, INT_PTR, LONG_PTR)
        );
        GetWindowRect = LINKER.downcallHandle(
                user32.find("GetWindowRect").get(),
                FunctionDescriptor.of(
                        INT, LONG, LONG_PTR
                )
        );

        DwmExtendFrameIntoClientArea = LINKER.downcallHandle(
                dwmApi.find("DwmExtendFrameIntoClientArea").get(),
                FunctionDescriptor.of(
                        INT, LONG, LONG_PTR
                )
        );
    }
}
