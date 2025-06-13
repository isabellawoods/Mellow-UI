package melonystudios.mellowui.config;

import melonystudios.mellowui.config.option.SeparatorOption;
import melonystudios.mellowui.config.option.StyleBooleanOption;
import melonystudios.mellowui.config.option.TextFieldOption;
import melonystudios.mellowui.config.option.TooltippedIterableOption;
import melonystudios.mellowui.config.type.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;
import static melonystudios.mellowui.util.MellowUtils.TOOLTIP_MAX_WIDTH;

public class MellowConfigEntries {
    // Tooltips
    public static final IFormattableTextComponent ADJACENT_TOOLTIP = new TranslationTextComponent("config.mellowui.main_menu_mod_button.desc", new TranslationTextComponent("config.mellowui.main_menu_mod_button.option_1.desc"));
    public static final IFormattableTextComponent MAIN_MENU_ICON_TOOLTIP = new TranslationTextComponent("config.mellowui.main_menu_mod_button.desc", new TranslationTextComponent("config.mellowui.main_menu_mod_button.option_2.desc"));
    public static final IFormattableTextComponent REPLACE_REALMS_TOOLTIP = new TranslationTextComponent("config.mellowui.main_menu_mod_button.desc", new TranslationTextComponent("config.mellowui.main_menu_mod_button.option_3.desc"));
    public static final IFormattableTextComponent REPLACE_TOOLTIP = new TranslationTextComponent("config.mellowui.pause_menu_mod_button.desc", new TranslationTextComponent("config.mellowui.pause_menu_mod_button.option_1.desc"));
    public static final IFormattableTextComponent PAUSE_MENU_ICON_TOOLTIP = new TranslationTextComponent("config.mellowui.pause_menu_mod_button.desc", new TranslationTextComponent("config.mellowui.pause_menu_mod_button.option_2.desc"));
    public static final IFormattableTextComponent MOD_LIST_STYLE_TOOLTIP = new TranslationTextComponent("config.mellowui.mod_list_style.desc");
    public static final IFormattableTextComponent MELLOMEDLEY_MAIN_MENU_ICON_TOOLTIP = new TranslationTextComponent("config.mellomedley.main_menu_mod_button.desc", new TranslationTextComponent("config.mellomedley.main_menu_mod_button.option_1.desc"));
    public static final IFormattableTextComponent BELOW_OPTIONS_TOOLTIP = new TranslationTextComponent("config.mellomedley.main_menu_mod_button.desc", new TranslationTextComponent("config.mellomedley.main_menu_mod_button.option_2.desc"));

    // Separators
    public static final SeparatorOption MAIN_MENU_SEPARATOR = new SeparatorOption(new TranslationTextComponent("separator.mellowui.main_menu"));
    public static final SeparatorOption PAUSE_MENU_SEPARATOR = new SeparatorOption(new TranslationTextComponent("separator.mellowui.pause_menu"));
    public static final SeparatorOption MENU_UPDATES_SEPARATOR = new SeparatorOption(new TranslationTextComponent("separator.mellowui.menu_updates"));
    public static final SeparatorOption REALMS_SEPARATOR = new SeparatorOption(new TranslationTextComponent("separator.mellowui.realms").withStyle(Style.EMPTY.withColor(Color.fromRgb(0xE43DC3))));
    public static final SeparatorOption ACCESSIBILITY_SEPARATOR = new SeparatorOption(new TranslationTextComponent("menu.minecraft.accessibility_settings.title"));
    public static final SeparatorOption MUSIC_AND_SOUNDS_SEPARATOR = new SeparatorOption(new TranslationTextComponent("options.sounds.title"));

