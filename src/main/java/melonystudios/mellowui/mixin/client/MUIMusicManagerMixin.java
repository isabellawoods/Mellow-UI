package melonystudios.mellowui.mixin.client;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.element.toast.MusicToast;
import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(MusicManager.class)
public class MUIMusicManagerMixin implements InterfaceMethods.MusicManagerMethods {
    @Shadow
    @Nullable
    private SoundInstance currentMusic;

    @Inject(method = "startPlaying", at = @At("TAIL"))
    public void addMusicToast(Music music, CallbackInfo callback) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || this.currentMusic == null) return;
        if (this.canShowToast(minecraft)) MusicToast.addOrUpdate(this.currentMusic.getSound().getPath(), false, minecraft.getToasts());
    }

    @Unique
    @Nullable
    public SoundInstance mui$getNowPlaying() {
        return this.currentMusic;
    }

    @Unique
    private boolean canShowToast(Minecraft minecraft) {
        return minecraft != null && this.currentMusic != null && this.currentMusic.getSound() != null && this.currentMusic.getSound() != SoundManager.EMPTY_SOUND && MellowConfigs.CLIENT_CONFIGS.musicToast.get() == ThreeStyles.OPTION_3 && this.musicTurnedOn(minecraft);
    }

    @Unique
    private boolean musicTurnedOn(Minecraft minecraft) {
        return minecraft.options.getSoundSourceVolume(SoundSource.MASTER) > 0 && minecraft.options.getSoundSourceVolume(SoundSource.MUSIC) > 0;
    }
}
