package melonystudios.mellowui.element;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.backport.scissor.ScissorStack;
import melonystudios.mellowui.backport.scissor.ScreenRectangle;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.element.widget.IconButton;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.renderer.LogoRenderer;
import melonystudios.mellowui.resource.panorama.Panoramas;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.util.shader.ShaderManager;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.versions.forge.ForgeVersion;

import javax.annotation.Nullable;

import java.util.List;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;
import static melonystudios.mellowui.util.MellowUtils.defaultBackground;

/// The global ***Render Components*** used by *Mellow UI*.
/// Contains almost every rendering method used more than once throughout the codebase.
@OnlyIn(Dist.CLIENT)
public class RenderComponents extends VanillaRenderComponents {
    /// The default instance of *Mellow UI*'s ***Render Components***.
    public static final RenderComponents INSTANCE = new RenderComponents(Minecraft.getInstance());
    /// The panorama that's currently being used by *Mellow UI*.
    public static PanoramaRenderer PANORAMA = new PanoramaRenderer(TitleScreen.CUBE_MAP);
    public static float PANORAMA_PITCH = 10;
    public static final int TOOLTIP_MAX_WIDTH = 170; // tooltip width is 200 in 1.18.2
    public static final int DEFAULT_TAB_WIDTH = 130;
    public static final int DEFAULT_BACKGROUND_BRIGHTNESS = 255;
    public static final int OLD_BACKGROUND_BRIGHTNESS = 64;
    public static final int OLD_LIST_BACKGROUND_BRIGHTNESS = 32;
    public static final int OLD_PANEL_BACKGROUND_BRIGHTNESS = 32;
    public static final int DEFAULT_TEXTURE_WIDTH = 32;
    private final ScissorStack scissorStack = new ScissorStack();

