package melonystudios.mellowui.resource;

import net.minecraftforge.resource.IResourceType;

/// An enum of all {@link IResourceType}s used by *Mellow UI*.
public enum MUIResourceTypes implements IResourceType {
    /// Used when handling the loading of {@linkplain melonystudios.mellowui.resource.flair.ModListFlair **mod list flairs**}.
    FLAIRS,
    /// Used when handling the loading of {@linkplain melonystudios.mellowui.resource.panorama.Panorama **panoramas**}.
    PANORAMAS
}
