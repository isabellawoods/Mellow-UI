package melonystudios.mellowui.element.widget.text;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.element.text.TextComponents;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;

public class PlainTextButton extends Button {
    private final FontRenderer font;
    private final ITextComponent message;
    private final ITextComponent underlinedMessage;

    public PlainTextButton(int x, int y, int width, int height, ITextComponent message, IPressable whenPressed, FontRenderer font) {
        super(x, y, width, height, message, whenPressed);
        this.font = font;
        this.message = TextComponentUtils.mergeStyles(message.copy(), TextComponents.selectableStyle(false, true));
        this.underlinedMessage = TextComponentUtils.mergeStyles(message.copy(), TextComponents.selectableStyle(true, true).withUnderlined(true));
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        ITextComponent component = this.isHovered() || this.isFocused() ? this.underlinedMessage : this.message;
        drawString(stack, this.font, component, this.x, this.y, 0xFFFFFF | MathHelper.ceil(this.alpha * 255) << 24);
    }
}
