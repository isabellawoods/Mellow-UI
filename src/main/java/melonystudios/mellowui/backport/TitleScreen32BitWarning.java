package melonystudios.mellowui.backport;

import melonystudios.mellowui.element.text.MultiLineLabel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.concurrent.CompletableFuture;

@OnlyIn(Dist.CLIENT)
public class TitleScreen32BitWarning {
    private final MultiLineLabel label;
    private final int x;
    private final int y;
    private final CompletableFuture<Boolean> realmsSubscriptionFuture;

    public TitleScreen32BitWarning(MultiLineLabel label, int x, int y, CompletableFuture<Boolean> realmsSubscriptionFuture) {
        this.label = label;
        this.x = x;
        this.y = y;
        this.realmsSubscriptionFuture = realmsSubscriptionFuture;
    }

    public MultiLineLabel label() {
        return this.label;
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public CompletableFuture<Boolean> realmsSubscriptionFuture() {
        return this.realmsSubscriptionFuture;
    }
}