    // Mellow UI options
    public static final SliderPercentageOption PANORAMA_CAMERA_PITCH = new SliderPercentageOption("config.mellowui.panorama_camera_pitch", -90, 90, 1,
            (options) -> Double.valueOf(CLIENT_CONFIGS.panoramaCameraPitch.get()),
            (options, newValue) -> CLIENT_CONFIGS.panoramaCameraPitch.set((int) Math.round(newValue)),
            (options, slider) -> {
                slider.setTooltip(Minecraft.getInstance().font.split(new TranslationTextComponent("config.mellowui.panorama_camera_pitch.desc"), TOOLTIP_MAX_WIDTH));
                return new TranslationTextComponent("config.mellowui.panorama_camera_pitch", new TranslationTextComponent("config.mellowui.panorama_camera_pitch.pitch", Math.round(slider.get(options))));
            });
    public static final BooleanOption PANORAMA_BOBBING = new BooleanOption("config.mellowui.panorama_bobbing", new TranslationTextComponent("config.mellowui.panorama_bobbing.desc"),
            options -> CLIENT_CONFIGS.panoramaBobbing.get(), (options, newValue) -> CLIENT_CONFIGS.panoramaBobbing.set(newValue));
    public static final TextFieldOption MONOCHROME_LOADING_SCREEN_COLOR = new TextFieldOption("config.mellowui.monochrome_loading_screen_color",
            new TranslationTextComponent("config.mellowui.monochrome_loading_screen_color.desc"),
            CLIENT_CONFIGS.monochromeLoadingScreenColor.get().toString(),
            newValue -> CLIENT_CONFIGS.monochromeLoadingScreenColor.set(Integer.valueOf(newValue)),
            (text, setter) -> {
                try {
                    int newValue = Integer.parseInt(text);
                    setter.accept(Integer.toString(MathHelper.clamp(newValue, 0, 16777215)));
                } catch (NumberFormatException ignored) {}
            });
    public static final TextFieldOption SPLASH_TEXT_COLOR = new TextFieldOption("config.mellowui.splash_text_color",
            new TranslationTextComponent("config.mellowui.splash_text_color.desc"),
            CLIENT_CONFIGS.splashTextColor.get().toString(),
            newValue -> CLIENT_CONFIGS.splashTextColor.set(Integer.valueOf(newValue)),
            (text, setter) -> {
                try {
                    int newValue = Integer.parseInt(text);
                    setter.accept(Integer.toString(MathHelper.clamp(newValue, 0, 16777215)));
                } catch (NumberFormatException ignored) {}
            });
    public static final TextFieldOption MELLO_SPLASH_TEXT_COLOR = new TextFieldOption("config.mellomedley.splash_text_color",
            new TranslationTextComponent("config.mellomedley.splash_text_color.desc"),
            CLIENT_CONFIGS.mellomedleySplashTextColor.get().toString(),
            newValue -> CLIENT_CONFIGS.mellomedleySplashTextColor.set(Integer.valueOf(newValue)),
            (text, setter) -> {
                try {
                    int newValue = Integer.parseInt(text);
                    setter.accept(Integer.toString(MathHelper.clamp(newValue, 0, 16777215)));
                } catch (NumberFormatException ignored) {}
            });
    public static final IteratableOption MAIN_MENU_MOD_BUTTON = new IteratableOption("config.mellowui.main_menu_mod_button",
            (options, identifier) -> CLIENT_CONFIGS.mainMenuModButton.set(ThreeStyles.byId(CLIENT_CONFIGS.mainMenuModButton.get().getId() + identifier)),
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
                }
                return new TranslationTextComponent("config.mellowui.main_menu_mod_button", new TranslationTextComponent("config.mellowui.main_menu_mod_button." + CLIENT_CONFIGS.mainMenuModButton.get().toString()));
            });
    public static final IteratableOption PAUSE_MENU_MOD_BUTTON = new IteratableOption("config.mellowui.pause_menu_mod_button",
            (options, identifier) -> CLIENT_CONFIGS.pauseMenuModButton.set(TwoStyles.byId(CLIENT_CONFIGS.pauseMenuModButton.get().getId() + identifier)),
            (options, option) -> {
                switch (CLIENT_CONFIGS.pauseMenuModButton.get()) {
                    case OPTION_1:
                        option.setTooltip(Minecraft.getInstance().font.split(REPLACE_TOOLTIP, TOOLTIP_MAX_WIDTH));
                        break;
                    case OPTION_2:
                        option.setTooltip(Minecraft.getInstance().font.split(PAUSE_MENU_ICON_TOOLTIP, TOOLTIP_MAX_WIDTH));
                }
                return new TranslationTextComponent("config.mellowui.pause_menu_mod_button", new TranslationTextComponent("config.mellowui.pause_menu_mod_button." + CLIENT_CONFIGS.pauseMenuModButton.get().toString()));
            });
    public static final IteratableOption MELLOMEDLEY_MAIN_MENU_MOD_BUTTON = new IteratableOption("config.mellomedley.main_menu_mod_button",
            (options, identifier) -> CLIENT_CONFIGS.mellomedleyMainMenuModButton.set(TwoStyles.byId(CLIENT_CONFIGS.mellomedleyMainMenuModButton.get().getId() + identifier)),
            (options, option) -> {
                switch (CLIENT_CONFIGS.mellomedleyMainMenuModButton.get()) {
                    case OPTION_1:
                        option.setTooltip(Minecraft.getInstance().font.split(BELOW_OPTIONS_TOOLTIP, TOOLTIP_MAX_WIDTH));
                        break;
                    case OPTION_2:
                        option.setTooltip(Minecraft.getInstance().font.split(MELLOMEDLEY_MAIN_MENU_ICON_TOOLTIP, TOOLTIP_MAX_WIDTH));
                }
                return new TranslationTextComponent("config.mellomedley.main_menu_mod_button", new TranslationTextComponent("config.mellomedley.main_menu_mod_button." + CLIENT_CONFIGS.mellomedleyMainMenuModButton.get().toString()));
            });
    public static final BooleanOption LEGACY_BUTTON_COLORS = new BooleanOption("config.mellowui.legacy_button_colors", new TranslationTextComponent("config.mellowui.legacy_button_colors.desc"),
            options -> CLIENT_CONFIGS.legacyButtonColors.get(), (options, newValue) -> CLIENT_CONFIGS.legacyButtonColors.set(newValue));
    public static final IteratableOption MAIN_MENU_STYLE = new TooltippedIterableOption("config.mellowui.main_menu_style", new TranslationTextComponent("config.mellowui.main_menu_style.desc"),
            (options, identifier) -> CLIENT_CONFIGS.mainMenuStyle.set(ThreeStyles.byId(CLIENT_CONFIGS.mainMenuStyle.get().getId() + identifier)),
            (options, option) -> new TranslationTextComponent("config.mellowui." + CLIENT_CONFIGS.mainMenuStyle.get().toString() + "_style", new TranslationTextComponent("config.mellowui.main_menu_style")));
    public static final IteratableOption MOD_LIST_STYLE = new IteratableOption("config.mellowui.mod_list_style",
            (options, identifier) -> {
                CLIENT_CONFIGS.modListStyle.set(ThreeStyles.byId(CLIENT_CONFIGS.modListStyle.get().getId() + identifier));
                if (CLIENT_CONFIGS.modListStyle.get() == ThreeStyles.OPTION_3 && !ModList.get().isLoaded("catalogue")) CLIENT_CONFIGS.modListStyle.set(ThreeStyles.OPTION_1);
            },
            (options, option) -> {
                option.setTooltip(Minecraft.getInstance().font.split(MOD_LIST_STYLE_TOOLTIP, TOOLTIP_MAX_WIDTH));
                return new TranslationTextComponent("config.mellowui.mod_list_style", new TranslationTextComponent("config.mellowui.mod_list_style." + CLIENT_CONFIGS.modListStyle.get().toString()));
            });
    public static final BooleanOption DISABLE_BRANDING = new BooleanOption("config.mellowui.disable_branding", new TranslationTextComponent("config.mellowui.disable_branding.desc"),
            options -> CLIENT_CONFIGS.disableBranding.get(), (options, newValue) -> CLIENT_CONFIGS.disableBranding.set(newValue));
    public static final StyleBooleanOption UPDATED_PAUSE_MENU = new StyleBooleanOption("config.mellowui.updated_pause_menu", new TranslationTextComponent("config.mellowui.updated_pause_menu.desc"),
            options -> CLIENT_CONFIGS.updatePauseMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updatePauseMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_OPTIONS_MENU = new StyleBooleanOption("config.mellowui.updated_options_menu", new TranslationTextComponent("config.mellowui.updated_options_menu.desc"),
            options -> CLIENT_CONFIGS.updateOptionsMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updateOptionsMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_SKIN_CUSTOMIZATION_MENU = new StyleBooleanOption("config.mellowui.updated_skin_customization_menu", new TranslationTextComponent("config.mellowui.updated_skin_customization_menu.desc"),
            options -> CLIENT_CONFIGS.updateSkinCustomizationMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updateSkinCustomizationMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_MUSIC_AND_SOUNDS_MENU = new StyleBooleanOption("config.mellowui.updated_music_and_sounds_menu", new TranslationTextComponent("config.mellowui.updated_music_and_sounds_menu.desc"),
            options -> CLIENT_CONFIGS.updateMusicAndSoundsMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updateMusicAndSoundsMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_CONTROLS_MENU = new StyleBooleanOption("config.mellowui.updated_controls_menu", new TranslationTextComponent("config.mellowui.updated_controls_menu.desc"),
            options -> CLIENT_CONFIGS.updateControlsMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updateControlsMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_PACK_MENU = new StyleBooleanOption("config.mellowui.updated_pack_menu", new TranslationTextComponent("config.mellowui.updated_pack_menu.desc"),
            options -> CLIENT_CONFIGS.updatePackMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updatePackMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_ACCESSIBILITY_MENU = new StyleBooleanOption("config.mellowui.updated_accessibility_menu", new TranslationTextComponent("config.mellowui.updated_accessibility_menu.desc"),
            options -> CLIENT_CONFIGS.updateAccessibilityMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updateAccessibilityMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_OUT_OF_MEMORY_MENU = new StyleBooleanOption("config.mellowui.updated_out_of_memory_menu", new TranslationTextComponent("config.mellowui.updated_out_of_memory_menu.desc"),
            options -> CLIENT_CONFIGS.updateOutOfMemoryMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updateOutOfMemoryMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_SCREEN_BACKGROUND = new StyleBooleanOption("config.mellowui.updated_screen_background", new TranslationTextComponent("config.mellowui.updated_screen_background.desc"),
            options -> CLIENT_CONFIGS.updateScreenBackground.get(), (options, newValue) -> CLIENT_CONFIGS.updateScreenBackground.set(newValue));
    public static final StyleBooleanOption UPDATED_LIST_BACKGROUND = new StyleBooleanOption("config.mellowui.updated_list_background", new TranslationTextComponent("config.mellowui.updated_list_background.desc"),
            options -> CLIENT_CONFIGS.updateListBackground.get(), (options, newValue) -> CLIENT_CONFIGS.updateListBackground.set(newValue));
    public static final BooleanOption REPLACE_REALMS_NOTIFICATIONS = new BooleanOption("config.mellowui.replace_realms_notifications", new TranslationTextComponent("config.mellowui.replace_realms_notifications.desc"),
            options -> CLIENT_CONFIGS.replaceRealmsNotifications.get(), (options, newValue) -> CLIENT_CONFIGS.replaceRealmsNotifications.set(newValue));
    public static final StyleBooleanOption SPLASH_TEXT_POSITION = new StyleBooleanOption("config.mellowui.splash_text_position", new TranslationTextComponent("config.mellowui.splash_text_position.desc"),
            options -> CLIENT_CONFIGS.splashTextPosition.get(), (options, newValue) -> CLIENT_CONFIGS.splashTextPosition.set(newValue));
    public static final IteratableOption LOGO_STYLE = new TooltippedIterableOption("config.mellowui.logo_style", new TranslationTextComponent("config.mellowui.logo_style.desc"),
            (options, identifier) -> CLIENT_CONFIGS.logoStyle.set(ThreeStyles.byId(CLIENT_CONFIGS.logoStyle.get().getId() + identifier)),
            (options, option) -> new TranslationTextComponent("config.mellowui." + CLIENT_CONFIGS.logoStyle.get().toString() + "_style", new TranslationTextComponent("config.mellowui.logo_style")));
    public static final BooleanOption SCROLLING_TEXT = new BooleanOption("config.mellowui.scrolling_text", new TranslationTextComponent("config.mellowui.scrolling_text.desc"),
            options -> CLIENT_CONFIGS.scrollingText.get(), (options, newValue) -> CLIENT_CONFIGS.scrollingText.set(newValue));

    // Forge options
    public static final IteratableOption MOD_LIST_SORTING = new IteratableOption("config.mellowui.mod_list_sorting",
            (options, identifier) -> CLIENT_CONFIGS.modListSorting.set(ModListSorting.byId(CLIENT_CONFIGS.modListSorting.get().getId() + identifier)),
            (options, newValue) -> new TranslationTextComponent("config.mellowui.mod_list_sorting", new TranslationTextComponent("config.mellowui.mod_list_sorting." + CLIENT_CONFIGS.modListSorting.get().toString())));
}
