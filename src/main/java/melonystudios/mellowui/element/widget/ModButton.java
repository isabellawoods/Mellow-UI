package melonystudios.mellowui.element.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.element.RenderComponents;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.loading.ClientModLoader;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.loading.FMLConfig;

public class ModButton extends Button {
    private VersionChecker.Status checkerStatus = null;
    private boolean checkedForUpdates = false;
    private boolean renderOnCorner = false;

    public ModButton(int x, int y, int width, int height, Component buttonText, OnPress onPress) {
        super(x, y, width, height, buttonText, onPress);
    }

    public ModButton(int x, int y, int width, int height, Component buttonText, OnPress onPress, OnTooltip buttonTooltip) {
        super(x, y, width, height, buttonText, onPress, buttonTooltip);
    }

    public ModButton renderOnCorner(boolean renderOnCorner) {
        this.renderOnCorner = renderOnCorner;
        return this;
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(stack, mouseX, mouseY, partialTicks);
        if (!this.checkedForUpdates) {
            this.checkerStatus = ClientModLoader.checkForUpdates();
            this.checkedForUpdates = true;
        }

        if (this.checkerStatus == null || !this.checkerStatus.shouldDraw() || !FMLConfig.runVersionCheck()) return;
        RenderComponents.INSTANCE.renderUpdateAvailableIcon(this.x, this.y, this.width, this.height, this.alpha, this.renderOnCorner, this.checkerStatus);
    }

    @Override
    public void renderToolTip(PoseStack stack, int mouseX, int mouseY) {
        if (this.isFocused()) this.onTooltip.onTooltip(this, stack, this.x, this.y);
        else if (this.isHovered) this.onTooltip.onTooltip(this, stack, mouseX, mouseY);
    }
}
