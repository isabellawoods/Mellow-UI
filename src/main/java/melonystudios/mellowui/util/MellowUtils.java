package melonystudios.mellowui.util;

import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.screen.MellomedleyTitleScreen;
import melonystudios.mellowui.screen.backport.MUIControlsScreen;
import melonystudios.mellowui.screen.backport.StatisticsScreen;
import melonystudios.mellowui.screen.update.MUIOptionsScreen;
import melonystudios.mellowui.screen.update.MUIPackSelectionScreen;
import melonystudios.mellowui.screen.update.MellowModListScreen;
import melonystudios.mellowui.util.pack.HighContrastPack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.*;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.function.Consumer;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;

public class MellowUtils {
    public static final DateFormat WORLD_DATE_FORMAT = new SimpleDateFormat(); // "dd-MM-yyyy '('EEE') - 'HH:mm:ss"
    public static final int DEFAULT_TITLE_HEIGHT = 12;
    public static final int TABBED_TITLE_HEIGHT = 2;
    public static final int PAUSE_MENU_Y_OFFSET = -16;
    public static boolean LOADING_ERRORS = DebuggingFlags.DEBUG_FAKE_LOADING_ERRORS;

    public static Screen modList(Screen lastScreen) {
        Screen defaultScreen = new MellowModListScreen(lastScreen);
        switch (CLIENT_CONFIGS.modListStyle.get()) {
            case OPTION_2: return defaultScreen;
            case OPTION_3: {
                if (!ModList.get().isLoaded("catalogue")) return defaultScreen;
                try {
                    Class<?> screen = Class.forName("com.mrcrayfish.catalogue.client.screen.CatalogueModListScreen");
                    return (Screen) screen.newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {
                    return defaultScreen;
                }
            }
            case OPTION_1: default: return new ModListScreen(lastScreen);
        }
    }

    public static Screen options(Screen lastScreen, Minecraft minecraft) {
        if (CLIENT_CONFIGS.optionsStyle.get()) return new MUIOptionsScreen(lastScreen, minecraft.options);
        else return new OptionsScreen(lastScreen, minecraft.options);
    }

    public static Screen videoSettings(Screen lastScreen, Minecraft minecraft) {
        Screen defaultScreen = new VideoSettingsScreen(lastScreen, minecraft.options);
        if (CLIENT_CONFIGS.videoSettingsStyle.get() == ThreeStyles.OPTION_3) {
            return getExternalScreen("me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI", null, defaultScreen, lastScreen);
        } else {
            return defaultScreen;
        }
    }

    public static Screen controls(Screen lastScreen, Minecraft minecraft) {
        if (CLIENT_CONFIGS.controlsStyle.get()) return new MUIControlsScreen(lastScreen, minecraft.options);
        else return new ControlsScreen(lastScreen, minecraft.options);
    }

    public static Screen resourcePackList(Screen lastScreen, Minecraft minecraft, Consumer<ResourcePackList> packInfo) {
        ITextComponent title = new TranslationTextComponent("resourcePack.title");
        if (CLIENT_CONFIGS.packListStyle.get()) return new MUIPackSelectionScreen(lastScreen, minecraft.getResourcePackRepository(), packInfo, minecraft.getResourcePackDirectory(), title);
        else return new PackScreen(lastScreen, minecraft.getResourcePackRepository(), packInfo, minecraft.getResourcePackDirectory(), title);
    }

    public static Screen statistics(Screen lastScreen, Minecraft minecraft) {
        if (CLIENT_CONFIGS.statisticsStyle.get()) return new StatisticsScreen(lastScreen, minecraft.player.getStats());
        else return new StatsScreen(lastScreen, minecraft.player.getStats());
    }

    public static void switchTitleScreenStyle(Minecraft minecraft) {
        ThreeStyles menuStyle = MellowConfigs.CLIENT_CONFIGS.titleStyle.get();
        MellowConfigs.CLIENT_CONFIGS.titleStyle.set(ThreeStyles.byId(menuStyle.getId() + 1));
        switch (menuStyle) {
            case OPTION_3: minecraft.setScreen(new MellomedleyTitleScreen());
            case OPTION_1: case OPTION_2: default: minecraft.setScreen(new MainMenuScreen());
        }
    }

    public static void openLink(Screen lastScreen, String url, boolean showWarning) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new ConfirmOpenLinkScreen(confirmed -> {
            if (confirmed) Util.getPlatform().openUri(url);
            minecraft.setScreen(lastScreen);
        }, url, !showWarning));
    }

    public static Screen getExternalScreen(String classPath, @Nullable String modID, Screen fallbackScreen, Object... parameters) {
        if (modID == null || ModList.get().isLoaded(modID)) {
            try {
                Class<?> screen = Class.forName(classPath);
                return (Screen) screen.getConstructor(Screen.class).newInstance(parameters);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
                return fallbackScreen;
            }
        }
        return fallbackScreen;
    }

    /// @return Whether the {@linkplain MellowConfigs#defaultBackground **Default Background**} option is on,
    /// or if mod loading broke enough to load *Mellow UI*'s mixins, but not to the point the assets literally can't load.
    public static boolean defaultBackground() {
        return CLIENT_CONFIGS.defaultBackground.get() || LOADING_ERRORS;
    }

    /// @author **TeamTwilight/twilightforest**. Copied and adapted from [TFClientSetup](https://github.com/TeamTwilight/twilightforest/blob/1.16.x/src/main/java/twilightforest/client/TFClientSetup.java#L91).
    public static void addHighContrastPack() {
        Minecraft.getInstance().getResourcePackRepository().addPackFinder((packInfo, infoFactory) -> packInfo.accept(ResourcePackInfo.create(
                GUITextures.MUI_HIGH_CONTRAST.toString(), false, () -> new HighContrastPack(ModList.get()
                        .getModFileById(MellowUI.MOD_ID).getFile()), infoFactory, ResourcePackInfo.Priority.TOP, IPackNameDecorator.BUILT_IN)));
    }

    /// @return Whether the "*High Contrast*" (either `mellowui:high_contrast` or `melonylib:high_contrast`) resource pack is enabled.
    public static boolean highContrastEnabled() {
        Collection<String> selectedPacks = Minecraft.getInstance().getResourcePackRepository().getSelectedIds();
        return selectedPacks.contains(GUITextures.MUI_HIGH_CONTRAST.toString()) || selectedPacks.contains(GUITextures.LIBRARY_HIGH_CONTRAST.toString());
    }

    /// @return `true` whenever *Mellow UI*'s "*High Contrast*" resource pack is unavailable for some reason.
    public static boolean highContrastUnavailable() {
        return !Minecraft.getInstance().getResourcePackRepository().getAvailableIds().contains(GUITextures.MUI_HIGH_CONTRAST.toString());
    }
}
