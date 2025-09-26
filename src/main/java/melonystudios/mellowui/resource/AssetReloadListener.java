package melonystudios.mellowui.resource;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import melonystudios.mellowui.MellowUI;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public abstract class AssetReloadListener extends ReloadListener<Map<ResourceLocation, JsonElement>> {
    public static final Logger LOGGER = LogManager.getLogger(MellowUI.MOD_ID + "/AssetReloader");
    public static final int PATH_SUFFIX_LENGTH = ".json".length();
    private final Gson gson;
    private final String directory;

    public AssetReloadListener(Gson gson, String directory) {
        this.gson = gson;
        this.directory = directory;
    }

    @Override
    @Nonnull
    protected Map<ResourceLocation, JsonElement> prepare(IResourceManager manager, IProfiler profiler) {
        Map<ResourceLocation, JsonElement> entries = new HashMap<>();
        int length = this.directory.length() + 1;

        for (ResourceLocation fileLocation : manager.listResources(this.directory, name -> name.endsWith(".json"))) {
            String path = fileLocation.getPath();
            ResourceLocation entryLocation = new ResourceLocation(fileLocation.getNamespace(), path.substring(length, path.length() - PATH_SUFFIX_LENGTH));

            try (Reader reader = new BufferedReader(new InputStreamReader(manager.getResource(fileLocation).getInputStream(), StandardCharsets.UTF_8))) {
                JsonElement element = JSONUtils.fromJson(this.gson, reader, JsonElement.class);
                if (element != null) {
                    JsonElement element1 = entries.put(entryLocation, element);
                    if (element1 != null) throw new IllegalArgumentException("Duplicate asset file with ID " + entryLocation + " ignored");
                } else {
                    MellowUI.LOGGER.error("Couldn't load asset file {} from {} as it's null or empty", entryLocation, fileLocation);
                }
            } catch (IllegalArgumentException | IOException | JsonParseException exception) {
                MellowUI.LOGGER.error("Couldn't parse asset file {} from {}", entryLocation, fileLocation, exception);
            }
        }

        return entries;
    }
}
