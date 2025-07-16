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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static melonystudios.mellowui.util.shader.PostEffects.*;

public class ShaderManager {
    public static PostEffect CURRENT_EFFECT = MUI_BLUR;
    public static List<PostEffect> EFFECTS = Lists.newArrayList(MUI_BLUR, ANTIALIAS, ART, BITS, BLOBS, BLOBS2, BLUR, BUMPY, COLOR_CONVOLVE, CREEPER, DECONVERGE,
            DESATURATE, ENTITY_OUTLINE, FLIP, FXAA, GREEN, INVERT, LOVE, NOTCH, NTSC, OUTLINE, PENCIL, PHOSPHOR, SCAN_PINCUSHION, SOBEL, SPIDER, WOBBLE);
    @Nullable
    public static PostChain PANORAMA_SHADER;

    public static boolean customShaderLoaded() {
        return CURRENT_EFFECT.shaderIdentifier() != -1;
    }

    public static void setPostEffect(Minecraft minecraft, PostEffect effect) {
        CURRENT_EFFECT = effect;
        reloadPanoramaShaders(minecraft.getResourceManager(), minecraft);
        if (minecraft.level != null) minecraft.gameRenderer.loadEffect(CURRENT_EFFECT.getPostEffectFile());
    }

    public static void clearPostEffect(Minecraft minecraft) {
        CURRENT_EFFECT = MUI_BLUR;
        reloadPanoramaShaders(minecraft.getResourceManager(), minecraft);
        minecraft.gameRenderer.shutdownEffect();
    }

    public static void reloadPanoramaShaders(ResourceManager resourceManager, Minecraft minecraft) {
        if (PANORAMA_SHADER != null) PANORAMA_SHADER.close();
        ResourceLocation shaderLocation = CURRENT_EFFECT.getPostEffectFile();

        try {
            PANORAMA_SHADER = new PostChain(minecraft.getTextureManager(), resourceManager, minecraft.getMainRenderTarget(), shaderLocation);
            PANORAMA_SHADER.resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
        } catch (IOException exception) {
            MellowUI.LOGGER.warn(new TranslatableComponent("error.mellowui.load_shader", shaderLocation).getString(), exception);
        } catch (JsonSyntaxException exception) {
            MellowUI.LOGGER.warn(new TranslatableComponent("error.mellowui.parse_shader", shaderLocation).getString(), exception);
        }
    }

    public static void processPanoramaShaders(float partialTicks) {
        if (PANORAMA_SHADER == null) return;
        if (CURRENT_EFFECT.shaderIdentifier() == -1 && MellowConfigs.CLIENT_CONFIGS.menuBackgroundBlurriness.get() <= 0) return;

        if (CURRENT_EFFECT.uniforms().isPresent()) {
            for (String uniform : CURRENT_EFFECT.uniforms().get()) {
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

    private static float getUniformValue(String name) {
        return switch (name) {
            case "Radius" -> MellowConfigs.CLIENT_CONFIGS.menuBackgroundBlurriness.get();
            default -> 0;
        };
    }

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
