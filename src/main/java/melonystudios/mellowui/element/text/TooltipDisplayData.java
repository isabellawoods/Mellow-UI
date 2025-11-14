package melonystudios.mellowui.element.text;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IBidiTooltip;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/// Represents the data of a tooltip that's going to be rendered.
@OnlyIn(Dist.CLIENT)
public class TooltipDisplayData implements IBidiTooltip {
    private final List<IReorderingProcessor> tooltipLines;
    private final int x;
    private final int y;

    /// Represents the data of a tooltip that's going to be rendered.
    /// @param tooltipLines A list of {@linkplain IReorderingProcessor reordering processors}, representing each line of the tooltip.
    /// @param x The x-position of the tooltip.
    /// @param y The y-position of the tooltip.
    public TooltipDisplayData(List<IReorderingProcessor> tooltipLines, int x, int y) {
        this.tooltipLines = tooltipLines;
        this.x = x;
        this.y = y;
    }

    /// Represents the data of a tooltip that's going to be rendered.
    /// @param component A {@linkplain ITextComponent text component} representing the text in the tooltip.
    /// @param maxWidth The maximum width of this tooltip.
    /// @param x The x-position of the tooltip.
    /// @param y The y-position of the tooltip.
    public TooltipDisplayData(ITextComponent component, int maxWidth, int x, int y) {
        this(Minecraft.getInstance().font.split(component, maxWidth), x, y);
    }

    /// @return A list of {@linkplain IReorderingProcessor reordering processors}, representing each line of the tooltip.
    public List<IReorderingProcessor> tooltipLines() {
        return this.tooltipLines;
    }

    /// @return The x-position of the tooltip.
    public int x() {
        return this.x;
    }

    /// @return The y-position of the tooltip.
    public int y() {
        return this.y;
    }

    @Override
    @Nonnull
    public Optional<List<IReorderingProcessor>> getTooltip() {
        return Optional.of(this.tooltipLines());
    }
}
