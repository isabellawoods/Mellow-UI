package melonystudios.mellowui.mixin.client;

import melonystudios.mellowui.config.MellowConfigs;
import net.minecraft.client.renderer.GlDebugTextUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlDebugTextUtils.class)
public class MUIGLDebugTextUtilsMixin {
    @Inject(method = "printDebugLog", at = @At("HEAD"), cancellable = true)
    private static void printDebugLog(int source, int type, int id, int severity, int messageLength, long message, long userParam, CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.logGLErrors.get()) callback.cancel();
    }
}
