package melonystudios.mellowui.resource.flair;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.resource.AssetReloadListener;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class FlairReloadListener extends AssetReloadListener {
    public static final Logger LOGGER = LogManager.getLogger(MellowUI.MOD_ID + "/Flairs");
    public static final Gson GSON = ModListFlair.createFlairSerializer().create();

    public FlairReloadListener() {
        super(GSON, "flair");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> entries, IResourceManager manager, IProfiler profiler) {
        ImmutableMap.Builder<ResourceLocation, ModListFlair> flairs = ImmutableMap.builder();

        entries.forEach((location, element) -> {
            try {
                if (element.isJsonObject()) {
                    ModListFlair flair = GSON.fromJson(element, ModListFlair.class);
                    flairs.put(location, flair);
                }
            } catch (Exception exception) {
                LOGGER.error(new TranslationTextComponent("logger.mellowui.mod_list_flair.parsing", location).getString(), exception);
            }
        });
        MellowUtils.FLAIRS.putAll(flairs.build());
        LOGGER.info(new TranslationTextComponent("logger.mellowui.mod_list_flair.loaded", flairs.build().size()).getString());
    }
}
