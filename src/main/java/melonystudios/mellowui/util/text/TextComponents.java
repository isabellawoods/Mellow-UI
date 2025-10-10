package melonystudios.mellowui.util.text;

import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.resource.flair.Flairs;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

/// Utility methods for creating {@linkplain ITextComponent text components} in *Mellow UI*.
public class TextComponents {
    /// Makes the title for a base config screen.
    /// @param modID The id the mod, used to get the mod's {@linkplain melonystudios.mellowui.resource.flair.Flair accent color}.
    /// @param modName The name of the mod.
    /// @return The name of the config screen: `[(Mod Name)] Options`.
    public static IFormattableTextComponent buildScreenTitle(String modID, String modName) {
        int accentColor = Flairs.accentColor(modID);
        return new TranslationTextComponent("menu.mellowui.options.title",
                new TranslationTextComponent("menu.mellowui.options.mod", modName).withStyle(MellowUtils.withColor(accentColor).withBold(true)));
    }

    /// Makes the title for a config subscreen.
    /// @param modID The id of the mod, used to get the mod's {@linkplain melonystudios.mellowui.resource.flair.Flair accent color}.
    /// @param modName The name of the mod.
    /// @param subtitle The subscreen's name.
    /// @return The name of the config subscreen: `[(Mod Name)] Options > (Subscreen)`.
    public static IFormattableTextComponent buildScreenSubtitle(String modID, String modName, ITextComponent subtitle) {
        int accentColor = Flairs.accentColor(modID);
        return new TranslationTextComponent("menu.mellowui.options.subtitle",
                new TranslationTextComponent("menu.mellowui.options.mod", modName).withStyle(MellowUtils.withColor(accentColor).withBold(true)),
                new TranslationTextComponent("menu.mellowui.options.arrow").withStyle(MellowUtils.withColor(accentColor).withBold(true)),
                subtitle);
    }

    public static Style descriptionStyle() {
        return MellowUtils.withColor(WidgetConfigs.WIDGET_CONFIGS.descriptionTextColor.get());
    }
}
