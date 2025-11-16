package melonystudios.mellowui.resource.panorama;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.resource.AssetReloadListener;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.util.text.TextComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class PanoramaReloadListener extends AssetReloadListener {
    public static final Logger LOGGER = LogManager.getLogger(MellowUI.MOD_ID + "/PanoramaReloader");
    public static final Gson GSON = Panorama.createPanoramaSerializer().create();

    public PanoramaReloadListener() {
        super(GSON, "panorama");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> entries, ResourceManager manager, ProfilerFiller profiler) {
        ImmutableMap.Builder<ResourceLocation, Panorama> panoramas = ImmutableMap.builder();

        entries.forEach((location, element) -> {
            try {
                if (element.isJsonObject()) panoramas.put(location, GSON.fromJson(element, Panorama.class));
            } catch (Exception exception) {
                LOGGER.error(TextComponents.translate("logger.mellowui.panorama.parsing", "Failed to parse panorama '%s'", location), exception);
            }
        });
        ImmutableMap<ResourceLocation, Panorama> map = panoramas.build();
        MellowUtils.PANORAMAS.clear();
        MellowUtils.PANORAMAS.putAll(map);
        LOGGER.info(TextComponents.translate("logger.mellowui.panorama.loaded", "Loaded %s panorama(s)", map.size()));

        // rollback to default panorama if the currently selected one was unloaded ~isa 28-9-25
        if (!map.containsKey(Panoramas.panoramaLocation())) {
            LOGGER.warn(TextComponents.translate("logger.mellowui.panorama.reset", "Set the panorama to default since '%s' was unloaded", Panoramas.panoramaLocation()));
            Panoramas.selectPanorama(Panoramas.DEFAULT, Panorama.DEFAULT_LOCATION.toString());
        }
    }
}
