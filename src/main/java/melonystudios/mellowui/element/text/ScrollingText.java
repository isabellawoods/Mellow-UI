package melonystudios.mellowui.element.text;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public interface ScrollingText {
    RenderComponents COMPONENTS = RenderComponents.INSTANCE;

    /// Renders a scrolling string if the {@linkplain MellowConfigs#scrollingText **Scrolling Text**} option is on, or the default centered string if not.
    /// @param ifScrollable What to render if the option is on.
    /// @param ifNot What to render if the option is off.
    default void renderWidgetText(Runnable ifScrollable, Runnable ifNot) {
        if (MellowConfigs.CLIENT_CONFIGS.scrollingText.get()) ifScrollable.run();
        else ifNot.run();
    }

    default void renderAlignedScrollingText(PoseStack stack, Font font, Component text, Alignment alignment, int minX, int minY, int maxX, int maxY, int color) {
        this.renderAlignedScrollingText(stack, font, text, alignment, (minX + maxX) / 2, minX, minY, maxX, maxY, color);
    }

    default void renderAlignedScrollingText(PoseStack stack, Font font, Component text, Alignment alignment, int centerX, int minX, int minY, int maxX, int maxY, int color) {
        int textWidth = font.width(text);
        int textY = (minY + maxY - 9) / 2 + 1;
        int buttonWidth = maxX - minX;
        if (textWidth > buttonWidth) {
            this.renderScrollingText(stack, font, text, minX, minY, maxX, maxY, color, textWidth, buttonWidth, textY);
        } else {
            switch (alignment) {
                case LEFT: {
                    GuiComponent.drawString(stack, font, text, minX, textY, color);
                    break;
                }
                case RIGHT: {
                    int textX = maxX - font.width(text);
                    GuiComponent.drawString(stack, font, text, textX, textY, color);
                    break;
                }
                case CENTER: default: {
                    int textX = Mth.clamp(centerX, minX + textWidth / 2, maxX - textWidth / 2);
                    GuiComponent.drawCenteredString(stack, font, text, textX, textY, color);
                    break;
                }
            }
        }
    }

    default void renderScrollingText(PoseStack stack, Font font, Component text, int minX, int minY, int maxX, int maxY, int color, int textWidth, int buttonWidth, int textY) {
        int i2 = textWidth - buttonWidth;
        double time = (double) Util.getMillis() / 1000;
        double i3 = Math.max((double) i2 * 0.5, 3);
        double i4 = Math.sin(Math.PI / 2 * Math.cos(Math.PI * 2 * time / i3)) / 2 + 0.5;
        double xOffset = Mth.lerp(i4, 0, i2);
        COMPONENTS.enableScissor(minX, minY, maxX, maxY);
        GuiComponent.drawString(stack, font, text, minX - (int) xOffset, textY, color);
        COMPONENTS.disableScissor();
    }
}
