package melonystudios.mellowui.mixin.client;

import melonystudios.mellowui.config.MellowConfigs;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleSoundInstance.class)
public class MUISimpleSoundInstanceMixin {
    @Inject(method = "forUI(Lnet/minecraft/sounds/SoundEvent;FF)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;", at = @At("HEAD"), cancellable = true)
    private static void forUI(SoundEvent sound, float pitch, float volume, CallbackInfoReturnable<SimpleSoundInstance> callback) {
        float newVolume = (float) (volume * MellowConfigs.CLIENT_CONFIGS.uiVolume.get());
        callback.setReturnValue(new SimpleSoundInstance(sound.getLocation(), SoundSource.MASTER, newVolume, pitch, false, 0, SoundInstance.Attenuation.NONE, 0, 0, 0, true));
    }
}
