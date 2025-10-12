package melonystudios.mellowui.util.shader;

import com.google.common.collect.Lists;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.resource.panorama.Panoramas;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static melonystudios.mellowui.util.shader.PostEffects.*;

/// *Mellow UI*'s default **shader manager**, for handling the selection and rendering of panoramic or world shaders.
public class ShaderManager {
    public static PostEffect CURRENT_EFFECT = MUI_BLUR;
    public static List<PostEffect> EFFECTS = Lists.newArrayList(MUI_BLUR, ANTIALIAS, ART, BITS, BLOBS, BLOBS2, BLUR, BUMPY, COLOR_CONVOLVE, CREEPER, DECONVERGE,
            DESATURATE, ENTITY_OUTLINE, FLIP, FXAA, GREEN, INVERT, LOVE, NOTCH, NTSC, OUTLINE, PENCIL, PHOSPHOR, SCAN_PINCUSHION, SOBEL, SPIDER, WOBBLE);
    @Nullable
    public static PostChain PANORAMA_SHADER;
    public static float BLUR_PROGRESS = 0;

    /// @return `true` whether a custom {@link PostEffect} is loaded.
    public static boolean customShaderLoaded() {
        return CURRENT_EFFECT.shaderIdentifier() != -1;
    }

    /// Sets the currently selected {@link PostEffect}.
    /// @param minecraft The *Minecraft* client instance.
    /// @param effectLocation A resource location of a post effect.
    /// @param applyInWorld Whether to apply the shader in-game through the game renderer.
    public static void setPostEffect(Minecraft minecraft, ResourceLocation effectLocation, boolean applyInWorld) {
        setPostEffect(minecraft, EFFECTS.stream().filter(effect -> effect.assetID().toString().equals(effectLocation.toString())).findFirst().orElse(MUI_BLUR), applyInWorld);
    }

    /// Sets the currently selected {@link PostEffect}.
    /// @param minecraft The *Minecraft* client instance.
    /// @param effect The post effect to be selected.
    /// @param applyInWorld Whether to apply the shader in-game through the game renderer.
    public static void setPostEffect(Minecraft minecraft, PostEffect effect, boolean applyInWorld) {
        CURRENT_EFFECT = effect;
        reloadPanoramaShaders(minecraft.getResourceManager(), minecraft);
        if (minecraft.level != null && applyInWorld) minecraft.gameRenderer.loadEffect(CURRENT_EFFECT.getPostEffectFile());
    }

    /// Clears any selected {@link PostEffect PostEffects} from the panorama and the game renderer.
    /// @param minecraft The *Minecraft* client instance.
    public static void clearPostEffect(Minecraft minecraft) {
        CURRENT_EFFECT = MUI_BLUR;
        reloadPanoramaShaders(minecraft.getResourceManager(), minecraft);
        minecraft.gameRenderer.shutdownEffect();
    }

    /// Reloads the {@link PostEffect} on the panorama.
    /// @param resourceManager The default resource manager,
    /// @param minecraft The *Minecraft* client instance.
    public static void reloadPanoramaShaders(ResourceManager resourceManager, Minecraft minecraft) {
        if (PANORAMA_SHADER != null) PANORAMA_SHADER.close();
        ResourceLocation shaderLocation = CURRENT_EFFECT.getPostEffectFile();

        try {
            PANORAMA_SHADER = new PostChain(minecraft.getTextureManager(), resourceManager, minecraft.getMainRenderTarget(), shaderLocation);
            PANORAMA_SHADER.resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
        } catch (IOException exception) {
            MellowUI.logger("ShaderManager").warn(MellowUtils.translate("error.mellowui.load_shader", "Failed to load shader: '%s'", shaderLocation), exception);
        } catch (JsonSyntaxException exception) {
            MellowUI.logger("ShaderManager").warn(MellowUtils.translate("error.mellowui.parse_shader", "Failed to parse shader: '%s'", shaderLocation), exception);
        }
    }

    /// Prepares the currently selected panorama {@linkplain PostEffect shader} to be bound to the {@linkplain Minecraft#mainRenderTarget **main render target**}.
    /// @param partialTicks The partial tick time.
    /// @param fadeIn Whether the shader should fade in (`true`), fade out (`false`) or not fade at all  (`null`).
    /// @apiNote Fading is **not** fully implemented.
    public static void preparePanoramaShaders(float partialTicks, Boolean fadeIn) {
        if (PANORAMA_SHADER == null) return;
        if (CURRENT_EFFECT.shaderIdentifier() == -1 && MellowConfigs.CLIENT_CONFIGS.menuBackgroundBlurriness.get() <= 0) return;

        if (CURRENT_EFFECT.uniforms().isPresent()) {
            for (String uniform : CURRENT_EFFECT.uniforms().get()) {
                // float radius = uniform.equals("Radius") && fadeIn != null ? fadeBackgroundBlurriness(fadeIn) : getUniformValue(uniform);
                ((InterfaceMethods.PostChainMethods) PANORAMA_SHADER).setUniform(uniform, getUniformValue(uniform));
            }
        }
        PoseStack stack = RenderSystem.getModelViewStack();

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.enableTexture();
        stack.pushPose();
        RenderSystem.resetTextureMatrix();
        PANORAMA_SHADER.process(partialTicks);
        stack.popPose();
        RenderSystem.enableTexture();
    }

