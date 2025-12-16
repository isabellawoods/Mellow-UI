package melonystudios.mellowui.element.widget.text;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StringWidget extends AbstractStringWidget {
    private int maxWidth = 0;
    private int cachedWidth = 0;
    private boolean cachedWidthDirty = true;
    private StringWidget.TextOverflow textOverflow = StringWidget.TextOverflow.CLAMPED;

    public StringWidget(Component message, Font font) {
        this(0, 0, font.width(message.getVisualOrderText()), 9, message, font);
    }

    public StringWidget(int width, int height, Component message, Font font) {
        this(0, 0, width, height, message, font);
    }

    public StringWidget(int x, int y, int width, int height, Component text, Font font) {
        super(x, y, width, height, text, font);
        this.active = false;
    }

    public StringWidget setColor(int color) {
        super.setColor(color);
        return this;
    }

    @Override
    public void setMessage(Component text) {
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
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        Component message = this.getMessage();
        Font font = this.getFont();
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
                    this.renderScrollingString(font, Mth.clamp(WidgetConfigs.WIDGET_CONFIGS.stringWidgetTextPadding.get(), 0, this.getWidth() / 2 - 1), this.getColor());
            }
        } else {
            this.components.drawString(message.getVisualOrderText(), true, this.x, textY, this.getColor());
        }

        if (this.isHovered(mouseX, mouseY)) this.renderTooltip(stack, Minecraft.getInstance().screen);
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + (this.maxWidth > 0 ? this.maxWidth : this.getWidth()) && mouseY < this.y + this.getHeight();
    }

    protected void renderScrollingString(Font font, int padding, int color) {
        int minX = this.x + padding;
        int maxX = this.x + this.getWidth() - padding;
        renderAlignedScrollingText(font, this.getMessage(), Alignment.RIGHT, minX, this.y, maxX, this.y + this.getHeight(), color);
    }

    private FormattedCharSequence clipText(Component message, int width) {
        Font font = this.getFont();
        FormattedText properties = font.substrByWidth(message, width - font.width(TextComponents.ELLIPSIS));
        return Language.getInstance().getVisualOrder(FormattedText.composite(properties, TextComponents.ELLIPSIS));
    }

    @OnlyIn(Dist.CLIENT)
    public enum TextOverflow {
        CLAMPED,
        SCROLLING;
    }
}
