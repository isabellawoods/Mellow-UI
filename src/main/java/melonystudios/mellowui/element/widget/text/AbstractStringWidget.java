package melonystudios.mellowui.element.widget.text;

import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.ScrollingText;
import melonystudios.mellowui.element.text.TooltipDisplayData;
import melonystudios.mellowui.element.text.TooltipProvider;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractStringWidget extends AbstractWidget implements TooltipProvider, ScrollingText {
    protected final RenderComponents components = RenderComponents.INSTANCE;
    private final Font font;
    private TooltipDisplayData tooltipData;
    private int color = 0xFFFFFF;

    public AbstractStringWidget(int x, int y, int width, int height, Component text, Font font) {
        super(x, y, width, height, text);
        this.font = font;
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {}

    public Font getFont() {
        return this.font;
    }

    public int getColor() {
        return this.color;
    }

    public AbstractStringWidget setColor(int color) {
        this.color = color;
        return this;
    }

    @Override
    public TooltipDisplayData tooltipData() {
        return this.tooltipData;
    }

    @Override
    public void setTooltipData(TooltipDisplayData data) {
        this.tooltipData = data;
    }

    @Override
    public void setMessage(Component text) {
        super.setMessage(text);
        this.setWidth(this.getFont().width(text.getVisualOrderText()));
    }
}
