package melonystudios.mellowui.screen.widget;

import com.google.common.collect.Lists;
import melonystudios.mellowui.screen.RenderComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TooltippedButton extends Button implements TooltipAccessor {
    @Nullable
    private final Component tooltipText;

    public TooltippedButton(int x, int y, int width, int height, Component text, @Nullable Component tooltipText, OnPress onPress) {
        super(x, y, width, height, text, onPress);
        this.tooltipText = tooltipText;
    }

    @Nonnull
    public List<FormattedCharSequence> getTooltip() {
       if (this.tooltipText != null) return Minecraft.getInstance().font.split(this.tooltipText, RenderComponents.TOOLTIP_MAX_WIDTH);
       return Lists.newArrayList();
    }
}
