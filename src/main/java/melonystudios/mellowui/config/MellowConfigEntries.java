package melonystudios.mellowui.config;

import melonystudios.mellowui.config.option.*;
import melonystudios.mellowui.config.type.*;
import net.minecraft.client.ProgressOption;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.fml.ModList;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;
import static melonystudios.mellowui.config.WidgetConfigs.WIDGET_CONFIGS;
import static melonystudios.mellowui.screen.RenderComponents.TOOLTIP_MAX_WIDTH;

public class MellowConfigEntries {
    // Tooltips
    public static final MutableComponent ADJACENT_TOOLTIP = new TranslatableComponent("config.mellowui.main_menu_mod_button.desc", new TranslatableComponent("config.mellowui.main_menu_mod_button.option_1.desc"));
    public static final MutableComponent MAIN_MENU_ICON_TOOLTIP = new TranslatableComponent("config.mellowui.main_menu_mod_button.desc", new TranslatableComponent("config.mellowui.main_menu_mod_button.option_2.desc"));
    public static final MutableComponent REPLACE_REALMS_TOOLTIP = new TranslatableComponent("config.mellowui.main_menu_mod_button.desc", new TranslatableComponent("config.mellowui.main_menu_mod_button.option_3.desc"));
    public static final MutableComponent PAUSE_MENU_ADJACENT_TOOLTIP = new TranslatableComponent("config.mellowui.pause_menu_mod_button.desc", new TranslatableComponent("config.mellowui.pause_menu_mod_button.option_1.desc"));
    public static final MutableComponent PAUSE_MENU_ICON_TOOLTIP = new TranslatableComponent("config.mellowui.pause_menu_mod_button.desc", new TranslatableComponent("config.mellowui.pause_menu_mod_button.option_2.desc"));
    public static final MutableComponent REPLACE_TOOLTIP = new TranslatableComponent("config.mellowui.pause_menu_mod_button.desc", new TranslatableComponent("config.mellowui.pause_menu_mod_button.option_3.desc"));
    public static final MutableComponent MOD_LIST_STYLE_TOOLTIP = new TranslatableComponent("config.mellowui.mod_list_style.desc");
    public static final MutableComponent MELLOMEDLEY_MAIN_MENU_ICON_TOOLTIP = new TranslatableComponent("config.mellomedley.main_menu_mod_button.desc", new TranslatableComponent("config.mellomedley.main_menu_mod_button.option_1.desc"));
    public static final MutableComponent BELOW_OPTIONS_TOOLTIP = new TranslatableComponent("config.mellomedley.main_menu_mod_button.desc", new TranslatableComponent("config.mellomedley.main_menu_mod_button.option_2.desc"));
    public static final MutableComponent UPDATED_VIDEO_SETTINGS_TOOLTIP = new TranslatableComponent("config.mellowui.updated_video_settings_menu.desc");

    // Separators
    public static final SeparatorOption MAIN_MENU_SEPARATOR = new SeparatorOption(new TranslatableComponent("separator.mellowui.main_menu"));
    public static final SeparatorOption INGAME_MENUS_SEPARATOR = new SeparatorOption(new TranslatableComponent("separator.mellowui.ingame_menus"));
    public static final SeparatorOption MENU_UPDATES_SEPARATOR = new SeparatorOption(new TranslatableComponent("separator.mellowui.menu_updates"));
    public static final SeparatorOption OPTION_MENU_UPDATES_SEPARATOR = new SeparatorOption(new TranslatableComponent("separator.mellowui.option_menu_updates"));
    public static final SeparatorOption REALMS_SEPARATOR = new SeparatorOption(new TranslatableComponent("separator.mellowui.realms").withStyle(style -> style.withColor(0xE43DC3)));
    public static final SeparatorOption ACCESSIBILITY_SEPARATOR = new SeparatorOption(new TranslatableComponent("menu.minecraft.accessibility_settings.title"));
    public static final SeparatorOption MUSIC_AND_SOUNDS_SEPARATOR = new SeparatorOption(new TranslatableComponent("options.sounds.title"));

