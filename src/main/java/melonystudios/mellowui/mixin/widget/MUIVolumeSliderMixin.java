package melonystudios.mellowui.mixin.widget;

import melonystudios.mellowui.sound.MUISoundSource;
import melonystudios.mellowui.sound.SoundPreviewHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractOptionSliderButton;
import net.minecraft.client.gui.components.VolumeSlider;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VolumeSlider.class)
public abstract class MUIVolumeSliderMixin extends AbstractOptionSliderButton {
    @Shadow
    @Final
    private SoundSource source;

    public MUIVolumeSliderMixin(Options options, int x, int y, int width, int height, double value) {
        super(options, x, y, width, height, value);
    }

    @Inject(method = "applyValue", at = @At("TAIL"))
    protected void playPreviewSound(CallbackInfo callback) {
        if (Minecraft.getInstance().level == null) {
            SoundPreviewHandler.preview(Minecraft.getInstance().getSoundManager(), MUISoundSource.toMUI(this.source), (float) this.value);
        }
    }
}
