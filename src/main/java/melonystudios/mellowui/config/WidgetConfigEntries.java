package melonystudios.mellowui.config;

import melonystudios.mellowui.config.option.EditColorConfigOption;
import melonystudios.mellowui.config.option.EditConfigOption;
import melonystudios.mellowui.config.option.SeparatorOption;
import net.minecraft.network.chat.TranslatableComponent;

import static melonystudios.mellowui.config.WidgetConfigs.WIDGET_CONFIGS;

public class WidgetConfigEntries {
    // Separators
    public static final SeparatorOption WIDGETS_SEPARATOR = new SeparatorOption(new TranslatableComponent("separator.mellowui.widgets"));
    public static final SeparatorOption SPLASHES_SEPARATOR = new SeparatorOption(new TranslatableComponent("separator.mellowui.splashes"));
    public static final SeparatorOption UPDATE_AVAILABILITY_SEPARATOR = new SeparatorOption(new TranslatableComponent("separator.mellowui.update_availability"));
    public static final SeparatorOption BACKGROUNDS_SEPARATOR = new SeparatorOption(new TranslatableComponent("separator.mellowui.backgrounds"));

    // Widgets
    public static final EditColorConfigOption DEFAULT_WIDGET_TEXT_COLOR = new EditColorConfigOption("config.mellowui.default_widget_text_color",
            new TranslatableComponent("config.mellowui.default_widget_text_color.tooltip"), WIDGET_CONFIGS.defaultWidgetTextColor);
    public static final EditColorConfigOption HIGHLIGHTED_WIDGET_TEXT_COLOR = new EditColorConfigOption("config.mellowui.highlighted_widget_text_color",
            new TranslatableComponent("config.mellowui.highlighted_widget_text_color.tooltip"), WIDGET_CONFIGS.highlightedWidgetTextColor);
    public static final EditColorConfigOption DISABLED_WIDGET_TEXT_COLOR = new EditColorConfigOption("config.mellowui.disabled_widget_text_color",
            new TranslatableComponent("config.mellowui.disabled_widget_text_color.tooltip"), WIDGET_CONFIGS.disabledWidgetTextColor);
    public static final EditColorConfigOption DEFAULT_LEGACY_WIDGET_TEXT_COLOR = new EditColorConfigOption("config.mellowui.default_legacy_widget_text_color",
            new TranslatableComponent("config.mellowui.default_legacy_widget_text_color.tooltip"), WIDGET_CONFIGS.defaultLegacyWidgetTextColor);
    public static final EditColorConfigOption HIGHLIGHTED_LEGACY_WIDGET_TEXT_COLOR = new EditColorConfigOption("config.mellowui.highlighted_legacy_widget_text_color",
            new TranslatableComponent("config.mellowui.highlighted_legacy_widget_text_color.tooltip"), WIDGET_CONFIGS.highlightedLegacyWidgetTextColor);
    public static final EditColorConfigOption DISABLED_LEGACY_WIDGET_TEXT_COLOR = new EditColorConfigOption("config.mellowui.disabled_legacy_widget_text_color",
            new TranslatableComponent("config.mellowui.disabled_legacy_widget_text_color.tooltip"), WIDGET_CONFIGS.disabledLegacyWidgetTextColor);
    public static final EditColorConfigOption HIGHLIGHTED_ICON_BUTTON_COLOR = new EditColorConfigOption("config.mellowui.highlighted_icon_button_color",
            new TranslatableComponent("config.mellowui.highlighted_icon_button_color.tooltip"), WIDGET_CONFIGS.highlightedIconButtonColor);

    // Update Availability
    public static final EditColorConfigOption DEFAULT_UPDATE_AVAILABLE_COLOR = new EditColorConfigOption("config.mellowui.default_update_available_color",
            new TranslatableComponent("config.mellowui.default_update_available_color.tooltip"), WIDGET_CONFIGS.defaultUpdateAvailableColor);
    public static final EditColorConfigOption HIGH_CONTRAST_UPDATE_AVAILABLE_COLOR = new EditColorConfigOption("config.mellowui.high_contrast_update_available_color",
            new TranslatableComponent("config.mellowui.high_contrast_update_available_color.tooltip"), WIDGET_CONFIGS.highContrastUpdateAvailableColor);

    // Splashes
    public static final EditColorConfigOption MELLO_SPLASH_TEXT_COLOR = new EditColorConfigOption("config.mellomedley.splash_text_color",
            new TranslatableComponent("config.mellomedley.splash_text_color.tooltip"), WIDGET_CONFIGS.mellomedleySplashTextColor);
    public static final EditColorConfigOption SPLASH_TEXT_COLOR = new EditColorConfigOption("config.mellowui.splash_text_color",
            new TranslatableComponent("config.mellowui.splash_text_color.tooltip"), WIDGET_CONFIGS.splashTextColor);
    public static final EditColorConfigOption HIGH_CONTRAST_SPLASH_TEXT_COLOR = new EditColorConfigOption("config.mellowui.high_contrast_splash_text_color",
            new TranslatableComponent("config.mellowui.high_contrast_splash_text_color.tooltip"), WIDGET_CONFIGS.highContrastSplashTextColor);

    // Backgrounds
    public static final EditColorConfigOption MONOCHROME_LOADING_SCREEN_COLOR = new EditColorConfigOption("config.mellowui.monochrome_loading_screen_color",
            new TranslatableComponent("config.mellowui.monochrome_loading_screen_color.tooltip"), WIDGET_CONFIGS.monochromeLoadingScreenColor);

    // Miscellaneous
    public static final EditColorConfigOption DESCRIPTION_TEXT_COLOR = new EditColorConfigOption("config.mellowui.description_text_color",
            new TranslatableComponent("config.mellowui.description_text_color.tooltip"), WIDGET_CONFIGS.descriptionTextColor);

    // Text padding
    public static final EditConfigOption BUTTON_TEXT_PADDING = new EditConfigOption("config.mellowui.button_text_padding",
            new TranslatableComponent("config.mellowui.button_text_padding.tooltip"), WIDGET_CONFIGS.buttonTextPadding);
    public static final EditConfigOption EDIT_BUTTON_TEXT_PADDING = new EditConfigOption("config.mellowui.edit_button_text_padding",
            new TranslatableComponent("config.mellowui.edit_button_text_padding.tooltip"), WIDGET_CONFIGS.editButtonTextPadding);
    public static final EditConfigOption TAB_TEXT_PADDING = new EditConfigOption("config.mellowui.tab_text_padding",
            new TranslatableComponent("config.mellowui.tab_text_padding.tooltip"), WIDGET_CONFIGS.tabTextPadding);
    public static final EditConfigOption MOD_NAME_TEXT_PADDING = new EditConfigOption("config.mellowui.mod_name_text_padding",
            new TranslatableComponent("config.mellowui.mod_name_text_padding.tooltip"), WIDGET_CONFIGS.modNameTextPadding);
}
