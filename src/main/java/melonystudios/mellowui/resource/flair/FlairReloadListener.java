package melonystudios.mellowui.resource.flair;

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

public class FlairReloadListener extends AssetReloadListener {
    public static final Logger LOGGER = LogManager.getLogger(MellowUI.MOD_ID + "/FlairReloader");
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
                LOGGER.error(MellowUtils.translate("logger.mellowui.flair.parsing", "Failed to parse mod list flair '%s'", location), exception);
            }
        });
        MellowUtils.FLAIRS.clear();
        MellowUtils.FLAIRS.putAll(flairs.build());
        LOGGER.info(MellowUtils.translate("logger.mellowui.flair.loaded", "Loaded %s mod list flair(s)", flairs.build().size()));
    }
}
