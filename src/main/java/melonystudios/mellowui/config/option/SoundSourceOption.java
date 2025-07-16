package melonystudios.mellowui.config.option;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.VolumeSlider;
import net.minecraft.sounds.SoundSource;

import javax.annotation.Nonnull;

public class SoundSourceOption extends MUIOption {
    private final SoundSource source;

    public SoundSourceOption(String translation, SoundSource source) {
        super(translation);
        this.source = source;
    }

    @Override
    @Nonnull
    public AbstractWidget createButton(Options options, int x, int y, int width) {
        return new VolumeSlider(Minecraft.getInstance(), x, y, this.source, width);
    }
}
