package melonystudios.mellowui.resource.panorama;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.resource.AssetReloadListener;
import melonystudios.mellowui.resource.MUIResourceTypes;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class PanoramaReloadListener extends AssetReloadListener {
    public static final Logger LOGGER = LogManager.getLogger(MellowUI.MOD_ID + "/PanoramaReloader");
    public static final Gson GSON = Panorama.createPanoramaSerializer().create();

    public PanoramaReloadListener() {
        super(GSON, MUIResourceTypes.PANORAMAS, "panorama");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> entries, IResourceManager manager, IProfiler profiler) {
        ImmutableMap.Builder<ResourceLocation, Panorama> panoramas = ImmutableMap.builder();

        entries.forEach((location, element) -> {
            try {
                if (element.isJsonObject()) panoramas.put(location, GSON.fromJson(element, Panorama.class));
            } catch (Exception exception) {
                LOGGER.error(MellowUtils.translate("logger.mellowui.panorama.parsing", "Failed to parse panorama '%s'", location), exception);
            }
        });
        ImmutableMap<ResourceLocation, Panorama> map = panoramas.build();
        MellowUtils.PANORAMAS.clear();
        MellowUtils.PANORAMAS.putAll(map);
        LOGGER.info(MellowUtils.translate("logger.mellowui.panorama.loaded", "Loaded %s panorama(s)", map.size()));

        // rollback to default panorama if the currently selected one was unloaded ~isa 28-9-25
        if (!map.containsKey(Panoramas.panoramaLocation())) {
            LOGGER.warn(MellowUtils.translate("logger.mellowui.panorama.reset", "Set the panorama to default since '%s' was unloaded", Panoramas.panoramaLocation()));
            Panoramas.selectPanorama(Panoramas.DEFAULT, Panorama.DEFAULT_LOCATION.toString());
        }
    }
}
