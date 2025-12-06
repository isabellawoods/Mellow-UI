package melonystudios.mellowui.mixin.widget;

import melonystudios.mellowui.sound.MUISoundCategory;
import melonystudios.mellowui.sound.SoundPreviewHandler;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.GameSettingsSlider;
import net.minecraft.client.gui.widget.SoundSlider;
import net.minecraft.util.SoundCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSlider.class)
public abstract class MUISoundSliderMixin extends GameSettingsSlider {
    @Shadow
    @Final
    private SoundCategory source;

    public MUISoundSliderMixin(GameSettings options, int x, int y, int width, int height, double value) {
        super(options, x, y, width, height, value);
    }

    @Inject(method = "applyValue", at = @At("TAIL"))
    protected void playPreviewSound(CallbackInfo callback) {
        if (Minecraft.getInstance().level == null) {
            SoundPreviewHandler.preview(Minecraft.getInstance().getSoundManager(), MUISoundCategory.toMUI(this.source), (float) this.value);
        }
    }
}
