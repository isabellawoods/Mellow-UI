package melonystudios.mellowui.resource.flair;

import melonystudios.mellowui.resource.theme.Themes;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/// Utility class for handling {@linkplain Flair flairs} added by *Mellow UI*.
public class Flairs {
    public static final Map<ResourceLocation, Flair> FLAIRS = new HashMap<>();

    /// Gets the accent color for a given mod.
    /// @param modID The id of the mod.
    /// @return The accent color of the mod, or `#FFFFA0` if it has none (`#FFFF55` if the *"High Contrast"* theme is selected).
    public static int accentColor(String modID) {
        for (ResourceLocation location : FLAIRS.keySet()) {
            if (location.getPath().equals(modID)) return Themes.theme().flairs().getOrDefault(location, FLAIRS.get(location).accentColor());
        }
        return Themes.theme().flairs().getOrDefault(Flair.DEFAULT_LOCATION, FLAIRS.get(Flair.DEFAULT_LOCATION).accentColor());
    }
}
