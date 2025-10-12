package melonystudios.mellowui.util.text;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/// Represents the data of a tooltip that's going to be rendered.
/// @param tooltipLines A list of {@linkplain FormattedCharSequence formatted char sequences}, representing each line of the tooltip.
/// @param x The x-position of the tooltip.
/// @param y The y-position of the tooltip.
@OnlyIn(Dist.CLIENT)
public record TooltipDisplayData(List<FormattedCharSequence> tooltipLines, int x, int y) implements TooltipAccessor {
    /// Represents the data of a tooltip that's going to be rendered.
    /// @param component A {@linkplain Component text component} representing the text in the tooltip.
    /// @param maxWidth  The maximum width of this tooltip.
    /// @param x The x-position of the tooltip.
    /// @param y The y-position of the tooltip.
    public TooltipDisplayData(Component component, int maxWidth, int x, int y) {
        this(Minecraft.getInstance().font.split(component, maxWidth), x, y);
    }

    /// @return A list of {@linkplain FormattedCharSequence formatted char sequences}, representing each line of the tooltip.
    @Override
    public List<FormattedCharSequence> tooltipLines() {
        return this.tooltipLines;
    }

    /// @return The x-position of the tooltip.
    @Override
    public int x() {
        return this.x;
    }

    /// @return The y-position of the tooltip.
    @Override
    public int y() {
        return this.y;
    }

    @Override
    @NotNull
    public List<FormattedCharSequence> getTooltip() {
        return this.tooltipLines();
    }
}
