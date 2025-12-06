package melonystudios.mellowui.mixin.client;

import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.util.shader.ShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.IResourceManager;
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
    public void reloadPanoramaShader(IResourceManager resourceManager, CallbackInfo callback) {
        ShaderManager.reloadPanoramaShaders(resourceManager, this.minecraft);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void applyCursorShape(float partialTicks, long nanoTime, boolean renderLevel, CallbackInfo callback) {
        RenderComponents.INSTANCE.applyCursor(this.minecraft.getWindow());
        RenderComponents.INSTANCE.requestCursor(CursorTypes.DEFAULT);
        if (this.minecraft.level != null && this.minecraft.screen == null) ShaderManager.fadeBackgroundBlurriness(false);
    }
}
