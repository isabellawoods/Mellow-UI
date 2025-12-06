package melonystudios.mellowui.config.option;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.element.toast.MusicToast;
import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.util.text.ITextComponent;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class MusicToastOption extends TooltippedIterableOption {
    public MusicToastOption(String translation, ITextComponent tooltipComponent, BiConsumer<GameSettings, Integer> setter, BiFunction<GameSettings, IteratableOption, ITextComponent> optionTooltip) {
        super(translation, tooltipComponent, setter, optionTooltip);
    }

    @Override
    public void toggle(GameSettings options, int identifier) {
        super.toggle(options, identifier);
        if (MellowConfigs.CLIENT_CONFIGS.musicToast.get() == ThreeStyles.OPTION_3) {
            Minecraft minecraft = Minecraft.getInstance();
            MusicTicker manager = minecraft.getMusicManager();
            ISound currentMusic = ((InterfaceMethods.MusicManagerMethods) manager).mui$getNowPlaying();
            if (currentMusic != null) MusicToast.addOrUpdate(currentMusic.getSound().getPath(), false, minecraft.getToasts());
        }
    }
}
