package melonystudios.mellowui.resource;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.element.text.TextComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public abstract class AssetReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {
    private static final Marker MARKER = MarkerManager.getMarker("AssetReloader");
    public static final int PATH_SUFFIX_LENGTH = ".json".length();
    private final Gson gson;
    private final String directory;

    public AssetReloadListener(Gson gson, String directory) {
        this.gson = gson;
        this.directory = directory;
    }

    @Override
    @Nonnull
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, JsonElement> entries = new HashMap<>();
        int length = this.directory.length() + 1;

        for (ResourceLocation fileLocation : manager.listResources(this.directory, name -> name.endsWith(".json"))) {
            String path = fileLocation.getPath();
            ResourceLocation entryLocation = new ResourceLocation(fileLocation.getNamespace(), path.substring(length, path.length() - PATH_SUFFIX_LENGTH));

            try (Reader reader = new BufferedReader(new InputStreamReader(manager.getResource(fileLocation).getInputStream(), StandardCharsets.UTF_8))) {
                JsonElement element = GsonHelper.fromJson(this.gson, reader, JsonElement.class);
                if (element != null) {
                    JsonElement element1 = entries.put(entryLocation, element);
                    if (element1 != null) throw new IllegalArgumentException(TextComponents.translate("logger.mellowui.asset_reloader.duplicate", "Ignored duplicate asset file with ID '%s'", entryLocation));
                } else {
                    MellowUI.LOGGER.error(MARKER, TextComponents.translate("logger.mellowui.asset_reloader.loading", "Couldn't load asset file '%s' from '%s' as it's null or empty", entryLocation, fileLocation));
                }
            } catch (IllegalArgumentException | IOException | JsonParseException exception) {
                MellowUI.LOGGER.error(MARKER, TextComponents.translate("logger.mellowui.asset_reloader.parsing", "Couldn't parse asset file '%s' from '%s'", entryLocation, fileLocation), exception);
            }
        }

        return entries;
    }
}
