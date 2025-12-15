package melonystudios.mellowui.element.text;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

/// Provides a tooltip to a screen, in order for the tooltip to render on top and not get culled by
/// {@linkplain melonystudios.mellowui.element.RenderComponents#enableScissor scissors}.
public interface TooltipProvider {
    /// Gets the {@linkplain TooltipDisplayData display data} for this provider.
    TooltipDisplayData tooltipData();

    /// Sets the display data for the provider.
    /// @param data The display data.
    void setTooltipData(TooltipDisplayData data);

    /// Helper method for rendering the tooltip to the screen.
    /// @param stack The default {@link PoseStack} used for rendering.
    /// @param screen The screen this tooltip is being rendered in.
    default void renderTooltip(PoseStack stack, Screen screen) {
        TooltipDisplayData data = this.tooltipData();
        if (data != null) screen.renderTooltip(stack, data.tooltipLines(), data.x(), data.y());
        this.setTooltipData(null);
    }

    /// Converts a list of {@linkplain net.minecraft.network.chat.Component text components} into an immutable list of
    /// {@linkplain FormattedCharSequence formatted char sequences}.
    /// @param lines A list of {@linkplain FormattedText formatted text} to convert.
    static List<FormattedCharSequence> getVisualOrder(List<? extends FormattedText> lines) {
        return lines.stream().map(Language.getInstance()::getVisualOrder).collect(ImmutableList.toImmutableList());
    }
}
