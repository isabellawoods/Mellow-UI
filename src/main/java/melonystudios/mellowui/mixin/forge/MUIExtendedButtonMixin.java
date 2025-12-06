package melonystudios.mellowui.mixin.forge;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.element.RenderComponents;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ExtendedButton.class, remap = false)
public class MUIExtendedButtonMixin extends Button {
    public MUIExtendedButtonMixin(int x, int y, int width, int height, ITextComponent text, IPressable whenPressed) {
        super(x, y, width, height, text, whenPressed);
    }

    @Inject(method = "renderButton", at = @At("TAIL"), remap = true)
    public void changeCursorShape(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (this.visible && this.isHovered && this.active) RenderComponents.INSTANCE.requestCursor(CursorTypes.POINTING_HAND);
    }
}
