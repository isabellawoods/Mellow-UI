package melonystudios.mellowui.config.option;

import melonystudios.mellowui.screen.widget.SeparatorWidget;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public class SeparatorOption extends MUIOption {
    private final Component captionComponent;

    public SeparatorOption(Component captionComponent) {
        super(captionComponent.getString());
        this.captionComponent = captionComponent;
    }

    @Override
    @Nonnull
    protected Component getCaption() {
        return this.captionComponent;
    }

    @Nonnull
    public AbstractWidget createButton(Options options, int x, int y, int width) {
        SeparatorWidget widget = new SeparatorWidget(x, y, width, 20, this.getCaption());
        if (this.captionComponent.getStyle().getColor() != null) widget.setFGColor(this.captionComponent.getStyle().getColor().getValue());
        return widget;
    }
}
