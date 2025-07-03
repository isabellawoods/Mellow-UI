package melonystudios.mellowui.mixin.client;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.screen.MusicToast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.util.SoundCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(MusicTicker.class)
public class MUIMusicTickerMixin implements InterfaceMethods.MusicManagerMethods {
    @Shadow @Nullable private ISound currentMusic;
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "startPlaying", at = @At("TAIL"))
    public void addMusicToast(BackgroundMusicSelector music, CallbackInfo callback) {
        if (this.minecraft.getOverlay() != null || this.currentMusic == null) return;
        if (this.canShowToast()) MusicToast.addOrUpdate(this.currentMusic.getSound().getPath(), false, this.minecraft.getToasts());
    }

    @Unique
    @Nullable
    public ISound mui$getNowPlaying() {
        return this.currentMusic;
    }

    @Unique
    private boolean canShowToast() {
        return this.currentMusic != null && this.currentMusic.getSound() != null && this.currentMusic.getSound() != SoundHandler.EMPTY_SOUND && MellowConfigs.CLIENT_CONFIGS.showMusicToast.get() && this.musicTurnedOn();
    }

    @Unique
    private boolean musicTurnedOn() {
        return this.minecraft.options.getSoundSourceVolume(SoundCategory.MASTER) > 0 && this.minecraft.options.getSoundSourceVolume(SoundCategory.MUSIC) > 0;
    }
}
