package melonystudios.mellowui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class DisableableImageButton extends ImageButton {
    private final ResourceLocation buttonLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int yOffset;
    private final int textureWidth;
    private final int textureHeight;

    public DisableableImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yOffset, ResourceLocation buttonLocation, int textureWidth, int textureHeight, IPressable whenPressed, ITooltip buttonTooltip, ITextComponent buttonText) {
        super(x, y, width, height, xTexStart, yTexStart, yOffset, buttonLocation, textureWidth, textureHeight, whenPressed, buttonTooltip, buttonText);
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yOffset = yOffset;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.buttonLocation = buttonLocation;
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(this.buttonLocation);
        int yTexStart = this.yTexStart;
        if (!this.active) {
            yTexStart = this.yOffset + this.yOffset;
        } else if (this.isHovered()) {
            yTexStart += this.yOffset;
        }

        RenderSystem.enableDepthTest();
        blit(stack, this.x, this.y, (float) this.xTexStart, (float) yTexStart, this.width, this.height, this.textureWidth, this.textureHeight);
        if (this.isHovered()) this.renderToolTip(stack, mouseX, mouseY);
    }
}
