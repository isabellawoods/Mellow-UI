package melonystudios.mellowui.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public class SeparatorWidget extends AbstractWidget {
    public SeparatorWidget(int x, int y, int width, int height, Component text) {
        super(x, y, width, height, text);
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        this.renderBg(stack, minecraft, mouseX, mouseY);
        int textColor = this.getFGColor();
        int height = this.y + (this.height / 2);
        int textWidth = minecraft.font.width(this.getMessage());
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, this.alpha);
        // Background
        RenderSystem.setShaderTexture(0, this.isFocused() ? GUITextures.SEPARATOR_HIGHLIGHTED : GUITextures.SEPARATOR);
        blit(stack, this.x, this.y, 0, 0, this.width / 2, this.height, 200, 20);
        blit(stack, this.x + this.width / 2, this.y, 200 - this.width / 2, 0, this.width / 2, this.height, 200, 20);
        RenderSystem.disableBlend();

        // Lines
        fill(stack, this.x + 2, height, (this.x + this.width / 2) - (textWidth / 2) - 4, height + 1, 0xFF000000 + textColor);
        fill(stack, this.x + 3, height + 1, (this.x + this.width / 2) - (textWidth / 2) - 3, height + 2, MellowUtils.getShadowColor(textColor, this.alpha));

        fill(stack, (this.x + this.width / 2) + (textWidth / 2) + 4, height, this.x + this.width - 2, height + 1, 0xFF000000 + textColor);
        fill(stack, (this.x + this.width / 2) + (textWidth / 2) + 5, height + 1, this.x + this.width - 1, height + 2, MellowUtils.getShadowColor(textColor, this.alpha));

        drawCenteredString(stack, minecraft.font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, textColor);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, this.getMessage());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }
}
