package melonystudios.mellowui.mixin.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(EditBox.class)
public abstract class MUIEditBoxMixin extends AbstractWidget {
    @Shadow
    public abstract boolean isVisible();

    public MUIEditBoxMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Inject(method = "renderButton", at = @At("TAIL"))
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (this.isVisible()) {
            if (this.isFocused()) {
                this.renderToolTip(stack, this.x + this.width, this.y);
            } else if (this.isHovered) {
                this.renderToolTip(stack, mouseX, mouseY);
            }
        }
    }
}
