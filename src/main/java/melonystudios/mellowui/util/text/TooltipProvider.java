package melonystudios.mellowui.util.text;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;

import java.util.List;

/// Provides a tooltip to a screen, in order for the tooltip to render on top and not get culled by
/// {@linkplain melonystudios.mellowui.screen.RenderComponents#enableScissor(int, int, int, int) scissors}.
public interface TooltipProvider {
    /// Gets the {@linkplain TooltipDisplayData display data} for this provider.
    TooltipDisplayData tooltipData();

    /// Sets the display data for the provider.
    /// @param data The display data.
    void setTooltipData(TooltipDisplayData data);

    /// Helper method for rendering the tooltip to the screen.
    /// @param stack The default {@link MatrixStack} used for rendering.
    /// @param screen The screen this tooltip is being rendered in.
    default void renderTooltip(MatrixStack stack, Screen screen) {
        TooltipDisplayData data = this.tooltipData();
        screen.renderTooltip(stack, data.tooltipLines(), data.x(), data.y());
        this.setTooltipData(null);
    }

    /// Converts a list of {@linkplain net.minecraft.util.text.ITextComponent text components} into an immutable list of
    /// {@linkplain IReorderingProcessor reordering processors}.
    /// @param lines A list of {@linkplain ITextProperties text properties} to convert.
    static List<IReorderingProcessor> getVisualOrder(List<? extends ITextProperties> lines) {
        return lines.stream().map(LanguageMap.getInstance()::getVisualOrder).collect(ImmutableList.toImmutableList());
    }
}
