package melonystudios.mellowui.util.debug;

import melonystudios.mellowui.util.MellowUtils;

/// Debugging flags used for *Mellow UI*'s development.
public class MUIDebuggingFlags {
    /// Allows texture overrides of themes to override ***every*** texture, including things like texture atlases and the
    /// {@linkplain net.minecraft.client.renderer.LightTexture light map} (didn't seem to work when I tested, but it's allowed).
    ///
    /// By default, it only allows overrides for textures inside the `gui` folder.
    /// @since 5.0.0-beta3
    /// @see melonystudios.mellowui.mixin.client.MUITextureManagerMixin#bindThemeTextureForSetup  MUITextureManagerMixin.bindThemeTextureForSetup
    /// @see melonystudios.mellowui.mixin.client.MUIRenderSystemMixin#setThemeTexture MUIRenderSystemMixin.setThemeTexture
    public static final boolean DEBUG_OOPS_ALL_TEXTURES = false;
    /// Makes the {@linkplain melonystudios.mellowui.element.toast.MusicToast music toast} never leave the screen when a song is playing.
    /// Used for making changelog thumbnails.
    /// @since 5.0.0-beta3
    public static final boolean DEBUG_CONSTANT_MUSIC_TOAST = false;
    /// Sets the {@link MellowUtils#LOADING_ERRORS LOADING_ERRORS} flag to `true` for testing purposes.
    ///
    /// This is used to revert basic UI changes, such as the background and separators, to their vanilla states so the missing texture isn't used when assets aren't loaded.
    /// @since 5.0.0-beta3
    public static final boolean DEBUG_FAKE_LOADING_ERRORS = false;
    /// Logs the {@linkplain melonystudios.mellowui.screen.SuperSecretSettingsScreen#playRandomSound random sound played}
    /// when selecting an entry in the Super Secret Settings screen.
    /// @since 5.0.0-beta3
    public static final boolean DEBUG_LOG_SECRET_SETTINGS_SOUNDS = false;
    /// Renders a colored rectangle around every scissored area (I've just made a debug renderer, haven't I?).
    /// @since 5.0.0-beta3
    /// @see melonystudios.mellowui.element.RenderComponents#applyScissor RenderComponents.applyScissor
    public static final boolean DEBUG_RECTANGLE_RAVE = false;
}
