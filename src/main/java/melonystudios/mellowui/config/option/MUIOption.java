package melonystudios.mellowui.config.option;

import com.google.common.collect.Lists;
import melonystudios.mellowui.screen.RenderComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class MUIOption extends Option implements TooltipAccessor {
    @Nullable
    protected Component tooltipComponent;

    public MUIOption(String translation) {
        this(translation, null);
    }

    public MUIOption(String translation, @Nullable Component tooltipComponent) {
        super(translation);
        this.tooltipComponent = tooltipComponent;
    }

    @Override
    @Nonnull
    public List<FormattedCharSequence> getTooltip() {
        return this.tooltipComponent != null ? Minecraft.getInstance().font.split(this.tooltipComponent, RenderComponents.TOOLTIP_MAX_WIDTH) : Lists.newArrayList();
    }

    public void setTooltip(@Nullable Component tooltipComponent) {
        this.tooltipComponent = tooltipComponent;
    }
}