    /// Gets the value of a uniform based on its name.
    /// @param name The shader uniform name.
    /// @return {@linkplain MellowConfigs#menuBackgroundBlurriness **Menu Background Blur**} if the uniform is `Radius`, or `0` if not.
    private static float getUniformValue(String name) {
        switch (name) {
            case "Radius": {
                Integer blurStrength = Panoramas.panorama().blurStrength();
                if (blurStrength != null) return Mth.clamp(blurStrength, 0, 20);
                return MellowConfigs.CLIENT_CONFIGS.menuBackgroundBlurriness.get();
            }
            default: return 0;
        }
    }

    /// Processes the panorama {@linkplain PostEffect shader} fade in/out.
    /// @param fadeIn Whether the shader should fade in (`true`), fade out (`false`) or not fade at all  (`null`).
    /// @apiNote Fading is **not** fully implemented.
    private static float fadeBackgroundBlurriness(boolean fadeIn) {
        int targetValue = MellowConfigs.CLIENT_CONFIGS.menuBackgroundBlurriness.get();
        float currentValue = BLUR_PROGRESS;
        float shift = 0.08F * targetValue;

        if (fadeIn) {
            BLUR_PROGRESS += shift;
        } else {
            BLUR_PROGRESS -= shift;
        }
        BLUR_PROGRESS = Mth.clamp(BLUR_PROGRESS, 0, targetValue);

        return currentValue == targetValue ? targetValue : Mth.clampedLerp(fadeIn ? targetValue : currentValue, fadeIn ? currentValue : targetValue, BLUR_PROGRESS);
    }

    /// Renders the **end portal** effect onto the screen.
    /// @param stack The default {@link PoseStack} used for rendering.
    /// @param x0 The minimum x-position to render the effect.
    /// @param y0 The minimum y-position to render the effect.
    /// @param x1 The maximum x-position to render the effect.
    /// @param y1 The maximum y-position to render the effect.
    /// @param z The z-position to render the effect, usually set to `-90`.
    public static void fillEndPortal(PoseStack stack, int x0, int y0, int x1, int y1, int z) {
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        Matrix4f matrix4F = stack.last().pose();
        Random random = new Random(31100);

        for (int i = 0; i < 15; ++i) {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            VertexConsumer builder = buffer.getBuffer(RenderType.endPortal());
            float multiplier = 2 / (float) (18 - i);
            float red = (random.nextFloat() * 0.5F + 0.1F) * multiplier;
            float green = (random.nextFloat() * 0.5F + 0.4F) * multiplier;
            float blue = (random.nextFloat() * 0.5F + 0.5F) * multiplier;
            builder.vertex(matrix4F, (float) x0, (float) y0, (float) z).color(red, green, blue, 1).uv(x0, y0).endVertex();
            builder.vertex(matrix4F, (float) x0, (float) y1, (float) z).color(red, green, blue, 1).uv(x0, y1).endVertex();
            builder.vertex(matrix4F, (float) x1, (float) y1, (float) z).color(red, green, blue, 1).uv(x1, y1).endVertex();
            builder.vertex(matrix4F, (float) x1, (float) y0, (float) z).color(red, green, blue, 1).uv(x1, y0).endVertex();
        }
    }

    /// Renders any **render types** onto the screen.
    /// @param stack The default {@link PoseStack} used for rendering.
    /// @param renderType The {@link RenderType} being rendered.
    /// @param x0 The minimum x-position to render the effect.
    /// @param y0 The minimum y-position to render the effect.
    /// @param x1 The maximum x-position to render the effect.
    /// @param y1 The maximum y-position to render the effect.
    /// @param z The z-position to render the effect.
    // this currently doesn't work because shader rendering in 1.16 is hard ~isa 27-4-25
    public static void fillRenderType(PoseStack stack, RenderType renderType, int x0, int y0, int x1, int y1, int z) {
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer builder = buffer.getBuffer(renderType);
        Matrix4f matrix4F = stack.last().pose();
        builder.vertex(matrix4F, (float) x0, (float) y0, (float) z).color(1, 1, 1, 1F).uv(x0, y0).endVertex();
        builder.vertex(matrix4F, (float) x0, (float) y1, (float) z).color(1, 1, 1, 1F).uv(x0, y1).endVertex();
        builder.vertex(matrix4F, (float) x1, (float) y1, (float) z).color(1, 1, 1, 1F).uv(x1, y1).endVertex();
        builder.vertex(matrix4F, (float) x1, (float) y0, (float) z).color(1, 1, 1, 1F).uv(x1, y0).endVertex();
    }
}
