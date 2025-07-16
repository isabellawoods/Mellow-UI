package melonystudios.mellowui.mixin.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.WidgetConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.IntSupplier;

@Mixin(LoadingOverlay.class)
public class MUILoadingOverlayMixin {
    @Shadow
    @Final
    @Mutable
    private static int LOGO_BACKGROUND_COLOR;
    @Shadow
    @Final
    @Mutable
    private static IntSupplier BRAND_BACKGROUND;

    @Inject(method = "render", at = @At("HEAD"))
    private void render(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        BRAND_BACKGROUND = () -> Minecraft.getInstance().options.darkMojangStudiosBackground ? WidgetConfigs.WIDGET_CONFIGS.monochromeLoadingScreenColor.get() : LOGO_BACKGROUND_COLOR;
    }
}
