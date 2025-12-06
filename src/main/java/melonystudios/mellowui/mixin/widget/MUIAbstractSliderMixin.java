package melonystudios.mellowui.mixin.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.element.RenderComponents;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSlider.class)
public abstract class MUIAbstractSliderMixin extends Widget {
    @Unique
    private boolean dragging;

    public MUIAbstractSliderMixin(int x, int y, int width, int height, ITextComponent text) {
        super(x, y, width, height, text);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        if (this.isHovered) RenderComponents.INSTANCE.requestCursor(this.dragging ? CursorTypes.RESIZE_EW : CursorTypes.POINTING_HAND);
    }

    @Inject(method = "onClick", at = @At("HEAD"))
    public void startDragging(double mouseX, double mouseY, CallbackInfo callback) {
        this.dragging = this.active;
    }

    @Inject(method = "onRelease", at = @At("HEAD"))
    public void stopDragging(double mouseX, double mouseY, CallbackInfo callback) {
        this.dragging = false;
    }
}
