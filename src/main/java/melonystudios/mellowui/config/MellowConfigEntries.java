package melonystudios.mellowui.config;

import melonystudios.mellowui.config.option.*;
import melonystudios.mellowui.config.type.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;
import static melonystudios.mellowui.element.RenderComponents.TOOLTIP_MAX_WIDTH;

public class MellowConfigEntries {
    // Tooltips
    public static final IFormattableTextComponent ADJACENT_TOOLTIP = new TranslationTextComponent("config.mellowui.main_menu_mod_button.tooltip", new TranslationTextComponent("config.mellowui.main_menu_mod_button.option_1.tooltip"));
    public static final IFormattableTextComponent MAIN_MENU_ICON_TOOLTIP = new TranslationTextComponent("config.mellowui.main_menu_mod_button.tooltip", new TranslationTextComponent("config.mellowui.main_menu_mod_button.option_2.tooltip"));
    public static final IFormattableTextComponent REPLACE_REALMS_TOOLTIP = new TranslationTextComponent("config.mellowui.main_menu_mod_button.tooltip", new TranslationTextComponent("config.mellowui.main_menu_mod_button.option_3.tooltip"));
    public static final IFormattableTextComponent BELOW_REALMS_TOOLTIP = new TranslationTextComponent("config.mellowui.main_menu_mod_button.tooltip", new TranslationTextComponent("config.mellowui.main_menu_mod_button.option_4.tooltip"));
    public static final IFormattableTextComponent PAUSE_MENU_ADJACENT_TOOLTIP = new TranslationTextComponent("config.mellowui.pause_menu_mod_button.tooltip", new TranslationTextComponent("config.mellowui.pause_menu_mod_button.option_1.tooltip"));
    public static final IFormattableTextComponent PAUSE_MENU_ICON_TOOLTIP = new TranslationTextComponent("config.mellowui.pause_menu_mod_button.tooltip", new TranslationTextComponent("config.mellowui.pause_menu_mod_button.option_2.tooltip"));
    public static final IFormattableTextComponent REPLACE_TOOLTIP = new TranslationTextComponent("config.mellowui.pause_menu_mod_button.tooltip", new TranslationTextComponent("config.mellowui.pause_menu_mod_button.option_3.tooltip"));
    public static final IFormattableTextComponent BELOW_OPTIONS_PM_TOOLTIP = new TranslationTextComponent("config.mellowui.pause_menu_mod_button.tooltip", new TranslationTextComponent("config.mellowui.pause_menu_mod_button.option_4.tooltip"));
    public static final IFormattableTextComponent VIDEO_SETTINGS_STYLE_TOOLTIP = new TranslationTextComponent("config.mellowui.video_settings_style.tooltip");
    public static final IFormattableTextComponent MOD_LIST_STYLE_TOOLTIP = new TranslationTextComponent("config.mellowui.mod_list_style.tooltip");

    // Separators
    public static final SeparatorOption MENU_UPDATES_SEPARATOR = new SeparatorOption(new TranslationTextComponent("separator.mellowui.menu_updates"));
    public static final SeparatorOption MISCELLANEOUS_SEPARATOR = new SeparatorOption(new TranslationTextComponent("separator.mellowui.miscellaneous"));
    public static final SeparatorOption STYLES_SEPARATOR = new SeparatorOption(new TranslationTextComponent("separator.mellowui.styles"));

