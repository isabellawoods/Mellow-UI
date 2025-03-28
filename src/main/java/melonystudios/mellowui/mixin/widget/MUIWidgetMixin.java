package melonystudios.mellowui.mixin.widget;

import melonystudios.mellowui.config.MellowConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(value = Widget.class)
public class MUIWidgetMixin {
    @Shadow(remap = false)
    @Final
    public static int UNSET_FG_COLOR;
    @Shadow(remap = false)
    protected int packedFGColor;
    @Shadow
    public boolean active;
    @Shadow
    private boolean focused;
    @Shadow
    protected boolean isHovered;

    @Inject(method = "getFGColor", at = @At("HEAD"), cancellable = true, remap = false)
    public void getFGColor(CallbackInfoReturnable<Integer> callback) {
        callback.cancel();
        if (this.packedFGColor != UNSET_FG_COLOR) callback.setReturnValue(this.packedFGColor);
        int activeColor = (MellowConfigs.CLIENT_CONFIGS.yellowButtonHighlight.get() || Minecraft.getInstance().getResourcePackRepository().getSelectedIds().contains("programmer_art"))
                && (this.isHovered || this.focused) ? 0xFFFFA0 : 0xFFFFFF;
        callback.setReturnValue(this.active ? activeColor : 0xA0A0A0); // White : Light Gray
    }
}
