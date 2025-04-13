package melonystudios.mellowui.screen.widget;

import net.minecraft.client.AbstractOption;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IBidiTooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class TooltippedTextFieldWidget extends TextFieldWidget implements IBidiTooltip {
    @Nullable
    private final AbstractOption option;

    public TooltippedTextFieldWidget(FontRenderer font, int x, int y, int width, int height, ITextComponent text, @Nullable AbstractOption option) {
        super(font, x, y, width, height, text);
        this.option = option;
    }

    @Override
    @Nonnull
    public Optional<List<IReorderingProcessor>> getTooltip() {
        if (this.option != null && this.option.getTooltip().isPresent()) return Optional.of(this.option.getTooltip().get());
        return Optional.empty();
    }
}
