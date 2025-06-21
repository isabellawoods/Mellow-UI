package melonystudios.mellowui.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class LogoRenderer {
    public static final ResourceLocation MINECRAFT_LOGO = MellowUI.gui("title/minecraft");
    public static final ResourceLocation MINCERAFT_LOGO = MellowUI.gui("title/minceraft");
    public static final ResourceLocation EDITION_SUBTITLE = MellowUI.gui("title/edition");
    public static final ResourceLocation OLD_MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
    public static final ResourceLocation OLD_EDITION_SUBTITLE = new ResourceLocation("textures/gui/title/edition.png");
    public static boolean SHOW_EASTER_EGG = new Random().nextFloat() < 1.0E-4;

    public static void rerollEasterEgg() {
        SHOW_EASTER_EGG = new Random().nextFloat() < 1.0E-4;
    }

    public static void renderUpdatedLogo(MatrixStack stack, int screenWidth, float transparency, boolean keepLogoThroughFade) {
        renderUpdatedLogo(stack, screenWidth, transparency, 30, keepLogoThroughFade);
    }

    public static void renderUpdatedLogo(MatrixStack stack, int screenWidth, float transparency, int height, boolean keepLogoThroughFade) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.color4f(1, 1, 1, keepLogoThroughFade ? 1 : transparency);

        // Logo
        int logoX = screenWidth / 2 - 128;
        minecraft.getTextureManager().bind(SHOW_EASTER_EGG ? MINCERAFT_LOGO : MINECRAFT_LOGO);
        AbstractGui.blit(stack, logoX, height, 0, 0, 256, 44, 256, 64);

        // Edition
        int editionX = screenWidth / 2 - 64;
        int editionY = height + 44 - 7;
        minecraft.getTextureManager().bind(EDITION_SUBTITLE);
        AbstractGui.blit(stack, editionX, editionY, 0, 0, 128, 14, 128, 16);
        RenderSystem.color4f(1, 1, 1, 1);
    }

    public static void renderOldLogo(MatrixStack stack, Screen screen, int screenWidth, float transparency, boolean keepLogoThroughFade) {
        renderOldLogo(stack, screen, screenWidth, transparency, 30, keepLogoThroughFade);
    }

    public static void renderOldLogo(MatrixStack stack, Screen screen, int screenWidth, float transparency, int height, boolean keepLogoThroughFade) {
        Minecraft minecraft = Minecraft.getInstance();

        // Logo
        int logoX = screenWidth / 2 - 137;
        minecraft.getTextureManager().bind(OLD_MINECRAFT_LOGO);
        RenderSystem.color4f(1, 1, 1, keepLogoThroughFade ? 1 : transparency);
        if (SHOW_EASTER_EGG) {
            screen.blitOutlineBlack(logoX, height, (i1, i2) -> {
                screen.blit(stack, i1, i2, 0, 0, 99, 44);
                screen.blit(stack, i1 + 99, i2, 129, 0, 27, 44);
                screen.blit(stack, i1 + 99 + 26, i2, 126, 0, 3, 44);
                screen.blit(stack, i1 + 99 + 26 + 3, i2, 99, 0, 26, 44);
                screen.blit(stack, i1 + 155, i2, 0, 45, 155, 44);
            });
        } else {
            screen.blitOutlineBlack(logoX, height, (i1, i2) -> {
                screen.blit(stack, i1, i2, 0, 0, 155, 44);
                screen.blit(stack, i1 + 155, i2, 0, 45, 155, 44);
            });
        }

        // Edition
        int editionY = height + 37;
        minecraft.getTextureManager().bind(OLD_EDITION_SUBTITLE);
        AbstractGui.blit(stack, logoX + 88, editionY, 0, 0, 98, 14, 128, 16);
        RenderSystem.color4f(1, 1, 1, 1);
    }

    public static void renderMellomedleyLogo(MatrixStack stack, int x, int y, int width, int height, float transparency, boolean keepLogoThroughFade) {
        RenderSystem.enableBlend();
        RenderSystem.color4f(1, 1, 1, keepLogoThroughFade ? 1 : transparency);
        Minecraft.getInstance().getTextureManager().bind(GUITextures.MELLOMEDLEY_LOGO);
        AbstractGui.blit(stack, x, y, 0, 0, width, height, width, height);
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.disableBlend();
    }
}
