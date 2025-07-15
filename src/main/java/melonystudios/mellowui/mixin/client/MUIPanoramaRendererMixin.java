package melonystudios.mellowui.mixin.client;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.methods.InterfaceMethods.*;
import melonystudios.mellowui.screen.RenderComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Arrays;

@Mixin(RenderSkybox.class)
public class MUIPanoramaRendererMixin implements PanoramaRendererMethods {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Final
    private RenderSkyboxCube cubeMap;
    @Shadow
    private float time;
    @Unique
    private float spin;
    @Unique
    private float bob;

    @Override
    public boolean samePanorama(RenderSkybox panoramaRenderer) {
        ResourceLocation[] panorama1 = ((CubeMapMethods) ((PanoramaRendererMethods) panoramaRenderer).cubeMap()).getPanoramaTextures();
        ResourceLocation[] panorama2 = ((CubeMapMethods) this.cubeMap).getPanoramaTextures();
        return panorama1 != null && Arrays.equals(panorama1, panorama2);
    }

    @Override
    @Nullable
    public RenderSkyboxCube cubeMap() {
        return this.cubeMap;
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(float partialTicks, float alpha, CallbackInfo callback) {
        callback.cancel();
        float scrollSpeed = (float) ((double) partialTicks * MellowConfigs.CLIENT_CONFIGS.panoramaScrollSpeed.get());
        this.spin = wrap(this.spin + scrollSpeed * 0.1F, 360);
        this.bob = wrap(this.bob + scrollSpeed * 0.001F, (float) (Math.PI * 2)); // "bob" is only used prior to 1.20 to make the panorama, well, bob a little (from -4º to 4º). ~isa 23-3-25
        this.time += partialTicks;
        float pitch = MellowConfigs.CLIENT_CONFIGS.panoramaBobbing.get() ? MathHelper.sin(this.time * 0.001F) * 5 + 25 : MellowConfigs.CLIENT_CONFIGS.panoramaCameraPitch.get();
        RenderComponents.PANORAMA_PITCH = pitch;
        this.cubeMap.render(this.minecraft, pitch, -this.spin, alpha);
    }

    @Unique
    private static float wrap(float value, float max) {
        return value > max ? value - max : value;
    }

    @Mixin(RenderSkyboxCube.class)
    public static class MUICubeMapMixin implements CubeMapMethods {
        @Shadow
        @Final
        private ResourceLocation[] images;

        @Override
        @Nullable
        public ResourceLocation[] getPanoramaTextures() {
            return this.images;
        }
    }
}
