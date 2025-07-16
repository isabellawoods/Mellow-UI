package melonystudios.mellowui.config.option;

import melonystudios.mellowui.screen.widget.MUIOptionButton;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class IterableOption extends MUIOption {
    private final BiConsumer<Options, Integer> setter;
    private final BiFunction<Options, IterableOption, Component> messageGetter;
    @Nullable
    private final Component tooltipComponent;

    public IterableOption(String translation, BiConsumer<Options, Integer> setter, BiFunction<Options, IterableOption, Component> messageGetter) {
        this(translation, null, setter, messageGetter);
    }

    public IterableOption(String translation, @Nullable Component tooltipComponent, BiConsumer<Options, Integer> setter, BiFunction<Options, IterableOption, Component> messageGetter) {
        super(translation);
        this.tooltipComponent = tooltipComponent;
        this.setter = setter;
        this.messageGetter = messageGetter;
    }

    public void toggle(Options options, int amount) {
        this.setter.accept(options, amount);
        options.save();
    }

    @Override
    @Nonnull
    public AbstractWidget createButton(Options options, int x, int y, int width) {
        if (this.tooltipComponent != null) this.setTooltip(this.tooltipComponent);
        return new MUIOptionButton(x, y, width, 20, this, this.getMessage(options), button -> {
            this.toggle(options, 1);
            button.setMessage(this.getMessage(options));
        });
    }

    public Component getMessage(Options options) {
        return this.messageGetter.apply(options, this);
    }
}
