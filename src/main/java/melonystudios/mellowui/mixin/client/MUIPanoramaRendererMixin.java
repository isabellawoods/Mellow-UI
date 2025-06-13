package melonystudios.mellowui.mixin.client;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSkybox.class)
public class MUIPanoramaRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Final
    private RenderSkyboxCube cubeMap;
    @Unique
    private float spin;
    @Unique
    private float bob;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(float partialTicks, float alpha, CallbackInfo callback) {
        callback.cancel();
        float scrollSpeed = (float) ((double) partialTicks * MellowConfigs.CLIENT_CONFIGS.panoramaScrollSpeed.get());
        this.spin = wrap(this.spin + scrollSpeed * 0.1F, 360);
        this.bob = wrap(this.bob + scrollSpeed * 0.001F, (float) (Math.PI * 2)); // "bob" is only used prior to 1.20 to make the panorama, well, bob a little (from -4º to 4º). ~isa 23-3-25
        float pitch = MellowConfigs.CLIENT_CONFIGS.panoramaBobbing.get() ? MellowConfigs.CLIENT_CONFIGS.panoramaCameraPitch.get() : this.bob;
        MellowUtils.PANORAMA_PITCH = pitch;
        this.cubeMap.render(this.minecraft, pitch, -this.spin, alpha);
    }

    @Unique
    private static float wrap(float f, float f1) {
        return f > f1 ? f - f1 : f;
    }
}
