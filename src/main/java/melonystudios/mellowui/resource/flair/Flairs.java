package melonystudios.mellowui.resource.flair;

import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.util.ResourceLocation;

/// Utility class for handling {@linkplain ModListFlair flairs} added by *Mellow UI*.
public class Flairs {
    /// Gets the accent color for a given mod.
    /// @param modID The id of the mod.
    /// @return The accent color of the mod, or `#FFFFA0` if it has none (`#FFFF55` if the *"High Contrast"* resource pack is enabled).
    public static int accentColor(String modID) {
        for (ResourceLocation location : MellowUtils.FLAIRS.keySet()) {
            if (location.getPath().equals(modID)) return MellowUtils.FLAIRS.get(location).accentColor();
        }
        return MellowUtils.highContrastEnabled() ? WidgetConfigs.WIDGET_CONFIGS.highContrastFlairAccentColor.get() :
                MellowUtils.FLAIRS.get(ModListFlair.DEFAULT_LOCATION).accentColor();
    }
}
