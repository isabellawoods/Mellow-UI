package melonystudios.mellowui.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class TabButton extends Button implements ScrollingText {
    private boolean selected;

    public TabButton(int x, int y, int width, int height, ITextComponent text, IPressable whenPressed) {
        super(x, y, width, height, text, whenPressed);
    }

    public TabButton(int x, int y, int width, int height, ITextComponent title, IPressable whenPressed, ITooltip tooltip) {
        super(x, y, width, height, title, whenPressed, tooltip);
    }

    public boolean selected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.selected()) return false;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onPress() {
        super.onPress();
        this.setSelected(true);
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer font = minecraft.font;
        int color = this.getFGColor();
        ResourceLocation tabLocation;
        if (this.selected() && (this.isFocused() || this.isHovered())) {
            tabLocation = GUITextures.TAB_SELECTED_HIGHLIGHTED;
        } else if (this.selected()) {
            tabLocation = GUITextures.TAB_SELECTED;
        } else if (this.isFocused() || this.isHovered()) {
            tabLocation = GUITextures.TAB_HIGHLIGHTED;
        } else {
            tabLocation = GUITextures.TAB;
        }

        minecraft.getTextureManager().bind(tabLocation);
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        blit(stack, this.x, this.y, 0, 0, this.width / 2, this.height, 130, 24);
        blit(stack, this.x + this.width / 2, this.y, 130 - this.width / 2, 0, this.width / 2, this.height, 130, 24);
        this.renderBg(stack, minecraft, mouseX, mouseY);
        this.renderString(stack, font, color);
        if (this.selected()) this.renderFocusUnderline(stack, font, color);

        if (this.isHovered()) this.renderToolTip(stack, mouseX, mouseY);
    }

    public void renderString(MatrixStack stack, FontRenderer font, int color) {
        int minX = this.x + 2;
        int minY = this.y + (this.selected() ? 0 : 3);
        int maxX = this.x + this.getWidth() - 2;
        int maxY = this.y + this.getHeight();
        renderScrollingString(stack, font, this.getMessage(), minX, minY, maxX, maxY, color);
    }

    private void renderFocusUnderline(MatrixStack stack, FontRenderer font, int color) {
        int xOffset = Math.min(font.width(this.getMessage()), this.width - 4);
        int minX = this.x + (this.width - xOffset) / 2;
        int minY = this.y + this.height - 2;
        fill(stack, minX, minY, minX + xOffset, minY + 1, color | 255 << 24);
    }
}
