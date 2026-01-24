package melonystudios.mellowui.element.text;

import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.resource.flair.Flairs;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.commons.lang3.StringUtils;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;
import static melonystudios.mellowui.config.WidgetConfigs.WIDGET_CONFIGS;
import static net.minecraft.util.FastColor.ARGB32.*;

/// Utility methods for creating {@linkplain Component text components}, {@linkplain Style styles} and colors in *Mellow UI*.
public class TextComponents {
    public static final String PROGRAMMER_ART_ID = "programer_art";
    public static final Component ELLIPSIS = new TranslatableComponent("menu.mellowui.ellipsis");

    /// Makes the title for a base config screen.
    /// @param modID The id the mod, used to get the mod's {@linkplain melonystudios.mellowui.resource.flair.Flair accent color}.
    /// @param modName The name of the mod.
    /// @return The name of the config screen: `[(Mod Name)] Options`.
    public static MutableComponent buildScreenTitle(String modID, String modName) {
        int accentColor = Flairs.accentColor(modID);
        return new TranslatableComponent("menu.mellowui.options.title",
                new TranslatableComponent("menu.mellowui.options.mod", modName).withStyle(withColor(accentColor).withBold(true)))
                .withStyle(titleStyle());
    }

    /// Makes the title for a config subscreen.
    /// @param modID The id of the mod, used to get the mod's {@linkplain melonystudios.mellowui.resource.flair.Flair accent color}.
    /// @param modName The name of the mod.
    /// @param subtitle The subscreen's name.
    /// @return The name of the config subscreen: `[(Mod Name)] Options > (Subscreen)`.
    public static MutableComponent buildScreenSubtitle(String modID, String modName, Component subtitle) {
        int accentColor = Flairs.accentColor(modID);
        return new TranslatableComponent("menu.mellowui.options.subtitle",
                new TranslatableComponent("menu.mellowui.options.mod", modName).withStyle(withColor(accentColor).withBold(true)),
                new TranslatableComponent("menu.mellowui.options.arrow").withStyle(withColor(accentColor).withBold(true)),
                subtitle).withStyle(titleStyle());
    }

    /// Gets the translated text for a translation key, and uses a fallback if not available.
    /// @param key The translation key to use and check.
    /// @param fallback A fallback string to use, using `%s` for arguments.
    /// @param args An optional array of arguments.
    public static String translate(String key, String fallback, Object... args) {
        if (I18n.exists(key)) return I18n.get(key, args);
        else return String.format(fallback, args);
    }

    /// @return A {@linkplain Style style} for the "*Search...*" suggestion for text boxes, using the
    /// {@linkplain WidgetConfigs#textFieldDefaultBorderColor **Default Border**} text field color config.
    public static Component searchText() {
        return new TranslatableComponent("button.mellowui.search").withStyle(withColor(WIDGET_CONFIGS.textFieldDefaultBorderColor.get()).withItalic(true));
    }

    /// @param search The search box text.
    /// @return Whether the provided string is considered blank (either fully blank, only whitespace, or only a `@`).
    public static boolean isBlank(String search) {
        return StringUtils.isBlank(search) || search.equals("@");
    }

    /// Makes a {@linkplain Style style} using a specified color.
    /// @param color The color to use.
    public static Style withColor(int color) {
        return Style.EMPTY.withColor(color);
    }

    /// @return A {@linkplain Style style} for titles, using the {@linkplain WidgetConfigs#titleTextColor **Title Text**} color option.
    public static Style titleStyle() {
        return withColor(WIDGET_CONFIGS.titleTextColor.get());
    }

    /// @return A {@linkplain Style style} for descriptions, using the {@linkplain WidgetConfigs#descriptionTextColor **Description Text**} color option.
    public static Style descriptionStyle() {
        return withColor(WIDGET_CONFIGS.descriptionTextColor.get());
    }

    /// Returns the color that should be used for {@linkplain melonystudios.mellowui.renderer.SplashRenderer rendering splashes}.
    /// It is overridden by the {@linkplain WidgetConfigs#highContrastSplashTextColor **High Contrast Splash**} color option when the "*High Contrast*" resource pack is enabled.
    /// @param defaultColor The default color of the splash.
    public static int splashColor(int defaultColor) {
        return MellowUtils.highContrastEnabled() ? WIDGET_CONFIGS.highContrastSplashTextColor.get() : defaultColor;
    }

    /// Creates a {@linkplain Style style} using the color that should be used for text rendering.
    /// @param selected Whether the widget (or text) is selected.
    /// @param active Whether the widget (or text) is active.
    public static Style selectableStyle(boolean selected, boolean active) {
        return withColor(selectableColor(selected, active));
    }

