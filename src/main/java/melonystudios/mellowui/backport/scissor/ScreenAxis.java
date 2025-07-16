package melonystudios.mellowui.backport.scissor;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ScreenAxis {
    HORIZONTAL,
    VERTICAL;

    public ScreenAxis orthogonal() {
        return switch (this) {
            case VERTICAL -> HORIZONTAL;
            default -> VERTICAL;
        };
    }

    public ScreenDirection getPositive() {
        return switch (this) {
            case VERTICAL -> ScreenDirection.DOWN;
            default -> ScreenDirection.RIGHT;
        };
    }

    public ScreenDirection getNegative() {
        return switch (this) {
            case VERTICAL -> ScreenDirection.UP;
            default -> ScreenDirection.LEFT;
        };
    }

    public ScreenDirection getDirection(boolean isPositive) {
        return isPositive ? this.getPositive() : this.getNegative();
    }
}
