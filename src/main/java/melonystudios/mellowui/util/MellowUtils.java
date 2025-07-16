package melonystudios.mellowui.util;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.screen.MellomedleyTitleScreen;
import melonystudios.mellowui.screen.backport.MUIControlsScreen;
import melonystudios.mellowui.screen.update.MUIOnlineOptionsScreen;
import melonystudios.mellowui.screen.update.MUIModListScreen;
import melonystudios.mellowui.screen.update.MUIOptionsScreen;
import melonystudios.mellowui.screen.update.MUIPackSelectionScreen;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraftforge.client.gui.ModListScreen;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Random;
import java.util.function.Consumer;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;
import static melonystudios.mellowui.config.WidgetConfigs.WIDGET_CONFIGS;
import static net.minecraft.util.FastColor.ARGB32.*;

public class MellowUtils {
    public static final DateFormat WORLD_DATE_FORMAT = new SimpleDateFormat(); // "dd-MM-yyyy '('EEE') - 'HH:mm:ss"
    public static final String PROGRAMMER_ART_ID = "programer_art";
    public static final int DEFAULT_TITLE_HEIGHT = 12;
    public static final int TABBED_TITLE_HEIGHT = 2;
    public static final int PAUSE_MENU_Y_OFFSET = 0;

    public static Screen modList(Screen lastScreen) {
        switch (CLIENT_CONFIGS.modListStyle.get()) {
            case OPTION_2: return new MUIModListScreen(lastScreen);
            case OPTION_3: {
                if (!ModList.get().isLoaded("catalogue")) return new MUIModListScreen(lastScreen);
                try {
                    Class<?> screen = Class.forName("com.mrcrayfish.catalogue.client.screen.CatalogueModListScreen");
                    return (Screen) screen.getConstructor().newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException| InvocationTargetException ignored) {
                    return new MUIModListScreen(lastScreen);
                }
            }
            case OPTION_1: default: return new ModListScreen(lastScreen);
        }
    }

    public static Screen onlineOptions(Screen lastScreen, Minecraft minecraft) {
        if (CLIENT_CONFIGS.updateOnlineOptionsMenu.get()) return new MUIOnlineOptionsScreen(lastScreen, minecraft.options);
        else return new OnlineOptionsScreen(lastScreen, minecraft.options);
    }

    public static Screen options(Screen lastScreen, Minecraft minecraft) {
        if (CLIENT_CONFIGS.updateOptionsMenu.get()) return new MUIOptionsScreen(lastScreen, minecraft.options);
        else return new OptionsScreen(lastScreen, minecraft.options);
    }

    public static Screen videoSettings(Screen lastScreen, Minecraft minecraft) {
        if (CLIENT_CONFIGS.updateVideoSettingsMenu.get() == ThreeStyles.OPTION_3) {
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
        if (CLIENT_CONFIGS.updateControlsMenu.get()) return new MUIControlsScreen(lastScreen, minecraft.options);
        else return new ControlsScreen(lastScreen, minecraft.options);
    }

    public static Screen resourcePackList(Screen lastScreen, Minecraft minecraft, Consumer<PackRepository> packInfo) {
        Component title = new TranslatableComponent("resourcePack.title");
        if (CLIENT_CONFIGS.updatePackMenu.get()) return new MUIPackSelectionScreen(lastScreen, minecraft.getResourcePackRepository(), packInfo, minecraft.getResourcePackDirectory(), title);
        else return new PackSelectionScreen(lastScreen, minecraft.getResourcePackRepository(), packInfo, minecraft.getResourcePackDirectory(), title);
    }

    public static void switchTitleScreenStyle(Minecraft minecraft) {
        ThreeStyles menuStyle = MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get();
        MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.set(ThreeStyles.byId(menuStyle.getId() + 1));
        switch (menuStyle) {
            case OPTION_3: minecraft.setScreen(new MellomedleyTitleScreen());
            case OPTION_1: case OPTION_2: default: minecraft.setScreen(new TitleScreen());
        }
    }

    public static void openLink(Screen lastScreen, String url, boolean showWarning) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new ConfirmLinkScreen(confirmed -> {
            if (confirmed) Util.getPlatform().openUri(url);
            minecraft.setScreen(lastScreen);
        }, url, !showWarning));
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
        if (CLIENT_CONFIGS.legacyButtonColors.get() || Minecraft.getInstance().getResourcePackRepository().getSelectedIds().contains(PROGRAMMER_ART_ID)) {
            return !active ? WIDGET_CONFIGS.disabledLegacyWidgetTextColor.get() : (selected ? WIDGET_CONFIGS.highlightedLegacyWidgetTextColor.get() : WIDGET_CONFIGS.defaultLegacyWidgetTextColor.get());
        } else {
            return !active ? WIDGET_CONFIGS.disabledWidgetTextColor.get() : (selected ? WIDGET_CONFIGS.highlightedWidgetTextColor.get() : WIDGET_CONFIGS.defaultWidgetTextColor.get());
        }
    }

    public static int getSelectableTextShadowColor(boolean selected, boolean active) {
        int color;
        if (CLIENT_CONFIGS.legacyButtonColors.get() || Minecraft.getInstance().getResourcePackRepository().getSelectedIds().contains(PROGRAMMER_ART_ID)) {
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
