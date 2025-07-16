package melonystudios.mellowui.config.option;

import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.screen.MusicToast;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class MusicToastOption extends BooleanOption {
    public MusicToastOption(String translation, @Nullable ITextComponent tooltipComponent, Predicate<GameSettings> getter, BiConsumer<GameSettings, Boolean> setter) {
        super(translation, tooltipComponent, getter, setter);
    }

    @Override
    public void toggle(GameSettings options) {
        super.toggle(options);
        if (this.get(options)) {
            Minecraft minecraft = Minecraft.getInstance();
            MusicTicker manager = minecraft.getMusicManager();
            ISound currentMusic = ((InterfaceMethods.MusicManagerMethods) manager).mui$getNowPlaying();
            if (currentMusic != null) MusicToast.addOrUpdate(currentMusic.getSound().getPath(), false, minecraft.getToasts());
        }
    }
}
