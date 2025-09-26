package melonystudios.mellowui.resource.flair;

import com.google.gson.*;
import net.minecraft.util.text.TranslationTextComponent;

import java.lang.reflect.Type;

public class ModListFlair {
    public ModListFlair() {}

    public static GsonBuilder createFlairSerializer() {
        return new GsonBuilder().registerTypeAdapter(ModListFlair.class, new Serializer());
    }

    private static class Serializer implements JsonSerializer<ModListFlair>, JsonDeserializer<ModListFlair> {
        @Override
        public ModListFlair deserialize(JsonElement element, Type sourceType, JsonDeserializationContext context) throws JsonParseException {
            if (element.isJsonObject()) {
                return new ModListFlair();
            } else {
                throw new JsonParseException(new TranslationTextComponent("logger.mellowui.mod_list_flair.parsing", element.toString()).getString());
            }
        }

        @Override
        public JsonElement serialize(ModListFlair flair, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            return object;
        }
    }
}
