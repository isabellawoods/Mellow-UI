package melonystudios.mellowui.mixin.client;

import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.methods.InterfaceMethods.*;
import melonystudios.mellowui.resource.panorama.BobbingPitch;
import melonystudios.mellowui.resource.panorama.ConstantPitch;
import melonystudios.mellowui.resource.panorama.Panoramas;
import melonystudios.mellowui.resource.panorama.PitchOverrider;
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
import java.util.List;

@Mixin(RenderSkybox.class)
public class MUIPanoramaRendererMixin implements PanoramaRendererMethods {
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private RenderSkyboxCube cubeMap;
    @Shadow private float time;
    @Unique private float spin;
    @Unique private float bob;

    @Override
    public boolean differentPanorama(RenderSkybox renderer) {
        ResourceLocation[] panorama1 = ((CubeMapMethods) ((PanoramaRendererMethods) renderer).cubeMap()).getPanoramaTextures();
        ResourceLocation[] panorama2 = ((CubeMapMethods) this.cubeMap).getPanoramaTextures();
        return panorama1 == null || !Arrays.equals(panorama1, panorama2);
    }

    @Override
    @Nullable
    public RenderSkyboxCube cubeMap() {
        return this.cubeMap;
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(float partialTicks, float alpha, CallbackInfo callback) {
        callback.cancel();
        float scrollSpeed = (float) ((double) partialTicks * this.scrollSpeed());
        this.spin = wrap(this.spin + scrollSpeed * 0.1F, 360);
        this.bob = wrap(this.bob + scrollSpeed * 0.001F, (float) (Math.PI * 2)); // "bob" is only used prior to 1.20 to make the panorama, well, bob a little (from -4º to 4º). ~isa 23-3-25
        this.time += partialTicks;
        float bobbingStrength = this.bobbingStrength();
        float pitch = bobbingStrength > 0 ? MathHelper.sin(this.time * bobbingStrength) * 5 + 25 : this.pitch();
        RenderComponents.PANORAMA_PITCH = pitch;
        this.cubeMap.render(this.minecraft, pitch, -this.spin, alpha);
    }

    @Unique
    private float scrollSpeed() {
        Float speedOverride = Panoramas.panorama().speedOverride();
        if (speedOverride != null) return speedOverride;
        return MellowConfigs.CLIENT_CONFIGS.panoramaScrollSpeed.get().floatValue();
    }

    @Unique
    private float bobbingStrength() {
        PitchOverrider overrider = Panoramas.panorama().pitchOverride();
        if (overrider instanceof BobbingPitch) return ((BobbingPitch) overrider).bobbingStrength();
        return MellowConfigs.CLIENT_CONFIGS.panoramaBobbing.get() ? 0.001F : 0;
    }

    @Unique
    private int pitch() {
        PitchOverrider overrider = Panoramas.panorama().pitchOverride();
        if (overrider instanceof ConstantPitch) return ((ConstantPitch) overrider).pitch();
        return MellowConfigs.CLIENT_CONFIGS.panoramaCameraPitch.get();
    }

    @Unique
    private static float wrap(float value, float max) {
        return value > max ? value - max : value;
    }

    @Override
    public int hashCode() {
        ResourceLocation[] cubeMap = ((InterfaceMethods.CubeMapMethods) this.cubeMap()).getPanoramaTextures();
        if (cubeMap == null) return super.hashCode();
        return 31 * cubeMap[0].hashCode() + cubeMap[1].hashCode() + cubeMap[2].hashCode() + cubeMap[3].hashCode() + cubeMap[4].hashCode() + cubeMap[5].hashCode();
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

        @Override
        public void setPanoramaTextures(List<ResourceLocation> textures) {
            for (int i = 0; i < 6; ++i) this.images[i] = MellowUI.toTexturePath(textures.get(i));
        }
    }
}
