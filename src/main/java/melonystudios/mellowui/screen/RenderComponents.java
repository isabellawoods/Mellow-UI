package melonystudios.mellowui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.backport.scissor.ScissorStack;
import melonystudios.mellowui.backport.scissor.ScreenRectangle;
import melonystudios.mellowui.resource.panorama.Panoramas;
import melonystudios.mellowui.screen.widget.IconButton;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.util.shader.ShaderManager;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.versions.forge.ForgeVersion;

import javax.annotation.Nullable;

import java.util.List;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;

/// The global ***Render Components*** used by *Mellow UI*.
/// Contains almost every rendering method used more than once throughout the codebase.
@OnlyIn(Dist.CLIENT)
@SuppressWarnings("deprecation")
public class RenderComponents extends AbstractGui {
    /// The default instance of *Mellow UI*'s ***Render Components***.
    public static final RenderComponents INSTANCE = new RenderComponents(Minecraft.getInstance());
    /// The panorama that's currently being rendered by *Mellow UI*.
    public static RenderSkybox PANORAMA = new RenderSkybox(MainMenuScreen.CUBE_MAP);
    public static float PANORAMA_PITCH = 10;
    public static final int TOOLTIP_MAX_WIDTH = 200; // tooltip width is 170 in 1.21.1
    public static final int DEFAULT_TAB_WIDTH = 130;
    public static final int DEFAULT_BACKGROUND_BRIGHTNESS = 255;
    public static final int OLD_BACKGROUND_BRIGHTNESS = 64;
    public static final int OLD_LIST_BACKGROUND_BRIGHTNESS = 32;
    public static final int DEFAULT_TEXTURE_WIDTH = 32;
    private final ScissorStack scissorStack = new ScissorStack();
    private final Minecraft minecraft;
    private final MatrixStack stack;

    /// The global ***Render Components*** used by *Mellow UI*.
    /// Contains almost every rendering method used more than once throughout the codebase.
    /// @param minecraft The *Minecraft* instance class.
    /// @param stack The default {@link MatrixStack} used for rendering.
    private RenderComponents(Minecraft minecraft, MatrixStack stack) {
        this.minecraft = minecraft;
        this.stack = stack;
    }

    /// The global **Render Components** used by *Mellow UI*.
    /// Contains almost every rendering method used more than once throughout the codebase.
    /// @param minecraft The *Minecraft* instance class.
    private RenderComponents(Minecraft minecraft) {
        this(minecraft, new MatrixStack());
    }

    /// @return The {@link MatrixStack} used by this instance of the *Render Components*.
    public MatrixStack matrixStack() {
        return this.stack;
    }

