package melonystudios.mellowui.mixin.sound;

import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.TwoStyles;
import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraft.client.audio.SoundSystem;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.OptionalLong;

@Mixin(SoundSystem.class)
public class MUISoundSystemMixin implements InterfaceMethods.SoundSystemMethods {
    @Shadow
    private long device;
    @Unique
    private boolean supportsDisconnections;
    @Unique
    @Nullable
    private String defaultDeviceName;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void constructor(CallbackInfo callback) {
        this.defaultDeviceName = this.getCurrentDeviceName();
    }

    @Inject(method = "init", at = @At("HEAD"))
    public void initHead(CallbackInfo callback) {
        this.supportsDisconnections = ALC10.alcIsExtensionPresent(this.device, "ALC_EXT_disconnect");
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void initTail(CallbackInfo callback) {
        ALCCapabilities capabilities = ALC.createCapabilities(this.device);
        if (checkForALCError(this.device, "Get capabilities")) {
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
        int i = ALC10.alcGetInteger(this.device, 6548);
        if (i > 0) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer buffer = (IntBuffer) stack.callocInt(10).put(6546).put(directionalAudio ? 1 : 0).put(6550).put(0).put(0).flip();
                if (!SOFTHRTF.alcResetDeviceSOFT(this.device, buffer)) {
                    MellowUI.LOGGER.warn("Failed to reset device: {}", ALC10.alcGetString(this.device, ALC10.alcGetError(this.device)));
                }
            }
        }
    }

    @Inject(method = "tryOpenDevice", at = @At("HEAD"), cancellable = true)
    private static void openDeviceOrFallback(CallbackInfoReturnable<Long> callback) {
        callback.cancel();
        OptionalLong deviceSpecifier = OptionalLong.empty();
        String device = MellowConfigs.CLIENT_CONFIGS.soundDevice.get().isEmpty() ? null : MellowConfigs.CLIENT_CONFIGS.soundDevice.get();

        if (device != null) deviceSpecifier = tryOpenDevice(device);
        if (!deviceSpecifier.isPresent()) deviceSpecifier = tryOpenDevice(getDefaultDeviceName());
        if (!deviceSpecifier.isPresent()) deviceSpecifier = tryOpenDevice(null);

        if (!deviceSpecifier.isPresent()) {
            throw new IllegalStateException("Failed to open OpenAL device");
        } else {
            callback.setReturnValue(deviceSpecifier.getAsLong());
        }
    }

    @Override
    public boolean isCurrentDeviceDisconnected() {
        return this.supportsDisconnections && ALC11.alcGetInteger(this.device, 787) == 0;
    }

    @Override
    public synchronized boolean hasDefaultDeviceChanged() {
        String defaultDeviceName = getDefaultDeviceName();
        if (Objects.equals(this.defaultDeviceName, defaultDeviceName)) {
            return false;
        } else {
            this.defaultDeviceName = defaultDeviceName;
            return true;
        }
    }

    @Override
    public String getCurrentDeviceName() {
        String deviceName = ALC10.alcGetString(this.device, 4115);

        if (deviceName == null) deviceName = ALC10.alcGetString(this.device, 4101);
        if (deviceName == null) deviceName = "Unknown";

        return deviceName;
    }

    @Unique
    private static OptionalLong tryOpenDevice(@Nullable String deviceSpecifier) {
        long deviceHandle = ALC10.alcOpenDevice(deviceSpecifier);
        return deviceHandle != 0L && !checkForALCError(deviceHandle, "Open device") ? OptionalLong.of(deviceHandle) : OptionalLong.empty();
    }

    @Unique
    @Nullable
    private static String getDefaultDeviceName() {
        if (!ALC10.alcIsExtensionPresent(0L, "ALC_ENUMERATE_ALL_EXT")) {
            return null;
        } else {
            ALUtil.getStringList(0L, 4115);
            return ALC10.alcGetString(0L, 4114);
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
        switch(errorID) {
            case 40961: return "Invalid device.";
            case 40962: return "Invalid context.";
            case 40963: return "Illegal enum.";
            case 40964: return "Invalid value.";
            case 40965: return "Unable to allocate memory.";
            default: return "An unrecognized error occurred.";
        }
    }
}
