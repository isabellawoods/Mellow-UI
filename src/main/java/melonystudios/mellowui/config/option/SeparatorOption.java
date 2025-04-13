package melonystudios.mellowui.config.option;

import melonystudios.mellowui.screen.widget.SeparatorWidget;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.Widget;

import javax.annotation.Nonnull;

public class SeparatorOption extends AbstractOption {
    public SeparatorOption(String translation) {
        super(translation);
    }

    @Nonnull
    public Widget createButton(GameSettings options, int x, int y, int width) {
        return new SeparatorWidget(x, y, width, 20, this.getCaption());
    }
}
