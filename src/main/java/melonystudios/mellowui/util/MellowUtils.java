package melonystudios.mellowui.util;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.update.*;
import melonystudios.mellowui.util.debug.MUIDebuggingFlags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.*;

import java.util.Collection;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;

public class MellowUtils {
    public static boolean LOADING_ERRORS = MUIDebuggingFlags.DEBUG_FAKE_LOADING_ERRORS;

    /// @return Whether the {@linkplain MellowConfigs#defaultBackground **Default Background**} option is on,
    /// or if mod loading broke enough to load *Mellow UI*'s mixins, but not to the point the assets literally can't load.
    public static boolean defaultBackground() {
        return CLIENT_CONFIGS.defaultBackground.get() || LOADING_ERRORS;
    }

    /// @return Whether the "*High Contrast*" (either `mellowui:high_contrast` or `melonylib:high_contrast`) resource pack is enabled.
    public static boolean highContrastEnabled() {
        Collection<String> selectedPacks = Minecraft.getInstance().getResourcePackRepository().getSelectedIds();
        return selectedPacks.contains(GUITextures.MUI_HIGH_CONTRAST.toString()) || selectedPacks.contains(GUITextures.LIBRARY_HIGH_CONTRAST.toString());
    }

    /// @return `true` whenever *Mellow UI*'s "*High Contrast*" resource pack is unavailable for some reason.
    public static boolean highContrastUnavailable() {
        return !Minecraft.getInstance().getResourcePackRepository().getAvailableIds().contains(GUITextures.MUI_HIGH_CONTRAST.toString());
    }
}
