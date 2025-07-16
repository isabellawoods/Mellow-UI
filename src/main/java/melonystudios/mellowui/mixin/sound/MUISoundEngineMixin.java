package melonystudios.mellowui.mixin.sound;

import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public abstract class MUISoundEngineMixin implements InterfaceMethods.SoundEngineMethods {
    @Shadow
    public abstract void reload();
    @Shadow
    protected abstract boolean shouldChangeDevice();

    @Override
    public void reloadSoundEngine() {
        this.reload();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(boolean gamePaused, CallbackInfo callback) {
        if (this.shouldChangeDevice()) this.reloadSoundEngine();
    }
}
