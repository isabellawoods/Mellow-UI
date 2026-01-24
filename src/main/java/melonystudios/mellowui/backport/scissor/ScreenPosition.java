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
        return switch (axis) {
            case VERTICAL -> this.y;
            default -> this.x;
        };
    }

    @Override
    public int hashCode() {
        return 31 * this.x() + this.y();
    }
}
