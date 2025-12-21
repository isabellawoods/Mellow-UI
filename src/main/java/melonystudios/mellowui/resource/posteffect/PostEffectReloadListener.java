package melonystudios.mellowui.resource.posteffect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.resource.AssetReloadListener;
import melonystudios.mellowui.resource.MUIResourceTypes;
import melonystudios.mellowui.util.ShaderManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostEffectReloadListener extends AssetReloadListener {
    private static final Marker MARKER = MarkerManager.getMarker("PostEffectReloader");
    public static final Gson GSON = new GsonBuilder().create();

    public PostEffectReloadListener() {
        super(GSON, MUIResourceTypes.POST_EFFECTS, "shaders/post");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> entries, IResourceManager manager, IProfiler profiler) {
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
        switch (assetID.toString()) {
            case "mellowui:blur":
            case "minecraft:blur": return new String[] {"Radius"}; // float
            case "minecraft:art": return new String[] {"LumaRamp"}; // float
            case "minecraft:blobs":
            case "minecraft:bumpy":
            case "minecraft:antialias": return new String[] {"OutSize"}; // vec2
            case "minecraft:invert": return new String[] {"InSize"}; // vec2
            case "minecraft:spider": return new String[] {"InSize", "Time"}; // vec2, float
            default: return new String[] {};
        }
    }
}
