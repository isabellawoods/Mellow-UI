package melonystudios.mellowui.config;

import melonystudios.mellowui.config.option.EditColorConfigOption;
import melonystudios.mellowui.config.option.EditConfigOption;
import melonystudios.mellowui.config.option.SeparatorOption;
import net.minecraft.util.text.TranslationTextComponent;

import static melonystudios.mellowui.config.WidgetConfigs.WIDGET_CONFIGS;

public class WidgetConfigEntries {
    // Separators
    public static final SeparatorOption WIDGETS_SEPARATOR = new SeparatorOption(new TranslationTextComponent("separator.mellowui.widgets"));
    public static final SeparatorOption TEXT_FIELDS_SEPARATOR = new SeparatorOption(new TranslationTextComponent("separator.mellowui.text_fields"));
    public static final SeparatorOption SPLASHES_SEPARATOR = new SeparatorOption(new TranslationTextComponent("separator.mellowui.splashes"));
    public static final SeparatorOption UPDATE_AVAILABILITY_SEPARATOR = new SeparatorOption(new TranslationTextComponent("separator.mellowui.update_availability"));
    public static final SeparatorOption BACKGROUNDS_SEPARATOR = new SeparatorOption(new TranslationTextComponent("separator.mellowui.backgrounds"));
    public static final SeparatorOption TOASTS_SEPARATOR = new SeparatorOption(new TranslationTextComponent("separator.mellowui.toasts"));
    public static final SeparatorOption MODDED_COLORS_SEPARATOR = new SeparatorOption(new TranslationTextComponent("separator.mellowui.modded_colors"));

    // Widgets
    public static final EditColorConfigOption DEFAULT_WIDGET_TEXT_COLOR = new EditColorConfigOption("config.mellowui.default_widget_text_color",
            new TranslationTextComponent("config.mellowui.default_widget_text_color.tooltip"), WIDGET_CONFIGS.defaultWidgetTextColor);
    public static final EditColorConfigOption HIGHLIGHTED_WIDGET_TEXT_COLOR = new EditColorConfigOption("config.mellowui.highlighted_widget_text_color",
            new TranslationTextComponent("config.mellowui.highlighted_widget_text_color.tooltip"), WIDGET_CONFIGS.highlightedWidgetTextColor);
    public static final EditColorConfigOption DISABLED_WIDGET_TEXT_COLOR = new EditColorConfigOption("config.mellowui.disabled_widget_text_color",
            new TranslationTextComponent("config.mellowui.disabled_widget_text_color.tooltip"), WIDGET_CONFIGS.disabledWidgetTextColor);
    public static final EditColorConfigOption DEFAULT_LEGACY_WIDGET_TEXT_COLOR = new EditColorConfigOption("config.mellowui.default_legacy_widget_text_color",
            new TranslationTextComponent("config.mellowui.default_legacy_widget_text_color.tooltip"), WIDGET_CONFIGS.defaultLegacyWidgetTextColor);
    public static final EditColorConfigOption HIGHLIGHTED_LEGACY_WIDGET_TEXT_COLOR = new EditColorConfigOption("config.mellowui.highlighted_legacy_widget_text_color",
            new TranslationTextComponent("config.mellowui.highlighted_legacy_widget_text_color.tooltip"), WIDGET_CONFIGS.highlightedLegacyWidgetTextColor);
    public static final EditColorConfigOption DISABLED_LEGACY_WIDGET_TEXT_COLOR = new EditColorConfigOption("config.mellowui.disabled_legacy_widget_text_color",
            new TranslationTextComponent("config.mellowui.disabled_legacy_widget_text_color.tooltip"), WIDGET_CONFIGS.disabledLegacyWidgetTextColor);
    public static final EditColorConfigOption LOCKED_WIDGET_TEXT_COLOR = new EditColorConfigOption("config.mellowui.locked_widget_text_color",
            new TranslationTextComponent("config.mellowui.locked_widget_text_color.tooltip"), WIDGET_CONFIGS.lockedWidgetTextColor);
    public static final EditColorConfigOption HIGHLIGHTED_ICON_BUTTON_COLOR = new EditColorConfigOption("config.mellowui.highlighted_icon_button_color",
            new TranslationTextComponent("config.mellowui.highlighted_icon_button_color.tooltip"), WIDGET_CONFIGS.highlightedIconButtonColor);

