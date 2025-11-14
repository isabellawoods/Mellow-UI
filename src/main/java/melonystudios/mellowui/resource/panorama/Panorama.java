package melonystudios.mellowui.resource.panorama;

import com.google.common.collect.Lists;
import com.google.gson.*;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/// A **panorama** is a set of textures and properties that are rendered on the background of nearly all screens.
public class Panorama {
    public static final ResourceLocation DEFAULT_LOCATION = MellowUI.mellowUI("default");
    private final List<ResourceLocation> cubeMap;
    private final ResourceLocation overlay;
    private final Float speedOverride;
    private final PitchOverrider pitchOverride;
    @Nullable
    private final ResourceLocation shader;
    private final Integer blurStrength;
    private RenderSkybox panorama;
    @Nullable
    private String descriptionID;

    /// A **panorama** is a set of textures and properties that are rendered on the background of nearly all screens.
    /// @param cubeMap A list of **exactly six** {@linkplain ResourceLocation resource locations} for each side of the cube.
    /// @param overlay A resource location of the {@linkplain GUITextures#PANORAMA_OVERLAY panorama overlay} texture.
    /// @param speedOverride A float that overrides the speed the panorama spins at.
    /// @param pitchOverride A {@linkplain PitchOverrider **pitch overrider**} that defines the pitch of fhe panoramic camera.
    /// @param shader A *nullable** resource location for a {@link melonystudios.mellowui.util.shader.PostEffect PostEffect} to render on the panorama.
    /// @param blurStrength An integer overriding the strength of the {@linkplain melonystudios.mellowui.config.MellowConfigs#menuBackgroundBlurriness **Menu Background Blur**}.
    private Panorama(List<ResourceLocation> cubeMap, ResourceLocation overlay, Float speedOverride, PitchOverrider pitchOverride, @Nullable ResourceLocation shader, Integer blurStrength) {
        this.cubeMap = cubeMap;
        this.overlay = overlay;
        this.speedOverride = speedOverride;
        this.pitchOverride = pitchOverride;
        this.shader = shader;
        this.blurStrength = blurStrength;
    }

    /// Makes a new instance of the {@linkplain Builder panorama builder}.
    /// @param cubeMap A list of **exactly six** {@linkplain ResourceLocation resource locations} for each side of the cube.
    public static Builder builder(List<ResourceLocation> cubeMap) {
        return new Builder(cubeMap);
    }

    public List<ResourceLocation> cubeMap() {
        return this.cubeMap;
    }

    public ResourceLocation overlayTexture() {
        return this.overlay;
    }

    public Float speedOverride() {
        return this.speedOverride;
    }

    public PitchOverrider pitchOverride() {
        return this.pitchOverride;
    }

    @Nullable
    public ResourceLocation shader() {
        return this.shader;
    }

    public Integer blurStrength() {
        return this.blurStrength;
    }

    /// Creates a new {@link RenderSkybox PanoramaRenderer} using the provided cube map textures.
    public RenderSkybox panorama() {
        if (this.panorama == null) this.panorama = new RenderSkybox(this.toCubeMap());
        return this.panorama;
    }

    /// Creates a new {@link RenderSkyboxCube CubeMap} using six textures instead of one path.
    public RenderSkyboxCube toCubeMap() {
        RenderSkyboxCube cube = new RenderSkyboxCube(this.cubeMap().get(0));
        ((InterfaceMethods.CubeMapMethods) cube).setPanoramaTextures(this.cubeMap());
        return cube;
    }

    protected String getOrCreateDescriptionID() {
        if (this.descriptionID == null) {
            MellowUtils.PANORAMAS.entrySet().stream()
                    .filter(entry -> entry.getValue() == this)
                    .map(Map.Entry::getKey).findFirst()
                    .ifPresent(location -> this.descriptionID = Util.makeDescriptionId("panorama", location));
            if (this.descriptionID == null) this.descriptionID = Util.makeDescriptionId("panorama", DEFAULT_LOCATION);
        }
        return this.descriptionID;
    }

    public String getDescriptionID() {
        return this.getOrCreateDescriptionID();
    }

    public static GsonBuilder createPanoramaSerializer() {
        return new GsonBuilder().registerTypeAdapter(Panorama.class, new Serializer());
    }

    /// A builder for creating new panoramas.
    public static class Builder {
        private final List<ResourceLocation> cubeMap;
        private ResourceLocation overlay = new ResourceLocation("gui/title/background/panorama_overlay");
        private Float speedOverride = null;
        private PitchOverrider pitchOverride = null;
        @Nullable
        private ResourceLocation shader = null;
        private Integer blurStrength = null;

        /// A builder for creating new panoramas.
        /// @param cubeMap A list of **exactly six** {@linkplain ResourceLocation resource locations} for each side of the cube.
        public Builder(List<ResourceLocation> cubeMap) {
            this.cubeMap = cubeMap;
        }

        /// Sets the `overlay` location of this panorama.
        /// @param overlay A resource location of the {@linkplain GUITextures#PANORAMA_OVERLAY panorama overlay} texture.
        public Builder overlay(ResourceLocation overlay) {
            this.overlay = overlay;
            return this;
        }

