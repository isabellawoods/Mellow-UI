package melonystudios.mellowui.mixin.sound;

import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraft.client.audio.SoundEngine;
import net.minecraft.client.audio.SoundHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SoundHandler.class)
public class MUISoundHandlerMixin implements InterfaceMethods.SoundEngineMethods {
    @Shadow
    @Final
    private SoundEngine soundEngine;

    @Override
    public void reloadSoundEngine() {
        this.soundEngine.reload();
    }
}
