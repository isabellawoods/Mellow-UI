package melonystudios.mellowui.config.option;

import melonystudios.mellowui.screen.RenderComponents;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class TooltippedIterableOption extends IteratableOption {
    private final ITextComponent tooltipText;

    public TooltippedIterableOption(String translation, ITextComponent tooltipText, BiConsumer<GameSettings, Integer> setter, BiFunction<GameSettings, IteratableOption, ITextComponent> optionTooltip) {
        super(translation, setter, optionTooltip);
        this.tooltipText = tooltipText;
    }

    @Override
    @Nonnull
    public Widget createButton(GameSettings options, int x, int y, int width) {
        this.setTooltip(Minecraft.getInstance().font.split(this.tooltipText, RenderComponents.TOOLTIP_MAX_WIDTH));
        return new OptionButton(x, y, width, 20, this, this.getMessage(options), button -> {
            this.toggle(options, 1);
            button.setMessage(this.getMessage(options));
        });
    }
}
