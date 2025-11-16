package melonystudios.mellowui.resource.flair;

import com.google.gson.*;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.screen.update.MellowModListScreen;
import melonystudios.mellowui.util.text.TextComponents;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;

/// **Flairs** are customization options applied to a mod's entry on {@linkplain MellowModListScreen *Mellow UI*'s mod list screen}.
/// @param accentColor An integer defining the accent color of the mod.
public record Flair(int accentColor) {
    public static final ResourceLocation DEFAULT_LOCATION = MellowUI.mellowUI("builtin/default");

    public static GsonBuilder createFlairSerializer() {
        return new GsonBuilder().registerTypeAdapter(Flair.class, new Serializer());
    }

    private static class Serializer implements JsonSerializer<Flair>, JsonDeserializer<Flair> {
        @Override
        public Flair deserialize(JsonElement element, Type sourceType, JsonDeserializationContext context) throws JsonParseException {
            if (element.isJsonObject()) {
                return new Flair(element.getAsJsonObject().get("accent_color").getAsInt());
            } else {
                throw new JsonParseException(TextComponents.translate("logger.mellowui.flair.parsing", "Failed to parse flair '%s'", element.toString()));
            }
        }

        @Override
        public JsonElement serialize(Flair flair, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("accent_color", flair.accentColor());
            return object;
        }
    }
}
