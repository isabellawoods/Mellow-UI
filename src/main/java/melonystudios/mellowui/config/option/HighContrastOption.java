package melonystudios.mellowui.config.option;

import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class HighContrastOption extends BooleanOption {
    @Nullable
    private final ITextComponent tooltipComponent;

    public HighContrastOption(String translation, @Nullable ITextComponent tooltipComponent, Predicate<GameSettings> getter, BiConsumer<GameSettings, Boolean> setter) {
        super(translation, tooltipComponent, getter, setter);
        this.tooltipComponent = tooltipComponent;
    }

    @Override
    @Nonnull
    public Optional<List<IReorderingProcessor>> getTooltip() {
        if (this.tooltipComponent != null) {
            if (MellowUtils.highContrastUnavailable()) {
                return Optional.of(Minecraft.getInstance().font.split(new TranslationTextComponent("config.minecraft.high_contrast.not_available"), RenderComponents.TOOLTIP_MAX_WIDTH));
            } else {
                return Optional.of(Minecraft.getInstance().font.split(this.tooltipComponent, RenderComponents.TOOLTIP_MAX_WIDTH));
            }
        }
        return super.getTooltip();
    }

    @Override
    @Nonnull
    public Widget createButton(GameSettings options, int x, int y, int width) {
        return new OptionButton(x, y, width, 20, this, this.getMessage(options), button -> {
            this.toggle(options);
            button.setMessage(this.getMessage(options));
        });
    }
}
