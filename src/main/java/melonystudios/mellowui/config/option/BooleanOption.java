package melonystudios.mellowui.config.option;

import melonystudios.mellowui.screen.widget.MUIOptionButton;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class BooleanOption extends MUIOption implements TooltipAccessor {
    private final Predicate<Options> getter;
    private final BiConsumer<Options, Boolean> setter;

    public BooleanOption(String translation, Predicate<Options> getter, BiConsumer<Options, Boolean> setter) {
        this(translation, null, getter, setter);
    }

    public BooleanOption(String translation, @Nullable Component tooltipComponent, Predicate<Options> getter, BiConsumer<Options, Boolean> setter) {
        super(translation);
        this.tooltipComponent = tooltipComponent;
        this.getter = getter;
        this.setter = setter;
    }

    public void set(Options options, String value) {
        this.set(options, "true".equals(value));
    }

    public void toggle(Options options) {
        this.set(options, !this.get(options));
        options.save();
    }

    public void set(Options options, boolean value) {
        this.setter.accept(options, value);
    }

    public boolean get(Options options) {
        return this.getter.test(options);
    }

    @Override
    @Nonnull
    public AbstractWidget createButton(Options options, int x, int y, int width) {
        if (this.tooltipComponent != null) this.setTooltip(this.tooltipComponent);
        return new MUIOptionButton(x, y, width, 20, this, this.getMessage(options), button -> {
            this.toggle(options);
            button.setMessage(this.getMessage(options));
        });
    }

    public Component getMessage(Options options) {
        return CommonComponents.optionStatus(this.getCaption(), this.get(options));
    }
}
