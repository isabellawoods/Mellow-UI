package melonystudios.mellowui.mixin.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.element.RenderComponents;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LockIconButton.class)
public class MUILockIconButtonMixin extends Button {
    public MUILockIconButtonMixin(int x, int y, int width, int height, Component text, OnPress whenPressed) {
        super(x, y, width, height, text, whenPressed);
    }

    @Inject(method = "renderButton", at = @At("TAIL"))
    public void changeCursorShape(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (this.isHovered && this.active) RenderComponents.INSTANCE.requestCursor(CursorTypes.POINTING_HAND);
    }
}
