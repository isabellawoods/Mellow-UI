package melonystudios.mellowui.screen.widget;

import melonystudios.mellowui.screen.RenderComponents;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IBidiTooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class TooltippedTextField extends TextFieldWidget implements IBidiTooltip {
    @Nullable
    private AbstractOption option = null;
    @Nullable
    private ITextComponent tooltipText = null;

    public TooltippedTextField(FontRenderer font, int x, int y, int width, int height, ITextComponent text, @Nullable AbstractOption option) {
        super(font, x, y, width, height, text);
        this.option = option;
    }

    public TooltippedTextField(FontRenderer font, int x, int y, int width, int height, ITextComponent text, ITextComponent tooltipText) {
        super(font, x, y, width, height, text);
        this.tooltipText = tooltipText;
    }

    @Override
    @Nonnull
    public Optional<List<IReorderingProcessor>> getTooltip() {
        if (this.option != null && this.option.getTooltip().isPresent()) {
            return Optional.of(this.option.getTooltip().get());
        } else if (this.tooltipText != null && this.tooltipText != StringTextComponent.EMPTY) {
            return Optional.of(Minecraft.getInstance().font.split(this.tooltipText, RenderComponents.TOOLTIP_MAX_WIDTH));
        }
        return Optional.empty();
    }

    public void setTooltip(@Nullable ITextComponent tooltipText) {
        this.tooltipText = tooltipText;
    }
}