    // Mellow UI options
    public static final ProgressOption PANORAMA_CAMERA_PITCH = new ProgressOption("config.mellowui.panorama_camera_pitch", -90, 90, 1,
            (options) -> Double.valueOf(CLIENT_CONFIGS.panoramaCameraPitch.get()),
            (options, newValue) -> CLIENT_CONFIGS.panoramaCameraPitch.set((int) Math.round(newValue)),
            (options, slider) -> new TranslatableComponent("config.mellowui.panorama_camera_pitch", new TranslatableComponent("config.mellowui.panorama_camera_pitch.pitch", Math.round(slider.get(options)))),
            minecraft -> minecraft.font.split(new TranslatableComponent("config.mellowui.panorama_camera_pitch.desc"), TOOLTIP_MAX_WIDTH));
    public static final BooleanOption PANORAMA_BOBBING = new BooleanOption("config.mellowui.panorama_bobbing", new TranslatableComponent("config.mellowui.panorama_bobbing.desc"),
            options -> CLIENT_CONFIGS.panoramaBobbing.get(), (options, newValue) -> CLIENT_CONFIGS.panoramaBobbing.set(newValue));
    public static final TextFieldOption MONOCHROME_LOADING_SCREEN_COLOR = new TextFieldOption("config.mellowui.monochrome_loading_screen_color",
            new TranslatableComponent("config.mellowui.monochrome_loading_screen_color.desc"),
            WIDGET_CONFIGS.monochromeLoadingScreenColor.get().toString(),
            newValue -> WIDGET_CONFIGS.monochromeLoadingScreenColor.set(Integer.valueOf(newValue)),
            (text, setter) -> {
                try {
                    int newValue = Integer.parseInt(text);
                    setter.accept(Integer.toString(Mth.clamp(newValue, 0, 16777215)));
                } catch (NumberFormatException ignored) {}
            });
    public static final TextFieldOption SPLASH_TEXT_COLOR = new TextFieldOption("config.mellowui.splash_text_color",
            new TranslatableComponent("config.mellowui.splash_text_color.desc"),
            WIDGET_CONFIGS.splashTextColor.get().toString(),
            newValue -> WIDGET_CONFIGS.splashTextColor.set(Integer.valueOf(newValue)),
            (text, setter) -> {
                try {
                    int newValue = Integer.parseInt(text);
                    setter.accept(Integer.toString(Mth.clamp(newValue, 0, 16777215)));
                } catch (NumberFormatException ignored) {}
            });
    public static final TextFieldOption MELLO_SPLASH_TEXT_COLOR = new TextFieldOption("config.mellomedley.splash_text_color",
            new TranslatableComponent("config.mellomedley.splash_text_color.desc"),
            WIDGET_CONFIGS.mellomedleySplashTextColor.get().toString(),
            newValue -> WIDGET_CONFIGS.mellomedleySplashTextColor.set(Integer.valueOf(newValue)),
            (text, setter) -> {
                try {
                    int newValue = Integer.parseInt(text);
                    setter.accept(Integer.toString(Mth.clamp(newValue, 0, 16777215)));
                } catch (NumberFormatException ignored) {}
            });
    public static final IterableOption MAIN_MENU_MOD_BUTTON = new IterableOption("config.mellowui.main_menu_mod_button",
            (options, identifier) -> CLIENT_CONFIGS.mainMenuModButton.set(ThreeStyles.byId(CLIENT_CONFIGS.mainMenuModButton.get().getId() + identifier)),
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
                }
                return new TranslatableComponent("config.mellowui.main_menu_mod_button", new TranslatableComponent("config.mellowui.main_menu_mod_button." + CLIENT_CONFIGS.mainMenuModButton.get().toString()));
            });
    public static final IterableOption PAUSE_MENU_MOD_BUTTON = new IterableOption("config.mellowui.pause_menu_mod_button",
            (options, identifier) -> CLIENT_CONFIGS.pauseMenuModButton.set(ThreeStyles.byId(CLIENT_CONFIGS.pauseMenuModButton.get().getId() + identifier)),
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
                }
                return new TranslatableComponent("config.mellowui.pause_menu_mod_button", new TranslatableComponent("config.mellowui.pause_menu_mod_button." + CLIENT_CONFIGS.pauseMenuModButton.get().toString()));
            });
    public static final IterableOption MELLOMEDLEY_MAIN_MENU_MOD_BUTTON = new IterableOption("config.mellomedley.main_menu_mod_button",
            (options, identifier) -> CLIENT_CONFIGS.mellomedleyMainMenuModButton.set(TwoStyles.byId(CLIENT_CONFIGS.mellomedleyMainMenuModButton.get().getId() + identifier)),
            (options, option) -> {
                switch (CLIENT_CONFIGS.mellomedleyMainMenuModButton.get()) {
                    case OPTION_1:
                        option.setTooltip(MELLOMEDLEY_MAIN_MENU_ICON_TOOLTIP);
                        break;
                    case OPTION_2:
                        option.setTooltip(BELOW_OPTIONS_TOOLTIP);
                }
                return new TranslatableComponent("config.mellomedley.main_menu_mod_button", new TranslatableComponent("config.mellomedley.main_menu_mod_button." + CLIENT_CONFIGS.mellomedleyMainMenuModButton.get().toString()));
            });
    public static final BooleanOption LEGACY_BUTTON_COLORS = new BooleanOption("config.mellowui.legacy_button_colors", new TranslatableComponent("config.mellowui.legacy_button_colors.desc"),
            options -> CLIENT_CONFIGS.legacyButtonColors.get(), (options, newValue) -> CLIENT_CONFIGS.legacyButtonColors.set(newValue));
    public static final IterableOption MAIN_MENU_STYLE = new IterableOption("config.mellowui.main_menu_style", new TranslatableComponent("config.mellowui.main_menu_style.desc"),
            (options, identifier) -> CLIENT_CONFIGS.mainMenuStyle.set(ThreeStyles.byId(CLIENT_CONFIGS.mainMenuStyle.get().getId() + identifier)),
            (options, option) -> new TranslatableComponent("config.mellowui." + CLIENT_CONFIGS.mainMenuStyle.get().toString() + "_style", new TranslatableComponent("config.mellowui.main_menu_style")));
    public static final IterableOption MOD_LIST_STYLE = new IterableOption("config.mellowui.mod_list_style",
            (options, identifier) -> {
                CLIENT_CONFIGS.modListStyle.set(ThreeStyles.byId(CLIENT_CONFIGS.modListStyle.get().getId() + identifier));
                if (CLIENT_CONFIGS.modListStyle.get() == ThreeStyles.OPTION_3 && !ModList.get().isLoaded("catalogue")) CLIENT_CONFIGS.modListStyle.set(ThreeStyles.OPTION_1);
            },
            (options, option) -> {
                option.setTooltip(MOD_LIST_STYLE_TOOLTIP);
                return new TranslatableComponent("config.mellowui.mod_list_style", new TranslatableComponent("config.mellowui.mod_list_style." + CLIENT_CONFIGS.modListStyle.get().toString()));
            });
    public static final IterableOption UPDATED_VIDEO_SETTINGS_MENU = new IterableOption("config.mellowui.updated_video_settings_menu",
            (options, identifier) -> {
                CLIENT_CONFIGS.updateVideoSettingsMenu.set(ThreeStyles.byId(CLIENT_CONFIGS.updateVideoSettingsMenu.get().getId() + identifier));
                if (!ModList.get().isLoaded("rubidium") && CLIENT_CONFIGS.updateVideoSettingsMenu.get() == ThreeStyles.OPTION_3) CLIENT_CONFIGS.updateVideoSettingsMenu.set(ThreeStyles.OPTION_1);
            },
            (options, option) -> {
                option.setTooltip(UPDATED_VIDEO_SETTINGS_TOOLTIP);
                return new TranslatableComponent("config.mellowui.updated_video_settings_menu", new TranslatableComponent("config.mellowui.updated_video_settings_menu." + CLIENT_CONFIGS.updateVideoSettingsMenu.get().toString()));
            });
    public static final BooleanOption DISABLE_BRANDING = new BooleanOption("config.mellowui.disable_branding", new TranslatableComponent("config.mellowui.disable_branding.desc"),
            options -> CLIENT_CONFIGS.disableBranding.get(), (options, newValue) -> CLIENT_CONFIGS.disableBranding.set(newValue));
    public static final StyleBooleanOption UPDATED_PAUSE_MENU = new StyleBooleanOption("config.mellowui.updated_pause_menu", new TranslatableComponent("config.mellowui.updated_pause_menu.desc"),
            options -> CLIENT_CONFIGS.updatePauseMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updatePauseMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_CREATE_NEW_WORLD_MENU = new StyleBooleanOption("config.mellowui.updated_create_new_world_menu", new TranslatableComponent("config.mellowui.updated_create_new_world_menu.desc"),
            options -> CLIENT_CONFIGS.updateCreateNewWorldMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updateCreateNewWorldMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_ONLINE_OPTIONS_MENU = new StyleBooleanOption("config.mellowui.updated_online_options_menu", new TranslatableComponent("config.mellowui.updated_online_options_menu.desc"),
            options -> CLIENT_CONFIGS.updateOnlineOptionsMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updateOnlineOptionsMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_OPTIONS_MENU = new StyleBooleanOption("config.mellowui.updated_options_menu", new TranslatableComponent("config.mellowui.updated_options_menu.desc"),
            options -> CLIENT_CONFIGS.updateOptionsMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updateOptionsMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_SKIN_CUSTOMIZATION_MENU = new StyleBooleanOption("config.mellowui.updated_skin_customization_menu", new TranslatableComponent("config.mellowui.updated_skin_customization_menu.desc"),
            options -> CLIENT_CONFIGS.updateSkinCustomizationMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updateSkinCustomizationMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_MUSIC_AND_SOUNDS_MENU = new StyleBooleanOption("config.mellowui.updated_music_and_sounds_menu", new TranslatableComponent("config.mellowui.updated_music_and_sounds_menu.desc"),
            options -> CLIENT_CONFIGS.updateMusicAndSoundsMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updateMusicAndSoundsMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_CONTROLS_MENU = new StyleBooleanOption("config.mellowui.updated_controls_menu", new TranslatableComponent("config.mellowui.updated_controls_menu.desc"),
            options -> CLIENT_CONFIGS.updateControlsMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updateControlsMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_MOUSE_SETTINGS_MENU = new StyleBooleanOption("config.mellowui.updated_mouse_settings_menu", new TranslatableComponent("config.mellowui.updated_mouse_settings_menu.desc"),
            options -> CLIENT_CONFIGS.updateMouseSettingsMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updateMouseSettingsMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_CHAT_SETTINGS_MENU = new StyleBooleanOption("config.mellowui.updated_chat_settings_menu", new TranslatableComponent("config.mellowui.updated_chat_settings_menu.desc"),
            options -> CLIENT_CONFIGS.updateChatSettingsMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updateChatSettingsMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_PACK_MENU = new StyleBooleanOption("config.mellowui.updated_pack_menu", new TranslatableComponent("config.mellowui.updated_pack_menu.desc"),
            options -> CLIENT_CONFIGS.updatePackMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updatePackMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_ACCESSIBILITY_MENU = new StyleBooleanOption("config.mellowui.updated_accessibility_menu", new TranslatableComponent("config.mellowui.updated_accessibility_menu.desc"),
            options -> CLIENT_CONFIGS.updateAccessibilityMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updateAccessibilityMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_OUT_OF_MEMORY_MENU = new StyleBooleanOption("config.mellowui.updated_out_of_memory_menu", new TranslatableComponent("config.mellowui.updated_out_of_memory_menu.desc"),
            options -> CLIENT_CONFIGS.updateOutOfMemoryMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updateOutOfMemoryMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_SCREEN_BACKGROUND = new StyleBooleanOption("config.mellowui.updated_screen_background", new TranslatableComponent("config.mellowui.updated_screen_background.desc"),
            options -> CLIENT_CONFIGS.updateScreenBackground.get(), (options, newValue) -> CLIENT_CONFIGS.updateScreenBackground.set(newValue));
    public static final StyleBooleanOption UPDATED_LIST_BACKGROUND = new StyleBooleanOption("config.mellowui.updated_list_background", new TranslatableComponent("config.mellowui.updated_list_background.desc"),
            options -> CLIENT_CONFIGS.updateListBackground.get(), (options, newValue) -> CLIENT_CONFIGS.updateListBackground.set(newValue));
    public static final BooleanOption REPLACE_REALMS_NOTIFICATIONS = new BooleanOption("config.mellowui.replace_realms_notifications", new TranslatableComponent("config.mellowui.replace_realms_notifications.desc"),
            options -> CLIENT_CONFIGS.replaceRealmsNotifications.get(), (options, newValue) -> CLIENT_CONFIGS.replaceRealmsNotifications.set(newValue));
    public static final StyleBooleanOption SPLASH_TEXT_POSITION = new StyleBooleanOption("config.mellowui.splash_text_position", new TranslatableComponent("config.mellowui.splash_text_position.desc"),
            options -> CLIENT_CONFIGS.splashTextPosition.get(), (options, newValue) -> CLIENT_CONFIGS.splashTextPosition.set(newValue));
    public static final IterableOption LOGO_STYLE = new IterableOption("config.mellowui.logo_style", new TranslatableComponent("config.mellowui.logo_style.desc"),
            (options, identifier) -> CLIENT_CONFIGS.logoStyle.set(ThreeStyles.byId(CLIENT_CONFIGS.logoStyle.get().getId() + identifier)),
            (options, option) -> new TranslatableComponent("config.mellowui." + CLIENT_CONFIGS.logoStyle.get().toString() + "_style", new TranslatableComponent("config.mellowui.logo_style")));
    public static final BooleanOption SCROLLING_TEXT = new BooleanOption("config.mellowui.scrolling_text", new TranslatableComponent("config.mellowui.scrolling_text.desc"),
            options -> CLIENT_CONFIGS.scrollingText.get(), (options, newValue) -> CLIENT_CONFIGS.scrollingText.set(newValue));
    public static final BooleanOption BACKGROUND_SHADERS = new BooleanOption("config.mellowui.background_shaders", new TranslatableComponent("config.mellowui.background_shaders.desc"),
            options -> CLIENT_CONFIGS.backgroundShaders.get(), (options, newValue) -> CLIENT_CONFIGS.backgroundShaders.set(newValue));
    public static final BooleanOption BLURRY_CONTAINERS = new BooleanOption("config.mellowui.blurry_containers", new TranslatableComponent("config.mellowui.blurry_containers.desc"),
            options -> CLIENT_CONFIGS.blurryContainers.get(), (options, newValue) -> CLIENT_CONFIGS.blurryContainers.set(newValue));
    public static final BooleanOption DEFAULT_BACKGROUND = new BooleanOption("config.mellowui.default_background", new TranslatableComponent("config.mellowui.default_background.desc"),
            options -> CLIENT_CONFIGS.defaultBackground.get(), (options, newValue) -> CLIENT_CONFIGS.defaultBackground.set(newValue));
    public static final BooleanOption GRADIENT_BACKGROUND = new BooleanOption("config.mellowui.gradient_background", new TranslatableComponent("config.mellowui.gradient_background.desc"),
            options -> CLIENT_CONFIGS.gradientBackground.get(), (options, newValue) -> CLIENT_CONFIGS.gradientBackground.set(newValue));
    public static final BooleanOption LOG_GL_ERRORS = new BooleanOption("config.mellowui.log_gl_errors", new TranslatableComponent("config.mellowui.log_gl_errors.desc"),
            options -> CLIENT_CONFIGS.logGLErrors.get(), (options, newValue) -> CLIENT_CONFIGS.logGLErrors.set(newValue));

    // Forge options
    public static final IterableOption MOD_LIST_SORTING = new IterableOption("config.mellowui.mod_list_sorting",
            (options, identifier) -> CLIENT_CONFIGS.modListSorting.set(ModListSorting.byId(CLIENT_CONFIGS.modListSorting.get().getId() + identifier)),
            (options, newValue) -> new TranslatableComponent("config.mellowui.mod_list_sorting", new TranslatableComponent("config.mellowui.mod_list_sorting." + CLIENT_CONFIGS.modListSorting.get().toString())));
}
