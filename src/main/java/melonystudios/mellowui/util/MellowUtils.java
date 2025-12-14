package melonystudios.mellowui.util;

import com.google.gson.JsonObject;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.resource.flair.Flair;
import melonystudios.mellowui.resource.panorama.BobbingPitch;
import melonystudios.mellowui.resource.panorama.ConstantPitch;
import melonystudios.mellowui.resource.panorama.Panorama;
import melonystudios.mellowui.resource.panorama.PitchOverrider;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraftforge.client.gui.ModListScreen;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;

public class MellowUtils {
    // Resource pack entries
    public static final Map<ResourceLocation, Flair> FLAIRS = new HashMap<>();
    public static final Map<ResourceLocation, Panorama> PANORAMAS = new HashMap<>();
    public static final Map<ResourceLocation, Function<JsonObject, PitchOverrider>> OVERRIDERS = Util.make(new HashMap<>(), map -> {
        map.put(MellowUI.mellowUI("constant"), ConstantPitch.DEFAULT::fromJSON);
        map.put(MellowUI.mellowUI("bobbing"), BobbingPitch.DEFAULT::fromJSON);
    });
    public static boolean LOADING_ERRORS = false;

    public static final DateFormat WORLD_DATE_FORMAT = new SimpleDateFormat(); // "dd-MM-yyyy '('EEE') - 'HH:mm:ss"
    public static final int DEFAULT_TITLE_HEIGHT = 12;
    public static final int TABBED_TITLE_HEIGHT = 2;
    public static final int PAUSE_MENU_Y_OFFSET = -16;

    @SuppressWarnings("deprecation")
    public static Screen modList(Screen lastScreen) {
        Screen defaultScreen = new MellowModListScreen(lastScreen);
        return switch (CLIENT_CONFIGS.modListStyle.get()) {
            case OPTION_2 -> defaultScreen;
            case OPTION_3 -> {
                if (!ModList.get().isLoaded("catalogue")) yield defaultScreen;
                try {
                    Class<?> screen = Class.forName("com.mrcrayfish.catalogue.client.screen.CatalogueModListScreen");
                    yield (Screen) screen.newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {
                    yield defaultScreen;
                }
            }
            default -> new ModListScreen(lastScreen);
        };
    }

    public static Screen onlineOptions(Screen lastScreen, Minecraft minecraft) {
        if (CLIENT_CONFIGS.onlineOptionsStyle.get()) return new MUIOnlineOptionsScreen(lastScreen, minecraft.options);
        else return new OnlineOptionsScreen(lastScreen, minecraft.options);
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

    public static Screen resourcePackList(Screen lastScreen, Minecraft minecraft, Consumer<PackRepository> packInfo) {
        Component title = new TranslatableComponent("resourcePack.title");
        if (CLIENT_CONFIGS.packListStyle.get()) return new MUIPackSelectionScreen(lastScreen, minecraft.getResourcePackRepository(), packInfo, minecraft.getResourcePackDirectory(), title);
        else return new PackSelectionScreen(lastScreen, minecraft.getResourcePackRepository(), packInfo, minecraft.getResourcePackDirectory(), title);
    }

    public static Screen statistics(Screen lastScreen, Minecraft minecraft) {
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

    public static void switchTitleScreenStyle(Minecraft minecraft) {
        ThreeStyles menuStyle = MellowConfigs.CLIENT_CONFIGS.titleStyle.get();
        MellowConfigs.CLIENT_CONFIGS.titleStyle.set(ThreeStyles.byId(menuStyle.getId() + 1));
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
