package melonystudios.mellowui.element.widget;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.screen.MellomedleyTitleScreen;
import melonystudios.mellowui.screen.backport.MUIControlsScreen;
import melonystudios.mellowui.screen.backport.StatisticsScreen;
import melonystudios.mellowui.screen.update.MUILanguageScreen;
import melonystudios.mellowui.screen.update.MUIOptionsScreen;
import melonystudios.mellowui.screen.update.MUIPackSelectionScreen;
import melonystudios.mellowui.screen.update.MellowModListScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.*;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;

public class WidgetLocations {
    public static void switchTitleScreenStyle(Minecraft minecraft) {
        ThreeStyles titleStyle = MellowConfigs.CLIENT_CONFIGS.titleStyle.get();
        MellowConfigs.CLIENT_CONFIGS.titleStyle.set(ThreeStyles.byId(titleStyle.getId() + 1));
        switch (titleStyle) {
            case OPTION_3: minecraft.setScreen(new MellomedleyTitleScreen());
            case OPTION_1: case OPTION_2: default: minecraft.setScreen(new MainMenuScreen());
        }
    }

    public static Screen openOptions(Screen lastScreen, Minecraft minecraft) {
        if (CLIENT_CONFIGS.optionsStyle.get()) return new MUIOptionsScreen(lastScreen, minecraft.options);
        else return new OptionsScreen(lastScreen, minecraft.options);
    }

    public static Screen openVideoSettings(Screen lastScreen, Minecraft minecraft) {
        Screen defaultScreen = new VideoSettingsScreen(lastScreen, minecraft.options);
        if (CLIENT_CONFIGS.videoSettingsStyle.get() == ThreeStyles.OPTION_3) {
            return getExternalScreen("me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI", null, defaultScreen, lastScreen);
        } else {
            return defaultScreen;
        }
    }

    public static Screen openControls(Screen lastScreen, Minecraft minecraft) {
        if (CLIENT_CONFIGS.controlsStyle.get()) return new MUIControlsScreen(lastScreen, minecraft.options);
        else return new ControlsScreen(lastScreen, minecraft.options);
    }

    public static Screen openLanguage(Screen lastScreen, Minecraft minecraft) {
        if (CLIENT_CONFIGS.languageStyle.get()) return new MUILanguageScreen(lastScreen, minecraft.options, minecraft.getLanguageManager());
        else return new LanguageScreen(lastScreen, minecraft.options, minecraft.getLanguageManager());
    }

    public static Screen openResourcePacksList(Screen lastScreen, Minecraft minecraft, Consumer<ResourcePackList> outputList) {
        ITextComponent title = new TranslationTextComponent("resourcePack.title");
        if (CLIENT_CONFIGS.packListStyle.get()) return new MUIPackSelectionScreen(lastScreen, minecraft.getResourcePackRepository(), outputList, minecraft.getResourcePackDirectory(), title);
        else return new PackScreen(lastScreen, minecraft.getResourcePackRepository(), outputList, minecraft.getResourcePackDirectory(), title);
    }

    public static Screen openStatistics(Screen lastScreen, Minecraft minecraft) {
        if (CLIENT_CONFIGS.statisticsStyle.get()) return new StatisticsScreen(lastScreen, minecraft.player.getStats());
        else return new StatsScreen(lastScreen, minecraft.player.getStats());
    }

    public static Screen openModList(Screen lastScreen) {
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
}
