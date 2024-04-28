package top.birthcat.notitlebar;

import java.lang.foreign.*;

@SuppressWarnings("preview")
public final class CUtils {


    private CUtils() {
    }

    /*
        Allocate as static field(live with app)
     */
    public static final Arena ARENA = Arena.ofShared();
    public static final Linker LINKER = Linker.nativeLinker();

    public static final ValueLayout.OfInt INT = ValueLayout.JAVA_INT;
    public static final ValueLayout.OfLong LONG = ValueLayout.JAVA_LONG;
    public static final AddressLayout LONG_PTR = ValueLayout.ADDRESS.withTargetLayout(ValueLayout.JAVA_LONG_UNALIGNED);
    public static final AddressLayout INT_PTR = ValueLayout.ADDRESS.withTargetLayout(ValueLayout.JAVA_INT_UNALIGNED);


    public static MemorySegment toLongPtr(long v) {
        return MemorySegment.ofAddress(v);
    }
}
