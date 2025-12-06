package melonystudios.mellowui.element.widget.text;

import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.ScrollingText;
import melonystudios.mellowui.element.text.TooltipDisplayData;
import melonystudios.mellowui.element.text.TooltipProvider;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractStringWidget extends Widget implements TooltipProvider, ScrollingText {
    protected final RenderComponents components = RenderComponents.INSTANCE;
    private final FontRenderer font;
    private TooltipDisplayData tooltipData;
    private int color = 0xFFFFFF;

    public AbstractStringWidget(int x, int y, int width, int height, ITextComponent text, FontRenderer font) {
        super(x, y, width, height, text);
        this.font = font;
    }

    public FontRenderer getFont() {
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
    public void setMessage(ITextComponent text) {
        super.setMessage(text);
        this.setWidth(this.getFont().width(text.getVisualOrderText()));
    }
}
