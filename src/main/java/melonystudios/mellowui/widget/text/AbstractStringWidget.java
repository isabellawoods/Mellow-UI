package melonystudios.mellowui.widget.text;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractStringWidget extends AbstractWidget {
    private final Font font;
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
}