    // Text fields
    public static final EditColorConfigOption TEXT_FIELD_CENTER_COLOR = new EditColorConfigOption("config.mellowui.text_field_center_color",
            new TranslationTextComponent("config.mellowui.text_field_center_color.tooltip"), WIDGET_CONFIGS.textFieldCenterColor);
    public static final EditColorConfigOption TEXT_FIELD_DEFAULT_BORDER_COLOR = new EditColorConfigOption("config.mellowui.text_field_default_border_color",
            new TranslationTextComponent("config.mellowui.text_field_default_border_color.tooltip"), WIDGET_CONFIGS.textFieldDefaultBorderColor);
    public static final EditColorConfigOption TEXT_FIELD_HIGHLIGHTED_BORDER_COLOR = new EditColorConfigOption("config.mellowui.text_field_highlighted_border_color",
            new TranslationTextComponent("config.mellowui.text_field_highlighted_border_color.tooltip"), WIDGET_CONFIGS.textFieldHighlightedBorderColor);
    public static final EditColorConfigOption TEXT_FIELD_SUGGESTION_COLOR = new EditColorConfigOption("config.mellowui.text_field_suggestion_color",
            new TranslationTextComponent("config.mellowui.text_field_suggestion_color.tooltip"), WIDGET_CONFIGS.textFieldSuggestionColor);
    public static final EditColorConfigOption TEXT_FIELD_HIGHLIGHTED_SUGGESTION_COLOR = new EditColorConfigOption("config.mellowui.text_field_highlighted_suggestion_color",
            new TranslationTextComponent("config.mellowui.text_field_highlighted_suggestion_color.tooltip"), WIDGET_CONFIGS.textFieldHighlightedSuggestionColor);
    public static final EditColorConfigOption TEXT_FIELD_HIGHLIGHT_COLOR = new EditColorConfigOption("config.mellowui.text_field_highlight_color",
            new TranslationTextComponent("config.mellowui.text_field_highlight_color.tooltip"), WIDGET_CONFIGS.textFieldHighlightColor);

    // Splashes
    public static final EditColorConfigOption SPLASH_TEXT_COLOR = new EditColorConfigOption("config.mellowui.splash_text_color",
            new TranslationTextComponent("config.mellowui.splash_text_color.tooltip"), WIDGET_CONFIGS.splashTextColor);
    public static final EditColorConfigOption MELLO_SPLASH_TEXT_COLOR = new EditColorConfigOption("config.mellomedley.splash_text_color",
            new TranslationTextComponent("config.mellomedley.splash_text_color.tooltip"), WIDGET_CONFIGS.mellomedleySplashTextColor);
    public static final EditColorConfigOption HIGH_CONTRAST_SPLASH_TEXT_COLOR = new EditColorConfigOption("config.mellowui.high_contrast_splash_text_color",
            new TranslationTextComponent("config.mellowui.high_contrast_splash_text_color.tooltip"), WIDGET_CONFIGS.highContrastSplashTextColor);

    // Update availability
    public static final EditColorConfigOption DEFAULT_UPDATE_AVAILABLE_COLOR = new EditColorConfigOption("config.mellowui.default_update_available_color",
            new TranslationTextComponent("config.mellowui.default_update_available_color.tooltip"), WIDGET_CONFIGS.defaultUpdateAvailableColor);
    public static final EditColorConfigOption HIGH_CONTRAST_UPDATE_AVAILABLE_COLOR = new EditColorConfigOption("config.mellowui.high_contrast_update_available_color",
            new TranslationTextComponent("config.mellowui.high_contrast_update_available_color.tooltip"), WIDGET_CONFIGS.highContrastUpdateAvailableColor);

