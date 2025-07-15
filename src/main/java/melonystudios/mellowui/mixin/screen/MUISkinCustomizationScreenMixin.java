package melonystudios.mellowui.mixin.screen;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.update.SkinCustomizationScreen;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.CustomizeSkinScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CustomizeSkinScreen.class)
public class MUISkinCustomizationScreenMixin extends SettingsScreen {
    public MUISkinCustomizationScreenMixin(Screen lastScreen, GameSettings options, ITextComponent title) {
        super(lastScreen, options, title);
    }

    @Inject(method = "init", at = @At("TAIL"), cancellable = true)
    protected void init(CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateSkinCustomizationMenu.get() && this.minecraft != null) {
            callback.cancel();
            this.minecraft.setScreen(new SkinCustomizationScreen(this.lastScreen, this.minecraft.options));
        }
    }
}
