package melonystudios.mellowui.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class HardcoreSetButton extends ImageSetButton {
    private boolean selected = false;

    public HardcoreSetButton(int x, int y, int width, int height, IPressable whenPressed, ITooltip buttonTooltip, ITextComponent buttonText) {
        super(x, y, width, height, null, whenPressed, buttonTooltip, buttonText);
    }

    public boolean selected() {
        return this.selected;
    }

    public HardcoreSetButton setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        ResourceLocation iconTexture = this.selected ? GUITextures.HARDCORE_ON : GUITextures.HARDCORE_OFF;

        // Button
        minecraft.getTextureManager().bind(WIDGETS_LOCATION);
        RenderSystem.color4f(1, 1, 1, this.alpha);
        int yImage = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(stack, this.x, this.y, 0, 46 + yImage * 20, this.width / 2, this.height);
        this.blit(stack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + yImage * 20, this.width / 2, this.height);

        // Text
        if (this.renderText) {
            this.renderScrollingString(stack, minecraft.font, 2, this.getFGColor() | MathHelper.ceil(this.alpha * 255F) << 24);
        }

        // Button icon
        minecraft.getTextureManager().bind(iconTexture);
        switch (this.alignment) {
            case RIGHT: {
                blit(stack, this.x + this.width - 20, this.y, 0, 0, 20, 20, 20, 20);
                break;
            }
            case LEFT: {
                blit(stack, this.x, this.y, 0, 0, 20, 20, 20, 20);
                break;
            }
            case CENTER: default: {
                blit(stack, this.x + this.width / 2 - 10, this.y, 0, 0, 20, 20, 20, 20);
            }
        }

        this.renderBg(stack, minecraft, mouseX, mouseY);
        if (this.isFocused()) this.renderToolTip(stack, this.x, this.y);
        else if (this.isHovered()) this.renderToolTip(stack, mouseX, mouseY);
    }
}
