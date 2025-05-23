package melonystudios.mellowui.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

/// ### <em>making ui mods compatible since 2025!</em>
/// Mods:
/// - Create (panorama);
/// - Blue Skies (panorama and <code>options_background.png</code>);
/// - spanorama (and maybe other panorama mods);
/// - Back Math;
public class GUICompatUtils {
    /// Currently there's a few ways the panorama can change with other mods (that I found):
    ///   - <b>Create</b>: the "home" screen resets the panorama, making the rotation between the main menu and other screens
    /// inconsistent;
    ///   - <b>Blue Skies</b>: replaces the {@link net.minecraft.client.gui.screen.MainMenuScreen#CUBE_MAP CUBE_MAP} and
    /// {@link net.minecraft.client.gui.screen.MainMenuScreen#panorama panorama} fields directly with the access transformers --
    /// works by default with this system;
    ///       - <code>SkiesClientEvents.setNewPanorama()</code> also replaces the backgrounds but this mod doesn't use the default
    /// <code>options_background.png</code> file;
    ///   - <b>spanorama</b>: same as Blue Skies, but it still replaces the {@link net.minecraft.client.gui.screen.MainMenuScreen#CUBE_MAP CUBE_MAP} even if there's no panoramas
    /// available (<code>PanoramaClientEvents.setRandomPanorama({@link javax.annotation.Nullable @Nullable}
    /// {@link net.minecraft.client.gui.screen.MainMenuScreen MainMenuScreen})</code>);
    ///   - <b>Configured</b>: the config screens render twice, causing the panorama to spin twice as fast;
    ///   - <b>Cloth Config API</b>: uses a custom system altogether to render the backgrounds, making all my code ineffective;
    ///
    /// @author ~isa 17-5-25
    public GUICompatUtils() {}

    public static boolean hasCustomBackground(Minecraft minecraft, MatrixStack stack, int width, int height, float vOffset) {
        return !AbstractGui.BACKGROUND_LOCATION.toString().equals(GUITextures.OPTIONS_BACKGROUND.toString());
    }
}
