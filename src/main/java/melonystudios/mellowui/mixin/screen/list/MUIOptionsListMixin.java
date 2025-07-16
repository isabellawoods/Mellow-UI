package melonystudios.mellowui.mixin.screen.list;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.OptionsList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OptionsList.class)
public abstract class MUIOptionsListMixin<E extends ContainerObjectSelectionList.Entry<E>> extends ContainerObjectSelectionList<E> {
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
