package melonystudios.mellowui.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class WidgetConfigs {
    private static final Pair<WidgetConfigs, ForgeConfigSpec> WIDGET_CONFIG_PAIR = new ForgeConfigSpec.Builder().configure(WidgetConfigs::new);
    public static final WidgetConfigs WIDGET_CONFIGS = WIDGET_CONFIG_PAIR.getLeft();
    public static final ForgeConfigSpec WIDGET_SPEC = WIDGET_CONFIG_PAIR.getRight();

    // Generic widget parameters
    public final ForgeConfigSpec.IntValue buttonTextBorderPadding;
    public final ForgeConfigSpec.IntValue modNameTextBorderPadding;

    // Colors for text in various locations
    public final ForgeConfigSpec.IntValue defaultWidgetTextColor;
    public final ForgeConfigSpec.IntValue highlightedWidgetTextColor;
    public final ForgeConfigSpec.IntValue disabledWidgetTextColor;
    public final ForgeConfigSpec.IntValue defaultLegacyWidgetTextColor;
    public final ForgeConfigSpec.IntValue highlightedLegacyWidgetTextColor;
    public final ForgeConfigSpec.IntValue disabledLegacyWidgetTextColor;

    public final ForgeConfigSpec.IntValue highContrastUpdateAvailableColor;
    public final ForgeConfigSpec.IntValue defaultUpdateAvailableColor;
    public final ForgeConfigSpec.IntValue splashTextColor;
    public final ForgeConfigSpec.IntValue mellomedleySplashTextColor;
    public final ForgeConfigSpec.IntValue highContrastSplashTextColor;

    // Background text colors
    public final ForgeConfigSpec.IntValue monochromeLoadingScreenColor;

    public WidgetConfigs(ForgeConfigSpec.Builder builder) {
        builder.push("widget");
        this.buttonTextBorderPadding = builder.comment("Border padding for a button with scrolling text. Defaults to 2px.", "Effectively capped at the 'buttonWidth / 2 - 1'.").defineInRange("buttonTextBorderPadding", 2, 0, Integer.MAX_VALUE);
        this.modNameTextBorderPadding = builder.comment("Border padding for a mod list entry's name. Defaults to 2px.", "Effectively capped at the 'buttonWidth / 2 - 1'.").defineInRange("modNameTextBorderPadding", 2, 0, Integer.MAX_VALUE);

        builder.pop().push("color");
        // Widgets
        this.defaultWidgetTextColor = builder.comment("Text color for an unselected widget.").defineInRange("widget.defaultTextColor", 0xFFFFFF, 0, 0xFFFFFF);
        this.highlightedWidgetTextColor = builder.comment("Text color for a selected/hovered widget.").defineInRange("widget.highlightedTextColor", 0xFFFFFF, 0, 0xFFFFFF);
        this.disabledWidgetTextColor = builder.comment("Text color for a disabled widget.").defineInRange("widget.disabledTextColor", 0xA0A0A0, 0, 0xFFFFFF);
        this.defaultLegacyWidgetTextColor = builder.comment("Text color for an unselected widget with 'Legacy Button Colors' turned on.").defineInRange("widget.defaultLegacyTextColor", 0xE0E0E0, 0, 0xFFFFFF);
        this.highlightedLegacyWidgetTextColor = builder.comment("Text color for a selected/hovered widget with 'Legacy Button Colors' turned on.").defineInRange("widget.highlightedLegacyTextColor", 0xFFFFA0, 0, 0xFFFFFF);
        this.disabledLegacyWidgetTextColor = builder.comment("Text color for a disabled widget with 'Legacy Button Colors' turned on.").defineInRange("widget.disabledLegacyTextColor", 0xA0A0A0, 0, 0xFFFFFF);

        // Splash Texts
        this.splashTextColor = builder.comment("The color to use for the splash text in the default main menu.").defineInRange("splash.defaultTextColor", 0xFFFF00, 0, 0xFFFFFF);
        this.mellomedleySplashTextColor = builder.comment("The color to use for the splash text in the Mellomedley main menu.").defineInRange("splash.mellomedleyTextColor", 0xBDCF73, 0, 0xFFFFFF);
        this.highContrastSplashTextColor = builder.comment("The color to use for the splash text while the high contrast resource pack is enabled.").defineInRange("splash.highContrastTextColor", 0x57FFE1, 0, 0xFFFFFF);

        // Mod Update Availability
        this.defaultUpdateAvailableColor = builder.comment("Text color for the 'Update Available!' button on Mellow UI's mod list screen.").defineInRange("widget.defaultUpdateAvailableColor", 0x41F384, 0, 0xFFFFFF);
        this.highContrastUpdateAvailableColor = builder.comment("Text color for the 'Update Available!' button on Mellow UI's mod list screen.").defineInRange("widget.highContrastUpdateAvailableColor", 0x57FFE1, 0, 0xFFFFFF);

        // Backgrounds
        this.monochromeLoadingScreenColor = builder.comment("The color to use for the loading screen when the \"Monochrome Logo\" config is true.").defineInRange("background.loadingScreenColor", 0, 0, 0xFFFFFF);
        builder.pop();
    }
}
