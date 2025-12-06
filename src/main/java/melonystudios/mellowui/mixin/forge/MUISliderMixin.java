package melonystudios.mellowui.mixin.forge;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.element.RenderComponents;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.fml.client.gui.widget.Slider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Slider.class, remap = false)
public class MUISliderMixin extends ExtendedButton {
    @Shadow
    public boolean dragging;

    public MUISliderMixin(int x, int y, int width, int height, ITextComponent text, IPressable whenPressed) {
        super(x, y, width, height, text, whenPressed);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        if (this.isHovered) RenderComponents.INSTANCE.requestCursor(this.dragging ? CursorTypes.RESIZE_EW : CursorTypes.POINTING_HAND);
    }
}