    // Backgrounds
    public static final EditColorConfigOption MONOCHROME_LOADING_SCREEN_COLOR = new EditColorConfigOption("config.mellowui.monochrome_loading_screen_color",
            new TranslationTextComponent("config.mellowui.monochrome_loading_screen_color.tooltip"), WIDGET_CONFIGS.monochromeLoadingScreenColor);
    public static final EditColorConfigOption WARNING_32BIT_COLOR = new EditColorConfigOption("config.mellowui.warning_32bit_color",
            new TranslationTextComponent("config.mellowui.warning_32bit_color.tooltip"), WIDGET_CONFIGS.warning32BitColor);

    // Toasts
    public static final EditColorConfigOption SYSTEM_TOAST_TITLE_COLOR = new EditColorConfigOption("config.mellowui.system_toast_title_color",
            new TranslationTextComponent("config.mellowui.system_toast_title_color.tooltip"), WIDGET_CONFIGS.systemToastTitleColor);
    public static final EditColorConfigOption SYSTEM_TOAST_DESCRIPTION_COLOR = new EditColorConfigOption("config.mellowui.system_toast_description_color",
            new TranslationTextComponent("config.mellowui.system_toast_description_color.tooltip"), WIDGET_CONFIGS.systemToastDescriptionColor);
    public static final EditColorConfigOption MUSIC_TOAST_TEXT_COLOR = new EditColorConfigOption("config.mellowui.music_toast_text_color",
            new TranslationTextComponent("config.mellowui.music_toast_text_color.tooltip"), WIDGET_CONFIGS.musicToastTextColor);

    // Miscellaneous
    public static final EditColorConfigOption TITLE_TEXT_COLOR = new EditColorConfigOption("config.mellowui.title_text_color",
            new TranslationTextComponent("config.mellowui.title_text_color.tooltip"), WIDGET_CONFIGS.titleTextColor);
    public static final EditColorConfigOption DESCRIPTION_TEXT_COLOR = new EditColorConfigOption("config.mellowui.description_text_color",
            new TranslationTextComponent("config.mellowui.description_text_color.tooltip"), WIDGET_CONFIGS.descriptionTextColor);

    // Text padding
    public static final EditConfigOption BUTTON_TEXT_PADDING = new EditConfigOption("config.mellowui.button_text_padding",
            new TranslationTextComponent("config.mellowui.button_text_padding.tooltip"), WIDGET_CONFIGS.buttonTextPadding);
    public static final EditConfigOption EDIT_BUTTON_TEXT_PADDING = new EditConfigOption("config.mellowui.edit_button_text_padding",
            new TranslationTextComponent("config.mellowui.edit_button_text_padding.tooltip"), WIDGET_CONFIGS.editButtonTextPadding);
    public static final EditConfigOption TAB_TEXT_PADDING = new EditConfigOption("config.mellowui.tab_text_padding",
            new TranslationTextComponent("config.mellowui.tab_text_padding.tooltip"), WIDGET_CONFIGS.tabTextPadding);
    public static final EditConfigOption STRING_WIDGET_TEXT_PADDING = new EditConfigOption("config.mellowui.string_widget_text_padding",
            new TranslationTextComponent("config.mellowui.string_widget_text_padding.tooltip"), WIDGET_CONFIGS.stringWidgetTextPadding);
    public static final EditConfigOption MOD_ENTRY_TEXT_PADDING = new EditConfigOption("config.mellowui.mod_entry_text_padding",
            new TranslationTextComponent("config.mellowui.mod_entry_text_padding.tooltip"), WIDGET_CONFIGS.modEntryTextPadding);
}
