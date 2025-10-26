package melonystudios.mellowui.util.text;

import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.resource.flair.Flairs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;

import static melonystudios.mellowui.config.WidgetConfigs.WIDGET_CONFIGS;
import static melonystudios.mellowui.util.MellowUtils.withColor;

/// Utility methods for creating {@linkplain Component text components} in *Mellow UI*.
public class TextComponents {
    /// Makes the title for a base config screen.
    /// @param modID The id the mod, used to get the mod's {@linkplain melonystudios.mellowui.resource.flair.Flair accent color}.
    /// @param modName The name of the mod.
    /// @return The name of the config screen: `[(Mod Name)] Options`.
    public static MutableComponent buildScreenTitle(String modID, String modName) {
        int accentColor = Flairs.accentColor(modID);
        return new TranslatableComponent("menu.mellowui.options.title",
                new TranslatableComponent("menu.mellowui.options.mod", modName).withStyle(withColor(accentColor).withBold(true)));
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
                subtitle);
    }

    /// @return A {@linkplain Style style} for descriptions, using the {@linkplain WidgetConfigs#descriptionTextColor **Description Text Color**} option.
    public static Style descriptionStyle() {
        return withColor(WIDGET_CONFIGS.descriptionTextColor.get());
    }

    /// @return A {@linkplain Style style} for the "*Search...*" suggestion for text boxes, using the
    /// {@linkplain WidgetConfigs#textFieldDefaultBorderColor **Default Border**} text field color config.
    public static Component searchText() {
        return new TranslatableComponent("button.mellowui.search").withStyle(withColor(WIDGET_CONFIGS.textFieldDefaultBorderColor.get()).withItalic(true));
    }
}
