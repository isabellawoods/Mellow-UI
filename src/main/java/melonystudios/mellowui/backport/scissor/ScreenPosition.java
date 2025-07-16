package melonystudios.mellowui.backport.scissor;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record ScreenPosition(int x, int y) {
    public ScreenPosition step(ScreenDirection direction) {
        return switch (direction) {
            case DOWN -> new ScreenPosition(this.x, this.y + 1);
            case UP -> new ScreenPosition(this.x, this.y - 1);
            case LEFT -> new ScreenPosition(this.x - 1, this.y);
            default -> new ScreenPosition(this.x + 1, this.y);
        };
    }

    public int getCoordinate(ScreenAxis axis) {
        switch (axis) {
            case VERTICAL:
                return this.y;
            case HORIZONTAL:
            default:
                return this.x;
        }
    }
}
