package melonystudios.mellowui.resource.panorama;

import com.google.gson.JsonObject;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/// A **pitch overrider** for the panorama that keeps it at a constant pitch.
public class ConstantPitch implements PitchOverrider {
    public static final ConstantPitch DEFAULT = new ConstantPitch(MellowConfigs.CLIENT_CONFIGS.panoramaCameraPitch.get());
    private final int pitch;

    /// A **pitch overrider** for the panorama that keeps it at a constant pitch.
    /// @param pitch An integer representing the pitch, clamped from `-90` to `90`.
    public ConstantPitch(int pitch) {
        this.pitch = MathHelper.clamp(pitch, -90, 90);
    }

    public int pitch() {
        return this.pitch;
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
