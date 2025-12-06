package melonystudios.mellowui.element.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.element.RenderComponents;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.client.ClientModLoader;
import net.minecraftforge.fml.loading.FMLConfig;

public class ModButton extends Button {
    private VersionChecker.Status checkerStatus = null;
    private boolean checkedForUpdates = false;
    private boolean renderOnCorner = false;

    public ModButton(int x, int y, int width, int height, ITextComponent buttonText, IPressable whenPressed) {
        super(x, y, width, height, buttonText, whenPressed);
    }

    public ModButton(int x, int y, int width, int height, ITextComponent buttonText, IPressable whenPressed, ITooltip buttonTooltip) {
        super(x, y, width, height, buttonText, whenPressed, buttonTooltip);
    }

    public ModButton renderOnCorner(boolean renderOnCorner) {
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

    @Override
    public void renderToolTip(MatrixStack stack, int mouseX, int mouseY) {
        if (this.isFocused()) this.onTooltip.onTooltip(this, stack, this.x, this.y);
        else if (this.isHovered()) this.onTooltip.onTooltip(this, stack, mouseX, mouseY);
    }
}
