package melonystudios.mellowui.screen.forge;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.client.ClientModLoader;
import net.minecraftforge.fml.loading.FMLConfig;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class MUIModUpdateScreen extends NotificationModUpdateScreen {
    private VersionChecker.Status checkerStatus = null;
    private boolean checkedForUpdates = false;
    private final Button modsButton;
    private final boolean renderOnCorner;

    public MUIModUpdateScreen(Button modsButton, boolean renderOnCorner) {
        super(modsButton);
        this.modsButton = modsButton;
        this.renderOnCorner = renderOnCorner;
    }

    public static NotificationModUpdateScreen create(@Nullable Screen screen, Button modsButton, boolean renderOnCorner) {
        MUIModUpdateScreen updateScreen = new MUIModUpdateScreen(modsButton, renderOnCorner);
        updateScreen.resize(screen.getMinecraft(), screen.width, screen.height);
        updateScreen.init();
        return updateScreen;
    }

    @Override
    public void init() {
        if (!this.checkedForUpdates) {
            if (this.modsButton != null) this.checkerStatus = ClientModLoader.checkForUpdates();
            this.checkedForUpdates = true;
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        if (this.checkerStatus == null || !this.checkerStatus.shouldDraw() || !FMLConfig.runVersionCheck()) return;
        if (this.minecraft == null) return;

        this.minecraft.getTextureManager().bind(GUITextures.VERSION_CHECKER_ICONS);
        GL11.glColor4f(1, 1, 1, 1);

        int x = this.modsButton.x;
        int y = this.modsButton.y;
        int width = this.modsButton.getWidth();
        int height = this.modsButton.getHeight();

        if (this.renderOnCorner) {
            blit(stack, x + this.modsButton.getWidth() - 5, y - 3, this.checkerStatus.getSheetOffset() * 8, (this.checkerStatus.isAnimated() && ((System.currentTimeMillis() / 800 & 1) == 1)) ? 8 : 0, 8, 8, 64, 16);
        } else {
            blit(stack, x + width - (height / 2 + 4), y + (height / 2 - 4), this.checkerStatus.getSheetOffset() * 8, (this.checkerStatus.isAnimated() && ((System.currentTimeMillis() / 800 & 1) == 1)) ? 8 : 0, 8, 8, 64, 16);
        }
    }
}
