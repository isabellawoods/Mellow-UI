package melonystudios.mellowui.mixin.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.element.RenderComponents;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSliderButton.class)
public abstract class MUIAbstractSliderButtonMixin extends AbstractWidget {
    @Unique
    private boolean dragging;

    public MUIAbstractSliderButtonMixin(int x, int y, int width, int height, Component text) {
        super(x, y, width, height, text);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
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
