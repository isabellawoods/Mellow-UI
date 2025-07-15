package melonystudios.mellowui.backport.scissor;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class ScreenRectangle {
    private static final ScreenRectangle EMPTY = new ScreenRectangle(0, 0, 0, 0);
    private final ScreenPosition position;
    private final int width;
    private final int height;

    public ScreenRectangle(ScreenPosition position, int width, int height) {
        this.position = position;
        this.width = width;
        this.height = height;
    }

    public ScreenRectangle(int x, int y, int width, int height) {
        this(new ScreenPosition(x, y), width, height);
    }

    public ScreenPosition position() {
        return this.position;
    }

    public int height() {
        return this.height;
    }

    public int width() {
        return this.width;
    }

    public static ScreenRectangle empty() {
        return EMPTY;
    }

    public static ScreenRectangle of(ScreenAxis axis, int primaryPosition, int secondaryPosition, int primaryLength, int secondaryLength) {
        switch (axis) {
            case VERTICAL: return new ScreenRectangle(secondaryPosition, primaryPosition, secondaryLength, primaryLength);
            case HORIZONTAL: default: return new ScreenRectangle(primaryPosition, secondaryPosition, primaryLength, secondaryLength);
        }
    }

    public ScreenRectangle step(ScreenDirection direction) {
        return new ScreenRectangle(this.position.step(direction), this.width, this.height);
    }

    public int getLength(ScreenAxis axis) {
        return axis == ScreenAxis.HORIZONTAL ? this.width : this.height;
    }

    public int getBoundInDirection(ScreenDirection direction) {
        ScreenAxis axis = direction.getAxis();
        return direction.isPositive() ? this.position.getCoordinate(axis) + this.getLength(axis) - 1 : this.position.getCoordinate(axis);
    }

    public ScreenRectangle getBorder(ScreenDirection direction) {
        int bound = this.getBoundInDirection(direction);
        ScreenAxis axis = direction.getAxis().orthogonal();
        int negBound = this.getBoundInDirection(axis.getNegative());
        int axisLength = this.getLength(axis);
        return of(direction.getAxis(), bound, negBound, 1, axisLength).step(direction);
    }

    public boolean overlaps(ScreenRectangle rectangle) {
        return this.overlapsInAxis(rectangle, ScreenAxis.HORIZONTAL) && this.overlapsInAxis(rectangle, ScreenAxis.VERTICAL);
    }

    public boolean overlapsInAxis(ScreenRectangle rectangle, ScreenAxis axis) {
        int i = this.getBoundInDirection(axis.getNegative());
        int j = rectangle.getBoundInDirection(axis.getNegative());
        int k = this.getBoundInDirection(axis.getPositive());
        int l = rectangle.getBoundInDirection(axis.getPositive());
        return Math.max(i, j) <= Math.min(k, l);
    }

    public int getCenterInAxis(ScreenAxis axis) {
        return (this.getBoundInDirection(axis.getPositive()) + this.getBoundInDirection(axis.getNegative())) / 2;
    }

    @Nullable
    public ScreenRectangle intersection(ScreenRectangle rectangle) {
        int left = Math.max(this.left(), rectangle.left());
        int top = Math.max(this.top(), rectangle.top());
        int right = Math.min(this.right(), rectangle.right());
        int bottom = Math.min(this.bottom(), rectangle.bottom());
        return left < right && top < bottom ? new ScreenRectangle(left, top, right - left, bottom - top) : null;
    }

    public int top() {
        return this.position.y();
    }

    public int bottom() {
        return this.position.y() + this.height;
    }

    public int left() {
        return this.position.x();
    }

    public int right() {
        return this.position.x() + this.width;
    }

    public boolean containsPoint(int x, int y) {
        return x >= this.left() && x < this.right() && y >= this.top() && y < this.bottom();
    }
}
