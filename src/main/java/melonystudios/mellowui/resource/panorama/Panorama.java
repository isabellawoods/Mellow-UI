package melonystudios.mellowui.resource.panorama;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.*;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;

/// A **panorama** is a set of textures and properties that are rendered on the background of nearly all screens.
public class Panorama {
    public static final ResourceLocation DEFAULT_LOCATION = MellowUI.mellowUI("default");
    private final List<ResourceLocation> cubeMap;
    private final ResourceLocation overlay;
    private final List<String> usedIn;
    private final Float speedOverride;
    private final PitchOverrider pitchOverride;
    @Nullable
    private final ResourceLocation shader;
    private final Integer blurStrength;
    private PanoramaRenderer panorama;
    @Nullable
    private String descriptionID;

    /// A **panorama** is a set of textures and properties that are rendered on the background of nearly all screens.
    /// @param cubeMap A list of **exactly six** {@linkplain ResourceLocation resource locations} for each side of the cube.
    /// @param overlay A resource location of the {@linkplain GUITextures#PANORAMA_OVERLAY panorama overlay} texture.
    /// @param usedIn A list of screen class paths that this panorama renders in. Panoramas fade in/out when switching screens.
    /// @param speedOverride A float that overrides the speed the panorama spins at.
    /// @param pitchOverride A {@linkplain PitchOverrider **pitch overrider**} that defines the pitch of fhe panoramic camera.
    /// @param shader A *nullable* resource location for a {@linkplain melonystudios.mellowui.resource.posteffect.PostEffect post-processing effect} to render on the panorama.
    /// @param blurStrength An integer overriding the strength of the {@linkplain melonystudios.mellowui.config.MellowConfigs#menuBackgroundBlurriness **Menu Background Blur**}.
    private Panorama(List<ResourceLocation> cubeMap, ResourceLocation overlay, List<String> usedIn, Float speedOverride, PitchOverrider pitchOverride, @Nullable ResourceLocation shader, Integer blurStrength) {
        this.cubeMap = cubeMap;
        this.overlay = overlay;
        this.usedIn = usedIn;
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

    public List<String> usedIn() {
        return this.usedIn;
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

    /// Creates a new {@link PanoramaRenderer} using the provided cube map textures.
    public PanoramaRenderer panorama() {
        if (this.panorama == null) this.panorama = new PanoramaRenderer(this.toCubeMap());
        return this.panorama;
    }

    /// Creates a new {@link CubeMap} using six textures instead of one path.
    public CubeMap toCubeMap() {
        CubeMap cube = new CubeMap(this.cubeMap().get(0));
        ((InterfaceMethods.CubeMapMethods) cube).setPanoramaTextures(this.cubeMap());
        return cube;
    }

    protected String getOrCreateDescriptionID() {
        if (this.descriptionID == null) {
            ResourceLocation location = Panoramas.locationFor(this);
            if (location != null) {
                this.descriptionID = Util.makeDescriptionId("panorama", location);
            } else {
                this.descriptionID = Util.makeDescriptionId("panorama", DEFAULT_LOCATION);
            }
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
        private final List<String> usedIn = Lists.newArrayList();
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

        /// Makes the provided screen class use this panorama when rendering its background.
        /// @param screens A list of screen class paths.
        public Builder usedIn(List<String> screens) {
            this.usedIn.addAll(screens);
            return this;
        }

        /// Makes the provided screen class use this panorama when rendering its background.
        /// @param classPath The path of the screen class, like {@link net.minecraft.client.gui.screens.OptionsScreen}.
        public Builder usedIn(String classPath) {
            this.usedIn.add(classPath);
            return this;
        }

        /// Makes the provided screen class use this panorama when rendering its background.
        /// @param screen A class that extends {@link Screen}.
        public Builder usedIn(Class<? extends Screen> screen) {
            return this.usedIn(screen.getName());
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
        /// @param shader A *nullable* resource location for a {@linkplain melonystudios.mellowui.resource.posteffect.PostEffect post-processing effect} to render on the panorama.
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
            return new Panorama(this.cubeMap, this.overlay, this.usedIn, this.speedOverride, this.pitchOverride, this.shader, this.blurStrength);
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

                // throw exception if the cube_map doesn't have exactly 6 textures ~isa 16-12-25
                if (cubeMap.size() > 6) throw new JsonParseException(TextComponents.translate("logger.mellowui.panorama.cube_map.too_many", "Panorama has more than 6 cube map textures"));
                else if (cubeMap.size() < 6) throw new JsonParseException(TextComponents.translate("logger.mellowui.panorama.cube_map.too_few", "Panorama has less than 6 cube map textures"));

                // Overlay texture
                ResourceLocation overlay = new ResourceLocation("gui/title/background/panorama_overlay");
                if (object.has("overlay") && object.get("overlay").isJsonPrimitive() && object.get("overlay").getAsJsonPrimitive().isString()) {
                    overlay = new ResourceLocation(object.get("overlay").getAsString());
                }

                // Used in (screens)
                List<String> usedIn = Lists.newArrayList();
                if (object.has("used_in") && object.get("used_in").isJsonArray()) {
                    JsonArray array = object.get("used_in").getAsJsonArray();
                    for (JsonElement element1 : array) {
                        if (element1.getAsJsonPrimitive().isString()) usedIn.add(element1.getAsString());
                    }
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
                    pitchOverride = Panoramas.OVERRIDERS.get(type).apply(override);
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
                if (blurStrength != null) Preconditions.checkArgument(blurStrength >= 0, TextComponents.translate("logger.mellowui.panorama.negative_blur", "Expected blur strength to be a positive value; got %s", blurStrength));

                Panorama panorama = Panorama.builder(cubeMap).overlay(overlay).overrideSpeed(speedOverride).overridePitch(pitchOverride).applyShader(shader).blurStrength(blurStrength).build();
                if (!usedIn.isEmpty()) {
                    Panoramas.MENUS_WITH_DEFINED_PANORAMAS.addAll(usedIn);
                    for (String classPath : usedIn) Panoramas.MENU_TO_PANORAMAS.put(classPath, panorama.panorama());
                }

                return panorama;
            } else {
                throw new JsonSyntaxException(TextComponents.translate("logger.mellowui.panorama.parsing", "Failed to parse panorama '%s'", element.toString()));
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
                if (cubeMapTextures > 6) throw new JsonParseException(TextComponents.translate("logger.mellowui.panorama.cube_map.too_many", "Panorama has more than 6 cube map textures"));
                else throw new JsonParseException(TextComponents.translate("logger.mellowui.panorama.cube_map.too_few", "Panorama has less than 6 cube map textures"));
            }

            if (!panorama.overlayTexture().equals(GUITextures.PANORAMA_OVERLAY)) {
                object.addProperty("overlay", panorama.overlayTexture().toString());
            }
            if (!panorama.usedIn().isEmpty()) {
                JsonArray usedIn = new JsonArray();
                for (String classPath : panorama.usedIn()) usedIn.add(classPath);
                object.add("used_in", usedIn);
            }
            if (panorama.speedOverride() != null) object.addProperty("speed_override", panorama.speedOverride());
            if (panorama.pitchOverride() != null) object.add("pitch_override", panorama.pitchOverride().save());
            if (panorama.shader() != null) object.addProperty("shader", panorama.shader().toString());
            Integer blurStrength = panorama.blurStrength();
            if (blurStrength != null) {
                Preconditions.checkArgument(blurStrength >= 0, TextComponents.translate("logger.mellowui.panorama.negative_blur", "Expected blur strength to be a positive value; got %s", blurStrength));
                object.addProperty("blur_strength", blurStrength);
            }
            return object;
        }
    }
}
