package melonystudios.mellowui.resource.panorama;

import com.google.gson.JsonObject;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/// A **pitch overrider** for the panorama that keeps it at a constant pitch.
/// @param pitch An integer representing the pitch, clamped from `-90` to `90`.
public record ConstantPitch(int pitch) implements PitchOverrider {
    public static final ConstantPitch DEFAULT = new ConstantPitch(MellowConfigs.CLIENT_CONFIGS.panoramaCameraPitch.get());

    /// A **pitch overrider** for the panorama that keeps it at a constant pitch.
    /// @param pitch An integer representing the pitch, clamped from `-90` to `90`.
    public ConstantPitch(int pitch) {
        this.pitch = Mth.clamp(pitch, -90, 90);
    }

    @Override
    public ResourceLocation assetID() {
        return MellowUI.mellowUI("constant");
    }

    @Override
    public void toJSON(JsonObject object) {
        object.addProperty("pitch", this.pitch);
    }

    @Override
    public PitchOverrider fromJSON(JsonObject object) {
        return new ConstantPitch(object.get("pitch").getAsInt());
    }
}
