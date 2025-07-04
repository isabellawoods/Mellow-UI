package melonystudios.mellowui.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.screen.list.OptionsList;
import melonystudios.mellowui.screen.backport.MUIControlsScreen;
import melonystudios.mellowui.screen.update.MUIModListScreen;
import melonystudios.mellowui.screen.update.MUIOptionsScreen;
import melonystudios.mellowui.screen.update.MUIPackScreen;
import melonystudios.mellowui.util.pack.HighContrastPack;
import melonystudios.mellowui.util.shader.ShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IBidiTooltip;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

import static melonystudios.mellowui.config.WidgetConfigs.WIDGET_CONFIGS;
import static net.minecraft.util.ColorHelper.PackedColor.*;

public class MellowUtils {
    public static RenderSkybox PANORAMA = new RenderSkybox(MainMenuScreen.CUBE_MAP);
    public static final DateFormat WORLD_DATE_FORMAT = new SimpleDateFormat(); // "dd-MM-yyyy '('EEE') - 'HH:mm:ss"
    public static final int TOOLTIP_MAX_WIDTH = 200;
    public static final int DEFAULT_TITLE_HEIGHT = 12;
    public static final int TABBED_TITLE_HEIGHT = 2;
    public static final int PAUSE_MENU_Y_OFFSET = 0;
    public static float PANORAMA_PITCH = 10;

    public static Screen modList(Screen lastScreen) {
        switch (MellowConfigs.CLIENT_CONFIGS.modListStyle.get()) {
            case OPTION_2: return new MUIModListScreen(lastScreen);
            case OPTION_3: {
                if (!ModList.get().isLoaded("catalogue")) return new MUIModListScreen(lastScreen);
                try {
                    Class<?> screen = Class.forName("com.mrcrayfish.catalogue.client.screen.CatalogueModListScreen");
                    return (Screen) screen.newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {
                    return new MUIModListScreen(lastScreen);
                }
            }
            case OPTION_1: default: return new ModListScreen(lastScreen);
        }
    }

    public static Screen options(Screen lastScreen, Minecraft minecraft) {
        if (MellowConfigs.CLIENT_CONFIGS.updateOptionsMenu.get()) return new MUIOptionsScreen(lastScreen, minecraft.options);
        else return new OptionsScreen(lastScreen, minecraft.options);
    }

    public static Screen videoSettings(Screen lastScreen, Minecraft minecraft) {
        if (MellowConfigs.CLIENT_CONFIGS.updateVideoSettingsMenu.get() == ThreeStyles.OPTION_3) {
            try {
                Class<?> screen = Class.forName("me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI");
                return (Screen) screen.getConstructor(Screen.class).newInstance(lastScreen);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
                return new VideoSettingsScreen(lastScreen, minecraft.options);
            }
        } else {
            return new VideoSettingsScreen(lastScreen, minecraft.options);
        }
    }

    public static Screen controls(Screen lastScreen, Minecraft minecraft) {
        if (MellowConfigs.CLIENT_CONFIGS.updateControlsMenu.get()) return new MUIControlsScreen(lastScreen, minecraft.options);
        else return new ControlsScreen(lastScreen, minecraft.options);
    }

    public static Screen resourcePackList(Screen lastScreen, Minecraft minecraft, Consumer<ResourcePackList> packInfo) {
        ITextComponent title = new TranslationTextComponent("resourcePack.title");
        if (MellowConfigs.CLIENT_CONFIGS.updatePackMenu.get()) return new MUIPackScreen(lastScreen, minecraft.getResourcePackRepository(), packInfo, minecraft.getResourcePackDirectory(), title);
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

    public static void textScissor(Runnable toCut, int startX, int endX) {
        int guiScale = (int) Minecraft.getInstance().getWindow().getGuiScale();
        RenderSystem.enableScissor(startX * guiScale, 0, (endX - startX) * guiScale, Minecraft.getInstance().getWindow().getHeight());
        toCut.run();
        RenderSystem.disableScissor();
    }

    public static void renderPanorama(MatrixStack stack, float partialTicks, int width, int height, float transparency) {
        Minecraft minecraft = Minecraft.getInstance();
        MellowUtils.PANORAMA.render(partialTicks, 1);
        minecraft.getTextureManager().bind(GUITextures.PANORAMA_OVERLAY);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1, 1, 1, MathHelper.ceil(MathHelper.clamp(transparency, 0, 1)));
        AbstractGui.blit(stack, 0, 0, width, height, 0, 0, 16, 128, 16, 128);
    }

    public static void renderBackground(MatrixStack stack, int width, int height, float vOffset) {
        renderTiledBackground(stack, MellowConfigs.CLIENT_CONFIGS.defaultBackground.get() ? AbstractGui.BACKGROUND_LOCATION : (Minecraft.getInstance().level != null ?
                GUITextures.INWORLD_MENU_BACKGROUND : GUITextures.MENU_BACKGROUND), width, height, vOffset);
    }

    public static void renderTiledBackground(MatrixStack stack, ResourceLocation textureLocation, int width, int height, float vOffset) {
        renderTiledBackground(stack, textureLocation, MellowConfigs.CLIENT_CONFIGS.defaultBackground.get() ? 64 : 255, width, height, vOffset);
    }

    public static void renderTiledBackground(MatrixStack stack, ResourceLocation textureLocation, int brightness, int width, int height, float vOffset) {
        Minecraft minecraft = Minecraft.getInstance();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        minecraft.getTextureManager().bind(textureLocation);
        RenderSystem.enableBlend();
        RenderSystem.color4f(1, 1, 1, 1);
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.vertex(0, height, 0).uv(0, (float) height / 32 + vOffset).color(brightness, brightness, brightness, 255).endVertex();
        buffer.vertex(width, height, 0).uv((float) width / 32, (float) height / 32 + vOffset).color(brightness, brightness, brightness, 255).endVertex();
        buffer.vertex(width, 0, 0).uv((float) width / 32, vOffset).color(brightness, brightness, brightness, 255).endVertex();
        buffer.vertex(0, 0, 0).uv(0, vOffset).color(brightness, brightness, brightness, 255).endVertex();
        tessellator.end();
        RenderSystem.disableBlend();
        MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.BackgroundDrawnEvent(minecraft.screen, stack));
    }

