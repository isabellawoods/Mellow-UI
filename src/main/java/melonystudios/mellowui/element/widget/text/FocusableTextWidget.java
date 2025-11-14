package melonystudios.mellowui.element.widget.text;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.element.RenderComponents;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class FocusableTextWidget extends MultiLineTextWidget {
    private final boolean alwaysShowBorder;
    private final int padding;

    public FocusableTextWidget(int maxWidth, ITextComponent message, FontRenderer font) {
        this(maxWidth, message, font, 5);
    }

    public FocusableTextWidget(int maxWidth, ITextComponent message, FontRenderer font, int padding) {
        this(maxWidth, message, font, true, padding);
    }

    public FocusableTextWidget(int maxWidth, ITextComponent message, FontRenderer font, boolean alwaysShowBorder, int padding) {
        super(message, font);
        this.setMaxWidth(maxWidth);
        this.setCentered(true);
        this.active = true;
        this.alwaysShowBorder = alwaysShowBorder;
        this.padding = padding;
    }

    public void containWithin(int width) {
        this.setMaxWidth(width - this.padding * 4);
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTick) {
        if (this.isFocused() || this.alwaysShowBorder) {
            WidgetConfigs configs = WidgetConfigs.WIDGET_CONFIGS;
            int x = this.x - this.padding;
            int y = this.y - this.padding;
            int width = this.getWidth() + this.padding * 2;
            int height = this.getHeight() + this.padding * 2;
            int color = this.alwaysShowBorder ? (this.isFocused() ? configs.textFieldHighlightedBorderColor.get() : configs.textFieldDefaultBorderColor.get()) : configs.textFieldHighlightedBorderColor.get();
            fill(stack, x + 1, y, x + width, y + height, configs.textFieldCenterColor.get() | MathHelper.ceil(this.alpha * 255F) << 24);
            RenderComponents.INSTANCE.renderOutline(x, y, width, height, color | MathHelper.ceil(this.alpha * 255F) << 24);
        }

        super.renderButton(stack, mouseX, mouseY, partialTick);
    }
}
