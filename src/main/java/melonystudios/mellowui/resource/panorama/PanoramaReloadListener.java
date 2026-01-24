package melonystudios.mellowui.resource.panorama;

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

public class PanoramaReloadListener extends AssetReloadListener {
    private static final Marker MARKER = MarkerManager.getMarker("PanoramaReloader");
    public static final Gson GSON = Panorama.createPanoramaSerializer().create();

    public PanoramaReloadListener() {
        super(GSON, "panorama");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> entries, ResourceManager manager, ProfilerFiller profiler) {
        ImmutableMap.Builder<ResourceLocation, Panorama> panoramas = ImmutableMap.builder();
        Panoramas.MENU_TO_PANORAMAS.clear();
        Panoramas.MENUS_WITH_DEFINED_PANORAMAS.clear();

        entries.forEach((location, element) -> {
            try {
                if (element.isJsonObject()) panoramas.put(location, GSON.fromJson(element, Panorama.class));
            } catch (Exception exception) {
                MellowUI.LOGGER.error(MARKER, TextComponents.translate("logger.mellowui.panorama.parsing", "Failed to parse panorama '%s'", location), exception);
            }
        });
        ImmutableMap<ResourceLocation, Panorama> map = panoramas.build();
        Panoramas.PANORAMAS.clear();
        Panoramas.PANORAMAS.putAll(map);
        MellowUI.LOGGER.info(MARKER, TextComponents.translate("logger.mellowui.panorama.loaded", "Loaded %s panorama(s)", map.size()));

        // rollback to default panorama if the currently selected one was unloaded ~isa 28-9-25
        if (!map.containsKey(Panoramas.assetID())) {
            MellowUI.LOGGER.warn(MARKER, TextComponents.translate("logger.mellowui.panorama.reset", "Set the panorama to default since '%s' was unloaded", Panoramas.assetID()));
            Panoramas.selectPanorama(Panoramas.DEFAULT, Panorama.DEFAULT_LOCATION.toString());
        }
    }
}
