package melonystudios.mellowui.backport.scissor;

import it.unimi.dsi.fastutil.ints.IntComparator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ScreenDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    private final IntComparator coordinateValueComparator = (first, second) -> first == second ? 0 : (this.isBefore(first, second) ? -1 : 1);

    public ScreenAxis getAxis() {
        return switch (this) {
            case UP, DOWN -> ScreenAxis.VERTICAL;
            default -> ScreenAxis.HORIZONTAL;
        };
    }

    public ScreenDirection getOpposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            default -> LEFT;
        };
    }

    public boolean isPositive() {
        return switch (this) {
            case UP, LEFT -> false;
            default -> true;
        };
    }

    public boolean isAfter(int first, int second) {
        return this.isPositive() ? first > second : second > first;
    }

    public boolean isBefore(int first, int second) {
        return this.isPositive() ? first < second : second < first;
    }

    public IntComparator coordinateValueComparator() {
        return this.coordinateValueComparator;
    }
}
