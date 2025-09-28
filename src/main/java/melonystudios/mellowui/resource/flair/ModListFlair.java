package melonystudios.mellowui.resource.flair;

import com.google.gson.*;
import melonystudios.mellowui.MellowUI;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.lang.reflect.Type;

/// **Mod list flairs** are customization options applied to a mod's entry on {@linkplain melonystudios.mellowui.screen.update.MellowModListScreen *Mellow UI*'s mod list screen}.
public class ModListFlair {
    public static final ResourceLocation DEFAULT_LOCATION = MellowUI.mellowUI("default");
    private final int accentColor;

    /// **Mod list flairs** are customization options applied to a mod's entry on {@linkplain melonystudios.mellowui.screen.update.MellowModListScreen *Mellow UI*'s mod list screen}.
    /// @param accentColor An integer defining the accent color of the mod.
    public ModListFlair(int accentColor) {
        this.accentColor = accentColor;
    }

    public int accentColor() {
        return this.accentColor;
    }

    public static GsonBuilder createFlairSerializer() {
        return new GsonBuilder().registerTypeAdapter(ModListFlair.class, new Serializer());
    }

    private static class Serializer implements JsonSerializer<ModListFlair>, JsonDeserializer<ModListFlair> {
        @Override
        public ModListFlair deserialize(JsonElement element, Type sourceType, JsonDeserializationContext context) throws JsonParseException {
            if (element.isJsonObject()) {
                return new ModListFlair(element.getAsJsonObject().get("accent_color").getAsInt());
            } else {
                throw new JsonParseException(new TranslationTextComponent("logger.mellowui.mod_list_flair.parsing", element.toString()).getString());
            }
        }

        @Override
        public JsonElement serialize(ModListFlair flair, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("accent_color", flair.accentColor());
            return object;
        }
    }
}
