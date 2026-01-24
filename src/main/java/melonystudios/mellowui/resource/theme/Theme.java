package melonystudios.mellowui.resource.theme;

import com.google.gson.*;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.resource.panorama.Panorama;
import melonystudios.mellowui.resource.panorama.Panoramas;
import melonystudios.mellowui.util.debug.MUIDebuggingFlags;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// A **theme** overrides various aspects of *Mellow UI*, such as panoramas, flairs and textures.
public class Theme {
    public static final ResourceLocation DEFAULT_LOCATION = MellowUI.mellowUI("default");
    @Nullable
    private final Panorama panorama;
    private final Map<ResourceLocation, Integer> flairs;
    private final List<ForgeConfigOverride> configs;
    private final List<String> resourcePacks;
    private final Map<ResourceLocation, ResourceLocation> textures;
    @Nullable
    private String descriptionID;

    /// A **theme** overrides various aspects of *Mellow UI*, such as panoramas, flairs and textures.
    /// @param panorama A {@linkplain Panorama panorama} to apply when this theme is selected.
    /// @param flairs A map of flair asset ids to their accent colors. Overrides defined here are prioritized over applied resource packs.
    /// @param configs Overrides entries of *Forge* configs. ***\[TO BE ADDED]***.
    /// @param resourcePacks A list of resource pack ids (such as `programer_art` or `file/Default Panorama.zip`) that this theme can enable/disable.
    /// @param textures A map of GUI texture overrides. The key value must always have the `textures/` prefix and `.png` suffix, as well as the namespace.
    public Theme(@Nullable Panorama panorama, Map<ResourceLocation, Integer> flairs, List<ForgeConfigOverride> configs, List<String> resourcePacks, Map<ResourceLocation, ResourceLocation> textures) {
        this.panorama = panorama;
        this.flairs = flairs;
        this.configs = configs;
        this.resourcePacks = resourcePacks;
        this.textures = textures;
    }

    /// Makes a new instance of the {@linkplain Builder theme builder}.
    public static Builder builder() {
        return new Builder();
    }

    @Nullable
    public Panorama panorama() {
        return this.panorama;
    }

    public Map<ResourceLocation, Integer> flairs() {
        return this.flairs;
    }

    public List<ForgeConfigOverride> configs() {
        return this.configs;
    }

    public List<String> resourcePacks() {
        return this.resourcePacks;
    }

    public Map<ResourceLocation, ResourceLocation> textures() {
        return this.textures;
    }

    protected String getOrCreateDescriptionID() {
        if (this.descriptionID == null) {
            ResourceLocation location = Themes.locationFor(this);
            if (location != null) {
                this.descriptionID = Util.makeDescriptionId("theme", location);
            } else {
                this.descriptionID = Util.makeDescriptionId("theme", DEFAULT_LOCATION);
            }
        }
        return this.descriptionID;
    }

    public String getDescriptionID() {
        return this.getOrCreateDescriptionID();
    }

    public static GsonBuilder createThemeSerializer() {
        return new GsonBuilder().registerTypeAdapter(Theme.class, new Serializer());
    }

    /// A builder for creating new themes.
    public static class Builder {
        @Nullable
        private Panorama panorama;
        private Map<ResourceLocation, Integer> flairs = new HashMap<>();
        private List<ForgeConfigOverride> configs = new ArrayList<>();
        private List<String> resourcePacks = new ArrayList<>();
        private Map<ResourceLocation, ResourceLocation> textures = new HashMap<>();

        /// Provides a {@linkplain Panorama panorama} to apply when this theme is selected.
        /// @param panorama The panorama.
        public Builder panorama(Panorama panorama) {
            this.panorama = panorama;
            return this;
        }

        /// Overrides the accent color of the provided flair with another color.
        /// @param flairLocation The **asset id** of the flair.
        /// @param accentColor The color to override the existing accent,
        public Builder withAccentColor(ResourceLocation flairLocation, int accentColor) {
            this.flairs.put(flairLocation, accentColor);
            return this;
        }

        /// Adds a *Forge* config override to this theme.
        /// @param override The override.
        public Builder overrideConfig(ForgeConfigOverride override) {
            this.configs.add(override);
            return this;
        }

        /// Makes this theme apply this resource pack when the *"Apply Packs"* button is pressed.
        /// @param packID The id of the resource pack, such as `programer_art` or  `file/Default Panorama.zip`.
        public Builder applyPack(String packID) {
            this.resourcePacks.add(packID);
            return this;
        }

        /// Overrides an existing texture with another.
        /// @param oldTexture The resource location of the old texture. **Must** have the `textures/` prefix and `.png` suffix.
        /// @param newTexture The resource location of the new texture. The prefix and suffix are *optional*.
        public Builder withTexture(ResourceLocation oldTexture, ResourceLocation newTexture) {
            this.textures.put(MellowUI.toTexturePath(oldTexture), newTexture);
            return this;
        }

