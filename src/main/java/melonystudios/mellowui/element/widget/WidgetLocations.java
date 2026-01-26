package melonystudios.mellowui.element.widget;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.screen.MellomedleyTitleScreen;
import melonystudios.mellowui.screen.backport.MUIControlsScreen;
import melonystudios.mellowui.screen.backport.StatisticsScreen;
import melonystudios.mellowui.screen.update.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraftforge.client.gui.ModListScreen;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;

public class WidgetLocations {
    public static void switchTitleScreenStyle(Minecraft minecraft) {
        ThreeStyles menuStyle = MellowConfigs.CLIENT_CONFIGS.titleStyle.get();
        MellowConfigs.CLIENT_CONFIGS.titleStyle.set(ThreeStyles.byId(menuStyle.getId() + 1));
        switch (menuStyle) {
            case OPTION_3: minecraft.setScreen(new MellomedleyTitleScreen());
            case OPTION_1: case OPTION_2: default: minecraft.setScreen(new TitleScreen());
        }
    }

    public static Screen openOptions(Screen lastScreen, Minecraft minecraft) {
        if (CLIENT_CONFIGS.optionsStyle.get()) return new MUIOptionsScreen(lastScreen, minecraft.options);
        else return new OptionsScreen(lastScreen, minecraft.options);
    }

    public static Screen openOnlineOptions(Screen lastScreen, Minecraft minecraft) {
        if (CLIENT_CONFIGS.onlineOptionsStyle.get()) return new MUIOnlineOptionsScreen(lastScreen, minecraft.options);
        else return new OnlineOptionsScreen(lastScreen, minecraft.options);
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
        if (CLIENT_CONFIGS.languageStyle.get()) return new LanguageScreen(lastScreen, minecraft.options, minecraft.getLanguageManager());
        else return new LanguageSelectScreen(lastScreen, minecraft.options, minecraft.getLanguageManager());
    }

    public static Screen openResourcePacksList(Screen lastScreen, Minecraft minecraft, Consumer<PackRepository> packInfo) {
        Component title = new TranslatableComponent("resourcePack.title");
        if (CLIENT_CONFIGS.packListStyle.get()) return new MUIPackSelectionScreen(lastScreen, minecraft.getResourcePackRepository(), packInfo, minecraft.getResourcePackDirectory(), title);
        else return new PackSelectionScreen(lastScreen, minecraft.getResourcePackRepository(), packInfo, minecraft.getResourcePackDirectory(), title);
    }

    public static Screen openStatistics(Screen lastScreen, Minecraft minecraft) {
        Screen defaultScreen = new StatisticsScreen(lastScreen, minecraft.player.getStats());
        return switch (CLIENT_CONFIGS.statisticsStyle.get()) {
            case OPTION_2 -> defaultScreen;
            case OPTION_3 -> {
                if (!ModList.get().isLoaded("betterstats")) yield defaultScreen;
                try {
                    Class<?> screen = Class.forName("io.github.thecsdev.betterstats.client.gui.screen.BetterStatsScreen");
                    yield (Screen) screen.getConstructor(Screen.class).newInstance(lastScreen);
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
                    yield defaultScreen;
                }
            }
            default -> new StatsScreen(lastScreen, minecraft.player.getStats());
        };
    }

    public static Screen openModList(Screen lastScreen) {
        Screen defaultScreen = new MellowModListScreen(lastScreen);
        return switch (CLIENT_CONFIGS.modListStyle.get()) {
            case OPTION_2 -> defaultScreen;
            case OPTION_3 -> {
                if (!ModList.get().isLoaded("catalogue")) yield defaultScreen;
                try {
                    Class<?> screen = Class.forName("com.mrcrayfish.catalogue.client.screen.CatalogueModListScreen");
                    yield (Screen) screen.getDeclaredConstructor().newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
                    yield defaultScreen;
                }
            }
            default -> new ModListScreen(lastScreen);
        };
    }

    public static void openLink(Screen lastScreen, String url, boolean showWarning) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new ConfirmLinkScreen(confirmed -> {
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
}
