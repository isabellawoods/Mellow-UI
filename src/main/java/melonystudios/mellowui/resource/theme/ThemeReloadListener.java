package melonystudios.mellowui.resource.theme;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.resource.AssetReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Map;

public class ThemeReloadListener extends AssetReloadListener {
    protected static final Marker MARKER = MarkerManager.getMarker("ThemeReloader");
    public static final Gson GSON = Theme.createThemeSerializer().create();

    public ThemeReloadListener() {
        super(GSON, "theme");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> entries, ResourceManager manager, ProfilerFiller profiler) {
        ImmutableMap.Builder<ResourceLocation, Theme> themes = ImmutableMap.builder();

        entries.forEach((location, element) -> {
            try {
                if (element.isJsonObject()) themes.put(location, GSON.fromJson(element, Theme.class));
            } catch (Exception exception) {
                MellowUI.LOGGER.error(MARKER, TextComponents.translate("logger.mellowui.theme.parsing", "Failed to parse theme '%s'", location), exception);
            }
        });
        ImmutableMap<ResourceLocation, Theme> map = themes.build();
        Themes.THEMES.clear();
        Themes.THEMES.putAll(map);
        MellowUI.LOGGER.info(MARKER, TextComponents.translate("logger.mellowui.theme.loaded", "Loaded %s theme(s)", map.size()));

        // rollback to default theme if the currently selected one was unloaded ~isa 19-12-25
        if (!map.containsKey(Themes.assetID())) {
            MellowUI.LOGGER.warn(MARKER, TextComponents.translate("logger.mellowui.theme.reset", "Set the theme to default since '%s' was unloaded", Themes.assetID()));
            Themes.selectTheme(Themes.DEFAULT, Theme.DEFAULT_LOCATION.toString());
        }
    }
}
