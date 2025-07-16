package melonystudios.mellowui.mixin.screen;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.update.MUISkinCustomizationScreen;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SkinCustomizationScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkinCustomizationScreen.class)
public class MUISkinCustomizationScreenMixin extends OptionsSubScreen {
    public MUISkinCustomizationScreenMixin(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title);
    }

    @Inject(method = "init", at = @At("TAIL"), cancellable = true)
    protected void init(CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateSkinCustomizationMenu.get() && this.minecraft != null) {
            callback.cancel();
            this.minecraft.setScreen(new MUISkinCustomizationScreen(this.lastScreen, this.minecraft.options));
        }
    }
}
