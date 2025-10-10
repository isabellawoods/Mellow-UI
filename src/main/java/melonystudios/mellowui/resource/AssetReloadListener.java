package melonystudios.mellowui.resource;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public abstract class AssetReloadListener extends ReloadListener<Map<ResourceLocation, JsonElement>> implements ISelectiveResourceReloadListener {
    public static final Logger LOGGER = LogManager.getLogger(MellowUI.MOD_ID + "/AssetReloader");
    public static final int PATH_SUFFIX_LENGTH = ".json".length();
    private final Gson gson;
    private final String directory;
    private final IResourceType type;

    public AssetReloadListener(Gson gson, IResourceType type, String directory) {
        this.gson = gson;
        this.directory = directory;
        this.type = type;
    }

    @Override
    public void onResourceManagerReload(IResourceManager manager, Predicate<IResourceType> predicate) {
        if (predicate.test(this.type)) this.prepare(manager, EmptyProfiler.INSTANCE);
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
                    if (element1 != null) throw new IllegalArgumentException(MellowUtils.translate("logger.mellowui.asset_reloader.duplicate", "Ignored duplicate asset file with ID '%s'", entryLocation));
                } else {
                    LOGGER.error(MellowUtils.translate("logger.mellowui.asset_reloader.loading", "Couldn't load asset file '%s' from '%s' as it's null or empty", entryLocation, fileLocation));
                }
            } catch (IllegalArgumentException | IOException | JsonParseException exception) {
                LOGGER.error(MellowUtils.translate("logger.mellowui.asset_reloader.parsing", "Couldn't parse asset file '%s' from '%s'", entryLocation, fileLocation), exception);
            }
        }

        return entries;
    }
}
