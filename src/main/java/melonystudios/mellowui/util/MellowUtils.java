package melonystudios.mellowui.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.util.ResourceLocation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MellowUtils {
    public static final RenderSkyboxCube CUBE_MAP = new RenderSkyboxCube(new ResourceLocation("textures/gui/title/background/panorama"));
    public static final RenderSkybox PANORAMA = new RenderSkybox(CUBE_MAP);
    public static final DateFormat WORLD_DATE_FORMAT = new SimpleDateFormat(); // "dd-MM-yyyy '('EEE') - 'HH:mm:ss"

    public static void scissor(Runnable toCut, int startX, int startY, int endX, int endY) {
        MainWindow window = Minecraft.getInstance().getWindow();
        int windowHeight = window.getHeight();
        int guiScale = (int) Minecraft.getInstance().getWindow().getGuiScale();
        double e = (double) startX * guiScale;
        double f = (double) windowHeight - (double) (startY + endY) * guiScale;
        double g = (double) (endX - startX) * guiScale;
        double h = (double) (endY - startY) * guiScale;
        //RenderSystem.enableScissor((int) e, (int) f, Math.max(0, (int) g), Math.max(0, (int) h));
        RenderSystem.enableScissor(startX * guiScale, startY * guiScale, endX * guiScale, endY * guiScale);
        toCut.run();
        RenderSystem.disableScissor();
    }

    /*
    private void updatePackList(ResourcePackList packList) {
        GameSettings options = this.minecraft.options;
        List<String> loadedPacks = ImmutableList.copyOf(options.resourcePacks);
        options.resourcePacks.clear();
        options.incompatibleResourcePacks.clear();

        for (ResourcePackInfo packInfo : packList.getSelectedPacks()) {
            if (!packInfo.isFixedPosition()) {
                options.resourcePacks.add(packInfo.getId());
                if (!packInfo.getCompatibility().isCompatible()) {
                    options.incompatibleResourcePacks.add(packInfo.getId());
                }
            }
        }

        options.save();
        List<String> newPacks = ImmutableList.copyOf(options.resourcePacks);
        if (!newPacks.equals(loadedPacks)) this.minecraft.reloadResourcePacks();
    }
     */
    //new MUIPackScreen(this, this.minecraft.getResourcePackRepository(), this::updatePackList, this.minecraft.getResourcePackDirectory(), new TranslationTextComponent("resourcePack.title"))
}