    /// The global **Render Components** used by *Mellow UI*.
    /// Contains almost every rendering method used more than once throughout the codebase.
    /// @param minecraft The *Minecraft* instance class.
    private RenderComponents(Minecraft minecraft) {
        super(minecraft);
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
            MinecraftForge.EVENT_BUS.post(new ScreenEvent.BackgroundDrawnEvent(screen, this.stack));
        }
    }

    /// Returns `true` whether the specified {@link Screen} classifies as a container.
    /// This includes any screens in the {@linkplain MellowConfigs#classifiedAsContainers **Classified as Containers**} option, and some of the vanilla screens.
    /// @param screen The screen currently open.
    public boolean classifiesAsContainer(Screen screen) {
        boolean vanillaScreens = screen instanceof AbstractContainerScreen || screen instanceof CommandBlockEditScreen || screen instanceof StructureBlockEditScreen || screen instanceof JigsawBlockEditScreen ||
                screen instanceof SignEditScreen || screen instanceof BookEditScreen || screen instanceof LecternScreen;
        boolean moddedScreens = CLIENT_CONFIGS.classifiedAsContainers.get().contains(screen.getClass().getName());
        return vanillaScreens || moddedScreens;
    }

    /// Renders the selected {@link melonystudios.mellowui.util.shader.PostEffect PostEffect} onto the panorama, without fading,
    /// if the effect isn't the default "blur".
    public void renderBackgroundShaders(float partialTicks) {
        if (ShaderManager.customShaderLoaded()) this.renderBlurredBackground(partialTicks, null);
    }

    /// Renders the currently selected {@link melonystudios.mellowui.util.shader.PostEffect PostEffect} onto the panorama.
    /// @param partialTicks The partial tick time.
    /// @param fadeIn Whether the shader should fade in (`true`), fade out (`false`) or not fade at all  (`null`).
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
        Panoramas.renderSituationalPanorama(partialTicks);
        this.setColor(1, 1, 1, Mth.ceil(Mth.clamp(transparency, 0, 1)));
        this.renderBackgroundTexture(Panoramas.overlayTexture(Panoramas.panorama()), width, height);
    }

    /// Replaces the {@linkplain #PANORAMA **default panorama**} used by *Mellow UI* with another.
    ///
    /// This method also creates a new **Generated** panorama with the ID of the old panorama, when the substitution occurs.
    /// This only happens if the replacement came from the {@linkplain TitleScreen vanilla title screen}.
    /// @param panorama The panorama to replace the current one.
    /// @param fromTitleScreen Whether the replacement came from the title screen.
    public void replacePanorama(PanoramaRenderer panorama, boolean fromTitleScreen) {
        Screen screen = Minecraft.getInstance().screen;
        if (fromTitleScreen && screen instanceof TitleScreen) {
            int hashCode = panorama.hashCode();
            if (hashCode != -1294886725) { // hash code of the default panorama from the vanilla title screen ~isa 28-9-25
                MellowUtils.PANORAMAS.put(MellowUI.generated("id_" + hashCode), Panoramas.createGenerated(panorama, ((InterfaceMethods.TitleScreenMethods) screen).getPanoramaOverlay()));
            }
        }
        if (((InterfaceMethods.PanoramaRendererMethods) PANORAMA).differentPanorama(panorama)) PANORAMA = panorama;
    }

    /// Renders a tiled, vertical **background texture** onto the screen, like the panorama overlay.
    /// @param backgroundTexture A resource location of the texture to use.
    /// @param width The width of the screen.
    /// @param height The height of the screen.
    public void renderBackgroundTexture(ResourceLocation backgroundTexture, int width, int height) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, backgroundTexture);
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
        ResourceLocation backgroundTexture = this.minecraft.level == null ? (defaultBackground() ? BACKGROUND_LOCATION :
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
        this.renderTiledBackground(backgroundTexture, defaultBackground() ? OLD_BACKGROUND_BRIGHTNESS : DEFAULT_BACKGROUND_BRIGHTNESS, x, y, width, height, vOffset);
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
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, backgroundTexture);
        RenderSystem.enableBlend();
        this.setColor(brightness / 255F, brightness / 255F, brightness / 255F, 1);
        blit(this.stack, x, y, 0, vOffset, width, height, DEFAULT_TEXTURE_WIDTH, DEFAULT_TEXTURE_WIDTH);
        this.setColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
        MinecraftForge.EVENT_BUS.post(new ScreenEvent.BackgroundDrawnEvent(this.minecraft.screen, this.stack));
    }

    /// Renders the **tab header background** onto the screen, choosing the texture and brightness based on the {@linkplain MellowConfigs#defaultBackground **Default Background**} option.
    /// @param x The x-position of the background.
    /// @param y The y-position of the background.
    /// @param width The width of the background.
    /// @param height The height of the background.
    public void renderTabHeaderBackground(int x, int y, int width, int height) {
        ResourceLocation backgroundTexture = this.minecraft.level == null ? (defaultBackground() ? BACKGROUND_LOCATION :
                GUITextures.TAB_HEADER_BACKGROUND) : GUITextures.TAB_HEADER_BACKGROUND;
        this.renderTabHeaderBackground(backgroundTexture, x, y, width, height, defaultBackground() ? OLD_BACKGROUND_BRIGHTNESS : DEFAULT_BACKGROUND_BRIGHTNESS);
    }

    /// Renders the **tab header background** onto the screen.
    /// @param backgroundTexture A resource location of the tab header texture.
    /// @param x The x-position of the background.
    /// @param y The y-position of the background.
    /// @param width The width of the background.
    /// @param height The height of the background.
    /// @param brightness The brightness of the background, ranging from `0` to `255`.
    public void renderTabHeaderBackground(ResourceLocation backgroundTexture, int x, int y, int width, int height, int brightness) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, backgroundTexture);
        RenderSystem.enableBlend();
        this.setColor(brightness / 255F, brightness / 255F, brightness / 255F, 1);
        blit(this.stack, x, y, 0, 0, width, height, DEFAULT_TEXTURE_WIDTH, DEFAULT_TEXTURE_WIDTH);
        this.setColor(1, 1, 1, 1);
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
        ResourceLocation backgroundTexture = this.minecraft.level == null ? (defaultBackground() ? BACKGROUND_LOCATION :
                GUITextures.MENU_LIST_BACKGROUND) : GUITextures.INWORLD_MENU_LIST_BACKGROUND;
        this.renderListBackground(backgroundTexture, x, y, width, height, uOffset, vOffset, defaultBackground() ? OLD_LIST_BACKGROUND_BRIGHTNESS : DEFAULT_BACKGROUND_BRIGHTNESS, scrollAmount);
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
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, backgroundTexture);
        this.setColor(brightness / 255F, brightness / 255F, brightness / 255F, 1);
        blit(this.stack, x, y, uOffset, (float) (vOffset + scrollAmount), width, height, DEFAULT_TEXTURE_WIDTH, DEFAULT_TEXTURE_WIDTH);
        this.setColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
    }

    /// Renders a small, transparent background for the **title screen icons**.
    /// @param x The x-position of the background.
    /// @param y The y-position of the background.
    /// @param alpha The transparency of the background.
    public void renderTitleScreenIconsBackground(int x, int y, float alpha) {
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, GUITextures.TITLE_SCREEN_ICONS_BACKGROUND);
        this.setColor(1, 1, 1, alpha);
        blit(this.stack, x, y, 0, 0, 14, 27, 14, 27);
        this.setColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
    }

    /// Renders an **outline rectangle** on the screen with the specified color.
    /// @param x The x-coordinate of the top-left corner of the rectangle.
    /// @param y The y-coordinate of the top-left corner of the rectangle.
    /// @param width The width of the blitted portion.
    /// @param height The height of the rectangle.
    /// @param color The color of the outline.
    public void renderOutline(int x, int y, int width, int height, int color) {
        fill(this.stack, x, y, x + width, y + 1, color);
        fill(this.stack, x, y + height - 1, x + width, y + height, color);
        fill(this.stack, x, y + 1, x + 1, y + height - 1, color);
        fill(this.stack, x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    /// Renders the **list separators** on the top and bottom of the screen.
    /// @param list An {@linkplain OptionsList options list} to grab the separator positions.
    /// @param width The width of the separator, usually the screen width.
    /// @param tabs The number of tabs at the top of the screen.
    /// @param tabWidth The width of the tabs.
    public void renderListSeparators(OptionsList list, int width, int tabs, int tabWidth) {
        this.renderListSeparators(width, list.getLeft(), list.getBottom(), list.getTop(), tabs, tabWidth);
    }

    /// Renders horizontal **list separators** on the top and bottom of the screen.
    /// @param width The width of the separator, usually the screen width.
    /// @param x The starting x-position of the separators.
    /// @param minY The y-position of the bottom separator.
    /// @param maxY The y-position of the top separator.
    /// @param tabs The number of tabs at the top of the screen.
    /// @param tabWidth The width of the tabs.
    /// @apiNote The `tabs` parameter is **not** fully functional.
    // todo: make "tabs" parameter functional ~isa 11-7-25
    public void renderListSeparators(int width, int x, int minY, int maxY, int tabs, int tabWidth) {
        RenderSystem.enableBlend();
        // Header (split into two parts for the tabs)
        int headerOneEnd = tabs == 4 ? width / 2 - tabWidth * 2 : width / 2 - tabWidth / 2 - tabWidth;
        int headerTwoStart = tabs == 4 ? width / 2 + tabWidth * 2 : width / 2 + tabWidth / 2 + tabWidth;

        if (MellowUtils.LOADING_ERRORS) { // only use these when Forge errors out, as sometimes the mod's assets can't load ~isa 27-10-25
            this.renderGradientListSeparators(x, width, minY, maxY, 0);
        } else {
            // Header
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, this.minecraft.level != null ? GUITextures.INWORLD_HEADER_SEPARATOR : GUITextures.HEADER_SEPARATOR);
            blit(this.stack, x, maxY, 0, 0, headerOneEnd, 2, 32, 2);
            blit(this.stack, headerTwoStart, maxY, 0, 0, width, 2, 32, 2);

            // Footer
            RenderSystem.setShaderTexture(0, this.minecraft.level != null ? GUITextures.INWORLD_FOOTER_SEPARATOR : GUITextures.FOOTER_SEPARATOR);
            blit(this.stack, x, minY, 0, 0, x + width, 2, 32, 2);
        }
        RenderSystem.disableBlend();
    }

    /// Renders horizontal **list separators** on the top and bottom of the screen. This the pre-1.20.5 version, so renders as a fading black gradient.
    /// @param minX The x-position of where to start drawing.
    /// @param maxX The x-position of where to finish drawing.
    /// @param maxY The y-position of the bottom separator.
    /// @param minY The y-position of the top separator.
    /// @param color The color to use for the separators. Defaults to `0` (black).
    public void renderGradientListSeparators(int minX, int maxX, int minY, int maxY, int color) {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder builder = tessellator.getBuilder();
        RenderSystem.enableDepthTest();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableTexture();

        int red = FastColor.ARGB32.red(color);
        int green = FastColor.ARGB32.green(color);
        int blue = FastColor.ARGB32.blue(color);

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        builder.vertex(minX, maxY + 4, 0).uv(0, 1).color(red, green, blue, 0).endVertex();
        builder.vertex(maxX, maxY + 4, 0).uv(1, 1).color(red, green, blue, 0).endVertex();
        builder.vertex(maxX, maxY, 0).uv(1, 0).color(red, green, blue, 255).endVertex();
        builder.vertex(minX, maxY, 0).uv(0, 0).color(red, green, blue, 255).endVertex();
        builder.vertex(minX, minY, 0).uv(0, 1).color(red, green, blue, 255).endVertex();
        builder.vertex(maxX, minY, 0).uv(1, 1).color(red, green, blue, 255).endVertex();
        builder.vertex(maxX, minY - 4, 0).uv(1, 0).color(red, green, blue, 0).endVertex();
        builder.vertex(minX, minY - 4, 0).uv(0, 0).color(red, green, blue, 0).endVertex();
        tessellator.end();
        RenderSystem.enableTexture();
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
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, right ? rightLocation : leftLocation);
        blit(this.stack, x, minY, 0, 0, 2, maxY, 2, 32);
        RenderSystem.disableBlend();
    }

    /// @param width The width of the screen.
    /// @return The width of four {@link melonystudios.mellowui.element.widget.TabButton TabButtons} based on the screen width.
    public int fourTabWidth(int width) {
        return width / 2 - DEFAULT_TAB_WIDTH * 2 <= 0 ? 90 : DEFAULT_TAB_WIDTH;
    }

    /// @param width The width of the screen.
    /// @return The width of three {@link melonystudios.mellowui.element.widget.TabButton TabButtons} based on the screen width.
    public int threeTabWidth(int width) {
        return width / 2 - DEFAULT_TAB_WIDTH + 65 <= 0 ? 90 : DEFAULT_TAB_WIDTH;
    }

    /// Renders the **"Update Available!"** icon at a *widget's position*. This is dependent on the {@linkplain MellowConfigs#updateAvailableIconStyle **Update Available Icon**} style option.
    /// @param x The x-position of the widget.
    /// @param y The y-position of the widget.
    /// @param width The width of the widget.
    /// @param height The height of the widget.
    /// @param alpha The transparency of the icon. Should be a value between `0` and `1`.
    /// @param renderOnCorner Whether to render the icon on the corner of the button.
    /// @param checkStatus *(optional)* The Version Checker status of the Forge Emerald.
    public void renderUpdateAvailableIcon(int x, int y, int width, int height, float alpha, boolean renderOnCorner, @Nullable VersionChecker.Status checkStatus) {
        if (CLIENT_CONFIGS.updateAvailableIconStyle.get()) {
            if (renderOnCorner) this.renderRealmsDiamond(x + width - 6, y - 3, alpha);
            else this.renderRealmsDiamond(x + width - (height / 2 + 4), y + (height / 2 - 4), alpha);
        } else if (checkStatus != null) {
            if (renderOnCorner) this.renderForgeEmerald(x + width - 6, y - 3, alpha, checkStatus);
            else this.renderForgeEmerald(x + width - (height / 2 + 4), y + (height / 2 - 4), alpha, checkStatus);
        }
    }

    /// Renders the **"Update Available!"** icon at the specified position.
    /// @param x The x-position of the icon.
    /// @param y The y-position of the icon.
    /// @param alpha The transparency of the icon. Should be a value between `0` to `1`.
    /// @param checkStatus *(optional)* The {@linkplain VersionChecker.Status Version Checker status} associated with the Forge Emerald.
    public void renderUpdateAvailableIcon(int x, int y, float alpha, @Nullable VersionChecker.Status checkStatus) {
        if (CLIENT_CONFIGS.updateAvailableIconStyle.get()) this.renderRealmsDiamond(x, y, alpha);
        else if (checkStatus != null) this.renderForgeEmerald(x, y, alpha, checkStatus);
    }

    /// Renders a **Realms Diamond** at the specified position. This is the updated, animated version of it from 1.20.5.
    /// @param x The x-position of the icon.
    /// @param y The y-position of the icon.
    /// @param alpha The transparency of the icon. Should be a value between `0` to `1`.
    public void renderRealmsDiamond(int x, int y, float alpha) {
        TextureAtlasSprite sprite = GUITextures.getSprite(GUITextures.UPDATE_AVAILABLE);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, GUITextures.GUI_SPRITES_ATLAS);
        this.setColor(1, 1, 1, alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        blit(this.stack, x, y, this.getBlitOffset(), 8, 8, sprite);

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        this.setColor(1, 1, 1, 1);
    }

    /// Renders a **Forge Emerald** at the specified position. Its animation is dependent on the Version Checker status associated with it.
    /// @param x The x-position of the icon.
    /// @param y The y-position of the icon.
    /// @param alpha The transparency of the icon. Should be a value between `0` to `1`.
    /// @param checkStatus The {@linkplain VersionChecker.Status Version Checker status} associated with this icon.
    public void renderForgeEmerald(int x, int y, float alpha, VersionChecker.Status checkStatus) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, GUITextures.VERSION_CHECKER_ICONS);
        this.setColor(1, 1, 1, alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        blit(this.stack, x, y, checkStatus.getSheetOffset() * 8,
                (checkStatus.isAnimated() && ((Util.getMillis() / 800 & 1) == 1)) ? 8 : 0,
                8, 8, 64, 16
        );

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        this.setColor(1, 1, 1, 1);
    }

    /// Renders the **text suggestion** of an {@link EditBox}, overriding the color to match the border.
    /// @param textField A nullable edit box widget.
    /// @param suggestion A text component for the text to render, usually the "{@linkplain melonystudios.mellowui.element.text.TextComponents#searchText *Search...*}" suggestion.
    public void renderTextBoxSuggestion(@Nullable EditBox textField, Component suggestion) {
        if (textField != null && textField.getValue().isEmpty() && !textField.isFocused()) {
            if (textField.isHoveredOrFocused()) suggestion = suggestion.copy().withStyle(style -> style.withColor(WidgetConfigs.WIDGET_CONFIGS.textFieldHighlightedSuggestionColor.get()));
            this.drawString(suggestion, true, textField.x + 4, textField.y + (textField.getHeight() - 8) / 2, 0xFFFFFF);
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
            this.drawCenteredString( new TranslatableComponent("forge.update.beta.1", ChatFormatting.RED, ChatFormatting.RESET).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true,
                    width / 2, textHeight, textColor | alpha);
            this.drawCenteredString( new TranslatableComponent("forge.update.beta.2"), true, width / 2, textHeight + 10, textColor | alpha);
            ForgeHooksClient.forgeStatusLine = new TranslatableComponent("forge.update.newversion", ForgeVersion.getTarget()).getString();
        }
    }

    /// Renders a **logo** based on the current main menu and logo style.
    /// @param screen The screen to render in.
    /// @param width The width of the screen.
    /// @param height The height of the screen.
    /// @param transparency The transparency of the logo, usually ranging from `0` to `1`.
    /// @param keepLogoThroughFade Whether to keep the logo visible during the fading animation.
    public void renderLogo(Screen screen, int width, int height, float transparency, boolean keepLogoThroughFade) {
        RenderSystem.enableBlend();
        switch (CLIENT_CONFIGS.titleStyle.get()) {
            case OPTION_1: {
                LogoRenderer.render116Logo(this.stack, screen, width, transparency, 30, keepLogoThroughFade);
                break;
            }
            case OPTION_3: {
                LogoRenderer.renderMellomedleyLogo(this.stack, width / 2 - 129, 10, 258, 100, transparency, keepLogoThroughFade);
                break;
            }
            case OPTION_2: {
                CLIENT_CONFIGS.logoStyle.get().renderLogo(this.stack, screen, true, width / 2, 30, width, height, transparency, keepLogoThroughFade);
            }
        }
    }

    public void renderScreenHeader() {}

    /// Creates a new *"Switch Style"* {@link IconButton}.
    /// @param onPress What happens when this button is {@linkplain net.minecraft.client.gui.components.Button.OnPress pressed}.
    /// @param x The x-position of the button.
    /// @param y The y-position of the button.
    public IconButton switchStyle(Button.OnPress onPress, int x, int y) {
        return new IconButton(x, y, 12, 12, GUITextures.SWITCH_STYLE_SET, new TranslatableComponent("button.mellowui.switch_style"), onPress);
    }

    /// Creates a new *"Customize"* {@link IconButton}.
    /// @param onPress What happens when this button is {@linkplain net.minecraft.client.gui.components.Button.OnPress pressed}.
    /// @param x The x-position of the button.
    /// @param y The y-position of the button.
    public IconButton customize(Button.OnPress onPress, int x, int y) {
        return new IconButton(x, y, 12, 12, GUITextures.CUSTOMIZE_SET, new TranslatableComponent("button.mellowui.customize.title"), onPress);
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
            Window window = this.minecraft.getWindow();
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
    public void renderTooltip(Screen screen, AbstractWidget widget, Component tooltipText, int mouseX, int mouseY) {
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
    public void renderTooltip(Screen screen, AbstractWidget widget, List<FormattedCharSequence> tooltipText, int mouseX, int mouseY) {
        int x = widget.isFocused() && !widget.isMouseOver(mouseX, mouseY) ? widget.x : mouseX;
        int y = widget.isFocused() && !widget.isMouseOver(mouseX, mouseY) ? widget.y : mouseY;
        if (this.containsPointInScissor(mouseX, mouseY) || widget.isFocused()) {
            screen.renderTooltip(this.stack, tooltipText, x, y);
        }
    }
}
