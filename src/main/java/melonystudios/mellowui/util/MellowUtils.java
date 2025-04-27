package melonystudios.mellowui.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.updated.MUIModListScreen;
import melonystudios.mellowui.screen.updated.MUIPackScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.PackScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static net.minecraft.util.ColorHelper.PackedColor.*;

public class MellowUtils {
    public static RenderSkybox PANORAMA = new RenderSkybox(MainMenuScreen.CUBE_MAP);
    public static final DateFormat WORLD_DATE_FORMAT = new SimpleDateFormat(); // "dd-MM-yyyy '('EEE') - 'HH:mm:ss"
    public static final int TOOLTIP_MAX_WIDTH = 200;
    public static final int DEFAULT_TITLE_HEIGHT = 12;
    public static final int PAUSE_MENU_Y_OFFSET = 0;

    public static Screen modList(Screen lastScreen) {
        if (MellowConfigs.CLIENT_CONFIGS.modListStyle.get()) return new MUIModListScreen(lastScreen);
        else return new ModListScreen(lastScreen);
    }

    public static Screen resourcePackList(Screen lastScreen, Minecraft minecraft) {
        ITextComponent title = new TranslationTextComponent("resourcePack.title");
        if (MellowConfigs.CLIENT_CONFIGS.packListStyle.get()) return new MUIPackScreen(lastScreen, minecraft.getResourcePackRepository(), packList -> {}, minecraft.getResourcePackDirectory(), title);
        else return new PackScreen(lastScreen, minecraft.getResourcePackRepository(), packList -> {}, minecraft.getResourcePackDirectory(), title);
    }

    public static void scissor(Runnable toCut, int startX, int endX, int y0, int y1, int height) {
        int guiScale = (int) Minecraft.getInstance().getWindow().getGuiScale();
        int startY = (int) ((double) (height - y1) * guiScale);
        int endY = (int) ((double) (height - (height - y1) - y0 - 2) * guiScale);
        RenderSystem.enableScissor(startX * guiScale, startY, endX * guiScale, endY);
        toCut.run();
        RenderSystem.disableScissor();
    }

    public static void renderTooltip(MatrixStack stack, Screen screen, Button button, ITextComponent tooltipText, int mouseX, int mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        int x = button.isFocused() ? button.x : mouseX;
        int y = button.isFocused() ? button.y : mouseY;
        screen.renderTooltip(stack, minecraft.font.split(tooltipText, TOOLTIP_MAX_WIDTH), x, y);
    }

    public static void buttonScissor(Runnable toCut, int startX, int endX) {
        int guiScale = (int) Minecraft.getInstance().getWindow().getGuiScale();
        RenderSystem.enableScissor(startX * guiScale, 0, (endX - startX) * guiScale, Minecraft.getInstance().getWindow().getHeight());
        toCut.run();
        RenderSystem.disableScissor();
    }

    public static int getSelectableTextColor(boolean selected, boolean active) {
        if (MellowConfigs.CLIENT_CONFIGS.legacyButtonColors.get()) {
            return !active ? 0xA0A0A0 : (selected ? 0xFFFFA0 : 0xE0E0E0);
        } else {
            return active ? 0xFFFFFF : 0xA0A0A0;
        }
    }

    public static int getSelectableTextShadowColor(boolean selected, boolean active) {
        int color;
        if (MellowConfigs.CLIENT_CONFIGS.legacyButtonColors.get()) {
            color = !active ? 0xA0A0A0 : (selected ? 0xFFFFA0 : 0xE0E0E0);
        } else {
            color = active ? 0xFFFFFF : 0xA0A0A0;
        }
        return getShadowColor(color, 1);
    }

    public static int getShadowColor(int color, float alpha) {
        float red = red(color) * 0.25F;
        float green = green(color) * 0.25F;
        float blue = blue(color) * 0.25F;
        return color((int) (alpha * 255), (int) red, (int) green, (int) blue);
    }
}
