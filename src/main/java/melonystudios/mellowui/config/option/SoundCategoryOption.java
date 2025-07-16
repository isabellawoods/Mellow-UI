package melonystudios.mellowui.config.option;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.SoundSlider;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;

public class SoundCategoryOption extends SliderPercentageOption {
    private final SoundCategory source;

    public SoundCategoryOption(String translation, SoundCategory source) {
        super(translation, 0, 1, 0.01F, options -> (double) options.getSoundSourceVolume(source), (options, newValue) -> {}, ((options, slider) -> StringTextComponent.EMPTY));
        this.source = source;
    }

    @Override
    @Nonnull
    public Widget createButton(GameSettings options, int x, int y, int width) {
        return new SoundSlider(Minecraft.getInstance(), x, y, this.source, width);
    }
}
