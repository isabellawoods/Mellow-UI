package melonystudios.mellowui.config.option;

import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.screen.MusicToast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class MusicToastOption extends BooleanOption {
    public MusicToastOption(String translation, @Nullable Component tooltipComponent, Predicate<Options> getter, BiConsumer<Options, Boolean> setter) {
        super(translation, tooltipComponent, getter, setter);
    }

    @Override
    public void toggle(Options options) {
        super.toggle(options);
        if (this.get(options)) {
            Minecraft minecraft = Minecraft.getInstance();
            MusicManager manager = minecraft.getMusicManager();
            SoundInstance currentMusic = ((InterfaceMethods.MusicManagerMethods) manager).mui$getNowPlaying();
            if (currentMusic != null) MusicToast.addOrUpdate(currentMusic.getSound().getPath(), false, minecraft.getToasts());
        }
    }
}
