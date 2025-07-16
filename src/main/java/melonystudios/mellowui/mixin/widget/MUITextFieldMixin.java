package melonystudios.mellowui.mixin.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(TextFieldWidget.class)
public abstract class MUITextFieldMixin extends Widget {
    @Shadow
    public abstract boolean isVisible();

    public MUITextFieldMixin(int x, int y, int width, int height, ITextComponent message) {
        super(x, y, width, height, message);
    }

    @Inject(method = "renderButton", at = @At("TAIL"))
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (this.isVisible()) {
            if (this.isFocused()) {
                this.renderToolTip(stack, this.x + this.width, this.y);
            } else if (this.isHovered()) {
                this.renderToolTip(stack, mouseX, mouseY);
            }
        }
    }
}
