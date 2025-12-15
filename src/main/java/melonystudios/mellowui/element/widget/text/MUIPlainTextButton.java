package melonystudios.mellowui.element.widget.text;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.util.Mth;

public class MUIPlainTextButton extends Button {
    private final Font font;
    private final Component message;
    private final Component underlinedMessage;

    public MUIPlainTextButton(int x, int y, int width, int height, Component message, OnPress whenPressed, Font font) {
        super(x, y, width, height, message, whenPressed);
        this.font = font;
        this.message = ComponentUtils.mergeStyles(message.copy(), TextComponents.selectableStyle(false, true));
        this.underlinedMessage = ComponentUtils.mergeStyles(message.copy(), TextComponents.selectableStyle(true, true).withUnderlined(true));
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        Component component = this.isHoveredOrFocused() ? this.underlinedMessage : this.message;
        drawString(stack, this.font, component, this.x, this.y, 0xFFFFFF | Mth.ceil(this.alpha * 255) << 24);
        if (this.isHovered) RenderComponents.INSTANCE.requestCursor(CursorTypes.POINTING_HAND);
    }
}
