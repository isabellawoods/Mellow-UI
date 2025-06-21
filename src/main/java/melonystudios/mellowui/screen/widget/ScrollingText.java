package melonystudios.mellowui.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public interface ScrollingText {
    default void renderScrollingString(MatrixStack stack, FontRenderer font, ITextComponent text, int minX, int minY, int maxX, int maxY, int color) {
        this.renderScrollingString(stack, font, text, (minX + maxX) / 2, minX, minY, maxX, maxY, color);
    }

    default void renderScrollingString(MatrixStack stack, FontRenderer font, ITextComponent text, int centerX, int minX, int minY, int maxX, int maxY, int color) {
        int textWidth = font.width(text);
        int textY = (minY + maxY - 9) / 2 + 1;
        int buttonWidth = maxX - minX;
        if (textWidth > buttonWidth) {
            int i2 = textWidth - buttonWidth;
            double time = (double) Util.getMillis() / 1000;
            double i3 = Math.max((double) i2 * 0.5, 3);
            double i4 = Math.sin(Math.PI / 2 * Math.cos(Math.PI * 2 * time / i3)) / 2 + 0.5;
            double xOffset = MathHelper.lerp(i4, 0, i2);
            MellowUtils.textScissor(() -> AbstractGui.drawString(stack, font, text, minX - (int) xOffset, textY, color), minX, maxX);
        } else {
            int textX = MathHelper.clamp(centerX, minX + textWidth / 2, maxX - textWidth / 2);
            AbstractGui.drawCenteredString(stack, font, text, textX, textY, color);
        }
    }
}
