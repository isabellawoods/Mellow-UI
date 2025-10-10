package melonystudios.mellowui.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class WidgetConfigs {
    private static final Pair<WidgetConfigs, ForgeConfigSpec> WIDGET_CONFIG_PAIR = new ForgeConfigSpec.Builder().configure(WidgetConfigs::new);
    public static final WidgetConfigs WIDGET_CONFIGS = WIDGET_CONFIG_PAIR.getLeft();
    public static final ForgeConfigSpec WIDGET_SPEC = WIDGET_CONFIG_PAIR.getRight();

    // Generic widget parameters
    public final ForgeConfigSpec.IntValue buttonTextPadding;
    public final ForgeConfigSpec.IntValue editButtonTextPadding;
    public final ForgeConfigSpec.IntValue tabTextPadding;
    public final ForgeConfigSpec.IntValue modNameTextPadding;

    // Colors for text in various locations
    public final ForgeConfigSpec.IntValue defaultWidgetTextColor;
    public final ForgeConfigSpec.IntValue highlightedWidgetTextColor;
    public final ForgeConfigSpec.IntValue disabledWidgetTextColor;
    public final ForgeConfigSpec.IntValue defaultLegacyWidgetTextColor;
    public final ForgeConfigSpec.IntValue highlightedLegacyWidgetTextColor;
    public final ForgeConfigSpec.IntValue disabledLegacyWidgetTextColor;
    public final ForgeConfigSpec.IntValue highlightedIconButtonColor;

    public final ForgeConfigSpec.IntValue highContrastUpdateAvailableColor;
    public final ForgeConfigSpec.IntValue defaultUpdateAvailableColor;
    public final ForgeConfigSpec.IntValue splashTextColor;
    public final ForgeConfigSpec.IntValue mellomedleySplashTextColor;
    public final ForgeConfigSpec.IntValue highContrastSplashTextColor;

    public final ForgeConfigSpec.IntValue descriptionTextColor;

    // Background text colors
    public final ForgeConfigSpec.IntValue monochromeLoadingScreenColor;

    public WidgetConfigs(ForgeConfigSpec.Builder builder) {
        builder.push("widget");
        this.buttonTextPadding = builder.comment("Border padding for a button with scrolling text. Defaults to 2mpx.", "Effectively capped at 'buttonWidth / 2 - 1'.").translation("config.mellowui.button_text_padding").defineInRange("buttonTextPadding", 2, 0, Integer.MAX_VALUE);
        this.editButtonTextPadding = builder.comment("Border padding for an edit button with scrolling text. Defaults to 2mpx.", "Effectively capped at 'buttonWidth / 2 - 1'.").translation("config.mellowui.edit_button_text_padding").defineInRange("editButtonTextPadding", 2, 0, Integer.MAX_VALUE);
        this.tabTextPadding = builder.comment("Border padding for a tab with scrolling text. Defaults to 2mpx.", "Effectively capped at 'tabWidth / 2 - 1'.").translation("config.mellowui.tab_text_padding").defineInRange("tabTextPadding", 2, 0, Integer.MAX_VALUE);
        this.modNameTextPadding = builder.comment("Border padding for a mod list entry's name. Defaults to 2mpx.", "Effectively capped at 'entryWidth / 2 - 1'.").translation("config.mellowui.mod_name_text_padding").defineInRange("modNameTextPadding", 2, 0, Integer.MAX_VALUE);

        builder.pop().push("color");
        // Widgets
        this.defaultWidgetTextColor = builder.comment("Text color for an unselected widget.").translation("config.mellowui.default_widget_text_color").defineInRange("widget.defaultTextColor", 0xFFFFFF, 0, 0xFFFFFF);
        this.highlightedWidgetTextColor = builder.comment("Text color for a selected/hovered widget.").translation("config.mellowui.highlighted_widget_text_color").defineInRange("widget.highlightedTextColor", 0xFFFFFF, 0, 0xFFFFFF);
        this.disabledWidgetTextColor = builder.comment("Text color for a disabled widget.").translation("config.mellowui.disabled_widget_text_color").defineInRange("widget.disabledTextColor", 0xA0A0A0, 0, 0xFFFFFF);
        this.defaultLegacyWidgetTextColor = builder.comment("Text color for an unselected widget with 'Legacy Button Colors' turned on.").translation("config.mellowui.default_legacy_widget_text_color").defineInRange("widget.defaultLegacyTextColor", 0xE0E0E0, 0, 0xFFFFFF);
        this.highlightedLegacyWidgetTextColor = builder.comment("Text color for a selected/hovered widget with 'Legacy Button Colors' turned on.").translation("config.mellowui.highlighted_legacy_widget_text_color").defineInRange("widget.highlightedLegacyTextColor", 0xFFFFA0, 0, 0xFFFFFF);
        this.disabledLegacyWidgetTextColor = builder.comment("Text color for a disabled widget with 'Legacy Button Colors' turned on.").translation("config.mellowui.disabled_legacy_widget_text_color").defineInRange("widget.disabledLegacyTextColor", 0xA0A0A0, 0, 0xFFFFFF);
        this.highlightedIconButtonColor = builder.comment("Text color for a highlighted icon button on the title screen.").translation("config.mellowui.highlighted_icon_button_text_color").defineInRange("widget.highlightedIconButtonColor", 0xFFFFA0, 0, 0xFFFFFF);

        // Splash Texts
        this.splashTextColor = builder.comment("The color to use for the splash text in the default main menu.").translation("config.mellowui.splash_text_color").defineInRange("splash.defaultTextColor", 0xFFFF00, 0, 0xFFFFFF);
        this.mellomedleySplashTextColor = builder.comment("The color to use for the splash text in the Mellomedley main menu.").translation("config.mellomedley.splash_text_color").defineInRange("splash.mellomedleyTextColor", 0xBDCF73, 0, 0xFFFFFF);
        this.highContrastSplashTextColor = builder.comment("The color to use for the splash text while the high contrast resource pack is enabled.").translation("config.mellowui.high_contrast_splash_text_color").defineInRange("splash.highContrastTextColor", 0x57FFE1, 0, 0xFFFFFF);

        // Mod Update Availability
        this.defaultUpdateAvailableColor = builder.comment("Text color for the 'Update Available!' button on Mellow UI's mod list screen.").translation("config.mellowui.default_update_available_color").defineInRange("widget.defaultUpdateAvailableColor", 0x41F384, 0, 0xFFFFFF);
        this.highContrastUpdateAvailableColor = builder.comment("Text color for the 'Update Available!' button on Mellow UI's mod list screen.").translation("config.mellowui.high_contrast_update_available_color").defineInRange("widget.highContrastUpdateAvailableColor", 0x57FFE1, 0, 0xFFFFFF);

        // Backgrounds
        this.monochromeLoadingScreenColor = builder.comment("The color to use for the loading screen when the \"Monochrome Logo\" config is true.").translation("config.mellowui.monochrome_loading_screen_color").defineInRange("background.loadingScreenColor", 0, 0, 0xFFFFFF);

        // Miscellaneous
        this.descriptionTextColor = builder.comment("Text color for descriptions in Mellow UI.").translation("config.mellowui.description_text_color").defineInRange("miscellaneous.descriptionTextColor", 0xAAAAAA, 0, 0xFFFFFF);
        builder.pop();
    }
}
