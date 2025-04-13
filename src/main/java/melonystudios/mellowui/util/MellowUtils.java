package melonystudios.mellowui.util;

import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.updated.MUIModListScreen;
import melonystudios.mellowui.screen.updated.MUIPackScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.PackScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MellowUtils {
    public static final RenderSkyboxCube CUBE_MAP = new RenderSkyboxCube(new ResourceLocation("textures/gui/title/background/panorama"));
    public static final RenderSkybox PANORAMA = new RenderSkybox(CUBE_MAP);
    public static final DateFormat WORLD_DATE_FORMAT = new SimpleDateFormat(); // "dd-MM-yyyy '('EEE') - 'HH:mm:ss"
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
}
