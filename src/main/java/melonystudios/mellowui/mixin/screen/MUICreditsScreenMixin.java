package melonystudios.mellowui.mixin.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WinGameScreen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WinGameScreen.class)
public class MUICreditsScreenMixin extends Screen {
    @Shadow
    @Final
    private Runnable onFinished;

    public MUICreditsScreenMixin(ITextComponent title) {
        super(title);
    }

    @Inject(method = "respawn", at = @At("HEAD"), cancellable = true)
    private void respawn(CallbackInfo callback) {
        callback.cancel();
        this.onFinished.run();
        if (this.minecraft.screen == this) this.minecraft.setScreen(null);
    }

    @Inject(method = "onClose", at = @At("TAIL"))
    public void onClose(CallbackInfo callback) {
        this.minecraft.getMusicManager().stopPlaying();
    }
}
