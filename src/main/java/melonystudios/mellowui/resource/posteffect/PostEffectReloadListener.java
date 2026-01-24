package melonystudios.mellowui.resource.posteffect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.resource.AssetReloadListener;
import melonystudios.mellowui.util.ShaderManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostEffectReloadListener extends AssetReloadListener {
    private static final Marker MARKER = MarkerManager.getMarker("PostEffectReloader");
    public static final Gson GSON = new GsonBuilder().create();

    public PostEffectReloadListener() {
        super(GSON, "shaders/post");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> entries, ResourceManager manager, ProfilerFiller profiler) {
        List<PostEffect> effects = new ArrayList<>();

        entries.forEach((assetID, element) -> {
            String[] uniforms = this.getUniformsByID(assetID);
            effects.add(new PostEffect(assetID, uniforms));
        });

        ShaderManager.EFFECTS.clear();
        ShaderManager.EFFECTS.addAll(effects);
        MellowUI.LOGGER.info(MARKER, TextComponents.translate("logger.mellowui.post_effect.loaded", "Loaded %s post effect(s)", effects.size()));
    }

    private String[] getUniformsByID(ResourceLocation assetID) {
        return switch (assetID.toString()) {
            case "mellowui:blur", "minecraft:blur" -> new String[] {"Radius"}; // float
            case "minecraft:art" -> new String[] {"LumaRamp"}; // float
            case "minecraft:blobs", "minecraft:bumpy", "minecraft:antialias" -> new String[] {"OutSize"}; // vec2
            case "minecraft:invert" -> new String[] {"InSize"}; // vec2
            case "minecraft:spider" -> new String[] {"InSize", "Time"}; // vec2, float
            default -> new String[] {};
        };
    }
}
