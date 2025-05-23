package melonystudios.mellowui.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.updated.MUIModListScreen;
import melonystudios.mellowui.screen.updated.MUIPackScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.PackScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

import static net.minecraft.util.ColorHelper.PackedColor.*;

public class MellowUtils {
    public static RenderSkybox PANORAMA = new RenderSkybox(MainMenuScreen.CUBE_MAP);
    public static final DateFormat WORLD_DATE_FORMAT = new SimpleDateFormat(); // "dd-MM-yyyy '('EEE') - 'HH:mm:ss"
    public static final int TOOLTIP_MAX_WIDTH = 200;
    public static final int DEFAULT_TITLE_HEIGHT = 20;
    public static final int PAUSE_MENU_Y_OFFSET = 0;

    public static Screen modList(Screen lastScreen) {
        if (MellowConfigs.CLIENT_CONFIGS.updateModListMenu.get()) return new MUIModListScreen(lastScreen);
        else return new ModListScreen(lastScreen);
    }

    public static Screen resourcePackList(Screen lastScreen, Minecraft minecraft, Consumer<ResourcePackList> packInfo) {
        ITextComponent title = new TranslationTextComponent("resourcePack.title");
        if (MellowConfigs.CLIENT_CONFIGS.updatePackMenu.get()) return new MUIPackScreen(lastScreen, minecraft.getResourcePackRepository(), packList -> {}, minecraft.getResourcePackDirectory(), title);
        else return new PackScreen(lastScreen, minecraft.getResourcePackRepository(), packInfo, minecraft.getResourcePackDirectory(), title);
    }

    public static void scissor(Runnable toCut, int startX, int endX, int y0, int y1, int height) {
        int guiScale = (int) Minecraft.getInstance().getWindow().getGuiScale();
        int startY = (int) ((double) (height - y1) * guiScale);
        int endY = (int) ((double) (height - (height - y1) - y0 - 2) * guiScale);
        RenderSystem.enableScissor(startX * guiScale, startY, endX * guiScale, endY);
        toCut.run();
        RenderSystem.disableScissor();
    }

    public static void buttonScissor(Runnable toCut, int startX, int endX) {
        int guiScale = (int) Minecraft.getInstance().getWindow().getGuiScale();
        RenderSystem.enableScissor(startX * guiScale, 0, (endX - startX) * guiScale, Minecraft.getInstance().getWindow().getHeight());
        toCut.run();
        RenderSystem.disableScissor();
    }

    public static boolean renderPanorama(MatrixStack stack, int width, int height, float transparency) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            MellowUtils.PANORAMA.render(minecraft.getDeltaFrameTime(), 1);
            minecraft.getTextureManager().bind(GUITextures.PANORAMA_OVERLAY);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.color4f(1, 1, 1, MathHelper.ceil(MathHelper.clamp(transparency, 0, 1)));
            AbstractGui.blit(stack, 0, 0, width, height, 0, 0, 16, 128, 16, 128);
            return true;
        }
        return false;
    }

    public static void renderBackground(MatrixStack stack, int width, int height, float vOffset) {
        Minecraft minecraft = Minecraft.getInstance();
        if (GUICompatUtils.hasCustomBackground(minecraft, stack, width, height, vOffset)) return;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        minecraft.getTextureManager().bind(minecraft.level != null ? GUITextures.INWORLD_MENU_BACKGROUND : GUITextures.MENU_BACKGROUND);
        RenderSystem.enableBlend();
        RenderSystem.color4f(1, 1, 1, 1);
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.vertex(0, height, 0).uv(0, (float) height / 32 + vOffset).color(255, 255, 255, 255).endVertex();
        buffer.vertex(width, height, 0).uv((float) width / 32, (float) height / 32 + vOffset).color(255, 255, 255, 255).endVertex();
        buffer.vertex(width, 0, 0).uv((float) width / 32, vOffset).color(255, 255, 255, 255).endVertex();
        buffer.vertex(0, 0, 0).uv(0, vOffset).color(255, 255, 255, 255).endVertex();
        tessellator.end();
        RenderSystem.disableBlend();
    }

    public static void renderTooltip(MatrixStack stack, Screen screen, Button button, ITextComponent tooltipText, int mouseX, int mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        int x = button.isFocused() ? button.x : mouseX;
        int y = button.isFocused() ? button.y : mouseY;
        screen.renderTooltip(stack, minecraft.font.split(tooltipText, TOOLTIP_MAX_WIDTH), x, y);
    }

    public static int getSelectableTextColor(boolean selected, boolean active) {
        if (MellowConfigs.CLIENT_CONFIGS.legacyButtonColors.get()) {
            return !active ? 0xA0A0A0 : (selected ? 0xFFFFA0 : 0xE0E0E0);
        } else {
            return active ? 0xFFFFFF : 0xA0A0A0;
        }
    }

    public static int getSelectableTextShadowColor(boolean selected, boolean active) {
        int color;
        if (MellowConfigs.CLIENT_CONFIGS.legacyButtonColors.get()) {
            color = !active ? 0xA0A0A0 : (selected ? 0xFFFFA0 : 0xE0E0E0);
        } else {
            color = active ? 0xFFFFFF : 0xA0A0A0;
        }
        return getShadowColor(color, 1);
    }

    public static int getShadowColor(int color, float alpha) {
        float red = red(color) * 0.25F;
        float green = green(color) * 0.25F;
        float blue = blue(color) * 0.25F;
        return color((int) (alpha * 255), (int) red, (int) green, (int) blue);
    }
}
