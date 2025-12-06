package melonystudios.mellowui.backport.cursor;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

// no "not allowed" shape unfortunately because this is an old version ~isa 4-12-25
@OnlyIn(Dist.CLIENT)
public class CursorTypes {
    public static final CursorType DEFAULT = new CursorType("default", 0L);
    public static final CursorType ARROW = CursorType.createStandardCursor(221185, "arrow", DEFAULT);
    public static final CursorType IBEAM = CursorType.createStandardCursor(221186, "ibeam", DEFAULT);
    public static final CursorType CROSSHAIR = CursorType.createStandardCursor(221187, "crosshair", DEFAULT);
    public static final CursorType POINTING_HAND = CursorType.createStandardCursor(221188, "pointing_hand", DEFAULT);
    public static final CursorType RESIZE_NS = CursorType.createStandardCursor(221190, "resize_ns", DEFAULT);
    public static final CursorType RESIZE_EW = CursorType.createStandardCursor(221189, "resize_ew", DEFAULT);
}
