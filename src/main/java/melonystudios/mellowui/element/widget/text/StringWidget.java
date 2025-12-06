package melonystudios.mellowui.element.widget.text;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StringWidget extends AbstractStringWidget {
    private int maxWidth = 0;
    private int cachedWidth = 0;
    private boolean cachedWidthDirty = true;
    private StringWidget.TextOverflow textOverflow = StringWidget.TextOverflow.CLAMPED;

    public StringWidget(ITextComponent message, FontRenderer font) {
        this(0, 0, font.width(message.getVisualOrderText()), 9, message, font);
    }

    public StringWidget(int width, int height, ITextComponent message, FontRenderer font) {
        this(0, 0, width, height, message, font);
    }

    public StringWidget(int x, int y, int width, int height, ITextComponent text, FontRenderer font) {
        super(x, y, width, height, text, font);
        this.active = false;
    }

    public StringWidget setColor(int color) {
        super.setColor(color);
        return this;
    }

    @Override
    public void setMessage(ITextComponent text) {
        super.setMessage(text);
        this.cachedWidthDirty = true;
    }

    public StringWidget setMaxWidth(int maxWidth) {
        return this.setMaxWidth(maxWidth, StringWidget.TextOverflow.CLAMPED);
    }

    public StringWidget setMaxWidth(int maxWidth, StringWidget.TextOverflow textOverflow) {
        this.maxWidth = maxWidth;
        this.textOverflow = textOverflow;
        return this;
    }

    @Override
    public int getWidth() {
        if (this.maxWidth > 0) {
            if (this.cachedWidthDirty) {
                this.cachedWidth = Math.min(this.maxWidth, this.getFont().width(this.getMessage().getVisualOrderText()));
                this.cachedWidthDirty = false;
            }

            return this.cachedWidth;
        } else {
            return super.getWidth();
        }
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        ITextComponent message = this.getMessage();
        FontRenderer font = this.getFont();
        int maxWidth = this.maxWidth > 0 ? this.maxWidth : this.getWidth();
        int messageWidth = font.width(message);
        int textY = this.y + (this.getHeight() - 9) / 2;
        boolean biggerThanMax = messageWidth > maxWidth;
        if (biggerThanMax) {
            switch (this.textOverflow) {
                case CLAMPED:
                    this.components.drawString(this.clipText(message, maxWidth), true, this.x, textY, this.getColor());
                    break;
                case SCROLLING:
                    this.renderScrollingString(font, WidgetConfigs.WIDGET_CONFIGS.stringWidgetTextPadding.get(), this.getColor());
            }
        } else {
            this.components.drawString(message.getVisualOrderText(), true, this.x, textY, this.getColor());
        }

        if (this.isHovered()) {
            this.renderTooltip(stack, Minecraft.getInstance().screen);
        }
    }

    protected void renderScrollingString(FontRenderer font, int padding, int color) {
        int minX = this.x + padding;
        int maxX = this.x + this.getWidth() - padding;
        renderAlignedScrollingText(font, this.getMessage(), Alignment.RIGHT, minX, this.y, maxX, this.y + this.getHeight(), color);
    }

    private IReorderingProcessor clipText(ITextComponent message, int width) {
        FontRenderer font = this.getFont();
        ITextProperties properties = font.substrByWidth(message, width - font.width(TextComponents.ELLIPSIS));
        return LanguageMap.getInstance().getVisualOrder(ITextProperties.composite(properties, TextComponents.ELLIPSIS));
    }

    @OnlyIn(Dist.CLIENT)
    public enum TextOverflow {
        CLAMPED,
        SCROLLING;
    }
}
