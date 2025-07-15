package melonystudios.mellowui.backport.scissor;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ScreenAxis {
    HORIZONTAL,
    VERTICAL;

    public ScreenAxis orthogonal() {
        switch (this) {
            case VERTICAL: return HORIZONTAL;
            case HORIZONTAL: default: return VERTICAL;
        }
    }

    public ScreenDirection getPositive() {
        switch (this) {
            case VERTICAL: return ScreenDirection.DOWN;
            case HORIZONTAL: default: return ScreenDirection.RIGHT;
        }
    }

    public ScreenDirection getNegative() {
        switch (this) {
            case VERTICAL: return ScreenDirection.UP;
            case HORIZONTAL: default: return ScreenDirection.LEFT;
        }
    }

    public ScreenDirection getDirection(boolean isPositive) {
        return isPositive ? this.getPositive() : this.getNegative();
    }
}
