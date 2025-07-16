package melonystudios.mellowui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.backport.scissor.ScissorStack;
import melonystudios.mellowui.backport.scissor.ScreenRectangle;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.screen.widget.IconButton;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.shader.ShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;

import java.util.List;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;

@OnlyIn(Dist.CLIENT)
public class RenderComponents extends GuiComponent {
    public static final RenderComponents INSTANCE = new RenderComponents(Minecraft.getInstance());
    private static PanoramaRenderer PANORAMA = new PanoramaRenderer(TitleScreen.CUBE_MAP);
    public static float PANORAMA_PITCH = 10;
    public static final int TOOLTIP_MAX_WIDTH = 200; // tooltip width is 170 in 1.21.1
    public static final int DEFAULT_TAB_WIDTH = 130;
    private final ScissorStack scissorStack = new ScissorStack();
    private final Minecraft minecraft;
    private final PoseStack stack;

    private RenderComponents(Minecraft minecraft, PoseStack stack) {
        this.minecraft = minecraft;
        this.stack = stack;
    }

    private RenderComponents(Minecraft minecraft) {
        this(minecraft, new PoseStack());
    }

    public PoseStack poseStack() {
        return this.stack;
    }

    public void renderBackground(Screen screen, float partialTicks, int vOffset, int width, int height) {
        if (this.minecraft.level == null) {
            this.renderPanorama(partialTicks, width, height, 1);
            this.renderBlurredBackground(partialTicks);
            screen.renderDirtBackground(vOffset);
        } else {
            boolean classifiesAsContainer = this.classifiesAsContainer(screen);
            if (CLIENT_CONFIGS.blurryContainers.get() || !classifiesAsContainer) {
                this.renderBlurredBackground(partialTicks);
            }

            if (CLIENT_CONFIGS.gradientBackground.get() || classifiesAsContainer) {
                this.renderBackgroundTexture(GUITextures.INWORLD_GRADIENT, width, height);
            } else {
                screen.renderDirtBackground(vOffset);
            }
            MinecraftForge.EVENT_BUS.post(new ScreenEvent.BackgroundDrawnEvent(screen, this.stack));
        }
    }

    public boolean classifiesAsContainer(Screen screen) {
        boolean vanillaScreens = screen instanceof AbstractContainerScreen || screen instanceof CommandBlockEditScreen || screen instanceof StructureBlockEditScreen || screen instanceof JigsawBlockEditScreen ||
                screen instanceof SignEditScreen || screen instanceof BookEditScreen;
        boolean moddedScreens = CLIENT_CONFIGS.classifiedAsContainers.get().contains(screen.getClass().getName());
        return vanillaScreens || moddedScreens;
    }

    public void renderBackgroundShaders(float partialTicks) {
        if (ShaderManager.customShaderLoaded()) this.renderBlurredBackground(partialTicks);
    }

    public void renderBlurredBackground(float partialTicks) {
        if (!CLIENT_CONFIGS.backgroundShaders.get()) return;
        RenderSystem.disableDepthTest();
        ShaderManager.processPanoramaShaders(partialTicks);
        this.minecraft.getMainRenderTarget().bindWrite(false);
        RenderSystem.enableDepthTest();
    }

    public void renderPanorama(float partialTicks, int width, int height, float transparency) {
        PANORAMA.render(partialTicks, 1);
        RenderSystem.setShaderColor(1, 1, 1, Mth.ceil(Mth.clamp(transparency, 0, 1)));
        this.renderBackgroundTexture(GUITextures.PANORAMA_OVERLAY, width, height);
    }

    public void replacePanorama(PanoramaRenderer panorama) {
        if (!((InterfaceMethods.PanoramaRendererMethods) PANORAMA).samePanorama(panorama)) PANORAMA = panorama;
    }

