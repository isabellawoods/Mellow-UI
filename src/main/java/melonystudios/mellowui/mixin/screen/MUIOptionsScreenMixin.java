package melonystudios.mellowui.mixin.screen;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.updated.MUIOptionsScreen;
import melonystudios.mellowui.screen.updated.MUISkinCustomizationScreen;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.CustomizeSkinScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public class MUIOptionsScreenMixin extends Screen {
    @Shadow
    @Final
    private Screen lastScreen;

    public MUIOptionsScreenMixin(ITextComponent title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    protected void init(CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateOptionsMenu.get() && this.minecraft != null) {
            this.minecraft.setScreen(new MUIOptionsScreen(this.lastScreen, this.minecraft.options));
        }
    }
}
