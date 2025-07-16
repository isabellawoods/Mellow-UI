package melonystudios.mellowui.screen.widget;

import melonystudios.mellowui.config.option.MUIOption;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nonnull;
import java.util.List;

public class MUIOptionButton extends Button implements TooltipAccessor {
    private final MUIOption option;

    public MUIOptionButton(int x, int y, int width, int height, MUIOption option, Component text, OnPress onPress) {
        super(x, y, width, height, text, onPress);
        this.option = option;
    }

    public MUIOptionButton(int x, int y, int width, int height, MUIOption option, Component text, OnPress onPress, OnTooltip tooltip) {
        super(x, y, width, height, text, onPress, tooltip);
        this.option = option;
    }

    public MUIOption getOption() {
        return this.option;
    }

    @Override
    @Nonnull
    public List<FormattedCharSequence> getTooltip() {
        return this.option.getTooltip();
    }
}
