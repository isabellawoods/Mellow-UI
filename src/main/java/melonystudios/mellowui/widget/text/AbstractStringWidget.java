package melonystudios.mellowui.widget.text;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractStringWidget extends Widget {
    private final FontRenderer font;
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
}
