package melonystudios.mellowui.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class SplashRenderer {
    public static void defaultSplash(MatrixStack stack, FontRenderer font, String splash, int screenWidth, int textAlpha) {
        render(stack, font, splash, screenWidth / 2 + 90, 70, MellowUtils.getSplashTextColor(WidgetConfigs.WIDGET_CONFIGS.splashTextColor.get()), textAlpha);
    }

    public static void updatedSplash(MatrixStack stack, FontRenderer font, String splash, int screenWidth, int textAlpha) {
        render(stack, font, splash, screenWidth / 2 + 123, 69, MellowUtils.getSplashTextColor(WidgetConfigs.WIDGET_CONFIGS.splashTextColor.get()), textAlpha);
    }

    public static void mellomedleySplash(MatrixStack stack, FontRenderer font, String splash, int textAlpha) {
        render(stack, font, splash, 173, 80, MellowUtils.getSplashTextColor(WidgetConfigs.WIDGET_CONFIGS.mellomedleySplashTextColor.get()), textAlpha);
    }

    public static void render(MatrixStack stack, FontRenderer font, String splash, int x, int y, int color, int textAlpha) {
        stack.pushPose();
        stack.translate((float) x, y, 0);
        stack.mulPose(Vector3f.ZP.rotationDegrees(-20));
        float f2 = 1.8F - MathHelper.abs(MathHelper.sin((float) (Util.getMillis() % 1000L) / 1000 * ((float) Math.PI * 2F)) * 0.1F);
        f2 = f2 * 100 / (float) (font.width(splash) + 32);
        stack.scale(f2, f2, f2);
        AbstractGui.drawCenteredString(stack, font, splash, 0, -8, color | textAlpha);
        stack.popPose();
    }
}
