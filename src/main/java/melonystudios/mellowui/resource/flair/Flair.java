package melonystudios.mellowui.resource.flair;

import com.google.gson.*;
import net.minecraft.client.resources.language.I18n;

import java.lang.reflect.Type;

public class Flair {
    public Flair() {}

    public static GsonBuilder createFlairSerializer() {
        return new GsonBuilder().registerTypeAdapter(Flair.class, new Serializer());
    }

    private static class Serializer implements JsonSerializer<Flair>, JsonDeserializer<Flair> {
        @Override
        public Flair deserialize(JsonElement element, Type sourceType, JsonDeserializationContext context) throws JsonParseException {
            if (element.isJsonObject()) {
                return new Flair();
            } else {
                throw new JsonParseException(I18n.get("logger.mellowui.flair.parsing", element.toString()));
            }
        }

        @Override
        public JsonElement serialize(Flair flair, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            return object;
        }
    }
}
