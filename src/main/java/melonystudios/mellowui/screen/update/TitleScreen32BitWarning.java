package melonystudios.mellowui.screen.update;

import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.concurrent.CompletableFuture;

@OnlyIn(Dist.CLIENT)
public record TitleScreen32BitWarning(MultiLineLabel label, int x, int y, CompletableFuture<Boolean> realmsSubscriptionFuture) {
}
