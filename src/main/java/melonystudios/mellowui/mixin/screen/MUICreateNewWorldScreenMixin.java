package melonystudios.mellowui.mixin.screen;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.backport.CreateNewWorldScreen;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public class MUICreateNewWorldScreenMixin extends Screen {
    @Shadow
    @Final
    private Screen lastScreen;

    public MUICreateNewWorldScreenMixin(ITextComponent title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    protected void init(CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateCreateNewWorldMenu.get() && this.minecraft != null) {
            callback.cancel();
            CreateNewWorldScreen.openFresh(this.minecraft, this.lastScreen);
        }
    }
}
