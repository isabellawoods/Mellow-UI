package melonystudios.mellowui.mixin.screen.list;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OptionsRowList.class)
public abstract class MUIOptionsListMixin extends AbstractOptionList<OptionsRowList.Row> {
    public MUIOptionsListMixin(Minecraft minecraft, int width, int height, int headerPosition, int footerPosition, int itemHeight) {
        super(minecraft, width, height, headerPosition, footerPosition, itemHeight);
    }

    @Inject(method = "getRowWidth", at = @At("HEAD"), cancellable = true)
    public void getRowWidth(CallbackInfoReturnable<Integer> callback) {
        callback.setReturnValue(310);
    }

    @Inject(method = "getScrollbarPosition", at = @At("HEAD"), cancellable = true)
    protected void getScrollbarPosition(CallbackInfoReturnable<Integer> callback) {
        callback.setReturnValue(this.getRealRowRight() + 10);
    }

    @Unique
    private int getRealRowRight() {
        return this.getRealRowLeft() + this.getRowWidth();
    }

    @Unique
    private int getRealRowLeft() {
        return this.x0 + this.width / 2 - this.getRowWidth() / 2;
    }
}
