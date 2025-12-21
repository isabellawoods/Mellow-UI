package melonystudios.mellowui.resource.theme;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.methods.InterfaceMethods.PackRepositoryMethods;
import melonystudios.mellowui.resource.panorama.Panorama;
import melonystudios.mellowui.resource.panorama.Panoramas;
import melonystudios.mellowui.screen.update.MUIOptionsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// Utility class for handling {@linkplain Theme themes} added by *Mellow UI*.
public class Themes {
    public static final Map<ResourceLocation, Theme> THEMES = new HashMap<>();

    /// Represents an instance of the `mellowui:default` theme.
    public static final Theme DEFAULT = Theme.builder().build();

    /// Represents the instance of the currently selected theme.
    private static Theme CURRENT_THEME = null;
    public static ResourceLocation lastSelectedPanorama = null;

    /// Applies a given theme to the game.
    /// @param newTheme The theme to apply.
    /// @param name The (resource location) of the theme, used to set the {@linkplain MellowConfigs#selectedTheme **Selected Theme**} option.
    public static void selectTheme(Theme newTheme, String name) {
        // saving the selected theme
        if (!MellowConfigs.CLIENT_CONFIGS.selectedTheme.get().equals(name)) MellowConfigs.CLIENT_CONFIGS.selectedTheme.set(name);

        // clear the cached theme instance
        CURRENT_THEME = newTheme;

        // selecting the panorama (if provided)
        if (newTheme.panorama() != null) {
            ResourceLocation location = Panoramas.locationFor(newTheme.panorama());
            if (location == null) return;

            if (!location.equals(Panoramas.assetID())) lastSelectedPanorama = Panoramas.assetID();
            Panoramas.selectPanorama(newTheme.panorama(), location.toString());
        } else if (lastSelectedPanorama != null && !lastSelectedPanorama.equals(Panoramas.assetID())) {
            // if none is provided, set the panorama back to what it was before
            // this does not survive a reload ~isa 19-12-25
            Panorama panorama = Panoramas.PANORAMAS.get(lastSelectedPanorama);
            Panoramas.selectPanorama(panorama, lastSelectedPanorama.toString());
            lastSelectedPanorama = Panoramas.assetID();
        }
    }

    /// Enables all resource packs provided by the theme to the game.
    /// @param theme The theme to apply.
    public static void enablePacksFrom(Theme theme) {
        ResourcePackList repository = Minecraft.getInstance().getResourcePackRepository();
        List<String> resourcePacks = theme.resourcePacks();

        // apply packs added by the current theme
        for (String pack : resourcePacks) ((PackRepositoryMethods) repository).addPack(pack);
        if (!resourcePacks.isEmpty()) MUIOptionsScreen.updateResourcePacksList(repository);
    }

    /// Disables all resource packs provided by the theme from the game.
    /// @param theme The theme to apply.
    public static void disablePacksFrom(Theme theme) {
        ResourcePackList repository = Minecraft.getInstance().getResourcePackRepository();
        List<String> resourcePacks = theme.resourcePacks();

        // remove packs added by the current theme
        for (String pack : resourcePacks) ((PackRepositoryMethods) repository).removePack(pack);
        if (!resourcePacks.isEmpty()) MUIOptionsScreen.updateResourcePacksList(repository);
    }

    /// Gets the **asset id** for a theme from the {@linkplain #THEMES themes map}.
    /// @param theme The theme to get.
    @Nullable
    public static ResourceLocation locationFor(Theme theme) {
        return THEMES.entrySet().stream()
                .filter(entry -> entry.getValue() == theme)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    /// @return The currently selected theme.
    public static Theme theme() {
        if (THEMES.isEmpty()) return DEFAULT; // don't set the current theme if themes haven't been loaded

        if (CURRENT_THEME == null) {
            CURRENT_THEME = THEMES.getOrDefault(assetID(), DEFAULT);
        }
        return CURRENT_THEME;
    }

    /// @return The **asset id** of the currently selected theme.
    public static ResourceLocation assetID() {
        ResourceLocation location = ResourceLocation.tryParse(MellowConfigs.CLIENT_CONFIGS.selectedTheme.get());
        return location == null ? Theme.DEFAULT_LOCATION : location;
    }

    /// @param assetID The **asset id** of the theme.
    /// @return A resource location pointing to the theme's icon.
    /// May not exist, check with {@link net.minecraft.resources.IResourceManager#hasResource IResourceManager.hasResource()}.
    public static ResourceLocation iconLocation(ResourceLocation assetID) {
        return new ResourceLocation(assetID.getNamespace(), "textures/gui/theme/icon/" + assetID.getPath() + ".png");
    }
}
