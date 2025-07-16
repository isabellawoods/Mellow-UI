package melonystudios.mellowui.config.option;

import net.minecraft.client.GameSettings;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class StyleBooleanOption extends BooleanOption {
    public StyleBooleanOption(String translation, Predicate<GameSettings> getter, BiConsumer<GameSettings, Boolean> setter) {
        super(translation, getter, setter);
    }

    public StyleBooleanOption(String translation, @Nullable ITextComponent tooltipComponent, Predicate<GameSettings> getter, BiConsumer<GameSettings, Boolean> setter) {
        super(translation, tooltipComponent, getter, setter);
    }

    @Nonnull
    public ITextComponent getMessage(GameSettings options) {
        return styleStatus(this.getCaption(), this.get(options));
    }

    public static IFormattableTextComponent styleStatus(ITextComponent widgetText, boolean on) {
        return new TranslationTextComponent(on ? "config.mellowui.option_2_style" : "config.mellowui.option_1_style", widgetText);
    }
}
