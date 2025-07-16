package melonystudios.mellowui.mixin.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.WidgetConfigs;
import net.minecraft.client.gui.ResourceLoadProgressGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.util.ColorHelper.PackedColor.*;

@Mixin(ResourceLoadProgressGui.class)
public class MUILoadingOverlayMixin {
    @Shadow
    @Final
    @Mutable
    private static int BRAND_BACKGROUND;
    @Shadow
    @Final
    @Mutable
    private static int BRAND_BACKGROUND_NO_ALPHA;

    @Inject(method = "render", at = @At("HEAD"))
    private void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        int brandBackground = 0xFFEF323D;

        if (MellowConfigs.CLIENT_CONFIGS.monochromeLoadingScreen.get()) {
            int color = WidgetConfigs.WIDGET_CONFIGS.monochromeLoadingScreenColor.get();
            BRAND_BACKGROUND = color(255, red(color), green(color), blue(color));
            BRAND_BACKGROUND_NO_ALPHA = color & 16777215;
        } else {
            BRAND_BACKGROUND = brandBackground;
            BRAND_BACKGROUND_NO_ALPHA = brandBackground & 16777215;
        }
    }
}
