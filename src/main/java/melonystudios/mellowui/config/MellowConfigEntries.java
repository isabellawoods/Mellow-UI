package melonystudios.mellowui.config;

import melonystudios.mellowui.config.option.*;
import melonystudios.mellowui.config.type.*;
import net.minecraft.client.ProgressOption;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fml.ModList;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;
import static melonystudios.mellowui.screen.RenderComponents.TOOLTIP_MAX_WIDTH;

public class MellowConfigEntries {
    // Tooltips
    public static final MutableComponent ADJACENT_TOOLTIP = new TranslatableComponent("config.mellowui.main_menu_mod_button.tooltip", new TranslatableComponent("config.mellowui.main_menu_mod_button.option_1.tooltip"));
    public static final MutableComponent MAIN_MENU_ICON_TOOLTIP = new TranslatableComponent("config.mellowui.main_menu_mod_button.tooltip", new TranslatableComponent("config.mellowui.main_menu_mod_button.option_2.tooltip"));
    public static final MutableComponent REPLACE_REALMS_TOOLTIP = new TranslatableComponent("config.mellowui.main_menu_mod_button.tooltip", new TranslatableComponent("config.mellowui.main_menu_mod_button.option_3.tooltip"));
    public static final MutableComponent BELOW_REALMS_TOOLTIP = new TranslatableComponent("config.mellowui.main_menu_mod_button.tooltip", new TranslatableComponent("config.mellowui.main_menu_mod_button.option_4.tooltip"));
    public static final MutableComponent PAUSE_MENU_ADJACENT_TOOLTIP = new TranslatableComponent("config.mellowui.pause_menu_mod_button.tooltip", new TranslatableComponent("config.mellowui.pause_menu_mod_button.option_1.tooltip"));
    public static final MutableComponent PAUSE_MENU_ICON_TOOLTIP = new TranslatableComponent("config.mellowui.pause_menu_mod_button.tooltip", new TranslatableComponent("config.mellowui.pause_menu_mod_button.option_2.tooltip"));
    public static final MutableComponent REPLACE_TOOLTIP = new TranslatableComponent("config.mellowui.pause_menu_mod_button.tooltip", new TranslatableComponent("config.mellowui.pause_menu_mod_button.option_3.tooltip"));
    public static final MutableComponent BELOW_OPTIONS_PM_TOOLTIP = new TranslatableComponent("config.mellowui.pause_menu_mod_button.tooltip", new TranslatableComponent("config.mellowui.pause_menu_mod_button.option_4.tooltip"));
    public static final MutableComponent MOD_LIST_STYLE_TOOLTIP = new TranslatableComponent("config.mellowui.mod_list_style.tooltip");
    public static final MutableComponent VIDEO_SETTINGS_STYLE_TOOLTIP = new TranslatableComponent("config.mellowui.video_settings_style.tooltip");
    public static final MutableComponent STATISTICS_STYLE_TOOLTIP = new TranslatableComponent("config.mellowui.statistics_style.tooltip");

    // Separators
    public static final SeparatorOption MENU_UPDATES_SEPARATOR = new SeparatorOption(new TranslatableComponent("separator.mellowui.menu_updates"));
    public static final SeparatorOption MISCELLANEOUS_SEPARATOR = new SeparatorOption(new TranslatableComponent("separator.mellowui.miscellaneous"));
    public static final SeparatorOption STYLES_SEPARATOR = new SeparatorOption(new TranslatableComponent("separator.mellowui.styles"));

