package melonystudios.mellowui.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.screen.IconSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class IconButton extends Button {
    private final IconSet iconSet;
    private final int textureWidth;
    private final int textureHeight;
    private boolean renderShadow = true;

    public IconButton(int x, int y, int width, int height, IconSet iconSet, ITextComponent text, IPressable whenPressed) {
        super(x, y, width, height, text, whenPressed);
        this.iconSet = iconSet;
        this.textureWidth = width;
        this.textureHeight = height;
    }

    public IconButton(int x, int y, int width, int height, IconSet iconSet, ITextComponent text, IPressable whenPressed, ITooltip tooltipText) {
        super(x, y, width, height, text, whenPressed, tooltipText);
        this.iconSet = iconSet;
        this.textureWidth = width;
        this.textureHeight = height;
    }

    public IconButton shouldRenderShadow(boolean renderShadow) {
        this.renderShadow = renderShadow;
        return this;
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(this.iconSet.getIconTexture(this.isHovered() || this.isFocused(), this.active));

        RenderSystem.color4f(1, 1, 1, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        // Icon shadow
        if (this.renderShadow) {
            RenderSystem.color4f(0.25F, 0.25F, 0.25F, this.alpha);
            blit(stack, this.x + 1, this.y + 1, 0, 0, this.width + 1, this.height + 1, this.textureWidth, this.textureHeight);
            RenderSystem.color4f(1, 1, 1, this.alpha);
        }

        // Icon
        blit(stack, this.x, this.y, 0, 0, this.width, this.height, this.textureWidth, this.textureHeight);

        // Tooltip
        if (this.isFocused()) this.renderToolTip(stack, this.x, this.y);
        else if (this.isHovered()) this.renderToolTip(stack, mouseX, mouseY);
    }
}