    public void renderBackgroundTexture(ResourceLocation backgroundTexture, int width, int height) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, backgroundTexture);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        blit(this.stack, 0, 0, width, height, 0, 0, 16, 128, 16, 128);
        RenderSystem.disableBlend();
    }

    public void renderMenuBackground(int x, int y, int width, int height, float vOffset) {
        this.renderTiledBackground(CLIENT_CONFIGS.defaultBackground.get() ? BACKGROUND_LOCATION : (this.minecraft.level != null ?
                GUITextures.INWORLD_MENU_BACKGROUND : GUITextures.MENU_BACKGROUND), x, y, width, height, vOffset);
    }

    public void renderTiledBackground(ResourceLocation backgroundTexture, int x, int y, int width, int height, float vOffset) {
        this.renderTiledBackground(backgroundTexture, CLIENT_CONFIGS.defaultBackground.get() ? 64 : 255, x, y, width, height, vOffset);
    }

    public void renderTiledBackground(ResourceLocation backgroundTexture, int brightness, int x, int y, int width, int height, float vOffset) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, backgroundTexture);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(brightness / 255F, brightness / 255F, brightness / 255F, 1);
        blit(this.stack, x, y, 0, vOffset, width, height, 32, 32);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
        MinecraftForge.EVENT_BUS.post(new ScreenEvent.BackgroundDrawnEvent(this.minecraft.screen, this.stack));
    }

    public void renderTabHeaderBackground(int x, int y, int width, int height) {
        this.renderTabHeaderBackground(CLIENT_CONFIGS.defaultBackground.get() ? BACKGROUND_LOCATION : GUITextures.TAB_HEADER_BACKGROUND,
                x, y, width, height, CLIENT_CONFIGS.defaultBackground.get() ? 64 : 255);
    }

    public void renderTabHeaderBackground(ResourceLocation backgroundTexture, int x, int y, int width, int height, int brightness) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, backgroundTexture);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(brightness / 255F, brightness / 255F, brightness / 255F, 1);
        blit(this.stack, x, y, 0, 0, width, height, 32, 32);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
    }

    public void renderListBackground(int x, int y, int width, int height, int uOffset, int vOffset, double scrollAmount) {
        this.renderListBackground(CLIENT_CONFIGS.defaultBackground.get() ? BACKGROUND_LOCATION : (this.minecraft.level != null ?
                GUITextures.INWORLD_MENU_LIST_BACKGROUND : GUITextures.MENU_LIST_BACKGROUND), x, y, width, height, uOffset, vOffset,
                CLIENT_CONFIGS.defaultBackground.get() ? 32 : 255, scrollAmount);
    }

    public void renderListBackground(ResourceLocation backgroundTexture, int x, int y, int width, int height, int uOffset, int vOffset, int brightness, double scrollAmount) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, backgroundTexture);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(brightness / 255F, brightness / 255F, brightness / 255F, 1);
        blit(this.stack, x, y, uOffset, (float) (vOffset + scrollAmount), width, height, 32, 32);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
    }

    public void renderListSeparators(OptionsList list, int width, int tabs, int tabWidth) {
        this.renderListSeparators(width, list.getLeft(), list.getBottom(), list.getTop(), tabs, tabWidth);
    }

    // todo: make "tabs" parameter functional ~isa 11-7-25
    public void renderListSeparators(int width, int x, int minY, int maxY, int tabs, int tabWidth) {
        RenderSystem.enableBlend();
        // Header (split into two parts for the tabs)
        int headerOneEnd = tabs == 4 ? width / 2 - tabWidth * 2 : width / 2 - tabWidth / 2 - tabWidth;
        int headerTwoStart = tabs == 4 ? width / 2 + tabWidth * 2 : width / 2 + tabWidth / 2 + tabWidth;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.minecraft.level != null ? GUITextures.INWORLD_HEADER_SEPARATOR : GUITextures.HEADER_SEPARATOR);
        blit(this.stack, x, maxY, 0, 0, headerOneEnd, 2, 32, 2);
        blit(this.stack, headerTwoStart, maxY, 0, 0, width, 2, 32, 2);

        // Footer
        RenderSystem.setShaderTexture(0, this.minecraft.level != null ? GUITextures.INWORLD_FOOTER_SEPARATOR : GUITextures.FOOTER_SEPARATOR);
        blit(this.stack, x, minY, 0, 0, x + width, 2, 32, 2);

        RenderSystem.disableBlend();
    }

    public int fourTabWidth(int width) {
        return width / 2 - DEFAULT_TAB_WIDTH * 2 <= 0 ? 90 : DEFAULT_TAB_WIDTH;
    }

    public int threeTabWidth(int width) {
        return width / 2 - DEFAULT_TAB_WIDTH + 65 <= 0 ? 90 : DEFAULT_TAB_WIDTH;
    }

    public IconButton switchStyle(Button.OnPress onPress, Screen screen, int x, int y) {
        return new IconButton(x, y, 12, 12, GUITextures.SWITCH_STYLE_SET, new TranslatableComponent("button.mellowui.switch_style"),
                onPress, (button, stack, mouseX, mouseY) ->
                this.renderTooltip(screen, button, new TranslatableComponent("button.mellowui.switch_style"), mouseX, mouseY));
    }

    public void enableScissor(int minX, int minY, int maxX, int maxY) {
        this.applyScissor(this.scissorStack.push(new ScreenRectangle(minX, minY, maxX - minX, maxY - minY)));
    }

    public void disableScissor() {
        this.applyScissor(this.scissorStack.pop());
    }

    public boolean containsPointInScissor(int x, int y) {
        return this.scissorStack.containsPoint(x, y);
    }

    private void applyScissor(@Nullable ScreenRectangle rectangle) {
        if (rectangle != null) {
            Window window = Minecraft.getInstance().getWindow();
            int height = window.getHeight();
            double guiScale = window.getGuiScale();
            double startX = (double) rectangle.left() * guiScale;
            double startY = (double) height - (double) rectangle.bottom() * guiScale;
            double endX = (double) rectangle.width() * guiScale;
            double endY = (double) rectangle.height() * guiScale;
            RenderSystem.enableScissor((int) startX, (int) startY, Math.max(0, (int) endX), Math.max(0, (int) endY));
        } else {
            RenderSystem.disableScissor();
        }
    }

    public void renderTooltip(Screen screen, AbstractWidget widget, Component tooltipText, int mouseX, int mouseY) {
        int x = widget.isFocused() && !widget.isMouseOver(mouseX, mouseY) ? widget.x : mouseX;
        int y = widget.isFocused() && !widget.isMouseOver(mouseX, mouseY) ? widget.y : mouseY;
        if (this.containsPointInScissor(mouseX, mouseY) || widget.isFocused()) {
            screen.renderTooltip(this.stack, this.minecraft.font.split(tooltipText, TOOLTIP_MAX_WIDTH), x, y);
        }
    }

    public void renderTooltip(Screen screen, AbstractWidget widget, List<FormattedCharSequence> tooltipText, int mouseX, int mouseY) {
        int x = widget.isFocused() && !widget.isMouseOver(mouseX, mouseY) ? widget.x : mouseX;
        int y = widget.isFocused() && !widget.isMouseOver(mouseX, mouseY) ? widget.y : mouseY;
        if (this.containsPointInScissor(mouseX, mouseY) || widget.isFocused()) {
            screen.renderTooltip(this.stack, tooltipText, x, y);
        }
    }
}