    /// Gets the color that should be used for rendering text. It is chosen based on the following circumstances:
    /// - If either the {@linkplain melonystudios.mellowui.config.MellowConfigs#legacyButtonColors **Legacy Button Colors**} option or the "*Programmer Art*" resource pack are enabled:
    ///   - If it's inactive, use **Disabled Legacy Widget Text** (`#A0A0A0`);
    ///   - if it's highlighted, use **Highlighted Legacy Widget Text** (`#FFFFA0`);
    ///   - Or else, use **Legacy Widget Text** (`#E0E0E0`).
    /// - Or else:
    ///   - If it's inactive, use **Disabled Widget Text** (`#A0A0A0`);
    ///   - if it's highlighted, use **Highlighted Widget Text** (`#FFFFFF`);
    ///   - Or else, use **Widget Text** (`#FFFFFF`).
    /// @param selected Whether this text is selected/hovered/focused.
    /// @param active Whether this text is active.
    public static int selectableColor(boolean selected, boolean active) {
        if (CLIENT_CONFIGS.legacyButtonColors.get() || Minecraft.getInstance().getResourcePackRepository().getSelectedIds().contains(PROGRAMMER_ART_ID)) {
            return !active ? WIDGET_CONFIGS.disabledLegacyWidgetTextColor.get() : (selected ? WIDGET_CONFIGS.highlightedLegacyWidgetTextColor.get() : WIDGET_CONFIGS.defaultLegacyWidgetTextColor.get());
        } else {
            return !active ? WIDGET_CONFIGS.disabledWidgetTextColor.get() : (selected ? WIDGET_CONFIGS.highlightedWidgetTextColor.get() : WIDGET_CONFIGS.defaultWidgetTextColor.get());
        }
    }

    /// Creates a {@linkplain Style style} using the color that should be used for text rendering.
    /// @param selected Whether the widget (or text) is selected.
    /// @param unlocked Whether the widget (or text) is unlocked.
    public static Style lockableStyle(boolean selected, boolean unlocked) {
        return withColor(lockableColor(selected, unlocked));
    }

    /// Gets the color that should be used for rendering text. It is chosen based on the following circumstances:
    /// - If either the {@linkplain melonystudios.mellowui.config.MellowConfigs#legacyButtonColors **Legacy Button Colors**} option or the "*Programmer Art*" resource pack are enabled:
    ///   - If it's locked, use **Locked Widget Text** (`#FF5555`);
    ///   - if it's highlighted, use **Highlighted Legacy Widget Text** (`#FFFFA0`);
    ///   - Or else, use **Legacy Widget Text** (`#E0E0E0`).
    /// - Or else:
    ///   - If it's locked, use **Locked Widget Text** (`#FF5555`);
    ///   - if it's highlighted, use **Highlighted Widget Text** (`#FFFFFF`);
    ///   - Or else, use **Widget Text** (`#FFFFFF`).
    /// @param selected Whether this text is selected/hovered/focused.
    /// @param unlocked Whether this text is unlocked.
    public static int lockableColor(boolean selected, boolean unlocked) {
        if (CLIENT_CONFIGS.legacyButtonColors.get() || Minecraft.getInstance().getResourcePackRepository().getSelectedIds().contains(PROGRAMMER_ART_ID)) {
            return !unlocked ? WIDGET_CONFIGS.lockedWidgetTextColor.get() : (selected ? WIDGET_CONFIGS.highlightedLegacyWidgetTextColor.get() : WIDGET_CONFIGS.defaultLegacyWidgetTextColor.get());
        } else {
            return !unlocked ? WIDGET_CONFIGS.lockedWidgetTextColor.get() : (selected ? WIDGET_CONFIGS.highlightedWidgetTextColor.get() : WIDGET_CONFIGS.defaultWidgetTextColor.get());
        }
    }

    /// Gets the color that should be used for rendering text shadow.
    /// @param selected Whether the widget (or text) is selected.
    /// @param active Whether the widget (or text) is active.
    /// @param alpha The transparency to be applied on the color. Defaults to `1`.
    public static int selectableShadowColor(boolean selected, boolean active, float alpha) {
        return darkenColor(selectableColor(selected, active), alpha, 0.25F);
    }

    /// Darkens a color based on a darkening factor.
    /// @param color The color to be darkened.
    /// @param alpha The transparency to be applied on the color. Defaults to `1`.
    /// @param darkeningFactor How much to darken the color. Defaults to `0.25`.
    public static int darkenColor(int color, float alpha, float darkeningFactor) {
        float red = red(color) * darkeningFactor;
        float green = green(color) * darkeningFactor;
        float blue = blue(color) * darkeningFactor;
        return color((int) (alpha * 255), (int) red, (int) green, (int) blue);
    }
}
