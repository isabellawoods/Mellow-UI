package melonystudios.mellowui.backport.scissor;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A utility class for managing a stack of screen rectangles for scissoring.
 */
@OnlyIn(Dist.CLIENT)
public class ScissorStack {
    private final Deque<ScreenRectangle> stack = new ArrayDeque<>();

    /**
     * Pushes a screen rectangle onto the scissor stack.
     * <p>
     * @return The resulting intersection of the pushed rectangle with the previous top rectangle on the stack, or the pushed rectangle if the stack is empty.
     *
     * @param scissor the screen rectangle to push.
     */
    public ScreenRectangle push(ScreenRectangle scissor) {
        ScreenRectangle rectangle = this.stack.peekLast();
        if (rectangle != null) {
            ScreenRectangle newRectangle = scissor.intersection(rectangle) != null ? scissor.intersection(rectangle) : ScreenRectangle.empty();
            this.stack.addLast(newRectangle);
            return newRectangle;
        } else {
            this.stack.addLast(scissor);
            return scissor;
        }
    }

    @Nullable
    public ScreenRectangle pop() {
        if (this.stack.isEmpty()) {
            throw new IllegalStateException("Scissor stack underflow");
        } else {
            this.stack.removeLast();
            return this.stack.peekLast();
        }
    }

    public boolean containsPoint(int x, int y) {
        return this.stack.isEmpty() || this.stack.peek().containsPoint(x, y);
    }
}
