package melonystudios.mellowui.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.screen.WidgetTextureSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ImageSetButton extends Button {
    private final WidgetTextureSet textureSet;

    public ImageSetButton(int x, int y, int width, int height, WidgetTextureSet textureSet, IPressable whenPressed, ITooltip buttonTooltip, ITextComponent buttonText) {
        super(x, y, width, height, buttonText, whenPressed, buttonTooltip);
        this.textureSet = textureSet;
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        ResourceLocation iconTexture = this.textureSet.getWidgetTexture(this.isHovered() || this.isFocused(), this.active);

        // Button
        minecraft.getTextureManager().bind(WIDGETS_LOCATION);
        RenderSystem.color4f(1, 1, 1, this.alpha);
        int yImage = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(stack, this.x, this.y, 0, 46 + yImage * 20, this.width / 2, this.height);
        this.blit(stack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + yImage * 20, this.width / 2, this.height);

        // Button icon
        minecraft.getTextureManager().bind(iconTexture);
        blit(stack, this.x + this.width / 2 - 10, this.y, 0, 0, 20, 20, 20, 20);

        this.renderBg(stack, minecraft, mouseX, mouseY);
        if (this.isHovered()) this.renderToolTip(stack, mouseX, mouseY);
    }
}