    // Options
    public static final SliderPercentageOption PANORAMA_CAMERA_PITCH = new SliderPercentageOption("config.mellowui.panorama_camera_pitch", -90, 90, 1,
            (options) -> Double.valueOf(CLIENT_CONFIGS.panoramaCameraPitch.get()),
            (options, newValue) -> CLIENT_CONFIGS.panoramaCameraPitch.set((int) Math.round(newValue)),
            (options, slider) -> {
                slider.setTooltip(Minecraft.getInstance().font.split(new TranslationTextComponent("config.mellowui.panorama_camera_pitch.tooltip"), TOOLTIP_MAX_WIDTH));
                return new TranslationTextComponent("config.mellowui.panorama_camera_pitch", new TranslationTextComponent("config.mellowui.panorama_camera_pitch.pitch", Math.round(slider.get(options))));
            });
    public static final BooleanOption PANORAMA_BOBBING = new BooleanOption("config.mellowui.panorama_bobbing", new TranslationTextComponent("config.mellowui.panorama_bobbing.tooltip"),
            options -> CLIENT_CONFIGS.panoramaBobbing.get(), (options, newValue) -> CLIENT_CONFIGS.panoramaBobbing.set(newValue));
    public static final IteratableOption MAIN_MENU_MOD_BUTTON = new IteratableOption("config.mellowui.main_menu_mod_button",
            (options, identifier) -> CLIENT_CONFIGS.mainMenuModButton.set(FourStyles.byId(CLIENT_CONFIGS.mainMenuModButton.get().getId() + identifier)),
            (options, option) -> {
                switch (CLIENT_CONFIGS.mainMenuModButton.get()) {
                    case OPTION_1:
                        option.setTooltip(Minecraft.getInstance().font.split(ADJACENT_TOOLTIP, TOOLTIP_MAX_WIDTH));
                        break;
                    case OPTION_2:
                        option.setTooltip(Minecraft.getInstance().font.split(MAIN_MENU_ICON_TOOLTIP, TOOLTIP_MAX_WIDTH));
                        break;
                    case OPTION_3:
                        option.setTooltip(Minecraft.getInstance().font.split(REPLACE_REALMS_TOOLTIP, TOOLTIP_MAX_WIDTH));
                        break;
                    case OPTION_4:
                        option.setTooltip(Minecraft.getInstance().font.split(BELOW_REALMS_TOOLTIP, TOOLTIP_MAX_WIDTH));
                }
                return new TranslationTextComponent("config.mellowui.main_menu_mod_button", new TranslationTextComponent("config.mellowui.main_menu_mod_button." + CLIENT_CONFIGS.mainMenuModButton.get().toString()));
            });
    public static final IteratableOption PAUSE_MENU_MOD_BUTTON = new IteratableOption("config.mellowui.pause_menu_mod_button",
            (options, identifier) -> CLIENT_CONFIGS.pauseMenuModButton.set(FourStyles.byId(CLIENT_CONFIGS.pauseMenuModButton.get().getId() + identifier)),
            (options, option) -> {
                switch (CLIENT_CONFIGS.pauseMenuModButton.get()) {
                    case OPTION_1:
                        option.setTooltip(Minecraft.getInstance().font.split(PAUSE_MENU_ADJACENT_TOOLTIP, TOOLTIP_MAX_WIDTH));
                        break;
                    case OPTION_2:
                        option.setTooltip(Minecraft.getInstance().font.split(PAUSE_MENU_ICON_TOOLTIP, TOOLTIP_MAX_WIDTH));
                        break;
                    case OPTION_3:
                        option.setTooltip(Minecraft.getInstance().font.split(REPLACE_TOOLTIP, TOOLTIP_MAX_WIDTH));
                        break;
                    case OPTION_4:
                        option.setTooltip(Minecraft.getInstance().font.split(BELOW_OPTIONS_PM_TOOLTIP, TOOLTIP_MAX_WIDTH));
                }
                return new TranslationTextComponent("config.mellowui.pause_menu_mod_button", new TranslationTextComponent("config.mellowui.pause_menu_mod_button." + CLIENT_CONFIGS.pauseMenuModButton.get().toString()));
            });

