package melonystudios.mellowui.mixin.screen;

import melonystudios.mellowui.screen.backport.StatisticsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatsScreen.class)
public class MUIStatsScreenMixin extends Screen {
    @Shadow
    @Final
    protected Screen lastScreen;
    @Shadow
    @Final
    private StatisticsManager stats;

    public MUIStatsScreenMixin(ITextComponent title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    protected void init(CallbackInfo callback) {
        callback.cancel();
        this.minecraft.setScreen(new StatisticsScreen(this.lastScreen, this.stats));
    }
}
