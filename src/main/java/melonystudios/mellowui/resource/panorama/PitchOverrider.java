package melonystudios.mellowui.resource.panorama;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

/// Represents an overrider for the camera pitch of the panorama.
public interface PitchOverrider {
    /// Saves this pitch overrider, including its {@linkplain #assetID() asset id} as well.
    default JsonObject save() {
        JsonObject object = new JsonObject();
        object.addProperty("type", this.assetID().toString());
        this.toJSON(object);
        return object;
    }

    /// @return The asset id (or name) of this overrider, like `mellowui:constant`.
    ResourceLocation assetID();

    /// Saves this overrider to JSON.
    /// @param object The JSON object to save to.
    void toJSON(JsonObject object);

    /// Loads this overrider from JSON.
    /// @param object The JSON object to read from.
    PitchOverrider fromJSON(JsonObject object);
}
