package melonystudios.mellowui.mixin.sound;

import com.mojang.blaze3d.audio.Library;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.TwoStyles;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.IntBuffer;

@Mixin(Library.class)
public class MUILibraryMixin {
    @Shadow
    private long currentDevice;

    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo callback) {
        ALCCapabilities capabilities = ALC.createCapabilities(this.currentDevice);
        if (checkForALCError(this.currentDevice, "Get capabilities")) {
            throw new IllegalStateException("Failed to get OpenAL capabilities");
        } else if (!capabilities.OpenALC11) {
            throw new IllegalStateException("OpenAL 1.1 not supported");
        } else {
            // Enabling HRTF audio
            this.setHRTF(capabilities.ALC_SOFT_HRTF && MellowConfigs.CLIENT_CONFIGS.directionalAudio.get() == TwoStyles.OPTION_2);
        }
    }

    @Unique
    private void setHRTF(boolean directionalAudio) {
        int i = ALC10.alcGetInteger(this.currentDevice, 6548);
        if (i > 0) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer buffer = stack.callocInt(10).put(6546).put(directionalAudio ? 1 : 0).put(6550).put(0).put(0).flip();
                if (!SOFTHRTF.alcResetDeviceSOFT(this.currentDevice, buffer)) {
                    MellowUI.LOGGER.warn("Failed to reset device: {}", ALC10.alcGetString(this.currentDevice, ALC10.alcGetError(this.currentDevice)));
                }
            }
        }
    }

    @Unique
    private static boolean checkForALCError(long deviceHandle, String operationState) {
        int errorID = ALC10.alcGetError(deviceHandle);
        if (errorID != 0) {
            MellowUI.LOGGER.error("{}{}: {}", operationState, deviceHandle, getErrorMessage(errorID));
            return true;
        } else {
            return false;
        }
    }

    @Unique
    private static String getErrorMessage(int errorID) {
        return switch (errorID) {
            case 40961 -> "Invalid device.";
            case 40962 -> "Invalid context.";
            case 40963 -> "Illegal enum.";
            case 40964 -> "Invalid value.";
            case 40965 -> "Unable to allocate memory.";
            default -> "An unrecognized error occurred.";
        };
    }
}
