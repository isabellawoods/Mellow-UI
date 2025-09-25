package melonystudios.mellowui.mixin.client;

import melonystudios.mellowui.config.MellowConfigs;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleSound.class)
public class MUISimpleSoundMixin {
    @Inject(method = "forUI(Lnet/minecraft/util/SoundEvent;FF)Lnet/minecraft/client/audio/SimpleSound;", at = @At("HEAD"), cancellable = true)
    private static void forUI(SoundEvent sound, float pitch, float volume, CallbackInfoReturnable<SimpleSound> callback) {
        float newVolume = (float) (volume * MellowConfigs.CLIENT_CONFIGS.uiVolume.get());
        callback.setReturnValue(new SimpleSound(sound.getLocation(), SoundCategory.MASTER, newVolume, pitch, false, 0, ISound.AttenuationType.NONE, 0, 0, 0, true));
    }
}
