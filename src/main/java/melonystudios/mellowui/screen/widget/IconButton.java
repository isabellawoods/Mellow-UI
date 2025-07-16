package melonystudios.mellowui.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public class IconButton extends Button {
    private final WidgetTextureSet textureSet;
    private final int textureWidth;
    private final int textureHeight;
    private boolean renderShadow = true;

    public IconButton(int x, int y, int width, int height, WidgetTextureSet textureSet, Component text, OnPress onPress) {
        super(x, y, width, height, text, onPress);
        this.textureSet = textureSet;
        this.textureWidth = width;
        this.textureHeight = height;
    }

    public IconButton(int x, int y, int width, int height, WidgetTextureSet textureSet, Component text, OnPress onPress, OnTooltip tooltipText) {
        super(x, y, width, height, text, onPress, tooltipText);
        this.textureSet = textureSet;
        this.textureWidth = width;
        this.textureHeight = height;
    }

    public IconButton shouldRenderShadow(boolean renderShadow) {
        this.renderShadow = renderShadow;
        return this;
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.textureSet.getWidgetTexture(this.isHoveredOrFocused(), this.active));
        RenderSystem.setShaderColor(1, 1, 1, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        // Icon shadow
        if (this.renderShadow) {
            RenderSystem.setShaderColor(0.25F, 0.25F, 0.25F, this.alpha);
            blit(stack, this.x + 1, this.y + 1, 0, 0, this.width + 1, this.height + 1, this.textureWidth, this.textureHeight);
            RenderSystem.setShaderColor(1, 1, 1, this.alpha);
        }

        // Icon
        blit(stack, this.x, this.y, 0, 0, this.width, this.height, this.textureWidth, this.textureHeight);

        // Tooltip
        if (this.isFocused()) this.renderToolTip(stack, this.x, this.y);
        else if (this.isHovered) this.renderToolTip(stack, mouseX, mouseY);
    }
}