        /// Overrides the speed of this panorama.
        /// @param speed A float that overrides the speed the panorama spins at.
        public Builder overrideSpeed(Float speed) {
            this.speedOverride = speed;
            return this;
        }

        /// Overrides the pitch of this panorama.
        /// @param overrider A {@linkplain PitchOverrider **pitch overrider**} that defines the pitch of fhe panoramic camera.
        public Builder overridePitch(PitchOverrider overrider) {
            this.pitchOverride = overrider;
            return this;
        }

        /// Defines a shader to render on this panorama.
        /// @param shader A *nullable** resource location for a {@link melonystudios.mellowui.util.shader.PostEffect PostEffect} to render on the panorama.
        public Builder applyShader(ResourceLocation shader) {
            this.shader = shader;
            return this;
        }

        /// Defines the strength of the background blur to apply on this panorama.
        /// @param strength An integer overriding the strength of the {@linkplain melonystudios.mellowui.config.MellowConfigs#menuBackgroundBlurriness **Menu Background Blur**}.
        public Builder blurStrength(Integer strength) {
            this.blurStrength = strength;
            return this;
        }

        /// Creates a new {@linkplain Panorama panorama}.
        public Panorama build() {
            return new Panorama(this.cubeMap, this.overlay, this.speedOverride, this.pitchOverride, this.shader, this.blurStrength);
        }
    }

    private static class Serializer implements JsonSerializer<Panorama>, JsonDeserializer<Panorama> {
        @Override
        public Panorama deserialize(JsonElement element, Type sourceType, JsonDeserializationContext context) throws JsonParseException {
            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();

                // Cube map
                List<ResourceLocation> cubeMap = Lists.newArrayList();
                if (object.has("cube_map") && object.get("cube_map").isJsonArray()) {
                    JsonArray array = object.get("cube_map").getAsJsonArray();
                    for (JsonElement element1 : array) {
                        if (element1.getAsJsonPrimitive().isString()) {
                            cubeMap.add(new ResourceLocation(element1.getAsString()));
                        }
                    }
                }

                // Overlay texture
                ResourceLocation overlay = new ResourceLocation("gui/title/background/panorama_overlay");
                if (object.has("overlay") && object.get("overlay").isJsonPrimitive() && object.get("overlay").getAsJsonPrimitive().isString()) {
                    overlay = new ResourceLocation(object.get("overlay").getAsString());
                }

                // Speed override
                Float speedOverride = null;
                if (object.has("speed_override") && object.get("speed_override").isJsonPrimitive() && object.get("speed_override").getAsJsonPrimitive().isNumber()) {
                    speedOverride = object.get("speed_override").getAsFloat();
                }

                // Pitch override
                PitchOverrider pitchOverride = null;
                if (object.has("pitch_override") && object.get("pitch_override").isJsonObject()) {
                    JsonObject override = object.get("pitch_override").getAsJsonObject();
                    ResourceLocation type = new ResourceLocation(override.get("type").getAsString());
                    pitchOverride = MellowUtils.OVERRIDERS.get(type).apply(override);
                }

                // Shader
                ResourceLocation shader = null;
                if (object.has("shader") && object.get("shader").isJsonPrimitive() && object.get("shader").getAsJsonPrimitive().isString()) {
                    shader = new ResourceLocation(object.get("shader").getAsString());
                }

                // Blur strength
                Integer blurStrength = null;
                if (object.has("blur_strength") && object.get("blur_strength").isJsonPrimitive() && object.get("blur_strength").getAsJsonPrimitive().isNumber()) {
                    blurStrength = object.get("blur_strength").getAsInt();
                }

                return Panorama.builder(cubeMap).overlay(overlay).overrideSpeed(speedOverride).overridePitch(pitchOverride).applyShader(shader).blurStrength(blurStrength).build();
            } else {
                throw new JsonParseException(TextComponents.translate("logger.mellowui.panorama.parsing", "Failed to parse panorama '%s'", element.toString()));
            }
        }

        @Override
        public JsonElement serialize(Panorama panorama, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            int cubeMapTextures = panorama.cubeMap().size();
            if (cubeMapTextures == 6) {
                JsonArray cubeMap = new JsonArray();
                for (ResourceLocation location : panorama.cubeMap()) cubeMap.add(location.toString());
                object.add("cube_map", cubeMap);
            } else {
                if (cubeMapTextures > 6) throw new JsonSyntaxException(TextComponents.translate("logger.mellowui.panorama.cube_map.too_many", "Panorama has more than 6 cube map textures"));
                else throw new JsonSyntaxException(TextComponents.translate("logger.mellowui.panorama.cube_map.too_few", "Panorama has less than 6 cube map textures"));
            }

            if (!panorama.overlayTexture().equals(GUITextures.PANORAMA_OVERLAY)) {
                object.addProperty("overlay", panorama.overlayTexture().toString());
            }
            if (panorama.speedOverride() != null) object.addProperty("speed_override", panorama.speedOverride());
            if (panorama.pitchOverride() != null) object.add("pitch_override", panorama.pitchOverride().save());
            if (panorama.shader() != null) object.addProperty("shader", panorama.shader().toString());
            if (panorama.blurStrength() != null) object.addProperty("blur_strength", panorama.blurStrength());
            return object;
        }
    }
}
