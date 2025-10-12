package melonystudios.mellowui.mixin.sound;

import com.mojang.blaze3d.audio.Library;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.TwoStyles;
import net.minecraft.client.resources.language.I18n;
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
public abstract class MUILibraryMixin {
    @Shadow
    private long currentDevice;
    @Shadow
    public abstract String getCurrentDeviceName();

    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo callback) {
        ALCCapabilities capabilities = ALC.createCapabilities(this.currentDevice);
        if (checkForALCError(this.currentDevice, "Get Capabilities")) {
            throw new IllegalStateException(I18n.get("logger.mellowui.library.no_capabilities"));
        } else if (!capabilities.OpenALC11) {
            throw new IllegalStateException(I18n.get("logger.mellowui.library.unsupported"));
        } else {
            // Enabling HRTF audio
            String currentDeviceName = this.getCurrentDeviceName().replace("OpenAL Soft on ", "");
            String translation = "logger.mellowui.library.initialized" + (currentDeviceName.isEmpty() ? ".unknown" : "");
            MellowUI.logger("Library").info(I18n.get(translation, currentDeviceName));
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
                    MellowUI.logger("Library").warn(I18n.get("logger.mellowui.library.reset", ALC10.alcGetString(this.currentDevice, ALC10.alcGetError(this.currentDevice))));
                }
            }
        }
    }

    @Unique
    private static boolean checkForALCError(long deviceHandle, String operation) {
        int errorID = ALC10.alcGetError(deviceHandle);
        if (errorID != 0) {
            MellowUI.logger("Library").error("[{}-{}]: {}", operation, deviceHandle, getErrorMessage(errorID));
            return true;
        } else {
            return false;
        }
    }

    @Unique
    private static String getErrorMessage(int errorID) {
        return switch (errorID) {
            case 40961 -> I18n.get("logger.mellowui.library.error_device");
            case 40962 -> I18n.get("logger.mellowui.library.error_context");
            case 40963 -> I18n.get("logger.mellowui.library.error_enum");
            case 40964 -> I18n.get("logger.mellowui.library.error_value");
            case 40965 -> I18n.get("logger.mellowui.library.error_memory");
            default -> I18n.get("logger.mellowui.library.error_unknown");
        };
    }
}
