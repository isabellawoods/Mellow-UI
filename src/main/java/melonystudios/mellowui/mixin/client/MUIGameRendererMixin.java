package melonystudios.mellowui.mixin.client;

import melonystudios.mellowui.util.shader.ShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static melonystudios.mellowui.util.shader.ShaderManager.PANORAMA_SHADER;

@Mixin(GameRenderer.class)
public class MUIGameRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "close", at = @At("TAIL"))
    public void close(CallbackInfo callback) {
        if (PANORAMA_SHADER != null) PANORAMA_SHADER.close();
    }

    @Inject(method = "resize", at = @At("HEAD"))
    public void resize(int width, int height, CallbackInfo callback) {
        if (PANORAMA_SHADER != null) PANORAMA_SHADER.resize(width, height);
    }

    @Inject(method = "onResourceManagerReload", at = @At("HEAD"))
    public void reloadPanoramaShader(ResourceManager resourceManager, CallbackInfo callback) {
        ShaderManager.reloadPanoramaShaders(resourceManager, this.minecraft);
    }
}
