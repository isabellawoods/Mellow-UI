package melonystudios.mellowui.mixin.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.element.RenderComponents;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.LockIconButton;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LockIconButton.class)
public class MUILockIconButtonMixin extends Button {
    public MUILockIconButtonMixin(int x, int y, int width, int height, ITextComponent text, IPressable whenPressed) {
        super(x, y, width, height, text, whenPressed);
    }

    @Inject(method = "renderButton", at = @At("TAIL"))
    public void changeCursorShape(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (this.isHovered && this.active) RenderComponents.INSTANCE.requestCursor(CursorTypes.POINTING_HAND);
    }
}
