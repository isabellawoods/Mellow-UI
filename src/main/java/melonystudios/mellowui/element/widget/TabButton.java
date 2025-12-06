package melonystudios.mellowui.element.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.ScrollingText;
import melonystudios.mellowui.util.Alignment;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class TabButton extends Button implements ScrollingText {
    private boolean selected;
    private final String name;

    public TabButton(int x, int y, int width, int height, String name, ITextComponent text, IPressable whenPressed) {
        super(x, y, width, height, text, whenPressed);
        this.name = name;
    }

    public TabButton(int x, int y, int width, int height, String name, ITextComponent title, IPressable whenPressed, ITooltip tooltip) {
        super(x, y, width, height, title, whenPressed, tooltip);
        this.name = name;
    }

    public String tabName() {
        return this.name;
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
        blit(stack, this.x + this.width / 2, this.y, 130 - this.width / 2F, 0, this.width / 2, this.height, 130, 24);
        this.renderBg(stack, minecraft, mouseX, mouseY);
        this.renderWidgetText(
                () -> this.renderString(font, color | MathHelper.ceil(this.alpha * 255F) << 24),
                () -> drawCenteredString(stack, font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2 + (this.selected() ? 0 : 2), color | MathHelper.ceil(this.alpha * 255F) << 24)
        );
        if (this.selected()) this.renderFocusUnderline(stack, font, color);
        if (this.isHovered && this.active && !this.selected()) RenderComponents.INSTANCE.requestCursor(CursorTypes.POINTING_HAND);
        if (this.isFocused()) this.renderToolTip(stack, this.x, this.y);
        else if (this.isHovered()) this.renderToolTip(stack, mouseX, mouseY);
    }

    public void renderString(FontRenderer font, int color) {
        int padding = WidgetConfigs.WIDGET_CONFIGS.tabTextPadding.get();
        int minX = this.x + padding;
        int minY = this.y + (this.selected() ? 0 : 3);
        int maxX = this.x + this.getWidth() - padding;
        int maxY = this.y + this.getHeight();
        this.renderAlignedScrollingText(font, this.getMessage(), Alignment.CENTER, minX, minY, maxX, maxY, color);
    }

    private void renderFocusUnderline(MatrixStack stack, FontRenderer font, int color) {
        int xOffset = Math.min(font.width(this.getMessage()), this.width - 4);
        int minX = this.x + (this.width - xOffset) / 2;
        int minY = this.y + this.height - 2;
        fill(stack, minX, minY, minX + xOffset, minY + 1, color | ((int) (this.alpha * 255) << 24));
    }
}
