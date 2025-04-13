package melonystudios.mellowui.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IBidiTooltip;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class TooltippedButton extends Button implements IBidiTooltip {
    @Nullable
    private final ITextComponent tooltipText;

    public TooltippedButton(int x, int y, int width, int height, ITextComponent text, @Nullable ITextComponent tooltipText, IPressable whenPressed) {
        super(x, y, width, height, text, whenPressed);
        this.tooltipText = tooltipText;
    }

    @Nonnull
    public Optional<List<IReorderingProcessor>> getTooltip() {
       if (this.tooltipText != null) return Optional.of(Minecraft.getInstance().font.split(this.tooltipText, 200));
       return Optional.empty();
    }
}
