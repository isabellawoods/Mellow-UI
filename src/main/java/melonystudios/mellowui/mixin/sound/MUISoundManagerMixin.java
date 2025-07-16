package melonystudios.mellowui.mixin.sound;

import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SoundManager.class)
public class MUISoundManagerMixin implements InterfaceMethods.SoundEngineMethods {
    @Shadow
    @Final
    private SoundEngine soundEngine;

    @Override
    public void reloadSoundEngine() {
        this.soundEngine.reload();
    }
}
