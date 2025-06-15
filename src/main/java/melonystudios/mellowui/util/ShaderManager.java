package melonystudios.mellowui.util;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.Map;

import static melonystudios.mellowui.util.MellowUtils.PANORAMA_SHADER;

public class ShaderManager {
    public static final ResourceLocation BLUR_SHADERS = MellowUI.mellowUI("shaders/post/blur.json");
    public static final Map<Integer, ResourceLocation> VANILLA_SHADERS = ImmutableMap.<Integer, ResourceLocation>builder().put(0, shader("antialias")).put(1, shader("art")).put(2, shader("bits")).put(3, shader("blobs"))
            .put(4, shader("blobs2")).put(5, shader("blur")).put(6, shader("bumpy")).put(7, shader("color_convolve")).put(8, shader("creeper")).put(9, shader("deconverge")).put(10, shader("desaturate"))
            .put(11, shader("entity_outline")).put(12, shader("flip")).put(13, shader("fxaa")).put(14, shader("green")).put(15, shader("invert")).put(16, shader("notch")).put(17, shader("ntsc"))
            .put(18, shader("outline")).put(19, shader("pencil")).put(20, shader("phosphor")).put(21, shader("scan_pincushion")).put(22, shader("sobel")).put(23, shader("spider")).put(24, shader("wobble")).build();
    public static int SHADER_ID = -1;

    private static ResourceLocation shader(String name) {
        return new ResourceLocation("shaders/post/" + name + ".json");
    }

    public static boolean customShaderLoaded() {
        return !GUITextures.PANORAMA_SHADERS.toString().equals(BLUR_SHADERS.toString());
    }

    public static void cycleShader(Minecraft minecraft) {
        SHADER_ID += 1;
        if (SHADER_ID > 24) SHADER_ID = 0;
        GUITextures.PANORAMA_SHADERS = VANILLA_SHADERS.get(SHADER_ID);
        reloadPanoramaShader(minecraft.getResourceManager(), minecraft);
        if (minecraft.level != null) minecraft.gameRenderer.loadEffect(VANILLA_SHADERS.get(SHADER_ID));
    }

    public static void resetShaders(Minecraft minecraft) {
        GUITextures.PANORAMA_SHADERS = BLUR_SHADERS;
        reloadPanoramaShader(minecraft.getResourceManager(), minecraft);
        SHADER_ID = -1;
        minecraft.gameRenderer.shutdownEffect();
    }

    public static void reloadPanoramaShader(IResourceManager resourceManager, Minecraft minecraft) {
        if (PANORAMA_SHADER != null) PANORAMA_SHADER.close();

        try {
            PANORAMA_SHADER = new ShaderGroup(minecraft.getTextureManager(), resourceManager, minecraft.getMainRenderTarget(), GUITextures.PANORAMA_SHADERS);
            PANORAMA_SHADER.resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
        } catch (IOException exception) {
            LogManager.getLogger().warn("Failed to load shader: {}", GUITextures.PANORAMA_SHADERS, exception);
        } catch (JsonSyntaxException exception) {
            LogManager.getLogger().warn("Failed to parse shader: {}", GUITextures.PANORAMA_SHADERS, exception);
        }
    }

    public static void processPanoramaShader(float partialTicks) {
        float menuBackgroundBlur = MellowConfigs.CLIENT_CONFIGS.menuBackgroundBlurriness.get();
        if (PANORAMA_SHADER != null && menuBackgroundBlur >= 1) {
            ((InterfaceMethods.PostChainMethods) PANORAMA_SHADER).setUniform("Radius", menuBackgroundBlur);
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.disableAlphaTest();
            RenderSystem.enableTexture();
            RenderSystem.matrixMode(5890);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            PANORAMA_SHADER.process(partialTicks);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(5888);
            RenderSystem.enableTexture();
        }
    }
}
