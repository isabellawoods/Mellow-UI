package melonystudios.mellowui.config.option;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.element.toast.MusicToast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class MusicToastOption extends IterableOption {
    public MusicToastOption(String translation, Component tooltipComponent, BiConsumer<Options, Integer> setter, BiFunction<Options, IterableOption, Component> optionTooltip) {
        super(translation, tooltipComponent, setter, optionTooltip);
    }

    @Override
    public void toggle(Options options, int identifier) {
        super.toggle(options, identifier);
        if (MellowConfigs.CLIENT_CONFIGS.musicToast.get() == ThreeStyles.OPTION_3) {
            Minecraft minecraft = Minecraft.getInstance();
            MusicManager manager = minecraft.getMusicManager();
            SoundInstance currentMusic = ((InterfaceMethods.MusicManagerMethods) manager).mui$getNowPlaying();
            if (currentMusic != null) MusicToast.addOrUpdate(currentMusic.getSound().getPath(), false, minecraft.getToasts());
        }
    }
}