    // Options
    public static final ProgressOption PANORAMA_CAMERA_PITCH = new ProgressOption("config.mellowui.panorama_camera_pitch", -90, 90, 1,
            (options) -> Double.valueOf(CLIENT_CONFIGS.panoramaCameraPitch.get()),
            (options, newValue) -> CLIENT_CONFIGS.panoramaCameraPitch.set((int) Math.round(newValue)),
            (options, slider) -> new TranslatableComponent("config.mellowui.panorama_camera_pitch", new TranslatableComponent("config.mellowui.panorama_camera_pitch.pitch", Math.round(slider.get(options)))),
            minecraft -> minecraft.font.split(new TranslatableComponent("config.mellowui.panorama_camera_pitch.tooltip"), TOOLTIP_MAX_WIDTH));
    public static final BooleanOption PANORAMA_BOBBING = new BooleanOption("config.mellowui.panorama_bobbing", new TranslatableComponent("config.mellowui.panorama_bobbing.tooltip"),
            options -> CLIENT_CONFIGS.panoramaBobbing.get(), (options, newValue) -> CLIENT_CONFIGS.panoramaBobbing.set(newValue));
    public static final IterableOption MAIN_MENU_MOD_BUTTON = new IterableOption("config.mellowui.main_menu_mod_button",
            (options, identifier) -> CLIENT_CONFIGS.mainMenuModButton.set(FourStyles.byId(CLIENT_CONFIGS.mainMenuModButton.get().getId() + identifier)),
            (options, option) -> {
                switch (CLIENT_CONFIGS.mainMenuModButton.get()) {
                    case OPTION_1:
                        option.setTooltip(ADJACENT_TOOLTIP);
                        break;
                    case OPTION_2:
                        option.setTooltip(MAIN_MENU_ICON_TOOLTIP);
                        break;
                    case OPTION_3:
                        option.setTooltip(REPLACE_REALMS_TOOLTIP);
                        break;
                    case OPTION_4:
                        option.setTooltip(BELOW_REALMS_TOOLTIP);
                }
                return new TranslatableComponent("config.mellowui.main_menu_mod_button", new TranslatableComponent("config.mellowui.main_menu_mod_button." + CLIENT_CONFIGS.mainMenuModButton.get().toString()));
            });
    public static final IterableOption PAUSE_MENU_MOD_BUTTON = new IterableOption("config.mellowui.pause_menu_mod_button",
            (options, identifier) -> CLIENT_CONFIGS.pauseMenuModButton.set(FourStyles.byId(CLIENT_CONFIGS.pauseMenuModButton.get().getId() + identifier)),
            (options, option) -> {
                switch (CLIENT_CONFIGS.pauseMenuModButton.get()) {
                    case OPTION_1:
                        option.setTooltip(PAUSE_MENU_ADJACENT_TOOLTIP);
                        break;
                    case OPTION_2:
                        option.setTooltip(PAUSE_MENU_ICON_TOOLTIP);
                        break;
                    case OPTION_3:
                        option.setTooltip(REPLACE_TOOLTIP);
                        break;
                    case OPTION_4:
                        option.setTooltip(BELOW_OPTIONS_PM_TOOLTIP);
                }
                return new TranslatableComponent("config.mellowui.pause_menu_mod_button", new TranslatableComponent("config.mellowui.pause_menu_mod_button." + CLIENT_CONFIGS.pauseMenuModButton.get().toString()));
            });
    public static final BooleanOption LEGACY_BUTTON_COLORS = new BooleanOption("config.mellowui.legacy_button_colors", new TranslatableComponent("config.mellowui.legacy_button_colors.tooltip"),
            options -> CLIENT_CONFIGS.legacyButtonColors.get(), (options, newValue) -> CLIENT_CONFIGS.legacyButtonColors.set(newValue));
    public static final BooleanOption DISABLE_BRANDING = new BooleanOption("config.mellowui.disable_branding", new TranslatableComponent("config.mellowui.disable_branding.tooltip"),
            options -> CLIENT_CONFIGS.disableBranding.get(), (options, newValue) -> CLIENT_CONFIGS.disableBranding.set(newValue));
    public static final StyleBooleanOption SCREEN_BACKGROUND_STYLE = new StyleBooleanOption("config.mellowui.screen_background_style", new TranslatableComponent("config.mellowui.screen_background_style.tooltip"),
            options -> CLIENT_CONFIGS.screenBackgroundStyle.get(), (options, newValue) -> CLIENT_CONFIGS.screenBackgroundStyle.set(newValue));
    public static final StyleBooleanOption LIST_BACKGROUND_STYLE = new StyleBooleanOption("config.mellowui.list_background_style", new TranslatableComponent("config.mellowui.list_background_style.tooltip"),
            options -> CLIENT_CONFIGS.listBackgroundStyle.get(), (options, newValue) -> CLIENT_CONFIGS.listBackgroundStyle.set(newValue));
    public static final IterableOption LOGO_STYLE = new IterableOption("config.mellowui.logo_style", new TranslatableComponent("config.mellowui.logo_style.tooltip"),
            (options, identifier) -> CLIENT_CONFIGS.logoStyle.set(LogoStyles.byId(CLIENT_CONFIGS.logoStyle.get().getId() + identifier)),
            (options, option) -> new TranslatableComponent("config.mellowui.logo_style." + CLIENT_CONFIGS.logoStyle.get().toString(), new TranslatableComponent("config.mellowui.logo_style")));
    public static final IterableOption TITLE_STYLE = new IterableOption("config.mellowui.title_style", new TranslatableComponent("config.mellowui.title_style.tooltip"),
            (options, identifier) -> CLIENT_CONFIGS.titleStyle.set(ThreeStyles.byId(CLIENT_CONFIGS.titleStyle.get().getId() + identifier)),
            (options, option) -> new TranslatableComponent("config.mellowui." + CLIENT_CONFIGS.titleStyle.get().toString() + "_style", new TranslatableComponent("config.mellowui.title_style")));
    public static final StyleBooleanOption PAUSE_STYLE = new StyleBooleanOption("config.mellowui.pause_style", new TranslatableComponent("config.mellowui.pause_style.tooltip"),
            options -> CLIENT_CONFIGS.pauseStyle.get(), (options, newValue) -> CLIENT_CONFIGS.pauseStyle.set(newValue));
    public static final StyleBooleanOption CREATE_NEW_WORLD_STYLE = new StyleBooleanOption("config.mellowui.create_new_world_style", new TranslatableComponent("config.mellowui.create_new_world_style.tooltip"),
            options -> CLIENT_CONFIGS.createNewWorldStyle.get(), (options, newValue) -> CLIENT_CONFIGS.createNewWorldStyle.set(newValue));
    public static final StyleBooleanOption WORLD_LOADING_STYLE = new StyleBooleanOption("config.mellowui.world_loading_style", new TranslatableComponent("config.mellowui.world_loading_style.tooltip"),
            options -> CLIENT_CONFIGS.worldLoadingStyle.get(), (options, newValue) -> CLIENT_CONFIGS.worldLoadingStyle.set(newValue));
    public static final StyleBooleanOption OPTIONS_STYLE = new StyleBooleanOption("config.mellowui.options_style", new TranslatableComponent("config.mellowui.options_style.tooltip"),
            options -> CLIENT_CONFIGS.optionsStyle.get(), (options, newValue) -> CLIENT_CONFIGS.optionsStyle.set(newValue));
    public static final StyleBooleanOption ONLINE_OPTIONS_STYLE = new StyleBooleanOption("config.mellowui.online_options_style", new TranslatableComponent("config.mellowui.online_options_style.tooltip"),
            options -> CLIENT_CONFIGS.onlineOptionsStyle.get(), (options, newValue) -> CLIENT_CONFIGS.onlineOptionsStyle.set(newValue));
    public static final StyleBooleanOption SKIN_CUSTOMIZATION_STYLE = new StyleBooleanOption("config.mellowui.skin_customization_style", new TranslatableComponent("config.mellowui.skin_customization_style.tooltip"),
            options -> CLIENT_CONFIGS.skinCustomizationStyle.get(), (options, newValue) -> CLIENT_CONFIGS.skinCustomizationStyle.set(newValue));
    public static final StyleBooleanOption MUSIC_AND_SOUNDS_STYLE = new StyleBooleanOption("config.mellowui.music_and_sounds_style", new TranslatableComponent("config.mellowui.music_and_sounds_style.tooltip"),
            options -> CLIENT_CONFIGS.musicAndSoundsStyle.get(), (options, newValue) -> CLIENT_CONFIGS.musicAndSoundsStyle.set(newValue));
    public static final IterableOption VIDEO_SETTINGS_STYLE = new IterableOption("config.mellowui.video_settings_style",
            (options, identifier) -> {
                CLIENT_CONFIGS.videoSettingsStyle.set(ThreeStyles.byId(CLIENT_CONFIGS.videoSettingsStyle.get().getId() + identifier));
                if (!ModList.get().isLoaded("rubidium") && CLIENT_CONFIGS.videoSettingsStyle.get() == ThreeStyles.OPTION_3) CLIENT_CONFIGS.videoSettingsStyle.set(ThreeStyles.OPTION_1);
            },
            (options, option) -> {
                option.setTooltip(VIDEO_SETTINGS_STYLE_TOOLTIP);
                return new TranslatableComponent("config.mellowui.video_settings_style", new TranslatableComponent("config.mellowui.video_settings_style." + CLIENT_CONFIGS.videoSettingsStyle.get().toString()));
            });
    public static final StyleBooleanOption CONTROLS_STYLE = new StyleBooleanOption("config.mellowui.controls_style", new TranslatableComponent("config.mellowui.controls_style.tooltip"),
            options -> CLIENT_CONFIGS.controlsStyle.get(), (options, newValue) -> CLIENT_CONFIGS.controlsStyle.set(newValue));
    public static final StyleBooleanOption MOUSE_SETTINGS_STYLE = new StyleBooleanOption("config.mellowui.mouse_settings_style", new TranslatableComponent("config.mellowui.mouse_settings_style.tooltip"),
            options -> CLIENT_CONFIGS.mouseSettingsStyle.get(), (options, newValue) -> CLIENT_CONFIGS.mouseSettingsStyle.set(newValue));
    public static final StyleBooleanOption CHAT_SETTINGS_STYLE = new StyleBooleanOption("config.mellowui.chat_settings_style", new TranslatableComponent("config.mellowui.chat_settings_style.tooltip"),
            options -> CLIENT_CONFIGS.chatSettingsStyle.get(), (options, newValue) -> CLIENT_CONFIGS.chatSettingsStyle.set(newValue));
    public static final StyleBooleanOption PACK_LIST_STYLE = new StyleBooleanOption("config.mellowui.pack_list_style", new TranslatableComponent("config.mellowui.pack_list_style.tooltip"),
            options -> CLIENT_CONFIGS.packListStyle.get(), (options, newValue) -> CLIENT_CONFIGS.packListStyle.set(newValue));
    public static final StyleBooleanOption ACCESSIBILITY_SETTINGS_STYLE = new StyleBooleanOption("config.mellowui.accessibility_settings_style", new TranslatableComponent("config.mellowui.accessibility_settings_style.tooltip"),
            options -> CLIENT_CONFIGS.accessibilitySettingsStyle.get(), (options, newValue) -> CLIENT_CONFIGS.accessibilitySettingsStyle.set(newValue));
    public static final StyleBooleanOption OUT_OF_MEMORY_STYLE = new StyleBooleanOption("config.mellowui.out_of_memory_style", new TranslatableComponent("config.mellowui.out_of_memory_style.tooltip"),
            options -> CLIENT_CONFIGS.outOfMemoryStyle.get(), (options, newValue) -> CLIENT_CONFIGS.outOfMemoryStyle.set(newValue));
    public static final IterableOption STATISTICS_STYLE = new IterableOption("config.mellowui.statistics_style",
            (options, identifier) -> {
                CLIENT_CONFIGS.statisticsStyle.set(ThreeStyles.byId(CLIENT_CONFIGS.statisticsStyle.get().getId() + identifier));
                if (!ModList.get().isLoaded("betterstats") && CLIENT_CONFIGS.statisticsStyle.get() == ThreeStyles.OPTION_3) CLIENT_CONFIGS.statisticsStyle.set(ThreeStyles.OPTION_1);
            },
            (options, option) -> {
                option.setTooltip(STATISTICS_STYLE_TOOLTIP);
                return new TranslatableComponent("config.mellowui.statistics_style", new TranslatableComponent("config.mellowui.statistics_style." + CLIENT_CONFIGS.statisticsStyle.get().toString()));
            });
    public static final IterableOption MOD_LIST_STYLE = new IterableOption("config.mellowui.mod_list_style",
            (options, identifier) -> {
                CLIENT_CONFIGS.modListStyle.set(ThreeStyles.byId(CLIENT_CONFIGS.modListStyle.get().getId() + identifier));
                if (CLIENT_CONFIGS.modListStyle.get() == ThreeStyles.OPTION_3 && !ModList.get().isLoaded("catalogue")) CLIENT_CONFIGS.modListStyle.set(ThreeStyles.OPTION_1);
            },
            (options, option) -> {
                option.setTooltip(MOD_LIST_STYLE_TOOLTIP);
                return new TranslatableComponent("config.mellowui.mod_list_style", new TranslatableComponent("config.mellowui.mod_list_style." + CLIENT_CONFIGS.modListStyle.get().toString()));
            });
    public static final StyleBooleanOption LOADING_ERRORS_STYLE = new StyleBooleanOption("config.mellowui.loading_errors_style", new TranslatableComponent("config.mellowui.loading_errors_style.tooltip"),
            options -> CLIENT_CONFIGS.loadingErrorsStyle.get(), (options, newValue) -> CLIENT_CONFIGS.loadingErrorsStyle.set(newValue));
    public static final BooleanOption REPLACE_REALMS_NOTIFICATIONS = new BooleanOption("config.mellowui.replace_realms_notifications", new TranslatableComponent("config.mellowui.replace_realms_notifications.tooltip"),
            options -> CLIENT_CONFIGS.replaceRealmsNotifications.get(), (options, newValue) -> CLIENT_CONFIGS.replaceRealmsNotifications.set(newValue));
    public static final StyleBooleanOption SPLASH_TEXT_POSITION = new StyleBooleanOption("config.mellowui.splash_text_position", new TranslatableComponent("config.mellowui.splash_text_position.tooltip"),
            options -> CLIENT_CONFIGS.splashTextPosition.get(), (options, newValue) -> CLIENT_CONFIGS.splashTextPosition.set(newValue));
    public static final BooleanOption SCROLLING_TEXT = new BooleanOption("config.mellowui.scrolling_text", new TranslatableComponent("config.mellowui.scrolling_text.tooltip"),
            options -> CLIENT_CONFIGS.scrollingText.get(), (options, newValue) -> CLIENT_CONFIGS.scrollingText.set(newValue));
    public static final EditListConfigOption<String> CLASSIFIED_AS_CONTAINERS = new EditListConfigOption<>("config.mellowui.classified_as_containers",
            new TranslatableComponent("config.mellowui.classified_as_containers.tooltip"), CLIENT_CONFIGS.classifiedAsContainers);
    public static final EditListConfigOption<String> OVERSIZED_IN_GUI = new EditListConfigOption<>("config.mellowui.oversized_in_gui",
            new TranslatableComponent("config.mellowui.oversized_in_gui.tooltip"), CLIENT_CONFIGS.oversizedInGUI);
    public static final BooleanOption CULL_OVERSIZED_ITEMS = new BooleanOption("config.mellowui.cull_oversized_items", new TranslatableComponent("config.mellowui.cull_oversized_items.tooltip"),
            options -> CLIENT_CONFIGS.cullOversizedItems.get(), (options, newValue) -> CLIENT_CONFIGS.cullOversizedItems.set(newValue));
    public static final BooleanOption BACKGROUND_SHADERS = new BooleanOption("config.mellowui.background_shaders", new TranslatableComponent("config.mellowui.background_shaders.tooltip"),
            options -> CLIENT_CONFIGS.backgroundShaders.get(), (options, newValue) -> CLIENT_CONFIGS.backgroundShaders.set(newValue));
    public static final BooleanOption BLURRY_CONTAINERS = new BooleanOption("config.mellowui.blurry_containers", new TranslatableComponent("config.mellowui.blurry_containers.tooltip"),
            options -> CLIENT_CONFIGS.blurryContainers.get(), (options, newValue) -> CLIENT_CONFIGS.blurryContainers.set(newValue));
    public static final BooleanOption DEFAULT_BACKGROUND = new BooleanOption("config.mellowui.default_background", new TranslatableComponent("config.mellowui.default_background.tooltip"),
            options -> CLIENT_CONFIGS.defaultBackground.get(), (options, newValue) -> CLIENT_CONFIGS.defaultBackground.set(newValue));
    public static final BooleanOption GRADIENT_BACKGROUND = new BooleanOption("config.mellowui.gradient_background", new TranslatableComponent("config.mellowui.gradient_background.tooltip"),
            options -> CLIENT_CONFIGS.gradientBackground.get(), (options, newValue) -> CLIENT_CONFIGS.gradientBackground.set(newValue));
    public static final BooleanOption LOG_GL_ERRORS = new BooleanOption("config.mellowui.log_gl_errors", new TranslatableComponent("config.mellowui.log_gl_errors.tooltip"),
            options -> CLIENT_CONFIGS.logGLErrors.get(), (options, newValue) -> CLIENT_CONFIGS.logGLErrors.set(newValue));
}
