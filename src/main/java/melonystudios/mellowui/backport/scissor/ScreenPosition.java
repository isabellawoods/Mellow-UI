package melonystudios.mellowui.backport.scissor;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenPosition {
    private final int x;
    private final int y;

    public ScreenPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public ScreenPosition step(ScreenDirection direction) {
        switch (direction) {
            case DOWN: return new ScreenPosition(this.x, this.y + 1);
            case UP: return new ScreenPosition(this.x, this.y - 1);
            case LEFT: return new ScreenPosition(this.x - 1, this.y);
            case RIGHT: default: return new ScreenPosition(this.x + 1, this.y);
        }
    }

    public int getCoordinate(ScreenAxis axis) {
        switch (axis) {
            case VERTICAL: return this.y;
            case HORIZONTAL: default: return this.x;
        }
    }
}
