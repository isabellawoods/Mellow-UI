package melonystudios.mellowui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.util.Alignment;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.text.ScrollingText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

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
        this.renderWidgetText(
                () -> this.renderString(stack, font, color | Mth.ceil(this.alpha * 255F) << 24),
                () -> drawCenteredString(stack, minecraft.font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2 + (this.selected() ? 0 : 2), color | Mth.ceil(this.alpha * 255F) << 24)
        );

        if (this.selected()) this.renderFocusUnderline(stack, font, color | Mth.ceil(this.alpha * 255F) << 24);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        if (this.isFocused()) this.renderToolTip(stack, this.x, this.y);
        else if (this.isHovered) this.renderToolTip(stack, mouseX, mouseY);
    }

    public void renderString(PoseStack stack, Font font, int color) {
        int padding = WidgetConfigs.WIDGET_CONFIGS.tabTextPadding.get();
        int minX = this.x + padding;
        int minY = this.y + (this.selected() ? 0 : 3);
        int maxX = this.x + this.getWidth() - padding;
        int maxY = this.y + this.getHeight();
        this.renderAlignedScrollingText(stack, font, this.getMessage(), Alignment.CENTER, minX, minY, maxX, maxY, color);
    }

    private void renderFocusUnderline(PoseStack stack, Font font, int color) {
        int xOffset = Math.min(font.width(this.getMessage()), this.width - 4);
        int minX = this.x + (this.width - xOffset) / 2;
        int minY = this.y + this.height - 2;
        fill(stack, minX, minY, minX + xOffset, minY + 1, color);
    }
}
