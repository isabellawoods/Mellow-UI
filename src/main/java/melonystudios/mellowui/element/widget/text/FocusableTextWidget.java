package melonystudios.mellowui.element.widget.text;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.element.RenderComponents;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class FocusableTextWidget extends MultiLineTextWidget {
    private final boolean alwaysShowBorder;
    private final int padding;

    public FocusableTextWidget(int maxWidth, Component message, Font font) {
        this(maxWidth, message, font, 5);
    }

    public FocusableTextWidget(int maxWidth, Component message, Font font, int padding) {
        this(maxWidth, message, font, true, padding);
    }

    public FocusableTextWidget(int maxWidth, Component message, Font font, boolean alwaysShowBorder, int padding) {
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
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        if (this.isFocused() || this.alwaysShowBorder) {
            WidgetConfigs configs = WidgetConfigs.WIDGET_CONFIGS;
            int x = this.x - this.padding;
            int y = this.y - this.padding;
            int width = this.getWidth() + this.padding * 2;
            int height = this.getHeight() + this.padding * 2;
            int color = this.alwaysShowBorder ? (this.isFocused() ? configs.textFieldHighlightedBorderColor.get() : configs.textFieldDefaultBorderColor.get()) : configs.textFieldHighlightedBorderColor.get();
            fill(stack, x + 1, y, x + width, y + height, configs.textFieldCenterColor.get() | Mth.ceil(this.alpha * 255F) << 24);
            RenderComponents.INSTANCE.renderOutline(x, y, width, height, color | Mth.ceil(this.alpha * 255F) << 24);
        }

        super.renderButton(stack, mouseX, mouseY, partialTick);
    }
}
