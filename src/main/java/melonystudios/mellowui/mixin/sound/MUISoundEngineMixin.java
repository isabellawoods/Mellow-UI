package melonystudios.mellowui.mixin.sound;

import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraft.client.audio.SoundEngine;
import net.minecraft.client.audio.SoundSystem;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicReference;

import static melonystudios.mellowui.methods.InterfaceMethods.DeviceCheckState.*;

@Mixin(SoundEngine.class)
public abstract class MUISoundEngineMixin implements InterfaceMethods.SoundEngineMethods {
    @Unique
    private final AtomicReference<InterfaceMethods.DeviceCheckState> devicePoolState = new AtomicReference<>(NO_CHANGE);
    @Unique
    private long lastDeviceCheckTime;
    @Shadow
    @Final
    private SoundSystem library;
    @Shadow
    public abstract void reload();

    @Override
    public void reloadSoundEngine() {
        this.reload();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(boolean gamePaused, CallbackInfo callback) {
        if (this.shouldChangeDevice()) this.reloadSoundEngine();
    }

    @Unique
    private boolean shouldChangeDevice() {
        if (((InterfaceMethods.SoundSystemMethods) this.library).isCurrentDeviceDisconnected()) {
            MellowUI.LOGGER.info("Audio device was lost!");
            return true;
        } else {
            long millis = Util.getMillis();
            boolean flag = millis - this.lastDeviceCheckTime >= 1000L;
            if (flag) {
                this.lastDeviceCheckTime = millis;
                if (this.devicePoolState.compareAndSet(NO_CHANGE, ONGOING)) {
                    String soundDevice = MellowConfigs.CLIENT_CONFIGS.soundDevice.get();
                    Util.ioPool().execute(() -> {
                        if ("".equals(soundDevice)) {
                            if (((InterfaceMethods.SoundSystemMethods) this.library).hasDefaultDeviceChanged()) {
                                MellowUI.LOGGER.info("System default audio device has changed!");
                                this.devicePoolState.compareAndSet(ONGOING, CHANGE_DETECTED);
                            }
                        } else if (!((InterfaceMethods.SoundSystemMethods) this.library).getCurrentDeviceName().equals(soundDevice) && this.getAvailableSoundDevices().contains(soundDevice)) {
                            MellowUI.LOGGER.info("Preferred audio device has become available!");
                            this.devicePoolState.compareAndSet(ONGOING, CHANGE_DETECTED);
                        }

                        this.devicePoolState.compareAndSet(ONGOING, NO_CHANGE);
                    });
                }
            }

            return this.devicePoolState.compareAndSet(CHANGE_DETECTED, NO_CHANGE);
        }
    }
}
