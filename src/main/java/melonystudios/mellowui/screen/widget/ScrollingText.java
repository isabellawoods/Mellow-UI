package melonystudios.mellowui.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.screen.RenderComponents;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public interface ScrollingText {
    default void renderScrollingString(PoseStack stack, Font font, Component text, int minX, int minY, int maxX, int maxY, int color) {
        this.renderScrollingString(stack, font, text, (minX + maxX) / 2, minX, minY, maxX, maxY, color);
    }

    default void renderScrollingString(PoseStack stack, Font font, Component text, int centerX, int minX, int minY, int maxX, int maxY, int color) {
        RenderComponents components = RenderComponents.INSTANCE;
        int textWidth = font.width(text);
        int textY = (minY + maxY - 9) / 2 + 1;
        int buttonWidth = maxX - minX;
        if (textWidth > buttonWidth) {
            int i2 = textWidth - buttonWidth;
            double time = (double) Util.getMillis() / 1000;
            double i3 = Math.max((double) i2 * 0.5, 3);
            double i4 = Math.sin(Math.PI / 2 * Math.cos(Math.PI * 2 * time / i3)) / 2 + 0.5;
            double xOffset = Mth.lerp(i4, 0, i2);
            components.enableScissor(minX, minY, maxX, maxY);
            GuiComponent.drawString(stack, font, text, minX - (int) xOffset, textY, color);
            components.disableScissor();
        } else {
            int textX = Mth.clamp(centerX, minX + textWidth / 2, maxX - textWidth / 2);
            GuiComponent.drawCenteredString(stack, font, text, textX, textY, color);
        }
    }
}
