package melonystudios.mellowui.mixin.option;

import melonystudios.mellowui.element.RenderComponents;
import net.minecraft.client.settings.BooleanOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BooleanOption.class)
public class MUIBooleanOptionMixin {
    @ModifyArg(method = "createButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;split(Lnet/minecraft/util/text/ITextProperties;I)Ljava/util/List;"))
    public int setTooltipWidth(int maxWidth) {
        return RenderComponents.TOOLTIP_MAX_WIDTH;
    }
}
