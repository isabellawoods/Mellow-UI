package melonystudios.mellowui.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.util.Mth;

public class SplashRenderer {
    public static void defaultSplash(PoseStack stack, Font font, String splash, int screenWidth, int textAlpha) {
        render(stack, font, splash, screenWidth / 2 + 90, 70, MellowUtils.getSplashTextColor(WidgetConfigs.WIDGET_CONFIGS.splashTextColor.get()), textAlpha);
    }

    public static void updatedSplash(PoseStack stack, Font font, String splash, int screenWidth, int textAlpha) {
        render(stack, font, splash, screenWidth / 2 + 123, 69, MellowUtils.getSplashTextColor(WidgetConfigs.WIDGET_CONFIGS.splashTextColor.get()), textAlpha);
    }

    public static void mellomedleySplash(PoseStack stack, Font font, String splash, int textAlpha) {
        render(stack, font, splash, 185, 80, MellowUtils.getSplashTextColor(WidgetConfigs.WIDGET_CONFIGS.mellomedleySplashTextColor.get()), textAlpha);
    }

    public static void render(PoseStack stack, Font font, String splash, int x, int y, int color, int textAlpha) {
        stack.pushPose();
        stack.translate((float) x, y, 0);
        stack.mulPose(Vector3f.ZP.rotationDegrees(-20));
        float f2 = 1.8F - Mth.abs(Mth.sin((float) (Util.getMillis() % 1000L) / 1000 * ((float) Math.PI * 2F)) * 0.1F);
        f2 = f2 * 100 / (float) (font.width(splash) + 32);
        stack.scale(f2, f2, f2);
        GuiComponent.drawCenteredString(stack, font, splash, 0, -8, color | textAlpha);
        stack.popPose();
    }
}
