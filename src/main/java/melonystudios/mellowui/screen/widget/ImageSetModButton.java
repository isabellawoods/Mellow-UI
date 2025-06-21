package melonystudios.mellowui.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.client.ClientModLoader;
import net.minecraftforge.fml.loading.FMLConfig;

public class ImageSetModButton extends ImageSetButton {
    private VersionChecker.Status checkerStatus = null;
    private boolean checkedForUpdates = false;
    private boolean renderOnCorner = false;

    public ImageSetModButton(int x, int y, int width, int height, WidgetTextureSet textureSet, IPressable whenPressed, ITooltip buttonTooltip, ITextComponent buttonText) {
        super(x, y, width, height, textureSet, whenPressed, buttonTooltip, buttonText);
    }

    public ImageSetModButton(int x, int y, int width, int height, WidgetTextureSet textureSet, IPressable whenPressed, ITextComponent buttonText) {
        super(x, y, width, height, textureSet, whenPressed, buttonText);
    }

    public ImageSetModButton renderOnCorner(boolean renderOnCorner) {
        this.renderOnCorner = renderOnCorner;
        return this;
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(stack, mouseX, mouseY, partialTicks);
        Minecraft minecraft = Minecraft.getInstance();
        if (!this.checkedForUpdates) {
            this.checkerStatus = ClientModLoader.checkForUpdates();
            this.checkedForUpdates = true;
        }

        if (this.checkerStatus == null || !this.checkerStatus.shouldDraw() || !FMLConfig.runVersionCheck()) return;

        minecraft.getTextureManager().bind(GUITextures.VERSION_CHECKER_ICONS);
        RenderSystem.color4f(1, 1, 1, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        if (this.renderOnCorner) {
            blit(stack, this.x + this.width - 5, this.y - 3, this.checkerStatus.getSheetOffset() * 8, (this.checkerStatus.isAnimated() && ((System.currentTimeMillis() / 800 & 1) == 1)) ? 8 : 0, 8, 8, 64, 16);
        } else {
            blit(stack, this.x + this.width - (this.height / 2 + 4), this.y + (this.height / 2 - 4), this.checkerStatus.getSheetOffset() * 8, (this.checkerStatus.isAnimated() && ((System.currentTimeMillis() / 800 & 1) == 1)) ? 8 : 0, 8, 8, 64, 16);
        }

        RenderSystem.color4f(1, 1, 1, 1);
    }
}
