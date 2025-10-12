package melonystudios.mellowui.resource.panorama;

import com.google.gson.JsonObject;
import melonystudios.mellowui.MellowUI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/// A **pitch overrider** for the panorama that defines the intensity of the bobbing.
/// @param bobbingStrength A float representing the strength, clamped from `0` to `10`.
public record BobbingPitch(float bobbingStrength) implements PitchOverrider {
    public static final BobbingPitch DEFAULT = new BobbingPitch(0.001F);

    /// A **pitch overrider** for the panorama that defines the intensity of the bobbing.
    /// @param bobbingStrength A float representing the strength, clamped from `0` to `10`.
    public BobbingPitch(float bobbingStrength) {
        this.bobbingStrength = Mth.clamp(bobbingStrength, 0, 10);
    }

    @Override
    public ResourceLocation assetID() {
        return MellowUI.mellowUI("bobbing");
    }

    @Override
    public void toJSON(JsonObject object) {
        object.addProperty("bobbing_strength", this.bobbingStrength);
    }

    @Override
    public PitchOverrider fromJSON(JsonObject object) {
        return new BobbingPitch(object.get("bobbing_strength").getAsFloat());
    }
}
