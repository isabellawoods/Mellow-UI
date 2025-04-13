package melonystudios.mellowui.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

import static net.minecraft.util.ColorHelper.PackedColor.*;

public class SeparatorWidget extends Widget {
    public SeparatorWidget(int x, int y, int width, int height, ITextComponent text) {
        super(x, y, width, height, text);
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        this.renderBg(stack, minecraft, mouseX, mouseY);
        int textColor = this.getFGColor();
        int height = this.y + (this.height / 2);
        int textWidth = minecraft.font.width(this.getMessage());

        if (this.active) {
            // Background
            RenderSystem.enableBlend();
            Minecraft.getInstance().getTextureManager().bind(this.isFocused() ? GUITextures.SEPARATOR_HIGHLIGHTED : GUITextures.SEPARATOR);
            blit(stack, this.x, this.y, 0, 0, this.width / 2, this.height, 200, 20);
            blit(stack, this.x + this.width / 2, this.y, 200 - this.width / 2, 0, this.width / 2, this.height, 200, 20);
            RenderSystem.disableBlend();
        }

        // Lines
        fill(stack, this.x + 2, height, (this.x + this.width / 2) - (textWidth / 2) - 4, height + 1, 0xFF000000 + textColor);
        fill(stack, this.x + 3, height + 1, (this.x + this.width / 2) - (textWidth / 2) - 3, height + 2, this.getShadowColor(textColor));

        fill(stack, (this.x + this.width / 2) + (textWidth / 2) + 4, height, this.x + this.width - 2, height + 1, 0xFF000000 + textColor);
        fill(stack, (this.x + this.width / 2) + (textWidth / 2) + 5, height + 1, this.x + this.width - 1, height + 2, this.getShadowColor(textColor));

        drawCenteredString(stack, minecraft.font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, textColor);
    }

    private int getShadowColor(int color) {
        float red = red(color) * 0.25F;
        float green = green(color) * 0.25F;
        float blue = blue(color) * 0.25F;
        return color((int) (this.alpha * 255), (int) red, (int) green, (int) blue);
    }
}
