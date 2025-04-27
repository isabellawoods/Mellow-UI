package melonystudios.mellowui.config;

import melonystudios.mellowui.config.option.SeparatorOption;
import melonystudios.mellowui.config.option.StyleBooleanOption;
import melonystudios.mellowui.config.option.TextFieldOption;
import melonystudios.mellowui.config.option.TooltippedIterableOption;
import melonystudios.mellowui.config.type.MainMenuModButton;
import melonystudios.mellowui.config.type.MainMenuStyle;
import melonystudios.mellowui.config.type.ModListSorting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;
import static melonystudios.mellowui.util.MellowUtils.TOOLTIP_MAX_WIDTH;

public class MellowConfigEntries {
    // Tooltips
    public static final IFormattableTextComponent ADJACENT_TOOLTIP = new TranslationTextComponent("config.mellowui.main_menu_mod_button.desc", new TranslationTextComponent("config.mellowui.main_menu_mod_button.adjacent.desc"));
    public static final IFormattableTextComponent ICON_TOOLTIP = new TranslationTextComponent("config.mellowui.main_menu_mod_button.desc", new TranslationTextComponent("config.mellowui.main_menu_mod_button.icon.desc"));
    public static final IFormattableTextComponent REPLACE_REALMS_TOOLTIP = new TranslationTextComponent("config.mellowui.main_menu_mod_button.desc", new TranslationTextComponent("config.mellowui.main_menu_mod_button.replace_realms.desc"));

    // Separators
    public static final SeparatorOption MAIN_MENU_SEPARATOR = new SeparatorOption("separator.mellowui.main_menu");
    public static final SeparatorOption MENU_UPDATES_SEPARATOR = new SeparatorOption("separator.mellowui.menu_updates");

    // Vanilla options
    public static final BooleanOption MONOCHROME_LOADING_SCREEN = new BooleanOption("config.minecraft.monochrome_loading_screen", new TranslationTextComponent("config.minecraft.monochrome_loading_screen.desc"),
            options -> CLIENT_CONFIGS.monochromeLoadingScreen.get(), (options, newValue) -> CLIENT_CONFIGS.monochromeLoadingScreen.set(newValue));
    public static final SliderPercentageOption PANORAMA_SCROLL_SPEED = new SliderPercentageOption("config.minecraft.panorama_scroll_speed", 0, 1, 0.01F,
            options -> CLIENT_CONFIGS.panoramaScrollSpeed.get(),
            (options, newValue) -> CLIENT_CONFIGS.panoramaScrollSpeed.set(newValue),
            (options, slider) -> new TranslationTextComponent("options.percent_value", new TranslationTextComponent("config.minecraft.panorama_scroll_speed"), (int) (slider.get(options) * 100)));
    public static final BooleanOption HIDE_SPLASH_TEXTS = new BooleanOption("config.minecraft.hide_splash_texts", new TranslationTextComponent("config.minecraft.hide_splash_texts.desc"),
            options -> CLIENT_CONFIGS.hideSplashTexts.get(), (options, newValue) -> CLIENT_CONFIGS.hideSplashTexts.set(newValue));

