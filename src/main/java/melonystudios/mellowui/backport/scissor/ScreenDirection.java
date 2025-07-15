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
        switch (this) {
            case UP: case DOWN: return ScreenAxis.VERTICAL;
            case LEFT: case RIGHT: default: return ScreenAxis.HORIZONTAL;
        }
    }

    public ScreenDirection getOpposite() {
        switch (this) {
            case UP: return DOWN;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            case RIGHT: default: return LEFT;
        }
    }

    public boolean isPositive() {
        switch (this) {
            case UP: case LEFT: return false;
            case DOWN: case RIGHT: default: return true;
        }
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