    public static void renderBackgroundWithShaders(float partialTicks) {
        if (ShaderManager.customShaderLoaded()) renderBlurredBackground(partialTicks);
    }

    public static void renderBlurredBackground(float partialTicks) {
        if (MellowConfigs.CLIENT_CONFIGS.backgroundShaders.get()) {
            RenderSystem.disableDepthTest();
            ShaderManager.processPanoramaShaders(partialTicks);
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
            RenderSystem.enableDepthTest();
        }
    }

    public static void renderTooltip(MatrixStack stack, Screen screen, Button button, ITextComponent tooltipText, int mouseX, int mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        int x = button.isFocused() ? button.x : mouseX;
        int y = button.isFocused() ? button.y : mouseY;
        screen.renderTooltip(stack, minecraft.font.split(tooltipText, TOOLTIP_MAX_WIDTH), x, y);
    }

    public static void openLink(Screen lastScreen, String url, boolean showWarning) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new ConfirmOpenLinkScreen(confirmed -> {
            if (confirmed) Util.getPlatform().openUri(url);
            minecraft.setScreen(lastScreen);
        }, url, !showWarning));
    }

    @Nullable
    public static List<IReorderingProcessor> tooltipAt(OptionsList list, int mouseX, int mouseY) {
        Optional<Widget> widget = list.getMouseOver(mouseX, mouseY);
        if (widget.isPresent() && widget.get() instanceof IBidiTooltip) {
            Optional<List<IReorderingProcessor>> tooltip = ((IBidiTooltip) widget.get()).getTooltip();
            return tooltip.orElse(null);
        } else {
            return null;
        }
    }

    // Copied from teamtwilight/twilightforest.
    public static void addHighContrastPack() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) return;

        minecraft.getResourcePackRepository().addPackFinder((packInfo, infoFactory) -> packInfo.accept(ResourcePackInfo.create(
                GUITextures.MUI_HIGH_CONTRAST.toString(), false, () -> new HighContrastPack(ModList.get()
                        .getModFileById(MellowUI.MOD_ID).getFile()), infoFactory, ResourcePackInfo.Priority.TOP, IPackNameDecorator.BUILT_IN)));
    }

    public static boolean highContrastEnabled() {
        Collection<String> selectedPacks = Minecraft.getInstance().getResourcePackRepository().getSelectedIds();
        return selectedPacks.contains(GUITextures.MUI_HIGH_CONTRAST.toString()) || selectedPacks.contains(GUITextures.LIBRARY_HIGH_CONTRAST.toString());
    }

    public static boolean highContrastUnavailable() {
        return !Minecraft.getInstance().getResourcePackRepository().getAvailableIds().contains(GUITextures.MUI_HIGH_CONTRAST.toString());
    }

    public static float randomBetween(Random rand, float minimum, float maximum) {
        return rand.nextFloat() * (maximum - minimum) + minimum;
    }

    public static int getSplashTextColor(int defaultSplashColor) {
        return highContrastEnabled() ? WIDGET_CONFIGS.highContrastSplashTextColor.get() : defaultSplashColor;
    }

    public static int getSelectableTextColor(boolean selected, boolean active) {
        if (MellowConfigs.CLIENT_CONFIGS.legacyButtonColors.get() || Minecraft.getInstance().getResourcePackRepository().getSelectedIds().contains("programer_art")) {
            return !active ? WIDGET_CONFIGS.disabledLegacyWidgetTextColor.get() : (selected ? WIDGET_CONFIGS.highlightedLegacyWidgetTextColor.get() : WIDGET_CONFIGS.defaultLegacyWidgetTextColor.get());
        } else {
            return !active ? WIDGET_CONFIGS.disabledWidgetTextColor.get() : (selected ? WIDGET_CONFIGS.highlightedWidgetTextColor.get() : WIDGET_CONFIGS.defaultWidgetTextColor.get());
        }
    }

    public static int getSelectableTextShadowColor(boolean selected, boolean active) {
        int color;
        if (MellowConfigs.CLIENT_CONFIGS.legacyButtonColors.get() || Minecraft.getInstance().getResourcePackRepository().getSelectedIds().contains("programer_art")) {
            color = !active ? WIDGET_CONFIGS.disabledLegacyWidgetTextColor.get() : (selected ? WIDGET_CONFIGS.highlightedLegacyWidgetTextColor.get() : WIDGET_CONFIGS.defaultLegacyWidgetTextColor.get());
        } else {
            color = !active ? WIDGET_CONFIGS.disabledWidgetTextColor.get() : (selected ? WIDGET_CONFIGS.highlightedWidgetTextColor.get() : WIDGET_CONFIGS.defaultWidgetTextColor.get());
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