    // Mellow UI options
    public static final SliderPercentageOption PANORAMA_CAMERA_PITCH = new SliderPercentageOption("config.mellowui.panorama_camera_pitch", -90, 90, 1,
            (options) -> Double.valueOf(CLIENT_CONFIGS.panoramaCameraPitch.get()),
            (options, newValue) -> CLIENT_CONFIGS.panoramaCameraPitch.set((int) Math.round(newValue)),
            (options, slider) -> {
                slider.setTooltip(Minecraft.getInstance().font.split(new TranslationTextComponent("config.mellowui.panorama_camera_pitch.desc"), TOOLTIP_MAX_WIDTH));
                return new TranslationTextComponent("config.mellowui.panorama_camera_pitch", new TranslationTextComponent("config.mellowui.panorama_camera_pitch.pitch", Math.round(slider.get(options))));
            });
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
    public static final TextFieldOption MELLO_SPLASH_TEXT_COLOR = new TextFieldOption("config.mellowui.mello_splash_text_color",
            new TranslationTextComponent("config.mellowui.mello_splash_text_color.desc"),
            CLIENT_CONFIGS.melloSplashTextColor.get().toString(),
            newValue -> CLIENT_CONFIGS.melloSplashTextColor.set(Integer.valueOf(newValue)),
            (text, setter) -> {
                try {
                    int newValue = Integer.parseInt(text);
                    setter.accept(Integer.toString(MathHelper.clamp(newValue, 0, 16777215)));
                } catch (NumberFormatException ignored) {}
            });
    public static final IteratableOption MAIN_MENU_MOD_BUTTON = new IteratableOption("config.mellowui.main_menu_mod_button",
            (options, identifier) -> CLIENT_CONFIGS.mainMenuModButton.set(MainMenuModButton.byId(CLIENT_CONFIGS.mainMenuModButton.get().getId() + identifier)),
            (options, option) -> {
                switch (CLIENT_CONFIGS.mainMenuModButton.get()) {
                    case ADJACENT:
                        option.setTooltip(Minecraft.getInstance().font.split(ADJACENT_TOOLTIP, TOOLTIP_MAX_WIDTH));
                        break;
                    case ICON:
                        option.setTooltip(Minecraft.getInstance().font.split(ICON_TOOLTIP, TOOLTIP_MAX_WIDTH));
                        break;
                    case REPLACE_REALMS:
                        option.setTooltip(Minecraft.getInstance().font.split(REPLACE_REALMS_TOOLTIP, TOOLTIP_MAX_WIDTH));
                }
                return new TranslationTextComponent("config.mellowui.main_menu_mod_button", new TranslationTextComponent("config.mellowui.main_menu_mod_button." + CLIENT_CONFIGS.mainMenuModButton.get().toString()));
            });
    public static final BooleanOption LEGACY_BUTTON_COLORS = new BooleanOption("config.mellowui.legacy_button_colors", new TranslationTextComponent("config.mellowui.legacy_button_colors.desc"),
            options -> CLIENT_CONFIGS.legacyButtonColors.get(), (options, newValue) -> CLIENT_CONFIGS.legacyButtonColors.set(newValue));
    public static final IteratableOption MAIN_MENU_STYLE = new TooltippedIterableOption("config.mellowui.main_menu_style", new TranslationTextComponent("config.mellowui.main_menu_style.desc"),
            (options, identifier) -> CLIENT_CONFIGS.mainMenuStyle.set(MainMenuStyle.byId(CLIENT_CONFIGS.mainMenuStyle.get().getId() + identifier)),
            (options, option) -> new TranslationTextComponent("config.mellowui." + CLIENT_CONFIGS.mainMenuStyle.get().toString() + "_style", new TranslationTextComponent("config.mellowui.main_menu_style")));
    public static final BooleanOption DISABLE_BRANDING = new BooleanOption("config.mellowui.disable_branding", new TranslationTextComponent("config.mellowui.disable_branding.desc"),
            options -> CLIENT_CONFIGS.disableBranding.get(), (options, newValue) -> CLIENT_CONFIGS.disableBranding.set(newValue));
    public static final StyleBooleanOption UPDATED_PAUSE_MENU = new StyleBooleanOption("config.mellowui.updated_pause_menu", new TranslationTextComponent("config.mellowui.updated_pause_menu.desc"),
            options -> CLIENT_CONFIGS.updatePauseMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updatePauseMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_ACCESSIBILITY_MENU = new StyleBooleanOption("config.mellowui.updated_accessibility_menu", new TranslationTextComponent("config.mellowui.updated_accessibility_menu.desc"),
            options -> CLIENT_CONFIGS.updateAccessibilityMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updateAccessibilityMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_OUT_OF_MEMORY_MENU = new StyleBooleanOption("config.mellowui.updated_out_of_memory_menu", new TranslationTextComponent("config.mellowui.updated_out_of_memory_menu.desc"),
            options -> CLIENT_CONFIGS.updateOutOfMemoryMenu.get(), (options, newValue) -> CLIENT_CONFIGS.updateOutOfMemoryMenu.set(newValue));
    public static final StyleBooleanOption UPDATED_SCREEN_BACKGROUND = new StyleBooleanOption("config.mellowui.updated_screen_background", new TranslationTextComponent("config.mellowui.updated_screen_background.desc"),
            options -> CLIENT_CONFIGS.updateScreenBackground.get(), (options, newValue) -> CLIENT_CONFIGS.updateScreenBackground.set(newValue));
    public static final StyleBooleanOption UPDATED_LIST_BACKGROUND = new StyleBooleanOption("config.mellowui.updated_list_background", new TranslationTextComponent("config.mellowui.updated_list_background.desc"),
            options -> CLIENT_CONFIGS.updateListBackground.get(), (options, newValue) -> CLIENT_CONFIGS.updateListBackground.set(newValue));
    public static final BooleanOption SCROLLING_TEXT = new BooleanOption("config.mellowui.scrolling_text", new TranslationTextComponent("config.mellowui.scrolling_text.desc"),
            options -> CLIENT_CONFIGS.scrollingText.get(), (options, newValue) -> CLIENT_CONFIGS.scrollingText.set(newValue));

    // Forge options
    public static final IteratableOption MOD_LIST_SORTING = new IteratableOption("config.mellowui.mod_list_sorting",
            (options, identifier) -> CLIENT_CONFIGS.modListSorting.set(ModListSorting.byId(CLIENT_CONFIGS.modListSorting.get().getId() + identifier)),
            (options, newValue) -> new TranslationTextComponent("config.mellowui.mod_list_sorting", new TranslationTextComponent("config.mellowui.mod_list_sorting." + CLIENT_CONFIGS.modListSorting.get().toString())));
}
