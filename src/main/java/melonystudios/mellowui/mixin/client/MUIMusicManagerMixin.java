package melonystudios.mellowui.mixin.client;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.screen.MusicToast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.util.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(MusicTicker.class)
public class MUIMusicManagerMixin implements InterfaceMethods.MusicManagerMethods {
    @Shadow @Nullable private ISound currentMusic;

    @Inject(method = "startPlaying", at = @At("TAIL"))
    public void addMusicToast(BackgroundMusicSelector music, CallbackInfo callback) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || this.currentMusic == null) return;
        if (this.canShowToast(minecraft)) MusicToast.addOrUpdate(this.currentMusic.getSound().getPath(), false, minecraft.getToasts());
    }

    @Unique
    @Nullable
    public ISound mui$getNowPlaying() {
        return this.currentMusic;
    }

    @Unique
    private boolean canShowToast(Minecraft minecraft) {
        return minecraft != null && this.currentMusic != null && this.currentMusic.getSound() != null && this.currentMusic.getSound() != SoundHandler.EMPTY_SOUND && MellowConfigs.CLIENT_CONFIGS.showMusicToast.get() && this.musicTurnedOn(minecraft);
    }

    @Unique
    private boolean musicTurnedOn(Minecraft minecraft) {
        return minecraft.options.getSoundSourceVolume(SoundCategory.MASTER) > 0 && minecraft.options.getSoundSourceVolume(SoundCategory.MUSIC) > 0;
    }
}
