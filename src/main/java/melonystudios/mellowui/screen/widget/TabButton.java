package melonystudios.mellowui.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TabButton extends Button implements ScrollingText {
    private boolean selected;

    public TabButton(int x, int y, int width, int height, Component text, OnPress onPress) {
        super(x, y, width, height, text, onPress);
    }

    public TabButton(int x, int y, int width, int height, Component title, OnPress onPress, OnTooltip tooltip) {
        super(x, y, width, height, title, onPress, tooltip);
    }

    public boolean selected() {
        return this.selected;
    }

    public TabButton setSelected(boolean selected) {
        this.selected = selected;
        return this;
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
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int color = this.getFGColor();
        ResourceLocation tabLocation;
        if (this.selected() && (this.isHoveredOrFocused())) {
            tabLocation = GUITextures.TAB_SELECTED_HIGHLIGHTED;
        } else if (this.selected()) {
            tabLocation = GUITextures.TAB_SELECTED;
        } else if (this.isHoveredOrFocused()) {
            tabLocation = GUITextures.TAB_HIGHLIGHTED;
        } else {
            tabLocation = GUITextures.TAB;
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, tabLocation);
        RenderSystem.setShaderColor(1, 1, 1, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        blit(stack, this.x, this.y, 0, 0, this.width / 2, this.height, 130, 24);
        blit(stack, this.x + this.width / 2, this.y, 130 - this.width / 2F, 0, this.width / 2, this.height, 130, 24);
        this.renderBg(stack, minecraft, mouseX, mouseY);
        this.renderString(stack, font, color);

        if (this.selected()) this.renderFocusUnderline(stack, font, color);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        if (this.isFocused()) this.renderToolTip(stack, this.x, this.y);
        else if (this.isHovered) this.renderToolTip(stack, mouseX, mouseY);
    }

    public void renderString(PoseStack stack, Font font, int color) {
        int padding = WidgetConfigs.WIDGET_CONFIGS.tabTextBorderPadding.get();
        int minX = this.x + padding;
        int minY = this.y + (this.selected() ? 0 : 3);
        int maxX = this.x + this.getWidth() - padding;
        int maxY = this.y + this.getHeight();
        this.renderScrollingString(stack, font, this.getMessage(), minX, minY, maxX, maxY, color);
    }

    private void renderFocusUnderline(PoseStack stack, Font font, int color) {
        int xOffset = Math.min(font.width(this.getMessage()), this.width - 4);
        int minX = this.x + (this.width - xOffset) / 2;
        int minY = this.y + this.height - 2;
        fill(stack, minX, minY, minX + xOffset, minY + 1, color | 255 << 24);
    }
}