        /// Creates a new {@linkplain Theme theme}.
        public Theme build() {
            return new Theme(this.panorama, this.flairs, this.configs, this.resourcePacks, this.textures);
        }
    }

    private static class Serializer implements JsonSerializer<Theme>, JsonDeserializer<Theme> {
        @Override
        public Theme deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (!element.isJsonObject()) {
                throw new JsonSyntaxException(TextComponents.translate("logger.mellowui.theme.parsing", "Failed to parse theme '%s'", element.getAsString()));
            }
            JsonObject object = element.getAsJsonObject();
            Builder builder = Theme.builder();

            // Panorama
            Panorama panorama = null;
            if (object.has("panorama") && object.get("panorama").isJsonPrimitive() && object.get("panorama").getAsJsonPrimitive().isString()) {
                panorama = Panoramas.PANORAMAS.get(ResourceLocation.tryParse(object.get("panorama").getAsString()));
            }
            builder.panorama(panorama);

            // Flairs
            if (object.has("flairs") && object.get("flairs").isJsonObject()) {
                JsonObject flairs = object.get("flairs").getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : flairs.entrySet()) {
                    ResourceLocation key = ResourceLocation.tryParse(entry.getKey());
                    JsonElement element1 = entry.getValue();
                    Integer value = element1.isJsonPrimitive() ? Integer.decode(element1.getAsString()) : null;

                    if (value == null) throw new JsonParseException(TextComponents.translate("logger.mellowui.theme.expected_int", "Expected '%s' to be an integer, but was %s", key, GsonHelper.getType(element1)));
                    builder.withAccentColor(key, value);
                }
            }

            // Configs
            if (object.has("configs") && object.get("configs").isJsonObject()) {
                MellowUI.LOGGER.warn(ThemeReloadListener.MARKER, TextComponents.translate("logger.mellowui.theme.configs_not_added", "A theme is defining the 'configs' field. This field doesn't work yet!"));
                JsonObject configs = object.get("configs").getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : configs.entrySet()) {
                    ForgeConfigOverride override = new ForgeConfigOverride(ResourceLocation.tryParse(entry.getKey()));
                    override.fromJSON(configs);
                    builder.overrideConfig(override);
                }
            }

            // Resource packs
            if (object.has("resource_packs") && object.get("resource_packs").isJsonArray()) {
                JsonArray resourcePacks = object.get("resource_packs").getAsJsonArray();
                for (JsonElement pack : resourcePacks) builder.applyPack(pack.getAsString());
            }

            // Textures
            if (object.has("textures") && object.get("textures").isJsonObject()) {
                JsonObject textures = object.get("textures").getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : textures.entrySet()) {
                    ResourceLocation key = ResourceLocation.tryParse(entry.getKey());
                    ResourceLocation value = ResourceLocation.tryParse(entry.getValue().getAsString());

                    if (key != null && !MUIDebuggingFlags.DEBUG_OOPS_ALL_TEXTURES && !key.getPath().startsWith("textures/gui/")) {
                        throw new JsonParseException(TextComponents.translate("logger.mellowui.theme.non_gui_texture", "Texture '%s' is not under 'textures/gui/'. Only GUI textures can be overridden", key));
                    }
                    builder.withTexture(key, value);
                }
            }

            return builder.build();
        }

        @Override
        public JsonElement serialize(Theme theme, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();

            // Panorama
            if (theme.panorama() != null) {
                ResourceLocation location = Panoramas.locationFor(theme.panorama());
                if (location != null) object.addProperty("panorama", location.toString());
            }

            // Flairs
            JsonObject flairs = new JsonObject();
            for (Map.Entry<ResourceLocation, Integer> entry : theme.flairs().entrySet()) {
                flairs.addProperty(entry.getKey().toString(), "#" + Integer.toHexString(entry.getValue()));
            }
            if (flairs.size() > 0) object.add("flairs", flairs);

            // Configs
            JsonObject configs = new JsonObject();
            for (ForgeConfigOverride override : theme.configs()) override.toJSON(configs);
            if (configs.size() > 0) object.add("configs", configs);

            // Resource packs
            JsonArray resourcePacks = new JsonArray();
            for (String pack : theme.resourcePacks()) resourcePacks.add(pack);
            if (!resourcePacks.isEmpty()) object.add("resource_packs", resourcePacks);

            // Textures
            JsonObject textures = new JsonObject();
            for (Map.Entry<ResourceLocation, ResourceLocation> entry : theme.textures().entrySet()) {
                ResourceLocation key = MellowUI.toTexturePath(entry.getKey());
                if (key != null && !MUIDebuggingFlags.DEBUG_OOPS_ALL_TEXTURES && !key.getPath().startsWith("textures/gui/")) {
                    throw new JsonParseException(TextComponents.translate("logger.mellowui.theme.non_gui_texture", "Texture '%s' is not under 'textures/gui/'. Only GUI textures can be overridden", key));
                }
                textures.addProperty(key.toString(), entry.getValue().toString());
            }
            if (textures.size() > 0) object.add("textures", textures);

            return object;
        }
    }
}
