package melonystudios.mellowui.backport.cursor;

import net.minecraft.client.MainWindow;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class CursorType {
    private final String name;
    private final long handle;

    public CursorType(String name, long handle) {
        this.name = name;
        this.handle = handle;
    }

    public void select(MainWindow window) {
        GLFW.glfwSetCursor(window.getWindow(), this.handle);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static CursorType createStandardCursor(int id, String name, CursorType fallback) {
        long standardCursor = GLFW.glfwCreateStandardCursor(id);
        return standardCursor == 0L ? fallback : new CursorType(name, standardCursor);
    }
}
