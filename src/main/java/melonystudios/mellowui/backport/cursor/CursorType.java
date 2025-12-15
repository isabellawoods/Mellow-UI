package melonystudios.mellowui.backport.cursor;

import com.mojang.blaze3d.platform.Window;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public record CursorType(String name, long handle) {
    public void select(Window window) {
        GLFW.glfwSetCursor(window.getWindow(), this.handle);
    }

    @Override
    @Nonnull
    public String toString() {
        return this.name;
    }

    public static CursorType createStandardCursor(int id, String name, CursorType fallback) {
        long standardCursor = GLFW.glfwCreateStandardCursor(id);
        return standardCursor == 0L ? fallback : new CursorType(name, standardCursor);
    }
}
