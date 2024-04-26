package top.birthcat.awtnotitlebar.internal;

public class WindowsConstant {

    public static final int SWP_NOMOVE = 0x0002;
    public static final int SWP_NOSIZE = 0x0001;
    public static final int SWP_NOZORDER = 0x0004;
    public static final int SWP_FRAMECHANGED = 0x0020;
    public static final int FRESH_FLAGS = SWP_NOMOVE | SWP_NOSIZE | SWP_NOZORDER | SWP_FRAMECHANGED;

    public static final int GWL_WNDPROC = -4;

    public static final int WM_NCHITTEST = 0x0084;
    public static final int WM_NCCALCSIZE = 0x0083;
}
