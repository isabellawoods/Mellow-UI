package melonystudios.mellowui.resource;

import net.minecraftforge.resource.IResourceType;

/// An enum of all {@link IResourceType}s used by *Mellow UI*.
public enum MUIResourceTypes implements IResourceType {
    /// Used when handling the loading of {@linkplain melonystudios.mellowui.resource.flair.Flair **flairs**}.
    FLAIRS,
    /// Used when handling the loading of {@linkplain melonystudios.mellowui.resource.theme.Theme **themes**}.
    THEMES,
    /// Used when handling the loading of {@linkplain melonystudios.mellowui.resource.panorama.Panorama **panoramas**}.
    PANORAMAS,
    /// Used when handling the loading of {@linkplain melonystudios.mellowui.resource.posteffect.PostEffect **post-processing effects**}.
    POST_EFFECTS
}
