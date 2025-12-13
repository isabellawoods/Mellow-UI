package melonystudios.mellowui.resource.panorama;

import com.google.common.collect.Lists;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.util.shader.ShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// Utility class for handling {@linkplain Panorama panoramas} added by *Mellow UI*.
public class Panoramas {
    /// A list of all screens that have their own panorama defined.
    public static final List<String> MENUS_WITH_DEFINED_PANORAMAS = Lists.newArrayList();

    /// Maps the class name of screens with defined panoramas with their {@linkplain RenderSkybox panorama renderers}.
    public static final Map<String, RenderSkybox> MENU_TO_PANORAMAS = new HashMap<>();

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
    public static final float TRANSPARENCY_SHIFT_PER_TICK = 0.02F;
    private static String lastScreenWithPanorama = "";
    private static float panoramaAlpha = 0;

    /// Applies a given panorama to the background.
    /// @param newPanorama The panorama to apply.
    /// @param name The name (resource location) of the panorama, used to set the {@linkplain MellowConfigs#selectedPanorama **Selected Panorama**} option.
    public static void selectPanorama(Panorama newPanorama, String name) {
        // saving the selected panorama
        if (!MellowConfigs.CLIENT_CONFIGS.selectedPanorama.get().equals(name)) MellowConfigs.CLIENT_CONFIGS.selectedPanorama.set(name);

        // setting the panorama
        RenderSkybox defaultPanorama = RenderComponents.PANORAMA;
        RenderSkybox selectedPanorama = newPanorama.panorama();

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

    /// Renders the currently selected panorama on the background, and another panorama on top if the
    /// {@linkplain #MENUS_WITH_DEFINED_PANORAMAS current screen has one}. This is rendered **before** the overlay and shaders.
    ///
    /// The second panorama is overlaid on top and faded in/out when opening/closing the screen.
    /// @param partialTicks The partial tick time.
    public static void renderSituationalPanorama(float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        String screenName = minecraft.screen != null ? minecraft.screen.getClass().getName() : "";
        RenderSkybox panorama = MENU_TO_PANORAMAS.get(lastScreenWithPanorama);

        if (panoramaAlpha != 1) RenderComponents.PANORAMA.render(partialTicks, 1);
        boolean shouldFadeIn = MENUS_WITH_DEFINED_PANORAMAS.contains(screenName); //&& panorama != null;

        if (shouldFadeIn) {
            panoramaAlpha = MathHelper.clamp(panoramaAlpha + TRANSPARENCY_SHIFT_PER_TICK, 0, 1);
            lastScreenWithPanorama = screenName;
        } else {
            panoramaAlpha = MathHelper.clamp(panoramaAlpha - TRANSPARENCY_SHIFT_PER_TICK, 0, 1);
        }
        if (panorama == null) return;

        // reset alpha if the panorama is the same (or else it spins a bit faster for a second)
        boolean samePanorama = panorama.hashCode() == panorama().panorama().hashCode();
        if (samePanorama) panoramaAlpha = 0;

        // don't try rendering the panorama if it doesn't exist
        if ((shouldFadeIn || panoramaAlpha > 0) && !samePanorama) panorama.render(partialTicks, panoramaAlpha);
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
        return MellowUI.toTexturePath(panorama.cubeMap().get(MathHelper.clamp(index, 0, 5)));
    }

    /// Gets the panorama overlay texture from the specified panorama.
    /// @param panorama The panorama.
    public static ResourceLocation overlayTexture(Panorama panorama) {
        return MellowUI.toTexturePath(panorama.overlayTexture());
    }

    /// Creates a new **Generated** panorama.
    /// @param renderer The panorama renderer it came from.
    /// @param overlay The overlay texture to use on it, taken from the {@linkplain net.minecraft.client.gui.screen.MainMenuScreen title screen}.
    public static Panorama createGenerated(RenderSkybox renderer, ResourceLocation overlay) {
        RenderSkyboxCube cubeMap = ((InterfaceMethods.PanoramaRendererMethods) renderer).cubeMap();
        ResourceLocation[] textures = ((InterfaceMethods.CubeMapMethods) cubeMap).getPanoramaTextures();
        assert textures != null;
        return Panorama.builder(Lists.newArrayList(textures)).overlay(overlay).build();
    }

    /// Gets the panorama renderer of a given panorama based on its id, or `null` if it doesn't exist.
    /// @param panoramaID The id of the panorama.
    @Nullable
    private static RenderSkybox getRenderer(ResourceLocation panoramaID) {
        Panorama panorama = MellowUtils.PANORAMAS.get(panoramaID);
        if (panorama != null) return panorama.panorama();
        return null;
    }
}
