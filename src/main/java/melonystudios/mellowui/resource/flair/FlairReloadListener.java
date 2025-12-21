package melonystudios.mellowui.resource.flair;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.resource.AssetReloadListener;
import melonystudios.mellowui.resource.MUIResourceTypes;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Map;

public class FlairReloadListener extends AssetReloadListener {
    private static final Marker MARKER = MarkerManager.getMarker("FlairReloader");
    public static final Gson GSON = Flair.createFlairSerializer().create();

    public FlairReloadListener() {
        super(GSON, MUIResourceTypes.FLAIRS, "flair");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> entries, IResourceManager manager, IProfiler profiler) {
        ImmutableMap.Builder<ResourceLocation, Flair> flairs = ImmutableMap.builder();

        entries.forEach((location, element) -> {
            try {
                if (element.isJsonObject()) flairs.put(location, GSON.fromJson(element, Flair.class));
            } catch (Exception exception) {
                MellowUI.LOGGER.error(MARKER, TextComponents.translate("logger.mellowui.flair.parsing", "Failed to parse flair '%s'", location), exception);
            }
        });
        Flairs.FLAIRS.clear();
        Flairs.FLAIRS.putAll(flairs.build());
        MellowUI.LOGGER.info(MARKER, TextComponents.translate("logger.mellowui.flair.loaded", "Loaded %s flair(s)", flairs.build().size()));
    }
}