    /// Renders the global **background** of a screen.
    /// @param screen The screen where the background is being rendered.
    /// @param partialTicks The partial tick time.
    public void renderBackground(Screen screen, float partialTicks, int vOffset, int width, int height) {
        if (this.minecraft.level == null) {
            this.renderPanorama(partialTicks, width, height, 1);
            this.renderBlurredBackground(partialTicks, true);
            screen.renderDirtBackground(vOffset);
        } else {
            boolean classifiesAsContainer = this.classifiesAsContainer(screen);
            if (CLIENT_CONFIGS.blurryContainers.get() || !classifiesAsContainer) {
                this.renderBlurredBackground(partialTicks, true);
            }

            if (CLIENT_CONFIGS.gradientBackground.get() || classifiesAsContainer) {
                this.renderBackgroundTexture(GUITextures.INWORLD_GRADIENT, width, height);
            } else {
                screen.renderDirtBackground(vOffset);
            }
            MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.BackgroundDrawnEvent(screen, this.stack));
        }
    }

    /// Returns `true` whether the specified {@link Screen} classifies as a container.
    /// This includes any screens in the {@linkplain MellowConfigs#classifiedAsContainers **Classified as Containers**} option, and some of the vanilla screens
    public boolean classifiesAsContainer(Screen screen) {
        boolean vanillaScreens = screen instanceof ContainerScreen || screen instanceof CommandBlockScreen || screen instanceof EditStructureScreen || screen instanceof JigsawScreen ||
                screen instanceof EditSignScreen || screen instanceof EditBookScreen || screen instanceof LecternScreen;
        boolean moddedScreens = CLIENT_CONFIGS.classifiedAsContainers.get().contains(screen.getClass().getName());
        return vanillaScreens || moddedScreens;
    }

    /// Renders the selected {@link melonystudios.mellowui.util.shader.PostEffect PostEffect} onto the panorama, without fading,
    /// if the effect isn't the default "blur".
    /// @param partialTicks The partial tick time.
    public void renderBackgroundShaders(float partialTicks) {
        if (ShaderManager.customShaderLoaded()) this.renderBlurredBackground(partialTicks, null);
    }

    /// Renders the currently selected {@link melonystudios.mellowui.util.shader.PostEffect PostEffect} onto the panorama.
    /// @param partialTicks The partial tick time.
    /// @param fadeIn Whether the shader should fade in (`true`), fade out (`false`) or not fade at all  (`null`).
    /// @apiNote Fading is **not** fully implemented.
    public void renderBlurredBackground(float partialTicks, Boolean fadeIn) {
        if (!CLIENT_CONFIGS.backgroundShaders.get()) return;
        RenderSystem.disableDepthTest();
        ShaderManager.preparePanoramaShaders(partialTicks, fadeIn);
        this.minecraft.getMainRenderTarget().bindWrite(false);
        RenderSystem.enableDepthTest();
    }

    /// Renders the **panoramic background** and the panorama overlay.
    /// @param partialTicks The partial tick time.
    /// @param width The width of the screen.
    /// @param height The height of the screen.
    /// @param transparency The transparency of the overlay texture, from `0` to `1`.
    public void renderPanorama(float partialTicks, int width, int height, float transparency) {
        PANORAMA.render(partialTicks, 1);
        RenderSystem.color4f(1, 1, 1, MathHelper.ceil(MathHelper.clamp(transparency, 0, 1)));
        this.renderBackgroundTexture(Panoramas.overlayTexture(Panoramas.panorama()), width, height);
    }

    /// Replaces the {@linkplain #PANORAMA **default panorama**} used by *Mellow UI* with another.
    ///
    /// This method also creates a new **Generated** panorama with the ID of the old panorama, when the substitution occurs.
    /// This only happens if the replacement came from the {@link MainMenuScreen vanilla title screen}.
    /// @param panorama The panorama to replace the current one.
    /// @param fromTitleScreen Whether the replacement came from the title screen.
    public void replacePanorama(RenderSkybox panorama, boolean fromTitleScreen) {
        if (fromTitleScreen && Minecraft.getInstance().screen instanceof MainMenuScreen) {
            int hashCode = panorama.hashCode();
            if (hashCode != -1294886725) { // hash code of the default panorama from the vanilla title screen ~isa 28-9-25
                MellowUtils.PANORAMAS.put(MellowUI.mellowUI("generated/" + hashCode), Panoramas.createGenerated(panorama, ((InterfaceMethods.TitleScreenMethods) Minecraft.getInstance().screen).getPanoramaOverlay()));
            }
        }
        if (((InterfaceMethods.PanoramaRendererMethods) PANORAMA).differentPanorama(panorama)) PANORAMA = panorama;
    }

    /// Renders a tiled, vertical **background texture** onto the screen, like the panorama overlay.
    /// @param backgroundTexture A resource location of the texture to use.
    /// @param width The width of the screen.
    /// @param height The height of the screen.
    public void renderBackgroundTexture(ResourceLocation backgroundTexture, int width, int height) {
        this.minecraft.getTextureManager().bind(backgroundTexture);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        blit(this.stack, 0, 0, width, height, 0, 0, 16, 128, 16, 128);
        RenderSystem.disableBlend();
    }

    /// Renders a tiled **menu background** onto the screen, choosing the background texture based on the {@linkplain MellowConfigs#defaultBackground **Default Background**} option.
    /// @param x The x-position of the background.
    /// @param y The y-position of the background.
    /// @param width The width of the background.
    /// @param height The height of the background.
    /// @param vOffset The vertical offset of the background texture.
    public void renderMenuBackground(int x, int y, int width, int height, float vOffset) {
        ResourceLocation backgroundTexture = this.minecraft.level == null ? (CLIENT_CONFIGS.defaultBackground.get() ? BACKGROUND_LOCATION :
                GUITextures.MENU_BACKGROUND) : GUITextures.INWORLD_MENU_BACKGROUND;
        this.renderTiledBackground(backgroundTexture, x, y, width, height, vOffset);
    }

    /// Renders a **tiled background** onto the screen, choosing the brightness based on the {@linkplain MellowConfigs#defaultBackground **Default Background**} option.
    /// @param backgroundTexture A resource location of the background texture.
    /// @param x The x-position of the background.
    /// @param y The y-position of the background.
    /// @param width The width of the background.
    /// @param height The height of the background.
    /// @param vOffset The vertical offset of the background texture.
    public void renderTiledBackground(ResourceLocation backgroundTexture, int x, int y, int width, int height, float vOffset) {
        this.renderTiledBackground(backgroundTexture, CLIENT_CONFIGS.defaultBackground.get() ? OLD_BACKGROUND_BRIGHTNESS : DEFAULT_BACKGROUND_BRIGHTNESS, x, y, width, height, vOffset);
    }

    /// Renders a **tiled background** onto the screen.
    /// @param backgroundTexture A resource location of the background texture.
    /// @param brightness The brightness of the background, ranging from `0` to `255`.
    /// @param x The x-position of the background.
    /// @param y The y-position of the background.
    /// @param width The width of the background.
    /// @param height The height of the background.
    /// @param vOffset The vertical offset of the background texture.
    public void renderTiledBackground(ResourceLocation backgroundTexture, int brightness, int x, int y, int width, int height, float vOffset) {
        this.minecraft.getTextureManager().bind(backgroundTexture);
        RenderSystem.enableBlend();
        RenderSystem.color4f(brightness / 255F, brightness / 255F, brightness / 255F, 1);
        blit(this.stack, x, y, 0, vOffset, width, height, DEFAULT_TEXTURE_WIDTH, DEFAULT_TEXTURE_WIDTH);
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.disableBlend();
        MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.BackgroundDrawnEvent(this.minecraft.screen, this.stack));
    }

    /// Renders the **tab header background** onto the screen, choosing the texture and brightness based on the {@linkplain MellowConfigs#defaultBackground **Default Background**} option.
    /// @param x The x-position of the background.
    /// @param y The y-position of the background.
    /// @param width The width of the background.
    /// @param height The height of the background.
    public void renderTabHeaderBackground(int x, int y, int width, int height) {
        ResourceLocation backgroundTexture = this.minecraft.level == null ? (CLIENT_CONFIGS.defaultBackground.get() ? BACKGROUND_LOCATION :
                GUITextures.TAB_HEADER_BACKGROUND) : GUITextures.TAB_HEADER_BACKGROUND;
        this.renderTabHeaderBackground(backgroundTexture, x, y, width, height, CLIENT_CONFIGS.defaultBackground.get() ? OLD_BACKGROUND_BRIGHTNESS : DEFAULT_BACKGROUND_BRIGHTNESS);
    }

    /// Renders the **tab header background** onto the screen.
    /// @param backgroundTexture A resource location of the tab header texture.
    /// @param x The x-position of the background.
    /// @param y The y-position of the background.
    /// @param width The width of the background.
    /// @param height The height of the background.
    /// @param brightness The brightness of the background, ranging from `0` to `255`.
    public void renderTabHeaderBackground(ResourceLocation backgroundTexture, int x, int y, int width, int height, int brightness) {
        this.minecraft.getTextureManager().bind(backgroundTexture);
        RenderSystem.enableBlend();
        RenderSystem.color4f(brightness / 255F, brightness / 255F, brightness / 255F, 1);
        blit(this.stack, x, y, 0, 0, width, height, DEFAULT_TEXTURE_WIDTH, DEFAULT_TEXTURE_WIDTH);
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.disableBlend();
    }

    /// Renders the **list background** onto the screen, choosing the texture and brightness based on the {@linkplain MellowConfigs#defaultBackground **Default Background**} option.
    /// @param x The x-position of the background.
    /// @param y The y-position of the background.
    /// @param width The width of the background.
    /// @param height The height of the background.
    /// @param vOffset The vertical offset of the background texture.
    /// @param scrollAmount The amount scrolled on the list, added with the vertical offset.
    public void renderListBackground(int x, int y, int width, int height, int uOffset, int vOffset, double scrollAmount) {
        ResourceLocation backgroundTexture = this.minecraft.level == null ? (CLIENT_CONFIGS.defaultBackground.get() ? BACKGROUND_LOCATION :
                GUITextures.MENU_LIST_BACKGROUND) : GUITextures.INWORLD_MENU_LIST_BACKGROUND;
        this.renderListBackground(backgroundTexture, x, y, width, height, uOffset, vOffset, CLIENT_CONFIGS.defaultBackground.get() ? OLD_LIST_BACKGROUND_BRIGHTNESS : DEFAULT_BACKGROUND_BRIGHTNESS, scrollAmount);
    }

    /// Renders the **list background** onto the screen.
    /// @param backgroundTexture A resource location of the list background texture.
    /// @param x The x-position of the background.
    /// @param y The y-position of the background.
    /// @param width The width of the background.
    /// @param height The height of the background.
    /// @param uOffset The horizontal offset of the background texture.
    /// @param vOffset The vertical offset of the background texture.
    /// @param brightness The brightness of the background, ranging from `0` to `255`.
    /// @param scrollAmount The amount scrolled on the list, added with the vertical offset.
    public void renderListBackground(ResourceLocation backgroundTexture, int x, int y, int width, int height, int uOffset, int vOffset, int brightness, double scrollAmount) {
        this.minecraft.getTextureManager().bind(backgroundTexture);
        RenderSystem.enableBlend();
        RenderSystem.color4f(brightness / 255F, brightness / 255F, brightness / 255F, 1);
        blit(this.stack, x, y, uOffset, (float) (vOffset + scrollAmount), width, height, DEFAULT_TEXTURE_WIDTH, DEFAULT_TEXTURE_WIDTH);
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.disableBlend();
    }

    /// Renders a small, transparent background for the **title screen icons**.
    /// @param x The x-position of the background.
    /// @param y The y-position of the background.
    /// @param alpha The transparency of the background.
    public void renderTitleScreenIconsBackground(int x, int y, float alpha) {
        this.minecraft.getTextureManager().bind(GUITextures.TITLE_SCREEN_ICONS_BACKGROUND);
        RenderSystem.enableBlend();
        RenderSystem.color4f(1, 1, 1, alpha);
        blit(this.stack, x, y, 0, 0, 14, 27, 14, 27);
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.disableBlend();
    }

    /// Renders the **list separators** on the top and bottom of the screen.
    /// @param list An {@linkplain OptionsRowList options list} to grab the separator positions.
    /// @param width The width of the separator, usually the screen width.
    /// @param tabs The number of tabs at the top of the screen.
    /// @param tabWidth The width of the tabs.
    public void renderListSeparators(OptionsRowList list, int width, int tabs, int tabWidth) {
        this.renderListSeparators(width, list.getLeft(), list.getBottom(), list.getTop(), tabs, tabWidth);
    }

    /// Renders horizontal **list separators** on the top and bottom of the screen.
    /// @param width The width of the separator, usually the screen width.
    /// @param x The starting x-position of the separators.
    /// @param minY The y-position of the bottom separator
    /// @param maxY The y-position of the top separator
    /// @param tabs The number of tabs at the top of the screen.
    /// @param tabWidth The width of the tabs.
    /// @apiNote The `tabs` parameter is **not** fully functional.
    // todo: make "tabs" parameter functional ~isa 11-7-25
    public void renderListSeparators(int width, int x, int minY, int maxY, int tabs, int tabWidth) {
        RenderSystem.enableBlend();
        // Header (split into two parts for the tabs)
        int headerOneEnd = tabs == 4 ? width / 2 - tabWidth * 2 : width / 2 - tabWidth / 2 - tabWidth;
        int headerTwoStart = tabs == 4 ? width / 2 + tabWidth * 2 : width / 2 + tabWidth / 2 + tabWidth;

        this.minecraft.getTextureManager().bind(this.minecraft.level != null ? GUITextures.INWORLD_HEADER_SEPARATOR : GUITextures.HEADER_SEPARATOR);
        blit(this.stack, x, maxY, 0, 0, headerOneEnd, 2, 32, 2);
        blit(this.stack, headerTwoStart, maxY, 0, 0, width, 2, 32, 2);

        // Footer
        this.minecraft.getTextureManager().bind(this.minecraft.level != null ? GUITextures.INWORLD_FOOTER_SEPARATOR : GUITextures.FOOTER_SEPARATOR);
        blit(this.stack, x, minY, 0, 0, x + width, 2, 32, 2);

        RenderSystem.disableBlend();
    }

    /// Renders vertical **list separators** on the right and left sides of the screen, choosing the texture based on the {@linkplain MellowConfigs#defaultBackground **Default Background**} option.
    /// @param x The x-position of the separator.
    /// @param minY The y-position of the separator.
    /// @param maxY The height of the separator.
    /// @param right Whether to use the right-facing separator instead of the left.
    public void renderVerticalSeparator(int x, int minY, int maxY, boolean right) {
        RenderSystem.enableBlend();
        ResourceLocation rightLocation = this.minecraft.level != null ? GUITextures.INWORLD_RIGHT_SEPARATOR : GUITextures.RIGHT_SEPARATOR;
        ResourceLocation leftLocation = this.minecraft.level != null ? GUITextures.INWORLD_LEFT_SEPARATOR : GUITextures.LEFT_SEPARATOR;
        this.minecraft.getTextureManager().bind(right ? rightLocation : leftLocation);
        blit(this.stack, x, minY, 0, 0, 2, maxY, 2, 32);
        RenderSystem.disableBlend();
    }

    /// @param width The width of the screen.
    /// @return The width of four {@link melonystudios.mellowui.screen.widget.TabButton TabButtons} based on the screen width.
    public int fourTabWidth(int width) {
        return width / 2 - DEFAULT_TAB_WIDTH * 2 <= 0 ? 90 : DEFAULT_TAB_WIDTH;
    }

    /// @param width The width of the screen.
    /// @return The width of three {@link melonystudios.mellowui.screen.widget.TabButton TabButtons} based on the screen width.
    public int threeTabWidth(int width) {
        return width / 2 - DEFAULT_TAB_WIDTH + 65 <= 0 ? 90 : DEFAULT_TAB_WIDTH;
    }

    /// Renders the **text suggestion** of a {@link TextFieldWidget}.
    /// @param textField A nullable text box widget.
    /// @param suggestion A text component for the text to render, usually the "{@linkplain melonystudios.mellowui.util.MellowUtils#SEARCH_TEXT *Search...*}" suggestion.
    public void renderTextBoxSuggestion(@Nullable TextFieldWidget textField, ITextComponent suggestion) {
        if (textField != null && textField.getValue().isEmpty() && !textField.isFocused()) {
            drawString(this.stack, this.minecraft.font, suggestion, textField.x + 4, textField.y + (textField.getHeight() - 8) / 2, 0xFFFFFF);
        }
    }

    /// Renders ***Forge*'s beta warning text** onto the screen.
    /// <blockquote>
    /// <p style='font-family: "Minecraft Seven v3"; color: #FF5555'>WARNING: Forge Beta</p>
    /// <p style='font-family: "Minecraft Seven v3"; color: #FFFFFF'>Major issues may arise, verify before reporting.</p>
    /// </blockquote>
    /// @param width The width of the screen.
    /// @param textHeight The y-position to render the text.
    /// @param textColor An RGB color to apply to the text.
    /// @param alpha The transparency of the text, usually ranging from `0` to `1`.
    public void renderForgeBetaText(int width, int textHeight, int textColor, int alpha) {
        VersionChecker.Status status = ForgeVersion.getStatus();
        if (status == VersionChecker.Status.BETA || status == VersionChecker.Status.BETA_OUTDATED) {
            drawCenteredString(this.stack, this.minecraft.font, new TranslationTextComponent("forge.update.beta.1", TextFormatting.RED, TextFormatting.RESET).withStyle(TextFormatting.RED, TextFormatting.BOLD),
                    width / 2, textHeight, textColor | alpha);
            drawCenteredString(this.stack, this.minecraft.font, new TranslationTextComponent("forge.update.beta.2"), width / 2, textHeight + 10, textColor | alpha);
            ForgeHooksClient.forgeStatusLine = new TranslationTextComponent("forge.update.newversion", ForgeVersion.getTarget()).getString();
        }
    }

    /// Creates a new *"Switch Style"* {@link IconButton}.
    /// @param onPressed What happens when this button is {@linkplain net.minecraft.client.gui.widget.button.Button.IPressable pressed}.
    /// @param x The x-position of the button.
    /// @param y The y-position of the button.
    public IconButton switchStyle(Button.IPressable onPressed, int x, int y) {
        return new IconButton(x, y, 12, 12, GUITextures.SWITCH_STYLE_SET, new TranslationTextComponent("button.mellowui.switch_style"), onPressed);
    }

    /// Creates a new *"Customize"* {@link IconButton}.
    /// @param onPressed What happens when this button is {@linkplain net.minecraft.client.gui.widget.button.Button.IPressable pressed}.
    /// @param x The x-position of the button.
    /// @param y The y-position of the button.
    public IconButton customize(Button.IPressable onPressed, int x, int y) {
        return new IconButton(x, y, 12, 12, GUITextures.CUSTOMIZE_SET, new TranslationTextComponent("button.mellowui.customize.title"), onPressed);
    }

    /// Creates a new **scissor** rectangle to blit things into.
    /// @param minX The starting x-position of the scissor area.
    /// @param minY The starting y-position of the scissor area.
    /// @param maxX The ending x-position of the scissor area.
    /// @param maxY The ending y-position of the scissor area.
    public void enableScissor(int minX, int minY, int maxX, int maxY) {
        this.applyScissor(this.scissorStack.push(new ScreenRectangle(minX, minY, maxX - minX, maxY - minY)));
    }

    /// Disables the currently enabled **scissor**.
    public void disableScissor() {
        this.applyScissor(this.scissorStack.pop());
    }

    /// Whether the specified point is within the currently enabled **scissor** rectangle.
    /// @param x The x-position to check.
    /// @param y The y-position to check.
    public boolean containsPointInScissor(int x, int y) {
        return this.scissorStack.containsPoint(x, y);
    }

    private void applyScissor(@Nullable ScreenRectangle rectangle) {
        if (rectangle != null) {
            MainWindow window = this.minecraft.getWindow();
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

    /// Renders a **tooltip** to the screen.
    /// @param screen The screen this tooltip is being rendered in.
    /// @param widget The widget that is displaying this tooltip.
    /// @param tooltipText The text component to render on the tooltip.
    /// @param mouseX The x-coordinate of the mouse cursor.
    /// @param mouseY The y-coordinate of the mouse cursor.
    public void renderTooltip(Screen screen, Widget widget, ITextComponent tooltipText, int mouseX, int mouseY) {
        int x = widget.isFocused() && !widget.isMouseOver(mouseX, mouseY) ? widget.x : mouseX;
        int y = widget.isFocused() && !widget.isMouseOver(mouseX, mouseY) ? widget.y : mouseY;
        if (this.containsPointInScissor(mouseX, mouseY) || widget.isFocused()) {
            screen.renderTooltip(this.stack, this.minecraft.font.split(tooltipText, TOOLTIP_MAX_WIDTH), x, y);
        }
    }

    /// Renders a **tooltip** to the screen.
    /// @param screen The screen this tooltip is being rendered in.
    /// @param widget The widget that is displaying this tooltip.
    /// @param tooltipText A list of reordering processors to render on the tooltip.
    /// @param mouseX The x-coordinate of the mouse cursor.
    /// @param mouseY The y-coordinate of the mouse cursor.
    public void renderTooltip(Screen screen, Widget widget, List<IReorderingProcessor> tooltipText, int mouseX, int mouseY) {
        int x = widget.isFocused() && !widget.isMouseOver(mouseX, mouseY) ? widget.x : mouseX;
        int y = widget.isFocused() && !widget.isMouseOver(mouseX, mouseY) ? widget.y : mouseY;
        if (this.containsPointInScissor(mouseX, mouseY) || widget.isFocused()) {
            screen.renderTooltip(this.stack, tooltipText, x, y);
        }
    }
}
