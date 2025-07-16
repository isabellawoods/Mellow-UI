package melonystudios.mellowui.screen.widget;

import com.google.common.collect.Lists;
import melonystudios.mellowui.config.option.MUIOption;
import melonystudios.mellowui.screen.RenderComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TooltippedTextField extends EditBox implements TooltipAccessor {
    @Nullable
    private MUIOption option = null;
    @Nullable
    private Component tooltipComponent = null;

    public TooltippedTextField(Font font, int x, int y, int width, int height, Component text, @Nullable MUIOption option) {
        super(font, x, y, width, height, text);
        this.option = option;
    }

    public TooltippedTextField(Font font, int x, int y, int width, int height, Component text, @Nullable Component tooltipComponent) {
        super(font, x, y, width, height, text);
        this.tooltipComponent = tooltipComponent;
    }

    @Override
    @Nonnull
    public List<FormattedCharSequence> getTooltip() {
        if (this.option != null && !this.option.getTooltip().isEmpty()) {
            return this.option.getTooltip();
        } else if (this.tooltipComponent != null && this.tooltipComponent != TextComponent.EMPTY) {
            return Minecraft.getInstance().font.split(this.tooltipComponent, RenderComponents.TOOLTIP_MAX_WIDTH);
        }
        return Lists.newArrayList();
    }

    public void setTooltip(@Nullable Component tooltipComponent) {
        this.tooltipComponent = tooltipComponent;
    }
}
