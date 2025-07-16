package melonystudios.mellowui.config.option;

import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class StyleBooleanOption extends BooleanOption {
    public StyleBooleanOption(String translation, Predicate<Options> getter, BiConsumer<Options, Boolean> setter) {
        super(translation, getter, setter);
    }

    public StyleBooleanOption(String translation, @Nullable Component tooltipComponent, Predicate<Options> getter, BiConsumer<Options, Boolean> setter) {
        super(translation, tooltipComponent, getter, setter);
    }

    @Override
    @Nonnull
    public Component getMessage(Options options) {
        return styleStatus(this.getCaption(), this.get(options));
    }

    public static MutableComponent styleStatus(Component widgetText, boolean on) {
        return new TranslatableComponent(on ? "config.mellowui.option_2_style" : "config.mellowui.option_1_style", widgetText);
    }
}