    public static final BooleanOption LEGACY_BUTTON_COLORS = new BooleanOption("config.mellowui.legacy_button_colors", new TranslationTextComponent("config.mellowui.legacy_button_colors.tooltip"),
            options -> CLIENT_CONFIGS.legacyButtonColors.get(), (options, newValue) -> CLIENT_CONFIGS.legacyButtonColors.set(newValue));
    public static final BooleanOption DISABLE_BRANDING = new BooleanOption("config.mellowui.disable_branding", new TranslationTextComponent("config.mellowui.disable_branding.tooltip"),
            options -> CLIENT_CONFIGS.disableBranding.get(), (options, newValue) -> CLIENT_CONFIGS.disableBranding.set(newValue));
    public static final StyleBooleanOption SCREEN_BACKGROUND_STYLE = new StyleBooleanOption("config.mellowui.screen_background_style", new TranslationTextComponent("config.mellowui.screen_background_style.tooltip"),
            options -> CLIENT_CONFIGS.screenBackgroundStyle.get(), (options, newValue) -> CLIENT_CONFIGS.screenBackgroundStyle.set(newValue));
    public static final StyleBooleanOption LIST_BACKGROUND_STYLE = new StyleBooleanOption("config.mellowui.list_background_style", new TranslationTextComponent("config.mellowui.list_background_style.tooltip"),
            options -> CLIENT_CONFIGS.listBackgroundStyle.get(), (options, newValue) -> CLIENT_CONFIGS.listBackgroundStyle.set(newValue));
    public static final StyleBooleanOption PANEL_BACKGROUND_STYLE = new StyleBooleanOption("config.mellowui.panel_background_style", new TranslationTextComponent("config.mellowui.panel_background_style.tooltip"),
            options -> CLIENT_CONFIGS.panelBackgroundStyle.get(), (options, newValue) -> CLIENT_CONFIGS.panelBackgroundStyle.set(newValue));
    public static final IteratableOption LOGO_STYLE = new TooltippedIterableOption("config.mellowui.logo_style", new TranslationTextComponent("config.mellowui.logo_style.tooltip"),
            (options, identifier) -> CLIENT_CONFIGS.logoStyle.set(LogoStyles.byId(CLIENT_CONFIGS.logoStyle.get().getId() + identifier)),
            (options, option) -> new TranslationTextComponent("config.mellowui.logo_style." + CLIENT_CONFIGS.logoStyle.get().toString(), new TranslationTextComponent("config.mellowui.logo_style")));
    public static final IteratableOption TITLE_STYLE = new TooltippedIterableOption("config.mellowui.title_style", new TranslationTextComponent("config.mellowui.title_style.tooltip"),
            (options, identifier) -> CLIENT_CONFIGS.titleStyle.set(ThreeStyles.byId(CLIENT_CONFIGS.titleStyle.get().getId() + identifier)),
            (options, option) -> new TranslationTextComponent("config.mellowui." + CLIENT_CONFIGS.titleStyle.get().toString() + "_style", new TranslationTextComponent("config.mellowui.title_style")));
    public static final StyleBooleanOption PAUSE_STYLE = new StyleBooleanOption("config.mellowui.pause_style", new TranslationTextComponent("config.mellowui.pause_style.tooltip"),
            options -> CLIENT_CONFIGS.pauseStyle.get(), (options, newValue) -> CLIENT_CONFIGS.pauseStyle.set(newValue));
    public static final StyleBooleanOption CREATE_NEW_WORLD_STYLE = new StyleBooleanOption("config.mellowui.create_new_world_style", new TranslationTextComponent("config.mellowui.create_new_world_style.tooltip"),
            options -> CLIENT_CONFIGS.createNewWorldStyle.get(), (options, newValue) -> CLIENT_CONFIGS.createNewWorldStyle.set(newValue));
    public static final StyleBooleanOption WORLD_LOADING_STYLE = new StyleBooleanOption("config.mellowui.world_loading_style", new TranslationTextComponent("config.mellowui.world_loading_style.tooltip"),
            options -> CLIENT_CONFIGS.worldLoadingStyle.get(), (options, newValue) -> CLIENT_CONFIGS.worldLoadingStyle.set(newValue));
    public static final StyleBooleanOption OPTIONS_STYLE = new StyleBooleanOption("config.mellowui.options_style", new TranslationTextComponent("config.mellowui.options_style.tooltip"),
            options -> CLIENT_CONFIGS.optionsStyle.get(), (options, newValue) -> CLIENT_CONFIGS.optionsStyle.set(newValue));
    public static final StyleBooleanOption SKIN_CUSTOMIZATION_STYLE = new StyleBooleanOption("config.mellowui.skin_customization_style", new TranslationTextComponent("config.mellowui.skin_customization_style.tooltip"),
            options -> CLIENT_CONFIGS.skinCustomizationStyle.get(), (options, newValue) -> CLIENT_CONFIGS.skinCustomizationStyle.set(newValue));
    public static final StyleBooleanOption MUSIC_AND_SOUNDS_STYLE = new StyleBooleanOption("config.mellowui.music_and_sounds_style", new TranslationTextComponent("config.mellowui.music_and_sounds_style.tooltip"),
            options -> CLIENT_CONFIGS.musicAndSoundsStyle.get(), (options, newValue) -> CLIENT_CONFIGS.musicAndSoundsStyle.set(newValue));
    public static final IteratableOption VIDEO_SETTINGS_STYLE = new IteratableOption("config.mellowui.video_settings_style",
            (options, identifier) -> {
                CLIENT_CONFIGS.videoSettingsStyle.set(ThreeStyles.byId(CLIENT_CONFIGS.videoSettingsStyle.get().getId() + identifier));
                if (!ModList.get().isLoaded("rubidium") && CLIENT_CONFIGS.videoSettingsStyle.get() == ThreeStyles.OPTION_3) CLIENT_CONFIGS.videoSettingsStyle.set(ThreeStyles.OPTION_1);
            },
            (options, option) -> {
                option.setTooltip(Minecraft.getInstance().font.split(VIDEO_SETTINGS_STYLE_TOOLTIP, TOOLTIP_MAX_WIDTH));
                return new TranslationTextComponent("config.mellowui.video_settings_style", new TranslationTextComponent("config.mellowui.video_settings_style." + CLIENT_CONFIGS.videoSettingsStyle.get().toString()));
            });
    public static final StyleBooleanOption CONTROLS_STYLE = new StyleBooleanOption("config.mellowui.controls_style", new TranslationTextComponent("config.mellowui.controls_style.tooltip"),
            options -> CLIENT_CONFIGS.controlsStyle.get(), (options, newValue) -> CLIENT_CONFIGS.controlsStyle.set(newValue));
    public static final StyleBooleanOption MOUSE_SETTINGS_STYLE = new StyleBooleanOption("config.mellowui.mouse_settings_style", new TranslationTextComponent("config.mellowui.mouse_settings_style.tooltip"),
            options -> CLIENT_CONFIGS.mouseSettingsStyle.get(), (options, newValue) -> CLIENT_CONFIGS.mouseSettingsStyle.set(newValue));
    public static final StyleBooleanOption CHAT_SETTINGS_STYLE = new StyleBooleanOption("config.mellowui.chat_settings_style", new TranslationTextComponent("config.mellowui.chat_settings_style.tooltip"),
            options -> CLIENT_CONFIGS.chatSettingsStyle.get(), (options, newValue) -> CLIENT_CONFIGS.chatSettingsStyle.set(newValue));
    public static final StyleBooleanOption PACK_LIST_STYLE = new StyleBooleanOption("config.mellowui.pack_list_style", new TranslationTextComponent("config.mellowui.pack_list_style.tooltip"),
            options -> CLIENT_CONFIGS.packListStyle.get(), (options, newValue) -> CLIENT_CONFIGS.packListStyle.set(newValue));
    public static final StyleBooleanOption ACCESSIBILITY_SETTINGS_STYLE = new StyleBooleanOption("config.mellowui.accessibility_settings_style", new TranslationTextComponent("config.mellowui.accessibility_settings_style.tooltip"),
            options -> CLIENT_CONFIGS.accessibilitySettingsStyle.get(), (options, newValue) -> CLIENT_CONFIGS.accessibilitySettingsStyle.set(newValue));
    public static final StyleBooleanOption OUT_OF_MEMORY_STYLE = new StyleBooleanOption("config.mellowui.out_of_memory_style", new TranslationTextComponent("config.mellowui.out_of_memory_style.tooltip"),
            options -> CLIENT_CONFIGS.outOfMemoryStyle.get(), (options, newValue) -> CLIENT_CONFIGS.outOfMemoryStyle.set(newValue));
    public static final StyleBooleanOption STATISTICS_STYLE = new StyleBooleanOption("config.mellowui.statistics_style", new TranslationTextComponent("config.mellowui.statistics_style.tooltip"),
            options -> CLIENT_CONFIGS.statisticsStyle.get(), (options, newValue) -> CLIENT_CONFIGS.statisticsStyle.set(newValue));
    public static final IteratableOption MOD_LIST_STYLE = new IteratableOption("config.mellowui.mod_list_style",
            (options, identifier) -> {
                CLIENT_CONFIGS.modListStyle.set(ThreeStyles.byId(CLIENT_CONFIGS.modListStyle.get().getId() + identifier));
                if (CLIENT_CONFIGS.modListStyle.get() == ThreeStyles.OPTION_3 && !ModList.get().isLoaded("catalogue")) CLIENT_CONFIGS.modListStyle.set(ThreeStyles.OPTION_1);
            },
            (options, option) -> {
                option.setTooltip(Minecraft.getInstance().font.split(MOD_LIST_STYLE_TOOLTIP, TOOLTIP_MAX_WIDTH));
                return new TranslationTextComponent("config.mellowui.mod_list_style", new TranslationTextComponent("config.mellowui.mod_list_style." + CLIENT_CONFIGS.modListStyle.get().toString()));
            });
    public static final StyleBooleanOption LOADING_ERRORS_STYLE = new StyleBooleanOption("config.mellowui.loading_errors_style", new TranslationTextComponent("config.mellowui.loading_errors_style.tooltip"),
            options -> CLIENT_CONFIGS.loadingErrorsStyle.get(), (options, newValue) -> CLIENT_CONFIGS.loadingErrorsStyle.set(newValue));
    public static final StyleBooleanOption UPDATE_AVAILABLE_ICON_STYLE = new StyleBooleanOption("config.mellowui.update_available_icon_style", new TranslationTextComponent("config.mellowui.update_available_icon_style.tooltip"),
            options -> CLIENT_CONFIGS.updateAvailableIconStyle.get(), (options, newValue) -> CLIENT_CONFIGS.updateAvailableIconStyle.set(newValue));
    public static final BooleanOption REPLACE_REALMS_NOTIFICATIONS = new BooleanOption("config.mellowui.replace_realms_notifications", new TranslationTextComponent("config.mellowui.replace_realms_notifications.tooltip"),
            options -> CLIENT_CONFIGS.replaceRealmsNotifications.get(), (options, newValue) -> CLIENT_CONFIGS.replaceRealmsNotifications.set(newValue));
    public static final StyleBooleanOption SPLASH_TEXT_POSITION = new StyleBooleanOption("config.mellowui.splash_text_position", new TranslationTextComponent("config.mellowui.splash_text_position.tooltip"),
            options -> CLIENT_CONFIGS.splashTextPosition.get(), (options, newValue) -> CLIENT_CONFIGS.splashTextPosition.set(newValue));
    public static final BooleanOption SCROLLING_TEXT = new BooleanOption("config.mellowui.scrolling_text", new TranslationTextComponent("config.mellowui.scrolling_text.tooltip"),
            options -> CLIENT_CONFIGS.scrollingText.get(), (options, newValue) -> CLIENT_CONFIGS.scrollingText.set(newValue));
    public static final EditListConfigOption CLASSIFIED_AS_CONTAINERS = new EditListConfigOption("config.mellowui.classified_as_containers",
            new TranslationTextComponent("config.mellowui.classified_as_containers.tooltip"), CLIENT_CONFIGS.classifiedAsContainers);
    public static final EditListConfigOption OVERSIZED_IN_GUI = new EditListConfigOption("config.mellowui.oversized_in_gui",
            new TranslationTextComponent("config.mellowui.oversized_in_gui.tooltip"), CLIENT_CONFIGS.oversizedInGUI);
    public static final BooleanOption CULL_OVERSIZED_ITEMS = new BooleanOption("config.mellowui.cull_oversized_items", new TranslationTextComponent("config.mellowui.cull_oversized_items.tooltip"),
            options -> CLIENT_CONFIGS.cullOversizedItems.get(), (options, newValue) -> CLIENT_CONFIGS.cullOversizedItems.set(newValue));
    public static final BooleanOption BACKGROUND_SHADERS = new BooleanOption("config.mellowui.background_shaders", new TranslationTextComponent("config.mellowui.background_shaders.tooltip"),
            options -> CLIENT_CONFIGS.backgroundShaders.get(), (options, newValue) -> CLIENT_CONFIGS.backgroundShaders.set(newValue));
    public static final BooleanOption BLURRY_CONTAINERS = new BooleanOption("config.mellowui.blurry_containers", new TranslationTextComponent("config.mellowui.blurry_containers.tooltip"),
            options -> CLIENT_CONFIGS.blurryContainers.get(), (options, newValue) -> CLIENT_CONFIGS.blurryContainers.set(newValue));
    public static final BooleanOption FADING_BLUR = new BooleanOption("config.mellowui.fading_blur", new TranslationTextComponent("config.mellowui.fading_blur.tooltip"),
            options -> CLIENT_CONFIGS.fadingBlur.get(), (options, newValue) -> CLIENT_CONFIGS.fadingBlur.set(newValue));
    public static final BooleanOption DEFAULT_BACKGROUND = new BooleanOption("config.mellowui.default_background", new TranslationTextComponent("config.mellowui.default_background.tooltip"),
            options -> CLIENT_CONFIGS.defaultBackground.get(), (options, newValue) -> CLIENT_CONFIGS.defaultBackground.set(newValue));
    public static final BooleanOption GRADIENT_BACKGROUND = new BooleanOption("config.mellowui.gradient_background", new TranslationTextComponent("config.mellowui.gradient_background.tooltip"),
            options -> CLIENT_CONFIGS.gradientBackground.get(), (options, newValue) -> CLIENT_CONFIGS.gradientBackground.set(newValue));
    public static final BooleanOption LOG_GL_ERRORS = new BooleanOption("config.mellowui.log_gl_errors", new TranslationTextComponent("config.mellowui.log_gl_errors.tooltip"),
            options -> CLIENT_CONFIGS.logGLErrors.get(), (options, newValue) -> CLIENT_CONFIGS.logGLErrors.set(newValue));
}
