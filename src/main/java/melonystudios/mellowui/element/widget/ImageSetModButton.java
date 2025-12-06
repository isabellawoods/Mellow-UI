package melonystudios.mellowui.element.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.WidgetTextureSet;
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
        if (!this.checkedForUpdates) {
            this.checkerStatus = ClientModLoader.checkForUpdates();
            this.checkedForUpdates = true;
        }

        if (this.checkerStatus == null || !this.checkerStatus.shouldDraw() || !FMLConfig.runVersionCheck()) return;
        RenderComponents.INSTANCE.renderUpdateAvailableIcon(this.x, this.y, this.width, this.height, this.alpha, this.renderOnCorner, this.checkerStatus);
    }
}
