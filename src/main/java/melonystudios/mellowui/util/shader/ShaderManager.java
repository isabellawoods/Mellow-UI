package melonystudios.mellowui.util.shader;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static melonystudios.mellowui.util.shader.PostEffects.*;

public class ShaderManager {
    public static PostEffect CURRENT_EFFECT = MUI_BLUR;
    public static List<PostEffect> EFFECTS = Lists.newArrayList(MUI_BLUR, ANTIALIAS, ART, BITS, BLOBS, BLOBS2, BLUR, BUMPY, COLOR_CONVOLVE, CREEPER, DECONVERGE,
            DESATURATE, ENTITY_OUTLINE, FLIP, FXAA, GREEN, INVERT, LOVE, NOTCH, NTSC, OUTLINE, PENCIL, PHOSPHOR, SCAN_PINCUSHION, SOBEL, SPIDER, WOBBLE);
    @Nullable
    public static ShaderGroup PANORAMA_SHADER;
    private static final List<RenderType> END_PORTAL_TYPES = IntStream.range(0, 16).mapToObj(layer -> RenderType.endPortal(layer + 1)).collect(ImmutableList.toImmutableList());

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

    public static void reloadPanoramaShaders(IResourceManager resourceManager, Minecraft minecraft) {
        if (PANORAMA_SHADER != null) PANORAMA_SHADER.close();
        ResourceLocation shaderLocation = CURRENT_EFFECT.getPostEffectFile();

        try {
            PANORAMA_SHADER = new ShaderGroup(minecraft.getTextureManager(), resourceManager, minecraft.getMainRenderTarget(), shaderLocation);
            PANORAMA_SHADER.resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
        } catch (IOException exception) {
            LogManager.getLogger().warn("Failed to load shader: {}", shaderLocation, exception);
        } catch (JsonSyntaxException exception) {
            LogManager.getLogger().warn("Failed to parse shader: {}", shaderLocation, exception);
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

    private static float getUniformValue(String name) {
        switch (name) {
            case "Radius": return MellowConfigs.CLIENT_CONFIGS.menuBackgroundBlurriness.get();
            default: return 0;
        }
    }

    public static void fillEndPortal(MatrixStack stack, int x0, int y0, int x1, int y1, int z) {
        IRenderTypeBuffer buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        Matrix4f matrix4F = stack.last().pose();
        Random random = new Random(31100);

        for (int i = 0; i < 15; ++i) {
            IVertexBuilder builder = buffer.getBuffer(END_PORTAL_TYPES.get(i));
            float multiplier = 2 / (float) (18 - i);
            float red = (random.nextFloat() * 0.5F + 0.1F) * multiplier;
            float green = (random.nextFloat() * 0.5F + 0.4F) * multiplier;
            float blue = (random.nextFloat() * 0.5F + 0.5F) * multiplier;
            builder.vertex(matrix4F, (float) x0, (float) y0, (float) z).color(red, green, blue, 1F).uv(x0, y0).endVertex();
            builder.vertex(matrix4F, (float) x0, (float) y1, (float) z).color(red, green, blue, 1F).uv(x0, y1).endVertex();
            builder.vertex(matrix4F, (float) x1, (float) y1, (float) z).color(red, green, blue, 1F).uv(x1, y1).endVertex();
            builder.vertex(matrix4F, (float) x1, (float) y0, (float) z).color(red, green, blue, 1F).uv(x1, y0).endVertex();
        }
    }

    // this currently doesn't work because shader rendering in 1.16 is hard ~isa 27-4-25
    public static void fillRenderType(MatrixStack stack, RenderType renderType, int x0, int y0, int x1, int y1, int z) {
        IRenderTypeBuffer buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        IVertexBuilder builder = buffer.getBuffer(renderType);
        Matrix4f matrix4F = stack.last().pose();
        builder.vertex(matrix4F, (float) x0, (float) y0, (float) z).color(1, 1, 1, 1F).uv(x0, y0).endVertex();
        builder.vertex(matrix4F, (float) x0, (float) y1, (float) z).color(1, 1, 1, 1F).uv(x0, y1).endVertex();
        builder.vertex(matrix4F, (float) x1, (float) y1, (float) z).color(1, 1, 1, 1F).uv(x1, y1).endVertex();
        builder.vertex(matrix4F, (float) x1, (float) y0, (float) z).color(1, 1, 1, 1F).uv(x1, y0).endVertex();
    }
}
