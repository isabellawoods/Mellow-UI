package melonystudios.mellowui.resource.panorama;

import com.google.common.collect.Lists;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.util.shader.ShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/// Utility class for handling {@linkplain Panorama panoramas} added by *Mellow UI*.
public class Panoramas {
    /// Represents an instance of the `mellowui:default` panorama.
    public static final Panorama DEFAULT = Panorama.builder(Lists.newArrayList(
            new ResourceLocation("gui/title/background/panorama_0"),
            new ResourceLocation("gui/title/background/panorama_1"),
            new ResourceLocation("gui/title/background/panorama_2"),
            new ResourceLocation("gui/title/background/panorama_3"),
            new ResourceLocation("gui/title/background/panorama_4"),
            new ResourceLocation("gui/title/background/panorama_5")
    )).overlay(new ResourceLocation("gui/title/background/panorama_overlay")).build();

    /// Represents an instance of the currently selected panorama.
    public static Panorama CURRENT_PANORAMA = null;

    /// Applies a given panorama to the background.
    /// @param newPanorama The panorama to apply.
    /// @param name The name (resource location) of the panorama, used to set the {@linkplain MellowConfigs#selectedPanorama **Selected Panorama**} option.
    public static void selectPanorama(Panorama newPanorama, String name) {
        // saving the selected panorama
        if (!MellowConfigs.CLIENT_CONFIGS.selectedPanorama.get().equals(name)) MellowConfigs.CLIENT_CONFIGS.selectedPanorama.set(name);

        // setting the panorama
        PanoramaRenderer defaultPanorama = RenderComponents.PANORAMA;
        PanoramaRenderer selectedPanorama = newPanorama.panorama();

        // clear the cached panorama instance
        CURRENT_PANORAMA = null;

        if (((InterfaceMethods.PanoramaRendererMethods) defaultPanorama).differentPanorama(selectedPanorama)) {
            RenderComponents.INSTANCE.replacePanorama(selectedPanorama, false);
        }

        // setting the shader
        if (newPanorama.shader() != null) {
            ShaderManager.setPostEffect(Minecraft.getInstance(), newPanorama.shader(), false, false);
        } else {
            ShaderManager.setPostEffect(Minecraft.getInstance(), ResourceLocation.tryParse(MellowConfigs.CLIENT_CONFIGS.selectedEffect.get()), false, true);
        }
    }

    /// @return The **asset id** of the currently selected panorama.
    public static ResourceLocation panoramaLocation() {
        ResourceLocation location = ResourceLocation.tryParse(MellowConfigs.CLIENT_CONFIGS.selectedPanorama.get());
        return location == null ? Panorama.DEFAULT_LOCATION : location;
    }

    /// @return The currently selected panorama.
    public static Panorama panorama() {
        if (CURRENT_PANORAMA == null) CURRENT_PANORAMA = MellowUtils.PANORAMAS.getOrDefault(panoramaLocation(), DEFAULT);
        return CURRENT_PANORAMA;
    }

    /// Gets a panorama cube map texture from the specified panorama.
    /// @param panorama The panorama.
    /// @param index The index of the texture to pick. Can be any number from `0` to `5`.
    public static ResourceLocation cubeMapTexture(Panorama panorama, int index) {
        return MellowUI.toTexturePath(panorama.cubeMap().get(Mth.clamp(index, 0, 5)));
    }

    /// Gets the panorama overlay texture from the specified panorama.
    /// @param panorama The panorama.
    public static ResourceLocation overlayTexture(Panorama panorama) {
        return MellowUI.toTexturePath(panorama.overlayTexture());
    }

    /// Creates a new **Generated** panorama.
    /// @param renderer The panorama renderer it came from.
    /// @param overlay The overlay texture to use on it, taken from the {@linkplain net.minecraft.client.gui.screens.TitleScreen title screen}.
    public static Panorama createGenerated(PanoramaRenderer renderer, ResourceLocation overlay) {
        CubeMap cubeMap = ((InterfaceMethods.PanoramaRendererMethods) renderer).cubeMap();
        ResourceLocation[] textures = ((InterfaceMethods.CubeMapMethods) cubeMap).getPanoramaTextures();
        assert textures != null;
        return Panorama.builder(Lists.newArrayList(textures)).overlay(overlay).build();
    }
}
